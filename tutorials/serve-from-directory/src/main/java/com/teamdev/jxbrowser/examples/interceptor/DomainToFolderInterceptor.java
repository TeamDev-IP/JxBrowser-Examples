/*
 *  Copyright 2024, TeamDev. All rights reserved.
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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.teamdev.jxbrowser.examples.interceptor.MimeTypes.mimeType;
import static com.teamdev.jxbrowser.internal.string.StringPreconditions.checkNotNullEmptyOrBlank;
import static com.teamdev.jxbrowser.logging.Logger.error;
import static com.teamdev.jxbrowser.net.HttpStatus.INTERNAL_SERVER_ERROR;
import static com.teamdev.jxbrowser.net.HttpStatus.NOT_FOUND;
import static com.teamdev.jxbrowser.net.HttpStatus.OK;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import com.teamdev.jxbrowser.net.HttpHeader;
import com.teamdev.jxbrowser.net.HttpStatus;
import com.teamdev.jxbrowser.net.UrlRequestJob;
import com.teamdev.jxbrowser.net.callback.InterceptUrlRequestCallback;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * An interceptor that treats every URL under the given domain as a path to the file on disk and
 * loads it.
 *
 * <p>The interceptor is configured with the domain name and the content directory. For every
 * request, it takes the path component of the URL and looks for it in the content directory. That
 * means a request to {@code example.com/docs/index.html} will load {@code docs/index.html} file
 * from the content directory. The mime type of the file is derived automatically.
 *
 * <p>This interceptor responds with the following status codes:
 *
 * <ul>
 *     <li><b>200 OK</b> - the file was found and read properly.
 *         In this case, the {@code Content-Type} header is sent.</li>
 *     <li><b>404 Not Found</b> - the file could not be found due to an invalid path.</li>
 *     <li><b>500 Internal Server Error</b> - couldn't read the file.</li>
 * </ul>
 *
 * <p>This interceptor considers only the path component of the URL request. It ignores request
 * parameters and headers.
 *
 * <p>Note: using this interceptor can reduce performance since it processes all incoming
 * traffic under the scheme.
 */
public final class DomainToFolderInterceptor implements InterceptUrlRequestCallback {

    private static final String CONTENT_TYPE = "Content-Type";

    private final String domain;
    private final Path contentRoot;

    private DomainToFolderInterceptor(String domain, Path contentRoot) {
        this.domain = domain;
        this.contentRoot = contentRoot;
    }

    /**
     * Creates a URL interceptor for the given domain to load files from the given directory.
     *
     * @param domain      a domain name to intercept
     * @param contentRoot a path to the directory with files to load
     */
    public static DomainToFolderInterceptor create(String domain, Path contentRoot) {
        checkNotNull(contentRoot);
        checkNotNullEmptyOrBlank(domain);
        return new DomainToFolderInterceptor(domain, contentRoot.toAbsolutePath());
    }

    @Override
    public Response on(Params params) {
        URI uri = URI.create(params.urlRequest().url());
        if (shouldNotBeIntercepted(uri)) {
            return Response.proceed();
        }
        Path filePath = getPathOnDisk(uri);
        UrlRequestJob job;
        if (fileExists(filePath)) {
            HttpHeader contentType = getContentType(filePath);
            job = createJob(params, OK, singletonList(contentType));
            try {
                readFile(filePath, job);
            } catch (IOException e) {
                error("Failed to read file {0}", e, filePath);
                job = createJob(params, INTERNAL_SERVER_ERROR);
            }
        } else {
            job = createJob(params, NOT_FOUND);
        }
        job.complete();
        return Response.intercept(job);
    }

    private boolean shouldNotBeIntercepted(URI uri) {
        return !uri.getHost().equals(domain);
    }

    private Path getPathOnDisk(URI uri) {
        return Paths.get(contentRoot.toString(), uri.getPath());
    }

    private boolean fileExists(Path filePath) {
        return Files.exists(filePath) && !Files.isDirectory(filePath);
    }

    private HttpHeader getContentType(Path file) {
        return HttpHeader.of(CONTENT_TYPE, mimeType(file).value());
    }

    private UrlRequestJob createJob(Params params,
            HttpStatus httpStatus,
            List<HttpHeader> httpHeaders) {
        UrlRequestJob.Options.Builder builder = UrlRequestJob.Options.newBuilder(httpStatus);
        httpHeaders.forEach(builder::addHttpHeader);
        return params.newUrlRequestJob(builder.build());
    }

    private UrlRequestJob createJob(Params params, HttpStatus httpStatus) {
        return createJob(params, httpStatus, emptyList());
    }

    private void readFile(Path filePath, UrlRequestJob job) throws IOException {
        try (FileInputStream stream = new FileInputStream(filePath.toFile())) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = stream.read(buffer)) > 0) {
                if (bytesRead != buffer.length) {
                    buffer = Arrays.copyOf(buffer, bytesRead);
                }
                job.write(buffer);
            }
        }
    }
}
