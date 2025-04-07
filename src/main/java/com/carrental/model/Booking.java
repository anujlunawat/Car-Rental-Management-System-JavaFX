package com.carrental.model;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Property;
import dev.morphia.annotations.Reference;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;

@Entity("bookings")
public class Booking {
    @Id
    private ObjectId id;
    
    @Reference
    private User user;
    
    @Reference
    private Car car;
    
    @Property("startDate")
    private LocalDateTime startDate;
    
    @Property("endDate")
    private LocalDateTime endDate;
    
    @Property("startDateStr")
    private String startDateStr;
    
    @Property("endDateStr")
    private String endDateStr;
    
    @Property("status")
    private BookingStatus status;
    
    @Property("totalPrice")
    private double totalPrice;
    
    @Property("paymentMethod")
    private PaymentMethod paymentMethod;

    public enum BookingStatus {
        PENDING,
        CONFIRMED,
        COMPLETED,
        CANCELLED
    }

    public enum PaymentMethod {
        CREDIT_CARD,
        DEBIT_CARD,
        PAYPAL
    }

    // Default constructor
    public Booking() {}

    // Constructor with fields
    public Booking(User user, Car car, LocalDateTime startDate, LocalDateTime endDate, PaymentMethod paymentMethod) {
        this.user = user;
        this.car = car;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = BookingStatus.PENDING;
        this.paymentMethod = paymentMethod;
        calculateTotalPrice();
    }

    private void calculateTotalPrice() {
        long days = java.time.Duration.between(startDate, endDate).toDays();
        this.totalPrice = days * car.getPricePerDay();
    }

    // Getters and Setters
    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
        calculateTotalPrice();
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
        calculateTotalPrice();
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getStartDateStr() {
        return startDateStr;
    }

    public void setStartDateStr(String startDateStr) {
        this.startDateStr = startDateStr;
    }

    public String getEndDateStr() {
        return endDateStr;
    }

    public void setEndDateStr(String endDateStr) {
        this.endDateStr = endDateStr;
    }
} 