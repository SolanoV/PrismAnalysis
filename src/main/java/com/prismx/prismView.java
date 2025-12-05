package com.prismx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class prismView extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("prismx.fxml"));
        Scene scene = new Scene(root);
        stage.setResizable(false);
        stage.setTitle("Prism");
        stage.getIcons().add(new Image("file:src/main/resources/com/images/logo.png"));
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}