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

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;
import com.teamdev.jxbrowser.chromium.swing.DefaultDialogHandler;
import com.teamdev.jxbrowser.chromium.swing.DefaultDownloadHandler;
import com.teamdev.jxbrowser.chromium.swing.DefaultPopupHandler;

final class TabFactory {

    static Tab createFirstTab() {
        return createTab("https://www.teamdev.com/jxbrowser");
    }

    static Tab createTab() {
        return createTab(Tab.DEFAULT_URL);
    }

    private static Tab createTab(String url) {
        Browser browser = new Browser();
        BrowserView browserView = new BrowserView(browser);
        TabContent tabContent = new TabContent(browserView);

        browser.setDownloadHandler(new DefaultDownloadHandler(browserView));
        browser.setDialogHandler(new DefaultDialogHandler(browserView));
        browser.setPopupHandler(new DefaultPopupHandler());

        final TabCaption tabCaption = new TabCaption();
        tabCaption.setTitle(Tab.DEFAULT_URL);

        tabContent.addPropertyChangeListener(
                Tab.Event.PAGE_TITLE_CHANGED,
            evt -> tabCaption.setTitle((String) evt.getNewValue())
        );

        browser.loadURL(url);
        return new Tab(tabCaption, tabContent);
    }
}
