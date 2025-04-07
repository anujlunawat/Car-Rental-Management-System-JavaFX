package com.carrental.controller;

import com.carrental.model.Car;
import com.carrental.util.DatabaseUtil;
import com.carrental.util.ImageUtil;
import com.carrental.util.WindowUtil;
import dev.morphia.Datastore;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class AddCarController {
    @FXML private TextField modelField;
    @FXML private TextField brandField;
    @FXML private TextField priceField;
    @FXML private ComboBox<Car.CarType> carTypeComboBox;
    @FXML private TextField yearField;
    @FXML private TextField licensePlateField;
    @FXML private ImageView carImageView;
    @FXML private Label selectedImageLabel;
    @FXML private Label errorLabel;
    
    private File selectedImageFile;
    private Datastore datastore;
    
    @FXML
    public void initialize() {
        datastore = DatabaseUtil.getDatastore();
        carTypeComboBox.getItems().addAll(Car.CarType.values());
    }
    
    @FXML
    private void handleChooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Car Image");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        
        File file = fileChooser.showOpenDialog(carImageView.getScene().getWindow());
        if (file != null) {
            selectedImageFile = file;
            selectedImageLabel.setText(file.getName());
            
            // Display the image preview
            Image image = new Image(file.toURI().toString());
            carImageView.setImage(image);
        }
    }
    
    @FXML
    private void handleAddCar() {
        try {
            // Validate inputs
            if (modelField.getText().isEmpty() || brandField.getText().isEmpty() || 
                priceField.getText().isEmpty() || carTypeComboBox.getValue() == null || 
                yearField.getText().isEmpty() || licensePlateField.getText().isEmpty()) {
                errorLabel.setText("Please fill in all fields");
                return;
            }
            
            if (selectedImageFile == null) {
                errorLabel.setText("Please select a car image");
                return;
            }
            
            // Parse inputs
            double price = Double.parseDouble(priceField.getText());
            int year = Integer.parseInt(yearField.getText());
            
            // Create images directory if it doesn't exist
            Path imagesDir = Paths.get("src/main/resources/images/cars");
            Files.createDirectories(imagesDir);
            
            // Generate unique filename for the image
            String imageFileName = System.currentTimeMillis() + "_" + selectedImageFile.getName();
            Path targetPath = imagesDir.resolve(imageFileName);
            
            // Copy and process the image
            Files.copy(selectedImageFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            File processedImageFile = targetPath.toFile();
            ImageUtil.processCarImageFile(processedImageFile, processedImageFile);
            
            // Create and save car
            Car car = new Car(
                modelField.getText(),
                brandField.getText(),
                price,
                carTypeComboBox.getValue(),
                year,
                licensePlateField.getText(),
                "/images/cars/" + imageFileName
            );
            
            datastore.save(car);
            
            // Return to admin dashboard
            handleCancel();
            
        } catch (NumberFormatException e) {
            errorLabel.setText("Please enter valid numbers for price and year");
        } catch (IOException e) {
            errorLabel.setText("Error processing image: " + e.getMessage());
        } catch (Exception e) {
            errorLabel.setText("Error adding car: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleCancel() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin_dashboard.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            
            Stage stage = (Stage) modelField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Car Rental System - Admin Dashboard");
            stage.setWidth(1280);
            stage.setHeight(720);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            errorLabel.setText("Error returning to dashboard: " + e.getMessage());
        }
    }
} 