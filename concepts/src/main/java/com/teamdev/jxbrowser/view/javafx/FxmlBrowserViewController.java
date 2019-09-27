package com.teamdev.jxbrowser.view.javafx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Represents FXML controller with the address bar and browser view area that
 * displays the URL entered in the address bar.
 */
public final class FxmlBrowserViewController implements Initializable {

    @FXML
    private TextField textField;

    @FXML
    private FxmlBrowserView browserView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        browserView.browser().navigation().loadUrl(textField.getText());
    }

    public void loadUrl(ActionEvent actionEvent) {
        browserView.browser().navigation().loadUrl(textField.getText());
    }
}
