package de.lexuna.school.chat.gui;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import de.lexuna.school.chat.dto.Contacts;
import de.lexuna.school.chat.dto.Login;
import de.lexuna.school.chat.dto.Message;
import de.lexuna.school.chat.gui.view.MainController;
import de.lexuna.school.chat.io.StreamDeserializer;
import de.lexuna.school.chat.io.StreamSerealizer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private static final Map<String, byte[]> CONTACTS = new HashMap<>();

    public static Login LOGIN;

    private static MainController controller;
    private static Socket socket;
    private static StreamSerealizer serealizer;
    private static StreamDeserializer deserealizer;
    private static KeyPair KEY_PAIR;

    @Override
    public void start(Stage primaryStage) {
        try {
//            Parent root = FXMLLoader.load(getClass().getResource("MainView.fxml"));

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("MainView.fxml"));
            Parent root = (Parent) loader.load();

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();

            controller = loader.getController();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(1024);
            KEY_PAIR = kpg.genKeyPair();
            LOGIN = new Login(args[2], KEY_PAIR.getPublic().getEncoded());
            socket = new Socket(args[0], Integer.parseInt(args[1]));
            serealizer = new StreamSerealizer(socket.getOutputStream());
            deserealizer = new StreamDeserializer(socket.getInputStream());
            System.out.println("Open connection: " + args[0] + ":" + Integer.parseInt(args[1]));
            login();
            new Thread(() -> {
                while (true) {
                    try {
                        readMessage(deserealizer.read());
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (NumberFormatException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        launch(args);
    }

    private static void readMessage(Object obj) {

        if (obj instanceof Message) {
            try {
                Message message = (Message) obj;
                Cipher cipher = Cipher.getInstance("RSA");
                cipher.init(Cipher.DECRYPT_MODE, KEY_PAIR.getPrivate());
                byte[] byteMessage = cipher.doFinal(message.getMessage());

                Platform.runLater(() -> {
                    controller.addMessage(new String(byteMessage, StandardCharsets.UTF_8), message.getSenderId(),
                            false);
                });
            } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException
                    | NoSuchPaddingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else if (obj instanceof Contacts) {
            System.out.println("Get Contacts");
            CONTACTS.putAll(((Contacts) obj).getContacts());
            Platform.runLater(() -> {
                controller.addContacts(CONTACTS.keySet());
            });
        }

    }

    private static void login() {
        try {
            serealizer.send(LOGIN);
            System.out.println("Send login");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void sendMessage(String text, String receverId) {
        // TODO Auto-generated method stub
        try {
            Message message = createMessage(text, receverId);
            serealizer.send(message);
        } catch (IOException e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    private static Message createMessage(String text, String receverId) {
        // TODO Auto-generated method stub
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE,
                    KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(CONTACTS.get(receverId))));
            byte[] byteMessage = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
            return new Message(byteMessage, System.currentTimeMillis(), LOGIN.getUserId(), receverId);
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException
                | NoSuchPaddingException | InvalidKeySpecException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
