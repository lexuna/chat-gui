package de.lexuna.school.chat.gui.view;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;

import de.lexuna.school.chat.gui.Main;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
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
    @FXML
    private Label userName;
    @FXML
    private VBox contactBox;
    @FXML
    private Label contactName;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userName.setText(Main.LOGIN.getUserId());
        contactName.setText("");
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

    public void addMessage(String text, String senderId, boolean userMassage) {

        if (senderId != null && !contactName.getText().equals(senderId)) {
            for (Node node : contactBox.getChildren()) {
                Label child = (Label) node;
                if (child.getText().equals(senderId)) {
                    child.getOnMouseClicked().handle(null);
                    break;
                }
            }
        }
        TextField field = new TextField(text);
        this.textArea.setWrapText(true);
        VBox.setMargin(field, new Insets(10));
        field.getStyleClass().add(userMassage ? "userMessage" : "message");
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
        addMessage(text, null, true);
        Main.sendMessage(text, contactName.getText());
    }

    public void addContacts(Set<String> contacts) {
        contactBox.getChildren().clear();

        for (String userId : contacts) {
            if (Main.LOGIN.getUserId().equals(userId)) {
                continue;
            }
            if (contactName.getText().isEmpty()) {
                contactName.setText(userId);
            }
            Label contact = new Label(userId);
            contact.getStyleClass().add("contact");
            contact.setOnMouseClicked(event -> {
                this.chatBox.getChildren().clear();
                contactName.setText(contact.getText());
            });
            contactBox.getChildren().add(contact);
        }
    }
}
