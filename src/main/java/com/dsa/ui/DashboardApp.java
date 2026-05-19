package com.dsa.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DashboardApp extends Application {

    @Override
    public void start(Stage stage) {
        DashboardController controller = new DashboardController();

        Scene scene = new Scene(controller.getRoot(), 1280, 820);
        scene.getStylesheets().add(
                getClass().getResource("/styles/dashboard.css").toExternalForm());

        stage.setTitle("Disaster Social Media Analytics System");
        stage.setMinWidth(1000);
        stage.setMinHeight(700);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
