package com.carrental.controller;

import com.carrental.model.Booking;
import com.carrental.model.Car;
import com.carrental.model.User;
import com.carrental.util.DatabaseUtil;
import com.carrental.util.SessionManager;
import dev.morphia.Datastore;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;

public class BookCarController {
    @FXML
    private ImageView carImage;
    
    @FXML
    private Text carModelText;
    
    @FXML
    private Text carYearText;
    
    @FXML
    private Text carTypeText;
    
    @FXML
    private Text carPriceText;
    
    @FXML
    private DatePicker startDatePicker;
    
    @FXML
    private DatePicker endDatePicker;
    
    @FXML
    private ComboBox<String> startTimeComboBox;
    
    @FXML
    private ComboBox<String> endTimeComboBox;
    
    @FXML
    private Text daysText;
    
    @FXML
    private Text pricePerDayText;
    
    @FXML
    private Text totalPriceText;
    
    @FXML
    private Label messageLabel;
    
    private Datastore datastore;
    private Car selectedCar;
    private User currentUser;
    private double totalPrice = 0;
    
    @FXML
    public void initialize() {
        datastore = DatabaseUtil.getDatastore();
        setupTimeComboBoxes();
        setupDatePickers();
        messageLabel.setVisible(false);
        currentUser = SessionManager.getInstance().getCurrentUser();
    }
    
    public void setSelectedCar(Car car) {
        this.selectedCar = car;
        displayCarDetails();
    }
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
    
    private void displayCarDetails() {
        if (selectedCar != null) {
            carModelText.setText(selectedCar.getBrand() + " " + selectedCar.getModel());
            carYearText.setText(String.valueOf(selectedCar.getYear()));
            carTypeText.setText(selectedCar.getCarType().toString());
            carPriceText.setText("₹" + String.format("%.2f", selectedCar.getPricePerDay()));
            pricePerDayText.setText("₹" + String.format("%.2f", selectedCar.getPricePerDay()));
            
            // Load car image if available
            if (selectedCar.getImagePath() != null && !selectedCar.getImagePath().isEmpty()) {
                try {
                    Image image = new Image(selectedCar.getImagePath());
                    carImage.setImage(image);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    private void setupTimeComboBoxes() {
        String[] times = {
            "00:00", "01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00",
            "08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00",
            "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00"
        };
        
        startTimeComboBox.getItems().addAll(times);
        endTimeComboBox.getItems().addAll(times);
        
        // Set default values
        startTimeComboBox.setValue("09:00");
        endTimeComboBox.setValue("17:00");
        
        // Add listeners to update total price
        startTimeComboBox.setOnAction(e -> updateTotalPrice());
        endTimeComboBox.setOnAction(e -> updateTotalPrice());
    }
    
    private void setupDatePickers() {
        // Set minimum date to today
        startDatePicker.setValue(LocalDate.now());
        endDatePicker.setValue(LocalDate.now().plusDays(1));
        
        // Add listeners to update total price
        startDatePicker.setOnAction(e -> updateTotalPrice());
        endDatePicker.setOnAction(e -> updateTotalPrice());
    }
    
    private void updateTotalPrice() {
        if (startDatePicker.getValue() != null && endDatePicker.getValue() != null) {
            long days = ChronoUnit.DAYS.between(startDatePicker.getValue(), endDatePicker.getValue());
            if (days < 0) days = 0;
            daysText.setText(String.valueOf(days));
            
            totalPrice = days * selectedCar.getPricePerDay();
            totalPriceText.setText("₹" + String.format("%.2f", totalPrice));
        }
    }
    
    private boolean validateInputs() {
        if (startDatePicker.getValue() == null || endDatePicker.getValue() == null) {
            showMessage("Please select both start and end dates.", true);
            return false;
        }
        
        if (startTimeComboBox.getValue() == null || endTimeComboBox.getValue() == null) {
            showMessage("Please select both start and end times.", true);
            return false;
        }
        
        if (startDatePicker.getValue().isAfter(endDatePicker.getValue())) {
            showMessage("End date must be after start date.", true);
            return false;
        }
        
        if (startDatePicker.getValue().equals(endDatePicker.getValue()) &&
            startTimeComboBox.getValue().compareTo(endTimeComboBox.getValue()) >= 0) {
            showMessage("End time must be after start time.", true);
            return false;
        }
        
        return true;
    }
    
    @FXML
    private void handleConfirmBooking() {
        try {
            // Get current user from session manager
            currentUser = SessionManager.getInstance().getCurrentUser();
            
            // Validate inputs
            if (startDatePicker.getValue() == null || endDatePicker.getValue() == null) {
                showMessage("Please select both start and end dates", true);
                return;
            }
            
            if (startTimeComboBox.getValue() == null || endTimeComboBox.getValue() == null) {
                showMessage("Please select both start and end times", true);
                return;
            }
            
            if (selectedCar == null) {
                showMessage("No car selected", true);
                return;
            }
            
            if (currentUser == null) {
                showMessage("User not logged in", true);
                return;
            }
            
            // Validate dates
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();
            
            if (endDate.isBefore(startDate)) {
                showMessage("End date cannot be before start date", true);
                return;
            }
            
            // Create a new booking
            Booking booking = new Booking();
            
            // Generate a random booking ID
            booking.setId(new ObjectId());
            
            // Set the car
            booking.setCar(selectedCar);
            
            // Set the current user
            booking.setUser(currentUser);
            
            // Set the status to CONFIRMED
            booking.setStatus(Booking.BookingStatus.CONFIRMED);
            
            // Format dates and times as strings
            String startDateTimeStr = startDate.format(DateTimeFormatter.ISO_DATE) + " " + startTimeComboBox.getValue();
            String endDateTimeStr = endDate.format(DateTimeFormatter.ISO_DATE) + " " + endTimeComboBox.getValue();
            
            // Set the dates and times as strings
            booking.setStartDateStr(startDateTimeStr);
            booking.setEndDateStr(endDateTimeStr);
            
            // Set the total price
            booking.setTotalPrice(totalPrice);
            
            // Save the booking to MongoDB first
            try {
                datastore.save(booking);
                System.out.println("Booking saved successfully to MongoDB");
            } catch (Exception e) {
                System.err.println("Error saving booking to MongoDB: " + e.getMessage());
                e.printStackTrace();
                showMessage("Error saving booking to database: " + e.getMessage(), true);
                return;
            }

            // Create payment document
            Document paymentDoc = new Document()
                .append("_id", new ObjectId())
                .append("bookingId", booking.getId().toString())
                .append("amount", totalPrice)
                .append("paymentMethod", "OTS")
                .append("transactionId", "TXN" + System.currentTimeMillis() + (int)(Math.random() * 1000))
                .append("paymentDate", new Date())
                .append("status", "COMPLETED");

            // Save payment to MongoDB using the same connection
            try {
                com.mongodb.client.MongoCollection<Document> paymentsCollection = 
                    datastore.getDatabase().getCollection("payments");
                paymentsCollection.insertOne(paymentDoc);
                System.out.println("Payment saved successfully to MongoDB");
                System.out.println("Payment details: " + paymentDoc.toJson());
            } catch (Exception e) {
                System.err.println("Error saving payment to MongoDB: " + e.getMessage());
                e.printStackTrace();
                showMessage("Error saving payment to database: " + e.getMessage(), true);
                return;
            }
            
            // Show success message
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("🎉 Booking Confirmed!");
            alert.setHeaderText("Your booking has been successfully confirmed!");
            
            // Create a styled content
            StringBuilder content = new StringBuilder();
            content.append("✨ Car Details:\n");
            content.append("   • Brand: ").append(selectedCar.getBrand()).append("\n");
            content.append("   • Model: ").append(selectedCar.getModel()).append("\n");
            content.append("   • Year: ").append(selectedCar.getYear()).append("\n");
            content.append("   • Type: ").append(selectedCar.getCarType()).append("\n\n");
            content.append("📅 Booking Dates:\n");
            content.append("   • Start: ").append(startDateTimeStr).append("\n");
            content.append("   • End: ").append(endDateTimeStr).append("\n\n");
            content.append("💰 Total Price: ₹").append(String.format("%.2f", totalPrice)).append("\n\n");
            content.append("Payment Method: OTS (On The Spot)\n");
            content.append("Transaction ID: ").append(paymentDoc.getString("transactionId")).append("\n\n");
            content.append("Thank you for choosing our service! 🚗");
            
            alert.setContentText(content.toString());
            
            // Add custom styling
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.setStyle(
                "-fx-background-color: #ffffff;" +
                "-fx-border-color: #4CAF50;" +
                "-fx-border-width: 2px;" +
                "-fx-border-radius: 5px;" +
                "-fx-background-radius: 5px;" +
                "-fx-padding: 20px;"
            );
            
            // Style the header
            if (dialogPane.lookup(".header-panel") != null) {
                dialogPane.lookup(".header-panel").setStyle(
                    "-fx-background-color: #4CAF50;" +
                    "-fx-text-fill: white;"
                );
            }
            
            // Style the content
            if (dialogPane.lookup(".content") != null) {
                dialogPane.lookup(".content").setStyle(
                    "-fx-font-size: 14px;" +
                    "-fx-text-fill: #333333;" +
                    "-fx-padding: 10px;"
                );
            }
            
            // Style the buttons
            if (dialogPane.lookup(".button") != null) {
                dialogPane.lookup(".button").setStyle(
                    "-fx-background-color: #4CAF50;" +
                    "-fx-text-fill: white;" +
                    "-fx-font-weight: bold;" +
                    "-fx-padding: 8px 16px;" +
                    "-fx-background-radius: 4px;"
                );
            }
            
            alert.showAndWait();
            
            // Go back to browse cars
            handleBrowseCars();
            
        } catch (Exception e) {
            e.printStackTrace();
            showMessage("Error creating booking: " + e.getMessage(), true);
        }
    }
    
    private void showMessage(String message, boolean isError) {
        messageLabel.setText(message);
        messageLabel.setVisible(true);
        messageLabel.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 10px;" +
            "-fx-background-radius: 5px;" +
            "-fx-background-color: " + (isError ? "#FFEBEE" : "#E8F5E9") + ";" +
            "-fx-text-fill: " + (isError ? "#D32F2F" : "#2E7D32") + ";" +
            "-fx-border-color: " + (isError ? "#FFCDD2" : "#C8E6C9") + ";" +
            "-fx-border-width: 1px;" +
            "-fx-border-radius: 5px;"
        );
    }
    
    @FXML
    private void handleBrowseCars() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/browse_cars.fxml"));
            Parent root = loader.load();
    
            BrowseCarsController controller = loader.getController();
            controller.setCurrentUser(currentUser);
    
            Scene scene = new Scene(root);
            Stage stage = (Stage) carImage.getScene().getWindow();
            stage.setTitle("Car Rental System - Browse Cars");
            stage.setWidth(1280);
            stage.setHeight(720);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }    
} 