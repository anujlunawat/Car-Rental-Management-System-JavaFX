# Car Rental System

A Java-based Car Rental System with JavaFX frontend and MongoDB backend.

## Features

- User Authentication (Login/Register)
- Role-based Access Control (Admin/Customer)
- Car Management (Add/Update/Remove)
- Car Browsing and Booking
- Booking Management
- Payment Processing

## Prerequisites

- Java JDK 17 or later
- MongoDB 4.4 or later
- Maven 3.6 or later

## Setup Instructions

1. Clone the repository:
```bash
git clone https://github.com/anujlunawat/Car-Rental-System.git
cd car-rental-system
```

2. Install MongoDB:
   - Download and install MongoDB from [MongoDB Download Center](https://www.mongodb.com/try/download/community)
   - Start MongoDB service
   - Create a database named `car_rental_db`

3. Build the project:
```bash
mvn clean install
```

4. Run the application:
```bash
mvn javafx:run
```

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── carrental/
│   │           ├── controller/    # JavaFX controllers
│   │           ├── model/         # Data models
│   │           ├── util/          # Utility classes
│   │           └── Main.java      # Application entry point
│   └── resources/
│       └── fxml/                  # FXML files for UI
```

After registration, you'll need to manually update the user's role to ADMIN in the MongoDB database.

## Database Collections

The application uses the following collections in MongoDB:
- users: Stores user information
- cars: Stores car information
- bookings: Stores booking information

## Contributing

1. Fork the repository
2. Create your feature branch
3. Commit your changes
4. Push to the branch
5. Create a new Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details. 
