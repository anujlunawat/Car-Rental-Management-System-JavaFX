package com.carrental.controller;

import com.carrental.model.User;
import com.carrental.util.DatabaseUtil;
import com.carrental.util.SessionManager;
import dev.morphia.Datastore;
import dev.morphia.query.filters.Filters;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController {
    @FXML
    private TextField emailField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Label messageLabel;
    
    private Datastore datastore;
    
    public LoginController() {
        this.datastore = DatabaseUtil.getDatastore();
    }
    
    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        messageLabel.setVisible(false);
        
        // Add key event handler for enter key
        passwordField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleLogin();
            }
        });
    }
    
    @FXML
    private void handleLogin() {
        String username = emailField.getText().trim();
        String password = passwordField.getText();
        
        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password.");
            return;
        }
        
        try {
            User user = datastore.find(User.class)
                    .filter(Filters.eq("username", username))
                    .first();
            
            if (user == null || !BCrypt.checkpw(password, user.getPassword())) {
                showError("Invalid username or password.");
                return;
            }
            
            // Set the current user in the session manager
            SessionManager.getInstance().setCurrentUser(user);
            
            // Determine which dashboard to load based on user role
            String fxmlPath;
            String dashboardTitle;
            
            if (user.getRole() == User.UserRole.ADMIN) {
                fxmlPath = "/fxml/admin_dashboard.fxml";
                dashboardTitle = "Admin Dashboard";
            } else {
                // Handle both USER and CUSTOMER roles
                fxmlPath = "/fxml/customer_dashboard.fxml";
                dashboardTitle = "Customer Dashboard";
            }
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            
            // Set the authenticated user in the controller
            if (user.getRole() != User.UserRole.ADMIN) {
                CustomerDashboardController controller = loader.getController();
                controller.setCurrentUser(user);
            }
            
            // Stage stage = (Stage) emailField.getScene().getWindow();
            // stage.setScene(new Scene(root));
            // stage.setTitle("Car Rental System - " + dashboardTitle);
            // stage.setWidth(1280);
            // stage.setHeight(720);
            // stage.centerOnScreen();
            // stage.show();

            Stage stage = (Stage) emailField.getScene().getWindow();
            // double currentWidth = stage.getWidth();
            // double currentHeight = stage.getHeight();
            
            Scene newScene = new Scene(root, 1280, 720);
            stage.setScene(newScene);
            stage.setTitle("Car Rental System - " + dashboardTitle);
            // stage.centerOnScreen(); // Optional: use only if you want it centered again
            stage.show();

        } catch (Exception e) {
            showError("Error during login: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/register.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Car Rental System - Register");
            stage.setWidth(1280);
            stage.setHeight(720);
            stage.setX(0);
            stage.setY(0);
            stage.show();
        } catch (IOException e) {
            showError("Error loading registration page: " + e.getMessage());
        }
    }
    
    private void showError(String message) {
        messageLabel.setText(message);
        messageLabel.setVisible(true);
    }
} 