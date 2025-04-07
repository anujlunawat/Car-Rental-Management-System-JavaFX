package com.carrental.controller;

import com.carrental.model.Booking;
import com.carrental.model.Car;
import com.carrental.model.User;
import com.carrental.util.DatabaseUtil;
import com.carrental.util.WindowUtil;
import dev.morphia.Datastore;
import dev.morphia.query.filters.Filters;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AdminDashboardController {
    @FXML
    private Label totalCarsLabel;
    
    @FXML
    private Label activeBookingsLabel;
    
    @FXML
    private Label totalUsersLabel;
    
    @FXML
    private Label totalRevenueLabel;
    
    @FXML
    private TableView<Booking> recentBookingsTable;
    
    @FXML
    private TableColumn<Booking, String> bookingIdColumn;
    
    @FXML
    private TableColumn<Booking, String> customerColumn;
    
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
    
    private Datastore datastore;
    private ObservableList<Booking> bookingsList;
    
    @FXML
    public void initialize() {
        datastore = DatabaseUtil.getDatastore();
        setupTableColumns();
        loadDashboardData();
    }
    
    private void setupTableColumns() {
        bookingIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        customerColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getUser().getUsername()));
        carColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCar().getModel()));
        startDateColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStartDateStr()));
        endDateColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEndDateStr()));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        totalCostColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(String.format("₹%.2f", cellData.getValue().getTotalPrice())));
    }
    
    private void loadDashboardData() {
        // Load total cars
        long totalCars = datastore.find(Car.class).count();
        totalCarsLabel.setText(String.valueOf(totalCars));
        
        // Load active bookings
        long activeBookings = datastore.find(Booking.class)
                .filter(Filters.eq("status", Booking.BookingStatus.CONFIRMED))
                .count();
        activeBookingsLabel.setText(String.valueOf(activeBookings));
        
        // Load total users
        long totalUsers = datastore.find(User.class).count();
        totalUsersLabel.setText(String.valueOf(totalUsers));
        
        // Calculate total revenue
        List<Booking> completedBookings = datastore.find(Booking.class)
                .filter(Filters.eq("status", Booking.BookingStatus.COMPLETED))
                .find()
                .toList();
        double totalRevenue = completedBookings.stream()
                .mapToDouble(Booking::getTotalPrice)
                .sum();
        totalRevenueLabel.setText(String.format("₹%.2f", totalRevenue));
        
        // Load recent bookings
        loadRecentBookings();
    }
    
    private void loadRecentBookings() {
        Datastore datastore = DatabaseUtil.getDatastore();
        List<Booking> recentBookings = datastore.find(Booking.class)
                .filter(Filters.exists("startDate"))
                .iterator()
                .toList();
        
        recentBookings.sort((b1, b2) -> b2.getStartDate().compareTo(b1.getStartDate()));
        recentBookingsTable.setItems(FXCollections.observableArrayList(recentBookings.subList(0, Math.min(5, recentBookings.size()))));
    }
    
    @FXML
    private void handleManageCars() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/manage_cars.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) totalCarsLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Car Rental System - Manage Cars");
            stage.setWidth(1280);
            stage.setHeight(720);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleViewBookings() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/view_bookings.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) totalCarsLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Car Rental System - View Bookings");
            stage.setWidth(1280);
            stage.setHeight(720);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleReports() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/reports.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) totalCarsLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Car Rental System - Reports");
            stage.setWidth(1280);
            stage.setHeight(720);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleManageUsers() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/manage_users.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) totalCarsLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Car Rental System - Manage Users");
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
            
            Stage stage = (Stage) totalCarsLabel.getScene().getWindow();
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
    
    @FXML
    private void handleAddCar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/add_car.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            
            Stage stage = (Stage) totalCarsLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Car Rental System - Add Car");
            stage.setWidth(1280);
            stage.setHeight(720);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 