# Vehicle Registration API
This project is a RESTful API for managing vehicle registrations, ownership transfers and retrieve vehicle details by one or get paginated list of vehicle registrations with customized pagination and sorting. The API ensures that deleted records are marked as invalid rather than removed from the database.

## Features
- Register a new vehicle.
- Retrieve vehicle details by ID.
- Transfer vehicle ownership.
- Soft-deletion of vehicle records.
- Get paginated list of vehicle registrations with customized pagination and sorting.

## API Documentation
The API documentation is available at http://localhost:8080/swagger-ui.html after running the application.

## Endpoints

### Vehicle Management
| Method | Endpoint                     | Description                                                  |
|--------|------------------------------|--------------------------------------------------------------|
| `POST` | `/vehicles/register`         | Register a new vehicle.                                      |
| `GET`  | `/vehicles/{vehicleId}`      | Retrieve vehicle details by ID.                              |
| `POST` | `/vehicles/{vehicleId}/transfer-owner` | Transfer ownership of a vehicle.                            |
| `DELETE` | `/vehicles/{vehicleId}`    | Soft-delete a vehicle (mark as invalid).                |
| `GET`  | `/vehicles`                  | Retrieve a paginated list of vehicle registrations.          |


## Installation

1. Clone the repository
2. Navigate to the project directory:
```cd transport-registration-system```
3. Install dependencies:
```mvn clean install```
4. Run the application:
```mvn spring-boot:run```

## Testing
Run tests using the following command:
```mvn test```