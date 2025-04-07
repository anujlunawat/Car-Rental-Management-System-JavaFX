package com.carrental.controller;

import com.carrental.model.Booking;
import com.carrental.model.User;
import com.carrental.util.DatabaseUtil;
import com.carrental.util.WindowUtil;
import dev.morphia.Datastore;
import dev.morphia.query.filters.Filters;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.bson.Document;
import javafx.application.Platform;

public class MyBookingsController {
    @FXML
    private ComboBox<String> statusFilter;
    
    @FXML
    private TableView<Booking> bookingsTable;
    
    @FXML
    private TableColumn<Booking, String> bookingIdColumn;
    
    @FXML
    private TableColumn<Booking, String> carColumn;
    
    @FXML
    private TableColumn<Booking, String> startDateColumn;
    
    @FXML
    private TableColumn<Booking, String> endDateColumn;
    
    @FXML
    private TableColumn<Booking, String> statusColumn;
    
    @FXML
    private TableColumn<Booking, String> totalCostColumn;
    
    @FXML
    private TableColumn<Booking, Void> actionsColumn;
    
    @FXML
    private VBox emptyStateContainer;
    
    private Datastore datastore;
    private ObservableList<Booking> bookingsList;
    private User currentUser;
    
    @FXML
    public void initialize() {
        datastore = DatabaseUtil.getDatastore();
        setupTableColumns();
        setupStatusFilter();
    }
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
        // Load bookings immediately after setting user
        Platform.runLater(() -> {
            loadBookings();
            // Force the table to refresh
            bookingsTable.refresh();
        });
    }
    
    private void setupTableColumns() {
        bookingIdColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getId().toString()));
        
        carColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getCar().getBrand() + " " + cellData.getValue().getCar().getModel()));
        
        startDateColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStartDateStr()));
        
        endDateColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEndDateStr()));
        
        statusColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatus().toString()));
        
        statusColumn.setCellFactory(column -> new TableCell<Booking, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    switch (Booking.BookingStatus.valueOf(status)) {
                        case CONFIRMED:
                            setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                            break;
                        case CANCELLED:
                            setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                            break;
                        case COMPLETED:
                            setStyle("-fx-text-fill: #3498db; -fx-font-weight: bold;");
                            break;
                        default:
                            setStyle("-fx-text-fill: #2c3e50; -fx-font-weight: bold;");
                    }
                }
            }
        });
        
        totalCostColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(String.format("₹%.2f", cellData.getValue().getTotalPrice())));
        
        // Setup actions column with styled buttons
        actionsColumn.setCellFactory(column -> new TableCell<Booking, Void>() {
            private final Button cancelButton = new Button("Cancel");
            private final Button viewButton = new Button("View");
            
            {
                // Style the buttons
                cancelButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 5 10;");
                viewButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 5 10;");
                
                cancelButton.setOnAction(event -> {
                    Booking booking = getTableView().getItems().get(getIndex());
                    handleCancelBooking(booking);
                });
                
                viewButton.setOnAction(event -> {
                    Booking booking = getTableView().getItems().get(getIndex());
                    handleViewBooking(booking);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Booking booking = getTableView().getItems().get(getIndex());
                    HBox buttons = new HBox(5);
                    buttons.setAlignment(Pos.CENTER);
                    
                    if (booking.getStatus() == Booking.BookingStatus.PENDING || 
                        booking.getStatus() == Booking.BookingStatus.CONFIRMED) {
                        buttons.getChildren().add(cancelButton);
                    }
                    buttons.getChildren().add(viewButton);
                    setGraphic(buttons);
                }
            }
        });
    }
    
    private void setupStatusFilter() {
        statusFilter.getItems().addAll("All", "Confirmed", "Completed", "Cancelled");
        statusFilter.setValue("All");
        
        statusFilter.setOnAction(e -> filterBookings());
    }
    
    private void loadBookings() {
        if (currentUser != null) {
            try {
                // Fetch all bookings for the current user
                List<Booking> bookings = datastore.find(Booking.class)
                        .filter(Filters.eq("user", currentUser))
                        .find()
                        .toList();

                System.out.println("Found " + bookings.size() + " bookings for user: " + currentUser.getEmail());

                // Convert to observable list
                bookingsList = FXCollections.observableArrayList(bookings);
                
                // Sort bookings by start date (most recent first), handling null dates
                bookingsList.sort((b1, b2) -> {
                    // Handle null cases
                    if (b1.getStartDate() == null && b2.getStartDate() == null) {
                        return 0;
                    }
                    if (b1.getStartDate() == null) {
                        return 1; // Null dates go to the end
                    }
                    if (b2.getStartDate() == null) {
                        return -1; // Null dates go to the end
                    }
                    // Both dates are non-null, compare normally
                    return b2.getStartDate().compareTo(b1.getStartDate());
                });
                
                // Set the items in the table
                bookingsTable.setItems(bookingsList);
                
                // Show/hide empty state
                emptyStateContainer.setVisible(bookings.isEmpty());
                bookingsTable.setVisible(!bookings.isEmpty());
                
                // Force the table to refresh
                bookingsTable.refresh();
                
            } catch (Exception e) {
                System.err.println("Error loading bookings: " + e.getMessage());
                e.printStackTrace();
                showError("Error loading bookings. Please try again later.");
            }
        } else {
            System.out.println("Current user is null");
            showError("User not logged in. Please login again.");
        }
    }
    
    private void filterBookings() {
        String selectedStatus = statusFilter.getValue();
        
        if ("All".equals(selectedStatus)) {
            bookingsTable.setItems(bookingsList);
        } else {
            ObservableList<Booking> filteredList = bookingsList.filtered(booking -> 
                booking.getStatus().toString().equals(selectedStatus.toUpperCase()));
            bookingsTable.setItems(filteredList);
        }
        
        // Show empty state if no bookings
        emptyStateContainer.setVisible(bookingsTable.getItems().isEmpty());
    }
    
    @FXML
    private void handleClearFilters() {
        statusFilter.setValue("All");
        filterBookings();
    }
    
    private void handleCancelBooking(Booking booking) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cancel Booking");
        alert.setHeaderText("Are you sure you want to cancel this booking?");
        alert.setContentText("This action cannot be undone.");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Update booking status
                booking.setStatus(Booking.BookingStatus.CANCELLED);
                
                try {
                    // Save to database
                    datastore.save(booking);
                    
                    // Update the table immediately
                    Platform.runLater(() -> {
                        // Refresh the current booking in the table
                        int index = bookingsList.indexOf(booking);
                        if (index >= 0) {
                            bookingsList.set(index, booking);
                        }
                        
                        // Force table refresh
                        bookingsTable.refresh();
                        
                        // Show success message
                        showSuccess("Booking cancelled successfully!");
                    });
                } catch (Exception e) {
                    System.err.println("Error cancelling booking: " + e.getMessage());
                    e.printStackTrace();
                    showError("Failed to cancel booking. Please try again.");
                }
            }
        });
    }
    
    private void handleViewBooking(Booking booking) {
        // Create a custom styled dialog
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Booking Details");
        dialog.setHeaderText("Booking #" + booking.getId());
        
        // Create a styled content
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
        
        // Car Details Section
        VBox carSection = new VBox(5);
        carSection.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 15; -fx-background-radius: 5;");
        Label carHeader = new Label("🚗 Car Details");
        carHeader.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        Label carInfo = new Label(String.format("%s %s (%d)\nType: %s\nPrice per day: ₹%.2f",
            booking.getCar().getBrand(),
            booking.getCar().getModel(),
            booking.getCar().getYear(),
            booking.getCar().getCarType(),
            booking.getCar().getPricePerDay()));
        carInfo.setStyle("-fx-font-size: 14px;");
        carSection.getChildren().addAll(carHeader, carInfo);
        
        // Booking Dates Section
        // VBox datesSection = new VBox(5);
        // datesSection.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 15; -fx-background-radius: 5;");
        // Label datesHeader = new Label("📅 Booking Dates");
        // datesHeader.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        // String startDateStr = booking.getStartDate() != null ? 
        //     booking.getStartDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")) : "Not set";
        // String endDateStr = booking.getEndDate() != null ? 
        //     booking.getEndDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")) : "Not set";
            
        // Label datesInfo = new Label(String.format("Start: %s\nEnd: %s", startDateStr, endDateStr));
        // datesInfo.setStyle("-fx-font-size: 14px;");
        // datesSection.getChildren().addAll(datesHeader, datesInfo);
        
        // Payment Details Section
        VBox paymentSection = new VBox(5);
        paymentSection.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 15; -fx-background-radius: 5;");
        Label paymentHeader = new Label("💰 Payment Details");
        paymentHeader.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        // Get payment details from MongoDB
        Document paymentDoc = datastore.getDatabase()
            .getCollection("payments")
            .find(new Document("bookingId", booking.getId().toString()))
            .first();
            
        String paymentInfo = String.format("Total Amount: ₹%.2f\n", booking.getTotalPrice());
        if (paymentDoc != null) {
            String paymentDateStr = paymentDoc.getDate("paymentDate") != null ?
                paymentDoc.getDate("paymentDate").toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")) : "Not set";
                    
            paymentInfo += String.format("Transaction ID: %s\nPayment Method: %s\nPayment Date: %s",
                paymentDoc.getString("transactionId"),
                paymentDoc.getString("paymentMethod"),
                paymentDateStr);
        }
        
        Label paymentDetails = new Label(paymentInfo);
        paymentDetails.setStyle("-fx-font-size: 14px;");
        paymentSection.getChildren().addAll(paymentHeader, paymentDetails);
        
        // Status Section
        VBox statusSection = new VBox(5);
        statusSection.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 15; -fx-background-radius: 5;");
        Label statusHeader = new Label("📊 Booking Status");
        statusHeader.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        Label statusInfo = new Label(booking.getStatus().toString());
        statusInfo.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        switch (booking.getStatus()) {
            case CONFIRMED:
                statusInfo.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                break;
            case CANCELLED:
                statusInfo.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                break;
            case COMPLETED:
                statusInfo.setStyle("-fx-text-fill: #3498db; -fx-font-weight: bold;");
                break;
        }
        statusSection.getChildren().addAll(statusHeader, statusInfo);
        
        content.getChildren().addAll(carSection, paymentSection, statusSection);
        
        // Set the dialog content
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        
        // Style the dialog
        dialog.getDialogPane().setStyle(
            "-fx-background-color: white; " +
            "-fx-border-color: #3498db; " +
            "-fx-border-width: 2px; " +
            "-fx-border-radius: 10px; " +
            "-fx-background-radius: 10px;"
        );
        
        // Style the OK button
        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setStyle(
            "-fx-background-color: #3498db; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 8 16; " +
            "-fx-background-radius: 5px;"
        );
        
        // Show the dialog
        dialog.showAndWait();
    }
    
    @FXML
    private void handleBrowseCars() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/browse_cars.fxml"));
            Parent root = loader.load();
            
            BrowseCarsController controller = loader.getController();
            controller.setCurrentUser(currentUser);
            
            // Stage stage = (Stage) bookingsTable.getScene().getWindow();
            // stage.setScene(new Scene(root));
            // stage.setTitle("Car Rental System - Browse Cars");
            // stage.setWidth(1280);
            // stage.setHeight(720);
            // stage.sizeToScene();
            // stage.centerOnScreen();
            // stage.show();

            Stage stage = (Stage) bookingsTable.getScene().getWindow();
            Scene scene = new Scene(root, 1280, 720); // set size here
            stage.setScene(scene);
            stage.sizeToScene();
            stage.setTitle("Car Rental System - Browse Cars");
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/profile.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) bookingsTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Car Rental System - Profile");
            stage.setWidth(1280);
            stage.setHeight(720);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) bookingsTable.getScene().getWindow();
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
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 