# Restaurant Service REST API

## Project Description
This project is a RESTful web service for managing restaurant orders using JDBC and Servlet. The service allows performing CRUD operations for various entities such as products, product categories, order details, and order approvals.
#### Technology Stack
*   Java
*   JDBC
*   Servlet
*   PostgreSQL
*   JUnit, Mockito
*   Testcontainers
*   MapStruct
*   Lombok
*   SLF4J/Logback

#### Requirements
*   Java 8 or higher
*   PostgreSQL
*   Maven

### Installation and Running

#### Cloning the Repository
```git clone https://github.com/pp8a/restaurant-service.git 
cd restaurant-service```
#### Database Setup
1.  Create a PostgreSQL database and user:

`CREATE DATABASE testdb;`
`CREATE USER testuser WITH ENCRYPTED PASSWORD 'testpassword';`
`GRANT ALL PRIVILEGES ON DATABASE testdb TO testuser; `

2.  Update the application.yml file with your database connection parameters.

#### Running the Application

1.  Build the project using Maven:
`mvn clean install` 
2.  Run the application:
`mvn tomcat10:run`

## API Documentation

### Products

*   GET /products - Retrieve all products
*   GET /products/{id} - Retrieve product by ID
*   POST /products - Create a new product
*   PUT /products/{id} - Update an existing product
*   DELETE /products/{id} - Delete a product by ID

### Product Categories
*   GET /product-categories - Retrieve all product categories
*   GET /product-categories/{id} - Retrieve product category by ID
*   POST /product-categories - Create a new product category
*   PUT /product-categories/{id} - Update an existing product category
*   DELETE /product-categories/{id} - Delete a product category by ID

### Order Details
*   GET /order-details - Retrieve all order details
*   GET /order-details/{id} - Retrieve order details by ID
*   POST /order-details - Create new order details
*   PUT /order-details/{id} - Update existing order details
*   DELETE /order-details/{id} - Delete order details by ID

### Order Approvals
*   GET /order-approvals - Retrieve all order approvals
*   GET /order-approvals/{id} - Retrieve order approval by ID
*   POST /order-approvals - Create a new order approval
*   PUT /order-approvals/{id} - Update an existing order approval
*   DELETE /order-approvals/{id} - Delete an order approval by ID

### Testing
#### Running Unit Tests
The project includes unit tests written using JUnit and Mockito. To run the tests, execute:
`mvn test` 
#### Running Integration Tests
Integration tests use Testcontainers to test database interactions. To run the integration tests, execute:
`mvn verify` 

Author: Aleksandr Mikhalchuk

Email: <tradingusdc@gmail.com>

GitHub: pp8a

License: &copy; This project is licensed under the MIT License. See the LICENSE file for details.