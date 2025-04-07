package com.carrental.util;

import com.carrental.model.Car;
import dev.morphia.Datastore;
import dev.morphia.query.filters.Filters;

public class ImagePathUpdater {
    public static void updateImagePaths() {
        Datastore datastore = DatabaseUtil.getDatastore();
        
        // Update BMW M image path
        Car bmwCar = datastore.find(Car.class)
            .filter(Filters.eq("brand", "BMW"))
            .filter(Filters.eq("model", "M"))
            .first();
            
        if (bmwCar != null) {
            bmwCar.setImagePath("/images/cars/bmw-m.jpg");
            datastore.save(bmwCar);
            System.out.println("Updated BMW M image path");
        }
        
        // Update Toyota Camry image path
        Car toyotaCar = datastore.find(Car.class)
            .filter(Filters.eq("brand", "Toyota"))
            .filter(Filters.eq("model", "Camry"))
            .first();
            
        if (toyotaCar != null) {
            toyotaCar.setImagePath("/images/cars/toyota-camry.jpg");
            datastore.save(toyotaCar);
            System.out.println("Updated Toyota Camry image path");
        }
        
        System.out.println("Image path update completed");
    }
    
    public static void main(String[] args) {
        updateImagePaths();
    }
} 