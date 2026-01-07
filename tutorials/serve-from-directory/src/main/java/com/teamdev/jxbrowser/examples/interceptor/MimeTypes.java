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

import static com.teamdev.jxbrowser.logging.Logger.warn;
import static java.util.Locale.ROOT;

import java.io.IOException;
import java.util.Properties;

/**
 * A utility for working with MIME types.
 */
final class MimeTypes {

    private static final String OCTET_STREAM = "application/octet-stream";
    private static final String MIME_TYPES_FILE = "ext-to-mime.properties";
    private static final Properties MIME_TYPES = MimeTypes.createMap();

    /**
     * Prevents instantiation of this utility class.
     */
    private MimeTypes() {
    }

    /**
     * Derives {@code MimeType} from the extension of the {@code fileName}.
     *
     * <p>If the file extension is not recognized, {@link #OCTET_STREAM} is
     * returned.
     *
     * @param fileName the file name
     */
    static String mimeType(String fileName) {
        var extension = fileName.substring(fileName.lastIndexOf(".") + 1)
                .toLowerCase(ROOT);
        var type = MIME_TYPES.getOrDefault(extension, OCTET_STREAM);
        return type.toString();
    }

    private static Properties createMap() {
        var properties = new Properties();
        var url = MimeTypes.class.getClassLoader().getResource(MIME_TYPES_FILE);
        if (url != null) {
            try (var inputStream = url.openStream()) {
                properties.load(inputStream);
            } catch (IOException ignore) {
                warn("Couldn't read the list of mime-types");
            }
        }
        return properties;
    }
}
