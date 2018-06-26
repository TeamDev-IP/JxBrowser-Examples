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

import static javax.swing.JOptionPane.OK_CANCEL_OPTION;
import static javax.swing.JOptionPane.OK_OPTION;
import static javax.swing.JOptionPane.showConfirmDialog;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.CloseStatus;
import com.teamdev.jxbrowser.chromium.UnloadDialogParams;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;
import com.teamdev.jxbrowser.chromium.swing.DefaultDialogHandler;
import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
 * The example demonstrates how to catch the onbeforeunload dialog.
 */
public class BeforeUnload {

    private static String BEFORE_UNLOAD_DIALOG_TITLE = "Confirm Navigation";

    public static void main(String[] args) {
        Browser browser = new Browser();
        final BrowserView view = new BrowserView(browser);

        JFrame frame = new JFrame("JxBrowser Example – BeforeUnload Dialog");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(view, BorderLayout.CENTER);
        frame.setSize(700, 500);
        frame.setVisible(true);

        browser.setDialogHandler(new DefaultDialogHandler(view) {
            @Override
            public CloseStatus onBeforeUnload(UnloadDialogParams params) {
                int returnValue = showConfirmDialog(
                        view, params.getMessage(), BEFORE_UNLOAD_DIALOG_TITLE, OK_CANCEL_OPTION);
                if (returnValue == OK_OPTION) {
                    return CloseStatus.OK;
                } else {
                    return CloseStatus.CANCEL;
                }
            }
        });
        browser.loadHTML("<html><body onbeforeunload='return myFunction()'>" +
                "<a href='http://www.google.com'>Click here to leave</a>" +
                "<script>function myFunction() { return 'Leave this web page?'; }" +
                "</script></body></html>");
    }
}