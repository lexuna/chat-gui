package de.lexuna.school.chat.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            primaryStage.setMaximized(true);
            Parent root = FXMLLoader.load(getClass().getResource("MainView.fxml"));
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static void sendMessage(String text) {
        // TODO Auto-generated method stub
        System.out.println(text);
    }
}
