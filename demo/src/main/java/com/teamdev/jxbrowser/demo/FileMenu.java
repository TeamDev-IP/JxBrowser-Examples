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
import com.teamdev.jxbrowser.chromium.SavePageType;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Optional;

/**
 * A menu with file-related operations of the demo.
 *
 * @author unascribed
 * @author Alexander Yevsyukov
 */
final class FileMenu {

    private final BrowserView browserView;
    private final Browser browser;
    private final JMenu menu;

    FileMenu(BrowserView browserView) {
        this.browserView = browserView;
        this.browser = browserView.getBrowser();

        this.menu = new JMenu("File");

        menu.add(createNewTabItem());
        menu.add(createOpenFileItem());
        menu.addSeparator();

        menu.add(createUploadItem());
        menu.add(createDownloadItem());
        menu.addSeparator();

        menu.add(createSavePageAsItem());
        menu.addSeparator();
        menu.add(createPrintItem());
    }

    Component getComponent() {
        return this.menu;
    }

    private Component createNewTabItem() {
        JMenuItem menuItem = new JMenuItem("New Tab");
        //TODO:2018-05-09:alexander.yevsyukov: implement.
        return menuItem;
    }

    private Component createOpenFileItem() {
        JMenuItem menuItem = new JMenuItem("Open File...");
        //TODO:2018-05-09:alexander.yevsyukov: implement.
        return menuItem;
    }

    private Component createSavePageAsItem() {
        JMenuItem menuItem = new JMenuItem("Save Page As...");
        menuItem.addActionListener(e -> {
            Optional<File> fileSelected = selectFile();
            if (fileSelected.isPresent()) {
                File selectedFile = fileSelected.get();
                //TODO:2018-05-09:alexander.yevsyukov: Use the name of the page file and
                // the suffix `-resources`, not just `resources`.
                String pageResourcesDir = new File(selectedFile.getParent(), "resources")
                        .getAbsolutePath();
                browser.saveWebPage(selectedFile.getAbsolutePath(),
                        pageResourcesDir,
                        SavePageType.COMPLETE_HTML);
            }
        });
        return menuItem;
    }

    private JMenuItem createUploadItem() {
        JMenuItem menuItem = new JMenuItem("Upload File");
        menuItem.addActionListener(
                e -> browser.loadURL("http://www.cs.tut.fi/~jkorpela/forms/file.html#example")
        );
        return menuItem;
    }

    private Component createDownloadItem() {
        JMenuItem menuItem = new JMenuItem("Download File");
        menuItem.addActionListener(
                //TODO:2018-05-09:alexander.yevsyukov: BUG. This does NOT download the referenced file.
                e -> browser.loadURL("https://s3.amazonaws.com/cloud.teamdev.com/downloads/demo/jxbrowserdemo.jnlp")
        );
        return menuItem;
    }

    private Component createPrintItem() {
        JMenuItem menuItem = new JMenuItem("Print...");
        menuItem.addActionListener(e -> browser.print());
        return menuItem;
    }

    /**
     * Displays file selection dialog and returns the name of the selected file
     * or empty {@code Optional} if the operation was not approved.
     */
    private Optional<File> selectFile() {
        JFileChooser fileChooser = new JFileChooser();
        //TODO:2018-05-09:alexander.yevsyukov: Get the name of the page file from the browser.
        fileChooser.setSelectedFile(new File("my-web-page.html"));
        int result = fileChooser.showSaveDialog(browserView);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            return Optional.of(selectedFile);
        }
        return Optional.empty();
    }
}
