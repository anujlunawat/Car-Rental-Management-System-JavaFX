package com.carrental.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {
    private static ConfigManager instance;
    private Properties properties;
    
    private ConfigManager() {
        properties = new Properties();
        loadProperties();
    }
    
    public static ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }
    
    private void loadProperties() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("Unable to find config.properties");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Error loading config.properties", e);
        }
    }
    
    public String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    public int getIntProperty(String key, int defaultValue) {
        String value = properties.getProperty(key);
        return value != null ? Integer.parseInt(value) : defaultValue;
    }
    
    public boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = properties.getProperty(key);
        return value != null ? Boolean.parseBoolean(value) : defaultValue;
    }
    
    public String getDbConnectionString() {
        String host = getProperty("db.host", "localhost");
        int port = getIntProperty("db.port", 27017);
        String name = getProperty("db.name", "car_rental_db");
        String username = getProperty("db.username");
        String password = getProperty("db.password");
        
        StringBuilder connectionString = new StringBuilder("mongodb://");
        if (username != null && !username.isEmpty() && password != null && !password.isEmpty()) {
            connectionString.append(username).append(":").append(password).append("@");
        }
        connectionString.append(host).append(":").append(port).append("/").append(name);
        
        return connectionString.toString();
    }
    
    public String getAppTitle() {
        return getProperty("app.title", "Car Rental System");
    }
    
    public String getAppVersion() {
        return getProperty("app.version", "1.0.0");
    }
    
    public String getAdminEmail() {
        return getProperty("app.admin.email", "admin@carrental.com");
    }
    
    public String getAdminPhone() {
        return getProperty("app.admin.phone", "1234567890");
    }
} 