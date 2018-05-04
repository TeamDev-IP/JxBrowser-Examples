/*
 * Copyright (c) 2000-2017 TeamDev Ltd. All rights reserved.
 * TeamDev PROPRIETARY and CONFIDENTIAL.
 * Use is subject to license terms.
 */

package com.teamdev.jxbrowser.demo.resources;

import javax.swing.ImageIcon;
import java.net.URL;

/**
 * Utility class for accessing program resources.
 */
public class Resources {

    /** Prevents instantiation of this  utility class. */
    private Resources() {
    }

    /**
     * Loads an {@code ImageIcon} from a resource file.
     */
    public static ImageIcon getIcon(String fileName) {
        URL resource = Resources.class.getResource(fileName);
        return new ImageIcon(resource);
    }
}
