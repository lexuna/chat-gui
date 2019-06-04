package de.lexuna.school.chat.gui;

import java.io.IOException;
import java.net.Socket;

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

    public static final Login LOGIN = new Login("Hannah", "bla");

    private static MainController controller;
    private static Socket socket;
    private static StreamSerealizer serealizer;
    private static StreamDeserializer deserealizer;

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

    public static void main(String[] args) {
        try {
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
            Platform.runLater(() -> {
                controller.addMessage(((Message) obj).getMessage(), false);
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

    public static void sendMessage(String text) {
        // TODO Auto-generated method stub
        try {
            Message message = createMessage(text);
            serealizer.send(message);
        } catch (IOException e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    private static Message createMessage(String text) {
        // TODO Auto-generated method stub
        return new Message(text, System.currentTimeMillis(), LOGIN.getUserId(), "Daniel");
    }
}
