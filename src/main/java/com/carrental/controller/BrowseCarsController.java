package com.carrental.controller;

import com.carrental.model.Car;
import com.carrental.model.User;
import com.carrental.util.DatabaseUtil;
import com.carrental.util.SessionManager;
import dev.morphia.Datastore;
import dev.morphia.query.filters.Filters;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class BrowseCarsController {
    @FXML
    private TextField searchField;
    
    @FXML
    private ComboBox<String> brandFilter;
    
    @FXML
    private ComboBox<String> typeFilter;
    
    @FXML
    private ComboBox<String> priceFilter;
    
    @FXML
    private GridPane carsGrid;
    
    private Datastore datastore;
    private List<Car> allCars;
    private User currentUser;
    
    @FXML
    public void initialize() {
        datastore = DatabaseUtil.getDatastore();
        loadCars();
        setupFilters();
        currentUser = SessionManager.getInstance().getCurrentUser();
        displayUserDetails();
    }
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
    
    private void loadCars() {
        allCars = datastore.find(Car.class).find().toList();
        displayCars(allCars);
    }
    
    private void setupFilters() {
        // Setup brand filter
        List<String> brands = allCars.stream()
                .map(Car::getBrand)
                .distinct()
                .collect(Collectors.toList());
        brandFilter.getItems().addAll(brands);
        
        // Setup type filter
        List<String> types = allCars.stream()
                .map(car -> car.getCarType().toString())
                .distinct()
                .collect(Collectors.toList());
        typeFilter.getItems().addAll(types);
        
        // Setup price filter
        priceFilter.getItems().addAll("Under ₹50", "₹50 - ₹100", "₹100 - ₹200", "Over ₹200");
    }
    
    private void displayCars(List<Car> cars) {
        carsGrid.getChildren().clear();
        
        if (cars.isEmpty()) {
            // Display empty state
            Text emptyText = new Text("No cars found matching your criteria");
            emptyText.setStyle("-fx-font-size: 16px; -fx-fill: #7f8c8d;");
            carsGrid.add(emptyText, 0, 0);
            return;
        }
        
        int column = 0;
        int row = 0;
        int maxColumns = 3; // Display 3 cars per row
        
        for (Car car : cars) {
            VBox carCard = createCarCard(car);
            carsGrid.add(carCard, column, row);
            
            column++;
            if (column >= maxColumns) {
                column = 0;
                row++;
            }
        }
    }
    
    private VBox createCarCard(Car car) {
        VBox card = new VBox(10);
        card.getStyleClass().add("dashboard-card");
        card.setPadding(new Insets(15));
        card.setPrefWidth(300);
        
        // Add light red background for unavailable cars
        if (!car.isAvailable()) {
            card.setStyle("-fx-background-color: #ffebee;"); // Light red background
        }
        
        // Car image or placeholder
        ImageView imageView = new ImageView();
        imageView.setFitWidth(270);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);
        
        // Try to load the image with better error handling
        if (car.getImagePath() != null && !car.getImagePath().isEmpty()) {
            try {
                // Try to load the image directly
                Image image = new Image(car.getImagePath());
                imageView.setImage(image);
            } catch (Exception e) {
                // If direct loading fails, try to load from classpath
                try {
                    String classpathImagePath = car.getImagePath().startsWith("/") ? 
                        car.getImagePath().substring(1) : car.getImagePath();
                    Image image = new Image(getClass().getResourceAsStream("/" + classpathImagePath));
                    imageView.setImage(image);
                } catch (Exception ex) {
                    // If both attempts fail, show placeholder
                    showPlaceholder(card);
                }
            }
        } else {
            // Show placeholder for no image path
            showPlaceholder(card);
        }
        
        // Add the image view to the card
        card.getChildren().add(imageView);
        
        // Car details
        Text modelText = new Text(car.getBrand() + " " + car.getModel());
        modelText.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        Text yearText = new Text("Year: " + car.getYear());
        Text typeText = new Text("Type: " + car.getCarType());
        Text priceText = new Text("₹" + car.getPricePerDay() + " per day");
        
        card.getChildren().addAll(modelText, yearText, typeText, priceText);
        
        // Book button
        Button bookButton = new Button("Book Now");
        bookButton.getStyleClass().add("button-primary");
        bookButton.setOnAction(e -> handleBookCar(car));
        
        card.getChildren().add(bookButton);
        
        return card;
    }
    
    private void showPlaceholder(VBox card) {
        Text placeholderText = new Text("No Image Available");
        placeholderText.setStyle("-fx-font-size: 16px; -fx-fill: #7f8c8d;");
        VBox placeholder = new VBox(placeholderText);
        placeholder.setAlignment(javafx.geometry.Pos.CENTER);
        placeholder.setPrefHeight(150);
        placeholder.setStyle("-fx-background-color: #f0f0f0;");
        card.getChildren().add(placeholder);
    }
    
    @FXML
    private void handleApplyFilters() {
        String searchText = searchField.getText().toLowerCase();
        String selectedBrand = brandFilter.getValue();
        String selectedType = typeFilter.getValue();
        String selectedPrice = priceFilter.getValue();
        
        List<Car> filteredCars = allCars.stream()
                .filter(car -> 
                    (searchText.isEmpty() || 
                     car.getBrand().toLowerCase().contains(searchText) || 
                     car.getModel().toLowerCase().contains(searchText)) &&
                    (selectedBrand == null || car.getBrand().equals(selectedBrand)) &&
                    (selectedType == null || car.getCarType().toString().equals(selectedType)) &&
                    (selectedPrice == null || matchesPriceRange(car.getPricePerDay(), selectedPrice)))
                .collect(Collectors.toList());
        
        displayCars(filteredCars);
    }
    
    private boolean matchesPriceRange(double price, String range) {
        switch (range) {
            case "Under ₹50":
                return price < 50;
            case "₹50 - ₹100":
                return price >= 50 && price <= 100;
            case "₹100 - ₹200":
                return price > 100 && price <= 200;
            case "Over ₹200":
                return price > 200;
            default:
                return true;
        }
    }
    
    @FXML
    private void handleClearFilters() {
        searchField.clear();
        brandFilter.setValue(null);
        typeFilter.setValue(null);
        priceFilter.setValue(null);
        displayCars(allCars);
    }
    
    private void handleBookCar(Car car) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/book_car.fxml"));
            Parent root = loader.load();
            
            BookCarController controller = loader.getController();
            controller.setSelectedCar(car);
            
            Stage stage = (Stage) carsGrid.getScene().getWindow();
            stage.setTitle("Car Rental System - Book Car");
            stage.setWidth(1280);
            stage.setHeight(720);
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleMyBookings() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/my_bookings.fxml"));
            Parent root = loader.load();
            
            MyBookingsController controller = loader.getController();
            controller.setCurrentUser(currentUser);
            
            // Stage stage = (Stage) carsGrid.getScene().getWindow();
            // stage.setScene(new Scene(root));
            // stage.setTitle("Car Rental System - My Bookings");
            // stage.setWidth(1280);
            // stage.setHeight(720);
            // stage.centerOnScreen();
            // stage.show();

            Scene newScene = new Scene(root, 1280, 720); // define size at scene level
            Stage stage = (Stage) carsGrid.getScene().getWindow();
            stage.setScene(newScene);
            stage.sizeToScene();
            stage.setTitle("Car Rental System - My Bookings");
            stage.centerOnScreen(); // now center with known size
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
            
            Stage stage = (Stage) carsGrid.getScene().getWindow();
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
            
            Stage stage = (Stage) carsGrid.getScene().getWindow();
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
    
    private void displayUserDetails() {
        if (currentUser != null) {
            System.out.println("Current User Details:");
            System.out.println("Name: " + currentUser.getFullName());
            System.out.println("Email: " + currentUser.getEmail());
            System.out.println("Phone: " + currentUser.getPhone());
        } else {
            System.out.println("No user is currently logged in.");
        }
    }
} 