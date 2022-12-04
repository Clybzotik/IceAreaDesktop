package com.hoffmann.icearenadesktop;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class IceArenaApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(IceArenaApplication.class.getResource("icearena-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 600);
        scene.getStylesheets().add("file:src/main/resources/com/hoffmann/icearenadesktop/style.css");
        stage.setTitle("Ice Area Admin Panel");
        stage.getIcons().add(new Image("file:src/main/resources/com/hoffmann/icearenadesktop/iceicon.png"));
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(windowEvent -> {
            System.exit(0);
        });
    }

    public static void main(String[] args) {
        try {
            new IceArenaDBManager().getConnection().isValid(1);
        } catch (SQLException e) {
            System.out.println("DB connection failed.");
        }
        launch();
    }
}