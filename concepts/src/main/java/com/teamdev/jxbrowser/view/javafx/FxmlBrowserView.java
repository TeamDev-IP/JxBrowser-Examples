package com.teamdev.jxbrowser.view.javafx;

import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.engine.EngineOptions;
import com.teamdev.jxbrowser.engine.RenderingMode;
import javafx.scene.layout.StackPane;

/**
 * A wrapper component for JavaFX {@link BrowserView} that allows using
 * the BrowserView instance in FXML applications. The JavaFX BrowserView
 * cannot be used in FXML directly, because it does not provide the default
 * public constructor.
 */
public final class FxmlBrowserView extends StackPane {

    private final BrowserView view;

    /**
     * Constructs an instance of {@code FxmlBrowserView}.
     */
    public FxmlBrowserView() {
        Engine engine = Engine.newInstance(
                EngineOptions.newBuilder(RenderingMode.HARDWARE_ACCELERATED)
                        .build());
        view = BrowserView.newInstance(engine.newBrowser());
        getChildren().add(view);
    }

    /**
     * Returns the {@link Browser} instance of the current browser view.
     */
    public Browser browser() {
        return view.getBrowser();
    }
}
