package com.carrental.model;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Property;
import org.bson.types.ObjectId;

@Entity("cars")
public class Car {
    @Id
    private ObjectId id;
    
    @Property("model")
    private String model;
    
    @Property("brand")
    private String brand;
    
    @Property("pricePerDay")
    private double pricePerDay;
    
    @Property("availabilityStatus")
    private AvailabilityStatus availabilityStatus;
    
    @Property("carType")
    private CarType carType;
    
    @Property("year")
    private int year;
    
    @Property("licensePlate")
    private String licensePlate;
    
    @Property("imagePath")
    private String imagePath;
    
    @Property("available")
    private boolean available;

    @Property("status")
    private CarStatus status;

    public enum AvailabilityStatus {
        AVAILABLE,
        RENTED,
        MAINTENANCE
    }

    public enum CarType {
        SUV,
        SEDAN,
        SPORTS,
        LUXURY,
        COMPACT,
        VAN
    }

    public enum CarStatus {
        AVAILABLE, RENTED, MAINTENANCE
    }

    // Default constructor
    public Car() {
        this.status = CarStatus.AVAILABLE;
    }

    // Constructor with fields
    public Car(String model, String brand, double pricePerDay, CarType carType, int year, String licensePlate, String imagePath) {
        this.model = model;
        this.brand = brand;
        this.pricePerDay = pricePerDay;
        this.availabilityStatus = AvailabilityStatus.AVAILABLE;
        this.carType = carType;
        this.year = year;
        this.licensePlate = licensePlate;
        this.imagePath = imagePath;
        this.available = true;
        this.status = CarStatus.AVAILABLE;
    }

    // Getters and Setters
    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public double getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(double pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public AvailabilityStatus getAvailabilityStatus() {
        return availabilityStatus;
    }

    public void setAvailabilityStatus(AvailabilityStatus availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }

    public CarType getCarType() {
        return carType;
    }

    public void setCarType(CarType carType) {
        this.carType = carType;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public CarStatus getStatus() {
        return status;
    }

    public void setStatus(CarStatus status) {
        this.status = status;
    }
} 