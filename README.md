# Food Order App - Spring Boot REST API

## Overview
A comprehensive food ordering application built with Spring Boot, featuring JWT authentication, role-based access control, and RESTful APIs for managing restaurants, menus, orders, and food suggestions.

## Technology Stack
- **Java**: 17
- **Spring Boot**: 3.2.0
- **Spring Security**: JWT-based authentication
- **Spring Data JPA**: with Hibernate
- **Database**: H2 (in-memory)
- **API Documentation**: OpenAPI 3.0 (Swagger UI)
- **Build Tool**: Maven
- **Validation**: Jakarta Bean Validation

## Features
- JWT-based authentication and authorization
- Role-based access control (ADMIN, RESTAURANT_OWNER, CONSUMER)
- Restaurant account management (Admin)
- Menu item management (Restaurant Owner)
- Restaurant timing management (Restaurant Owner)
- Order placement and tracking (Consumer)
- Order approval and status updates (Restaurant Owner)
- Restaurant browsing by location or all restaurants 
- Food search with multiple filters 
- Personalized food suggestions based on order history (Consumer)  

### Locally To Run

1. **Build the project**
bash
mvnw clean install

3. **Run the application**
bash
mvnw spring-boot:run


The application will start on http://localhost:8080

### Access Points
- **API Base URL**: http://localhost:8080/api
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/v3/api-docs

## API Endpoints

### Authentication
Public endpoints.

- POST /api/auth/register - Register new user
- POST /api/auth/login - Login and receive JWT token

### Admin
Requires ADMIN role.

- POST /api/admin/restaurants - Create new restaurant account
- PUT /api/admin/restaurants/{id} - Update restaurant details
- DELETE /api/admin/restaurants/{id} - Delete restaurant

### Restaurant Owner
Requires RESTAURANT_OWNER role.

- GET /api/restaurant-owners/restaurants - View all owned restaurants
- POST /api/restaurant-owners/restaurants/{restaurantId}/menu-items - Add menu item
- PUT /api/restaurant-owners/restaurants/{restaurantId}/menu-items/{itemId} - Update menu item
- DELETE /api/restaurant-owners/restaurants/{restaurantId}/menu/{itemId} - Delete menu item
- PUT /api/restaurant-owners/restaurants/{restaurantId}/timings - Update restaurant timings
- GET /api/restaurant-owners/restaurants/{restaurantId}/orders - View orders
- POST /api/restaurant-owners/restaurants/{restaurantId}/orders/{orderId}/approve - Approve order
- PATCH /api/restaurant-owners/restaurants/{restaurantId}/orders/{orderId}/status - Update order status

### Consumer
Requires CONSUMER role.

- POST /api/orders - Place new order
- GET /api/orders - Get order history
- GET /api/orders/{orderId} - Get order details
- GET /api/suggestions - Get personalized suggestions

Below are public
- GET /api/restaurants - Browse restaurants
- GET /api/restaurants/nearby - Browse nearby restaurants
- GET /api/search/foods - Search food items
- GET /api/restaurant-owners/restaurants/{restaurantId} - View specific restaurant details
- GET /api/restaurant-owners/restaurants/{restaurantId}/menu-items - List menu items
- GET /api/restaurant-owners/restaurants/{restaurantId}/timings - View restaurant timings



