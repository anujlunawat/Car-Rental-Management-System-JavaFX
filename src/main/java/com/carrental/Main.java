package com.carrental;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Screen;
import com.carrental.util.DatabaseUtil;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // Initialize database connection
        DatabaseUtil.getDatastore();
        
        // Load the login screen
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/fxml/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        
        // Apply CSS
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        
        // Set up stage
        stage.setTitle("Car Rental System - Login");
        
        // Set window size
        stage.setWidth(1280);
        stage.setHeight(720);
        stage.centerOnScreen();
        stage.setScene(scene);
        
        // Show stage
        stage.show();
    }

    @Override
    public void stop() {
        // Close database connection when application stops
        DatabaseUtil.closeConnection();
    }

    public static void main(String[] args) {
        launch();
    }
} 