package com.carrental.controller;

import com.carrental.model.User;
import com.carrental.util.SessionManager;
import com.carrental.util.DatabaseUtil;
import dev.morphia.Datastore;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import com.carrental.util.SceneUtil;

import java.io.IOException;

public class EditProfileController {
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextArea addressField;
    @FXML private Label messageLabel;
    
    private User currentUser;
    private Datastore datastore;
    
    @FXML
    public void initialize() {
        currentUser = SessionManager.getInstance().getCurrentUser();
        datastore = DatabaseUtil.getDatastore();
        
        if (currentUser != null) {
            nameField.setText(currentUser.getFullName());
            emailField.setText(currentUser.getEmail());
            phoneField.setText(currentUser.getPhone() != null ? currentUser.getPhone() : "");
            addressField.setText(currentUser.getAddress() != null ? currentUser.getAddress() : "");
        }
    }
    
    @FXML
    private void handleSave() {
        try {
            // Validate inputs
            if (nameField.getText().isEmpty() || emailField.getText().isEmpty()) {
                showMessage("Name and email are required fields", true);
                return;
            }
            
            // Update user
            currentUser.setFullName(nameField.getText());
            currentUser.setEmail(emailField.getText());
            currentUser.setPhone(phoneField.getText());
            currentUser.setAddress(addressField.getText());
            
            // Save to database
            datastore.save(currentUser);
            
            // Update session
            SessionManager.getInstance().setCurrentUser(currentUser);
            
            showMessage("Profile updated successfully!", false);
            
            // Close edit window and refresh profile
            closeAndRefreshProfile();
        } catch (Exception e) {
            showMessage("Error updating profile: " + e.getMessage(), true);
        }
    }
    
    @FXML
    private void handleCancel() {
        closeAndRefreshProfile();
    }
    
    private void closeAndRefreshProfile() {
        SceneUtil.switchScene("/fxml/profile.fxml", "Profile", nameField);
    }
    
    private void showMessage(String message, boolean isError) {
        messageLabel.setText(message);
        messageLabel.setStyle(isError ? "-fx-text-fill: #e74c3c;" : "-fx-text-fill: #27ae60;");
        messageLabel.setVisible(true);
        
        if (!isError) {
            // Auto-hide success message after 2 seconds
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    javafx.application.Platform.runLater(() -> messageLabel.setVisible(false));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
} 
