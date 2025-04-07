package com.carrental.controller;

import com.carrental.model.User;
import com.carrental.util.DatabaseUtil;
import dev.morphia.Datastore;
import dev.morphia.query.filters.Filters;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.util.regex.Pattern;

public class RegisterController {
    @FXML
    private TextField fullNameField;
    
    @FXML
    private TextField emailField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private PasswordField confirmPasswordField;
    
    @FXML
    private TextField phoneField;
    
    @FXML
    private Label messageLabel;
    
    private Datastore datastore;
    
    @FXML
    public void initialize() {
        datastore = DatabaseUtil.getDatastore();
        messageLabel.setVisible(false);
    }
    
    @FXML
    private void handleRegister() {
        // Clear previous error
        messageLabel.setVisible(false);
        
        // Get input values
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String phone = phoneField.getText().trim();
        
        // Validate inputs
        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || 
            confirmPassword.isEmpty() || phone.isEmpty()) {
            showError("Please fill in all fields.");
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match.");
            return;
        }
        
        if (password.length() < 8) {
            showError("Password must be at least 8 characters long.");
            return;
        }
        
        if (!isValidEmail(email)) {
            showError("Please enter a valid email address.");
            return;
        }
        
        if (!isValidPhoneNumber(phone)) {
            showError("Please enter a valid 10-digit phone number.");
            return;
        }
        
        // Check if email already exists
        if (datastore.find(User.class).filter(Filters.eq("email", email)).first() != null) {
            showError("Email already registered.");
            return;
        }
        
        try {
            // Create new user
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            User newUser = new User(email, hashedPassword, fullName, email, phone, "", User.UserRole.USER);
            
            datastore.save(newUser);
            
            // Show success message and navigate to login
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Registration Successful");
            alert.setHeaderText(null);
            alert.setContentText("Your account has been created successfully. Please login to continue.");
            
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    navigateToLogin();
                }
            });
            
        } catch (Exception e) {
            showError("Error creating account: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleLogin() {
        navigateToLogin();
    }
    
    private void navigateToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) fullNameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Car Rental System - Login");
            stage.setWidth(1280);
            stage.setHeight(720);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void showError(String message) {
        messageLabel.setText(message);
        messageLabel.setVisible(true);
    }
    
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }
    
    private boolean isValidPhoneNumber(String phoneNumber) {
        String phoneRegex = "^[0-9]{10}$";
        Pattern pattern = Pattern.compile(phoneRegex);
        return pattern.matcher(phoneNumber).matches();
    }
} 