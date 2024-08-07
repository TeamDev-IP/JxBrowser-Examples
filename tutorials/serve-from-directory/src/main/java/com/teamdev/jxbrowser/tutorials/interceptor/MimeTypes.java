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

package com.teamdev.jxbrowser.tutorials.interceptor;

import com.google.common.collect.ImmutableMap;
import com.teamdev.jxbrowser.internal.Lazy;
import com.teamdev.jxbrowser.net.MimeType;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import static com.teamdev.jxbrowser.logging.Logger.warn;
import static java.lang.String.format;

/**
 * A utility for working with MIME types.
 */
public final class MimeTypes {

    private static final MimeType OCTET_STREAM = MimeType.of("application/octet-stream");
    private static final Lazy<Map<String, MimeType>> extToMime = new Lazy<>(MimeTypes::createMap);

    /**
     * Prevents instantiation of this utility class.
     */
    private MimeTypes() {
    }

    /**
     * Derives {@code MimeType} from the extension of the {@code file}.
     *
     * <p>If the file extension is not recognized, {@link #OCTET_STREAM} is returned.
     *
     * @param file the path to the file
     */
    public static MimeType mimeType(Path file) {
        String fileName = file.getFileName().toString();
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
        return extToMime.get().getOrDefault(extension.toLowerCase(Locale.ENGLISH), OCTET_STREAM);
    }

    private static Map<String, MimeType> createMap() {
        ImmutableMap.Builder<String, MimeType> mapBuilder = ImmutableMap.builder();
        Properties properties = new Properties();
        URL propertiesUrl = MimeTypes.class.getClassLoader().getResource("ext-to-mime.properties");
        if (propertiesUrl != null) {
            try (InputStream inputStream = propertiesUrl.openStream()) {
                properties.load(inputStream);
                properties.forEach((key, value) -> mapBuilder.put(key.toString(),
                        MimeType.of(value.toString())));
            } catch (IOException ignore) {
                warn(format("Couldn't read the list of mime-types from: %s", propertiesUrl));
            }
        }
        return mapBuilder.build();
    }
}
