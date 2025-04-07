package com.carrental.util;

import com.carrental.model.User;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;

public class DatabaseUpdater {
    
    public static void updateUserRoles() {
        try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
            MongoDatabase database = mongoClient.getDatabase("carrental");
            MongoCollection<Document> usersCollection = database.getCollection("users");
            
            // Update all users with role "CUSTOMER" to "USER"
            Bson filter = Filters.eq("role", "CUSTOMER");
            Bson update = Updates.set("role", "USER");
            
            usersCollection.updateMany(filter, update);
            
            System.out.println("Database updated successfully. Changed CUSTOMER roles to USER.");
        } catch (Exception e) {
            System.err.println("Error updating database: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        updateUserRoles();
    }
} 