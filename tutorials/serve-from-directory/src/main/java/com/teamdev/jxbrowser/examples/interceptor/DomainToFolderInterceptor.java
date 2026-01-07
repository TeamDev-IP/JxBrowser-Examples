/*
 *  Copyright 2026, TeamDev. All rights reserved.
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

import static java.nio.file.Files.exists;
import static java.nio.file.Files.isDirectory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

/**
 * An interceptor that treats every URL under the given domain as a path to a
 * file on disk and loads it.
 *
 * <p>The interceptor is configured with the domain name and the content
 * directory. For every request, it takes the path component of the URL and
 * looks for it in the content directory. That means a request to
 * {@code example.com/docs/index.html} will load {@code docs/index.html} file
 * from the content directory. The MIME type of the file is derived
 * automatically.
 *
 * <p>This interceptor responds with the following status codes:
 *
 * <ul>
 *     <li><b>200 OK</b> - the file was found and read properly.
 *         In this case, the {@code Content-Type} header is sent.</li>
 *     <li><b>404 Not Found</b> - the file could not be found.</li>
 *     <li><b>500 Internal Server Error</b> - couldn't read the file.</li>
 * </ul>
 *
 * <p>This interceptor considers only the path component of the URL request.
 * It ignores request parameters and headers.
 */
public final class DomainToFolderInterceptor extends DomainContentInterceptor {

    private final Path contentRoot;

    /**
     * Creates a URL interceptor for the given domain to load files from the
     * given directory.
     *
     * @param domain      a domain name to intercept
     * @param contentRoot a path to the directory with files to load
     */
    public DomainToFolderInterceptor(String domain, Path contentRoot) {
        super(domain);
        this.contentRoot = contentRoot.toAbsolutePath();
    }

    /**
     * Resolves the requested path to a file and opens it.
     */
    @Override
    protected InputStream openContent(String path) throws IOException {
        var filePath = contentRoot.resolve(path);
        if (exists(filePath) && !isDirectory(filePath)) {
            return new FileInputStream(filePath.toFile());
        }
        return null;
    }
}
