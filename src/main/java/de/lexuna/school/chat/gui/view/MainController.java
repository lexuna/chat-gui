package de.lexuna.school.chat.gui.view;

import java.net.URL;
import java.util.ResourceBundle;

import de.lexuna.school.chat.gui.Main;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class MainController implements Initializable {

    @FXML
    private HBox header;
    @FXML
    private VBox contacts;
    @FXML
    private HBox chatHeader;
    @FXML
    private TextArea textArea;
    @FXML
    private VBox chatBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @FXML
    private void onKey(KeyEvent event) {
        String text = this.textArea.getText();
        if ("\n".equals(text) || text.isEmpty() || text == null) {
            this.textArea.clear();
            return;
        }
        if (event.getCode() == KeyCode.ENTER && !event.isShiftDown()) {
            sendMessage(text);
            this.textArea.clear();
        }
        if (event.getCode() == KeyCode.ENTER && event.isShiftDown()) {
            this.textArea.setText(text + "\n");
            this.textArea.end();

        }
    }

    private void addMessage(String text) {
        TextField field = new TextField(text);
        this.textArea.setWrapText(true);
//        field.setPrefHeight(50);
//        field.setMinWidth(10);
//        field.setPrefWidth(text.length() * 7);
//        field.setMaxWidth(400);
        VBox.setMargin(field, new Insets(10));
        field.getStyleClass().add("userMessage");
        field.setEditable(false);
        this.chatBox.getChildren().add(field);
    }

    @FXML
    private void sendClicked(MouseEvent event) {
        String text = this.textArea.getText();
        if ("\n".equals(text) || text.isEmpty() || text == null) {
            this.textArea.clear();
            return;
        }
        sendMessage(text);
    }

    private void sendMessage(String text) {
        addMessage(text);
        Main.sendMessage(text);
    }
}
