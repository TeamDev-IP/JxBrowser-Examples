/*
 *  Copyright 2020, TeamDev. All rights reserved.
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

package javafx;

import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.browser.callback.OpenPopupCallback;
import com.teamdev.jxbrowser.browser.event.BrowserClosed;
import com.teamdev.jxbrowser.browser.event.TitleChanged;
import com.teamdev.jxbrowser.browser.event.UpdateBoundsRequested;
import com.teamdev.jxbrowser.os.Environment;
import com.teamdev.jxbrowser.ui.Rect;
import com.teamdev.jxbrowser.view.javafx.BrowserView;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * The default {@link OpenPopupCallback} implementation for the Swing UI toolkit that creates and shows a new window
 * with the embedded popup browser.
 */
public final class DefaultOpenPopupCallback implements OpenPopupCallback {

    private static final int DEFAULT_POPUP_WIDTH = 800;
    private static final int DEFAULT_POPUP_HEIGHT = 600;

    @Override
    public Response on(Params params) {
        Browser browser = params.popupBrowser();
        Platform.runLater(() -> {
            BrowserView view = BrowserView.newInstance(browser);
            Stage stage = new Stage();
            StackPane root = new StackPane();
            Scene scene = new Scene(root);
            root.getChildren().add(view);
            stage.setScene(scene);

            updateBounds(stage, params.initialBounds());

            stage.setOnCloseRequest(event -> {
                if (Environment.isWindows()) {
                    new Thread(browser::close).start();
                } else {
                    browser.close();
                }
            });
            browser.on(TitleChanged.class, event ->
                    Platform.runLater(() -> stage.setTitle(event.title())));
            browser.on(BrowserClosed.class, event ->
                    Platform.runLater(stage::close));
            browser.on(UpdateBoundsRequested.class, event ->
                    Platform.runLater(() -> updateBounds(stage, event.bounds())));
            stage.show();
        });
        return Response.proceed();
    }

    private static void updateBounds(Stage stage, Rect bounds) {
        if (bounds.size().isEmpty()) {
            stage.setWidth(DEFAULT_POPUP_WIDTH);
            stage.setHeight(DEFAULT_POPUP_HEIGHT);
        } else {
            stage.setX(bounds.origin().x());
            stage.setY(bounds.origin().y());
            stage.setWidth(bounds.size().width());
            stage.setHeight(bounds.size().height());
        }
    }
}
