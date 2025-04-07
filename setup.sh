#!/bin/bash

echo "Setting up Car Rental System..."

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "Error: Java is not installed or not in PATH"
    echo "Please install Java JDK 17 or later"
    exit 1
fi

# Check if MongoDB is installed
if ! command -v mongod &> /dev/null; then
    echo "Error: MongoDB is not installed or not in PATH"
    echo "Please install MongoDB 4.4 or later"
    exit 1
fi

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "Error: Maven is not installed or not in PATH"
    echo "Please install Maven 3.6 or later"
    exit 1
fi

# Create necessary directories
mkdir -p src/main/java/com/carrental/{controller,model,util}
mkdir -p src/main/resources/{fxml,css}

# Build the project
echo "Building project..."
mvn clean install

if [ $? -ne 0 ]; then
    echo "Error: Failed to build project"
    exit 1
fi

# Start MongoDB service
echo "Starting MongoDB service..."
if command -v systemctl &> /dev/null; then
    sudo systemctl start mongodb
elif command -v service &> /dev/null; then
    sudo service mongodb start
else
    echo "Warning: Could not start MongoDB service"
    echo "Please make sure MongoDB is running"
fi

# Create database
echo "Creating database..."
mongosh --eval "use car_rental_db" &> /dev/null

echo "Setup completed successfully!"
echo "You can now run the application using: mvn javafx:run" 