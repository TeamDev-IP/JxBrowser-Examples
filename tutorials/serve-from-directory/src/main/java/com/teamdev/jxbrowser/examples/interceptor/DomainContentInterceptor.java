/*
 *  Copyright 2025, TeamDev. All rights reserved.
 *
 *  Redistribution and use in source and/or binary forms, with or without
 *  modification, must retain the above copyright notice and the following
 *  disclaimer.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 *  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 *  A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 *  OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 *  SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 *  LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 *  OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.teamdev.jxbrowser.examples.interceptor;

import static com.teamdev.jxbrowser.net.HttpStatus.INTERNAL_SERVER_ERROR;
import static com.teamdev.jxbrowser.net.HttpStatus.NOT_FOUND;
import static com.teamdev.jxbrowser.net.HttpStatus.OK;

import com.teamdev.jxbrowser.net.HttpHeader;
import com.teamdev.jxbrowser.net.HttpStatus;
import com.teamdev.jxbrowser.net.UrlRequestJob;
import com.teamdev.jxbrowser.net.callback.InterceptUrlRequestCallback;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 * A base URL interceptor that serves content for a specific domain by
 * delegating the actual content lookup to subclasses.
 *
 * <p>This interceptor:
 * <ul>
 *     <li>Intercepts only requests whose host matches the configured domain.</li>
 *     <li>Delegates the content lookup to {@link #openContent(String)}.</li>
 *     <li>Streams the returned content into the {@link UrlRequestJob}.</li>
 * </ul>
 *
 * <p>Subclasses need to provide the logic that locates the content (for example,
 * on disk or on the classpath) and returns it as an {@link InputStream}.
 * The MIME type is derived from the requested path by this base class.
 */
abstract class DomainContentInterceptor implements InterceptUrlRequestCallback {

    private static final String CONTENT_TYPE = "Content-Type";
    private final String domain;

    DomainContentInterceptor(String domain) {
        this.domain = domain;
    }

    @Override
    public Response on(Params params) {
        var uri = URI.create(params.urlRequest().url());
        if (!uri.getHost().equals(domain)) {
            // Let Chromium process requests to other domains as usual.
            return Response.proceed();
        }
        var path = uri.getPath().substring(1);
        try (var content = openContent(path)) {
            if (content == null) {
                var job = createJob(params, NOT_FOUND);
                job.complete();
                return Response.intercept(job);
            }
            var mimeType = MimeTypes.mimeType(path);
            var contentType = HttpHeader.of(CONTENT_TYPE, mimeType);
            var job = createJob(params, OK, contentType);
            writeToJob(content, job);
            job.complete();
            return Response.intercept(job);
        } catch (IOException e) {
            // Return 500 response when the file read failed.
            var job = createJob(params, INTERNAL_SERVER_ERROR);
            job.complete();
            return Response.intercept(job);
        } catch (Exception e) {
            return Response.proceed();
        }
    }

    /**
     * Locates the content for the given {@code uri}.
     *
     * <p>If the content cannot be found, this method should return
     * {@code null}. If the content cannot be read, it should throw an
     * {@link IOException}.
     */
    protected abstract InputStream openContent(String path) throws IOException;

    private UrlRequestJob createJob(Params params, HttpStatus status,
            HttpHeader... headers) {
        var options = UrlRequestJob.Options.newBuilder(status);
        for (var header : headers) {
            options.addHttpHeader(header);
        }
        return params.newUrlRequestJob(options.build());
    }

    /**
     * Writes content of the input stream into the HTTP response.
     */
    private void writeToJob(InputStream stream, UrlRequestJob job)
            throws IOException {
        var content = stream.readAllBytes();
        job.write(content);
    }
}
