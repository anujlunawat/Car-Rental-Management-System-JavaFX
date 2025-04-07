package com.carrental.controller;

import com.carrental.model.Booking;
import com.carrental.model.Car;
import com.carrental.model.User;
import com.carrental.util.DatabaseUtil;
import dev.morphia.Datastore;
import dev.morphia.query.filters.Filters;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class CustomerDashboardController {
    @FXML
    private TableView<Car> availableCarsTable;

    @FXML
    private TableColumn<Car, String> modelColumn;

    @FXML
    private TableColumn<Car, String> brandColumn;

    @FXML
    private TableColumn<Car, String> typeColumn;

    @FXML
    private TableColumn<Car, Integer> yearColumn;

    @FXML
    private TableColumn<Car, String> priceColumn;

    @FXML
    private TableColumn<Car, String> statusColumn;

    @FXML
    private TableView<Booking> activeBookingsTable;

    @FXML
    private TableColumn<Booking, String> bookingIdColumn;

    @FXML
    private TableColumn<Booking, String> carColumn;

    @FXML
    private TableColumn<Booking, String> startDateColumn;

    @FXML
    private TableColumn<Booking, String> endDateColumn;

    @FXML
    private TableColumn<Booking, String> totalCostColumn;

    @FXML
    private TableColumn<Booking, String> bookingStatusColumn;

    private Datastore datastore;
    private ObservableList<Car> carsList;
    private ObservableList<Booking> bookingsList;
    private User currentUser;

    @FXML
    public void initialize() {
        datastore = DatabaseUtil.getDatastore();
        setupTableColumns();
        loadDashboardData();
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        loadDashboardData();
    }

    private void setupTableColumns() {
        // Setup available cars table columns
        modelColumn.setCellValueFactory(new PropertyValueFactory<>("model"));
        brandColumn.setCellValueFactory(new PropertyValueFactory<>("brand"));
        typeColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCarType().toString()));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        priceColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(String.format("₹%.2f", cellData.getValue().getPricePerDay())));
        statusColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getAvailabilityStatus().toString()));

        // Setup active bookings table columns
        bookingIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        carColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCar().getModel()));
        startDateColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStartDateStr()));
        endDateColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEndDateStr()));
        totalCostColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(String.format("₹%.2f", cellData.getValue().getTotalPrice())));
        bookingStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void loadDashboardData() {
        // Load available cars
        List<Car> availableCars = datastore.find(Car.class)
                .filter(Filters.eq("availabilityStatus", Car.AvailabilityStatus.AVAILABLE))
                .find()
                .toList();
        carsList = FXCollections.observableArrayList(availableCars);
        availableCarsTable.setItems(carsList);

        // Load active bookings for the current user
        if (currentUser != null) {
            List<Booking> activeBookings = datastore.find(Booking.class)
                    .filter(Filters.and(
                            Filters.eq("user", currentUser),
                            Filters.eq("status", Booking.BookingStatus.CONFIRMED)
                    ))
                    .find()
                    .toList();
            bookingsList = FXCollections.observableArrayList(activeBookings);
            activeBookingsTable.setItems(bookingsList);
        }
    }

    @FXML
private void handleBrowseCars() {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/browse_cars.fxml"));
        Parent root = loader.load();

        BrowseCarsController controller = loader.getController();
        controller.setCurrentUser(currentUser);

        Scene newScene = new Scene(root, 1280, 720);
        Stage stage = (Stage) availableCarsTable.getScene().getWindow();
        stage.setScene(newScene);
        stage.sizeToScene();
        stage.setTitle("Car Rental System - Browse Cars");
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

        Scene newScene = new Scene(root, 1280, 720);
        Stage stage = (Stage) availableCarsTable.getScene().getWindow();
        stage.setScene(newScene);
        stage.sizeToScene();
        stage.setTitle("Car Rental System - My Bookings");
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

        ProfileController controller = loader.getController();
        controller.setCurrentUser(currentUser);

        Scene newScene = new Scene(root, 1280, 720);
        Stage stage = (Stage) availableCarsTable.getScene().getWindow();
        stage.setScene(newScene);
        stage.sizeToScene();
        stage.setTitle("Car Rental System - Profile");
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

        Scene newScene = new Scene(root, 1280, 720);
        Stage stage = (Stage) availableCarsTable.getScene().getWindow();
        stage.setScene(newScene);
        stage.sizeToScene();
        stage.setTitle("Car Rental System - Login");
        stage.centerOnScreen();
        stage.show();
    } catch (IOException e) {
        e.printStackTrace();
    }
    }
}
