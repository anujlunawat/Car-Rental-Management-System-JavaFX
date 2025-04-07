@echo off
echo Setting up Car Rental System...

REM Check if Java is installed
java -version >nul 2>&1
if errorlevel 1 (
    echo Error: Java is not installed or not in PATH
    echo Please install Java JDK 17 or later
    pause
    exit /b 1
)

REM Check if MongoDB is installed
mongod --version >nul 2>&1
if errorlevel 1 (
    echo Error: MongoDB is not installed or not in PATH
    echo Please install MongoDB 4.4 or later
    pause
    exit /b 1
)

REM Check if Maven is installed
mvn -version >nul 2>&1
if errorlevel 1 (
    echo Error: Maven is not installed or not in PATH
    echo Please install Maven 3.6 or later
    pause
    exit /b 1
)

REM Create necessary directories
mkdir src\main\java\com\carrental\controller
mkdir src\main\java\com\carrental\model
mkdir src\main\java\com\carrental\util
mkdir src\main\resources\fxml
mkdir src\main\resources\css

REM Build the project
echo Building project...
call mvn clean install

if errorlevel 1 (
    echo Error: Failed to build project
    pause
    exit /b 1
)

REM Start MongoDB service
echo Starting MongoDB service...
net start MongoDB >nul 2>&1
if errorlevel 1 (
    echo Warning: Could not start MongoDB service
    echo Please make sure MongoDB is running
)

REM Create database
echo Creating database...
mongosh --eval "use car_rental_db" >nul 2>&1

echo Setup completed successfully!
echo You can now run the application using: mvn javafx:run
pause 