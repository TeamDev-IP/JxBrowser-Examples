import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * This example demonstrates how to use JavaFX BrowserView in FXML app
 * through the {@link com.teamdev.jxbrowser.view.javafx.FxmlBrowserView} control.
 */
public final class BrowserViewInFxml extends Application {

    public static void main(String[] args) {
        Application.launch(BrowserViewInFxml.class, args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderPane pane = FXMLLoader.load(
                BrowserViewInFxml.class.getResource("browser-view.fxml"));

        primaryStage.setTitle("JavaFX BrowserView in FXML");
        primaryStage.setScene(new Scene(pane, 1024, 600));
        primaryStage.show();
    }
}
