/*
 *  Copyright 2018, TeamDev. All rights reserved.
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

package com.teamdev.jxbrowser.demo;

import javax.annotation.Nullable;
import javax.swing.*;

import static com.teamdev.jxbrowser.demo.resources.Resources.loadIcon;
import static javax.swing.BorderFactory.createEmptyBorder;

/**
 * A button with an image icon and associated {@code Action}.
 */
final public class ActionButton extends JButton {

    /**
     * Creates a new button and associates it with the passed action.
     *
     * <p>The button is created with an image icon and rollover image
     * icon loaded from resource images corresponding to the hint
     * of the button.
     */
    public ActionButton(String hint, @Nullable Action action) {
        super(action);
        setContentAreaFilled(false);
        setBorder(createEmptyBorder());
        setBorderPainted(false);
        setRolloverEnabled(true);
        setToolTipText(hint);
        setText(null);
        setFocusable(false);
        setDefaultCapable(false);

        String imageName = hint.toLowerCase();
        setIcon(loadIcon(imageName + ".png"));
        setRolloverIcon(loadIcon(imageName + "-selected.png"));
    }
}
