/*
 * Copyright (c) 2000-2017 TeamDev Ltd. All rights reserved.
 * TeamDev PROPRIETARY and CONFIDENTIAL.
 * Use is subject to license terms.
 */

package com.teamdev.jxbrowser.chromium.demo.resources;

import javax.swing.ImageIcon;

public class Resources {

    public static ImageIcon getIcon(String fileName) {
        return new ImageIcon(Resources.class.getResource(fileName));
    }
}
