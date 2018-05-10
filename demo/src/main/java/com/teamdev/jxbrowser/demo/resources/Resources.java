/*
 * Copyright (c) 2000-2017 TeamDev Ltd. All rights reserved.
 * TeamDev PROPRIETARY and CONFIDENTIAL.
 * Use is subject to license terms.
 */

package com.teamdev.jxbrowser.demo.resources;

import com.google.common.base.Charsets;

import javax.swing.ImageIcon;
import java.io.IOException;
import java.net.URL;

/**
 * Utility class for accessing program resources.
 */
public class Resources {

    /** Prevents instantiation of this  utility class. */
    private Resources() {
    }

    /**
     * Loads an {@code ImageIcon} from the resource file.
     *
     * <p>Assumes that all icons are stored under the {@code icon/} sub-directory.
     */
    public static ImageIcon loadIcon(String fileName) {
        URL resource = Resources.class.getResource("icon/" + fileName);
        return new ImageIcon(resource);
    }

    /**
     * Loads a text file from resources.
     *
     * <p>The file must have UTF-8 encoding.
     */
    public static String load(String fileName) {
        URL url = Resources.class.getResource(fileName);
        String result;
        try {
            result = com.google.common.io.Resources.toString(url, Charsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load resource " + fileName, e);
        }
        return result;
    }
}
