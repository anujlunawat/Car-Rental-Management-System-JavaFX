package com.carrental.controller;

import com.carrental.model.User;
import com.carrental.model.Booking;
import com.carrental.util.SessionManager;
import com.carrental.util.DatabaseUtil;
import dev.morphia.Datastore;
import dev.morphia.query.filters.Filters;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import com.carrental.util.SceneUtil;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class ProfileController {
    @FXML private Text nameText;
    @FXML private Text emailText;
    @FXML private Text phoneText;
    @FXML private Text addressText;
    @FXML private Text memberSinceText;
    @FXML private Text totalBookingsText;
    @FXML private Text activeBookingsText;
    @FXML private Text totalSpentText;
    @FXML private ListView<String> activityListView;
    @FXML private Label messageLabel;
    
    // Edit Profile Fields
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextArea addressField;
    
    private User currentUser;
    private Datastore datastore;
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
        loadUserProfile();
    }
    
    @FXML
    public void initialize() {
        currentUser = SessionManager.getInstance().getCurrentUser();
        datastore = DatabaseUtil.getDatastore();
        
        // Set up ListView cell factory
        activityListView.setCellFactory(listView -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle(null);
                } else {
                    setText(item);
                    setStyle("-fx-padding: 10px; -fx-background-color: #f8f9fa; -fx-background-radius: 5px; -fx-border-radius: 5px; -fx-border-color: #e0e0e0; -fx-border-width: 1px; -fx-margin: 5px;");
                }
            }
        });
        
        loadUserProfile();
    }
    
    private void loadUserProfile() {
        if (currentUser != null) {
            nameText.setText(currentUser.getFullName());
            emailText.setText(currentUser.getEmail());
            phoneText.setText(currentUser.getPhone() != null ? currentUser.getPhone() : "Not provided");
            addressText.setText(currentUser.getAddress() != null ? currentUser.getAddress() : "Not provided");
            // Use current date as join date since it's not stored in the model
            memberSinceText.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM d, yyyy")));
            
            // Load statistics
            loadUserStatistics();
            // Load recent activity
            loadRecentActivity();
        }
    }
    
    private void loadUserStatistics() {
        if (currentUser != null) {
            try {
                // Get all bookings for the user
                List<Booking> userBookings = datastore.find(Booking.class)
                    .filter(Filters.eq("user", currentUser))
                    .find()
                    .toList();
                
                // Calculate statistics
                int totalBookings = userBookings.size();
                int activeBookings = (int) userBookings.stream()
                    .filter(booking -> booking.getStatus() == Booking.BookingStatus.CONFIRMED)
                    .count();
                
                double totalSpent = userBookings.stream()
                    .mapToDouble(Booking::getTotalPrice)
                    .sum();
                
                // Update UI
                totalBookingsText.setText(String.valueOf(totalBookings));
                activeBookingsText.setText(String.valueOf(activeBookings));
                totalSpentText.setText("₹" + String.format("%.2f", totalSpent));
            } catch (Exception e) {
                showMessage("Error loading statistics: " + e.getMessage(), true);
                // Set default values in case of error
                totalBookingsText.setText("0");
                activeBookingsText.setText("0");
                totalSpentText.setText("₹0");
            }
        }
    }
    
    private void loadRecentActivity() {
        if (currentUser != null) {
            try {
                // Get recent bookings
                List<Booking> recentBookings = datastore.find(Booking.class)
                    .filter(Filters.eq("user", currentUser))
                    .find()
                    .toList();
                
                // Sort bookings by start date (most recent first)
                recentBookings.sort((b1, b2) -> {
                    String date1 = b1.getStartDateStr();
                    String date2 = b2.getStartDateStr();
                    if (date1 == null && date2 == null) return 0;
                    if (date1 == null) return 1;
                    if (date2 == null) return -1;
                    return date2.compareTo(date1);
                });
                
                // Limit to 10 most recent bookings
                if (recentBookings.size() > 10) {
                    recentBookings = recentBookings.subList(0, 10);
                }
                
                // Format bookings as activity items
                List<String> activities = recentBookings.stream()
                    .map(booking -> {
                        String carName = booking.getCar().getBrand() + " " + booking.getCar().getModel();
                        String status = booking.getStatus().toString();
                        String startDate = booking.getStartDateStr();
                        String endDate = booking.getEndDateStr();
                        return String.format("Booked %s\nStatus: %s\nFrom: %s to %s", 
                            carName, status, startDate, endDate);
                    })
                    .collect(Collectors.toList());
                
                // Add to list view
                activityListView.getItems().clear();
                if (activities.isEmpty()) {
                    activityListView.getItems().add("No recent activity");
                } else {
                    activityListView.getItems().addAll(activities);
                }
            } catch (Exception e) {
                showMessage("Error loading recent activity: " + e.getMessage(), true);
                activityListView.getItems().clear();
                activityListView.getItems().add("Error loading activity");
            }
        }
    }
    
    @FXML
    private void handleEditProfile() {
        SceneUtil.switchScene("/fxml/edit_profile.fxml", "Edit Profile", nameText);
    }
    
    private void setEditFields(User user) {
        nameField.setText(user.getFullName());
        emailField.setText(user.getEmail());
        phoneField.setText(user.getPhone() != null ? user.getPhone() : "");
        addressField.setText(user.getAddress() != null ? user.getAddress() : "");
    }
    
    @FXML
    private void handleSaveProfile() {
        try {
            // Validate inputs
            if (nameField.getText().isEmpty() || emailField.getText().isEmpty()) {
                showMessage("Name and email are required fields", true);
                return;
            }
            
            // Update user in MongoDB using Datastore
            currentUser.setFullName(nameField.getText());
            currentUser.setEmail(emailField.getText());
            currentUser.setPhone(phoneField.getText());
            currentUser.setAddress(addressField.getText());
            
            datastore.save(currentUser);
            
            showMessage("Profile updated successfully!", false);
            handleBackToProfile();
        } catch (Exception e) {
            showMessage("Error updating profile: " + e.getMessage(), true);
        }
    }
    
    @FXML
    private void handleBackToProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/profile.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) nameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Car Rental System - Profile");
            stage.show();
        } catch (IOException e) {
            showMessage("Error returning to profile page: " + e.getMessage(), true);
        }
    }
    
    @FXML
    private void handleBackToBrowseCars() {
        SceneUtil.switchScene("/fxml/browse_cars.fxml", "Browse Cars", nameText);
    }
    
    private void showMessage(String message, boolean isError) {
        messageLabel.setText(message);
        messageLabel.setStyle(isError ? "-fx-text-fill: #e74c3c;" : "-fx-text-fill: #27ae60;");
        messageLabel.setVisible(true);
        
        // Auto-hide message after 3 seconds
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                javafx.application.Platform.runLater(() -> messageLabel.setVisible(false));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
} 