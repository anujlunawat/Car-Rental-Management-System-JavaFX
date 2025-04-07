package com.carrental.util;

import javafx.stage.Stage;

public class WindowUtil {
    public static void setWindowSize(Stage stage) {
        // Set a fixed size that works well for most displays
        stage.setWidth(1280);
        stage.setHeight(720);
        
        // Center the window on the screen
        stage.centerOnScreen();
    }
} 