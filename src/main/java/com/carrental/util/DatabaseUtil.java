package com.carrental.util;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import dev.morphia.Datastore;
import dev.morphia.Morphia;

public class DatabaseUtil {
    private static MongoClient mongoClient;
    private static Datastore datastore;
    
    private DatabaseUtil() {
        // Private constructor to prevent instantiation
    }
    
    public static Datastore getDatastore() {
        if (datastore == null) {
            initializeDatastore();
        }
        return datastore;
    }
    
    private static void initializeDatastore() {
        try {
            String connectionString = ConfigManager.getInstance().getDbConnectionString();
            mongoClient = MongoClients.create(connectionString);
            
            datastore = Morphia.createDatastore(mongoClient, "car_rental_db");
            datastore.getMapper().mapPackage("com.carrental.model");
            datastore.ensureIndexes();
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize database connection", e);
        }
    }
    
    public static void closeConnection() {
        if (mongoClient != null) {
            mongoClient.close();
            mongoClient = null;
            datastore = null;
        }
    }
} 