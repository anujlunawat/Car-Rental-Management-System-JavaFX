package com.carrental.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.function.Consumer;

public class SceneUtil {
    /**
     * Switches to a new scene with optional controller initialization
     * @param <T> The type of the controller
     * @param fxmlPath Path to the FXML file
     * @param title Window title
     * @param currentNode Any node from the current scene
     * @param initializer Optional consumer to initialize the controller
     */
    public static <T> void switchScene(String fxmlPath, String title, Node currentNode, Consumer<T> initializer) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneUtil.class.getResource(fxmlPath));
            Parent root = loader.load();
    
            // Run controller-specific setup if provided
            T controller = loader.getController();
            if (initializer != null) {
                initializer.accept(controller);
            }
    
            Scene newScene = new Scene(root, 1280, 720);
    
            // ✅ Make the root resizable with the scene
            if (root instanceof Region) {
                ((Region) root).prefWidthProperty().bind(newScene.widthProperty());
                ((Region) root).prefHeightProperty().bind(newScene.heightProperty());
            }
    
            Stage stage = (Stage) currentNode.getScene().getWindow();
            stage.setScene(newScene);
            stage.setTitle(title);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

    /**
     * Switches to a new scene without controller initialization
     * @param fxmlPath Path to the FXML file
     * @param title Window title
     * @param currentNode Any node from the current scene
     */
    public static void switchScene(String fxmlPath, String title, Node currentNode) {
        switchScene(fxmlPath, title, currentNode, null);
    }
} 