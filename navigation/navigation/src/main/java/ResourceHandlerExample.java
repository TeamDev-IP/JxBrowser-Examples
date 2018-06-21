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

import com.teamdev.jxbrowser.chromium.*;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;

import javax.swing.*;
import java.awt.*;

/**
 * This example demonstrates how to handle all resources such as
 * HTML, PNG, JavaScript, CSS files and decide whether web browser
 * engine should load them from web server or not. For example, in
 * this sample we cancel loading of all Images.
 */
public class ResourceHandlerExample {
    public static void main(String[] args) {
        Browser browser = new Browser();
        BrowserView browserView = new BrowserView(browser);

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(browserView, BorderLayout.CENTER);
        frame.setSize(700, 500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        NetworkService networkService = browser.getContext().getNetworkService();
        networkService.setResourceHandler(new ResourceHandler() {
            @Override
            public boolean canLoadResource(ResourceParams params) {
                System.out.println("URL: " + params.getURL());
                System.out.println("Type: " + params.getResourceType());
                boolean isNotImageType =
                        params.getResourceType() != ResourceType.IMAGE;
                if (isNotImageType) {
                    return true;    // Cancel loading of all images
                }
                return false;
            }
        });

        browser.loadURL("http://www.google.com");
    }
}