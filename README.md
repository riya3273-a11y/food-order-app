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
- User Authentication & Authorization (JWT)  
-  Role-based Access Control (ADMIN, RESTAURANT_OWNER, CONSUMER)  
-  Restaurant Management  
-  Menu Item CRUD Operations  
-  Order Management & Status Tracking  
-  Restaurant Browsing 
-  Advanced Food Search  
-  Personalized Food Suggestions  
-  Restaurant Timing Management  

### Locally Run

1. **Clone the repository**
```bash
git clone <repository-url>
cd food-order-app
```

2. **Build the project**
```bash
mvnw clean install
```

3. **Run the application**
```bash
mvnw spring-boot:run
```

The application will start on `http://localhost:8080`

### Access Points
- **API Base URL**: http://localhost:8080/api
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/v3/api-docs
- **H2 Console**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:foodorderdb`
  - Username: `sa`
  - Password: (leave blank)

## API Documentation

### Authentication Endpoints (`/api/auth`)

#### Register User
- POST /api/auth/register - Register user

#### Login
- POST /api/auth/login - Login user

### Admin Endpoints (`/api/admin`)
- `POST /restaurants` - Create restaurant
- `PUT /restaurants/{id}` - Update restaurant
- `DELETE /restaurants/{id}` - Delete restaurant

### Restaurant Owner Endpoints (`/api/restaurant/owner`)
- `POST /{restaurantId}/menu` - Add menu item
- `PUT /{restaurantId}/menu/{itemId}` - Update menu item
- `DELETE /{restaurantId}/menu/{itemId}` - Delete menu item
- `GET /{restaurantId}/menu` - List menu items
- `PUT /{restaurantId}/timings` - Update restaurant timings
- `GET /{restaurantId}/orders` - View restaurant orders
- `PATCH /{restaurantId}/orders/{orderId}/status` - Update order status

### Consumer Endpoints
#### Orders (`/api/orders`)
- `POST /` - Place order
- `GET /` - Get order history
- `GET /{orderId}` - Check order status

#### Search (`/api/search`)
- `GET /foods` - Search food items with filters

#### Browse (`/api/restaurants`)
- `GET /browse` - Browse nearby restaurants

#### Suggestions (`/api/suggestions`)
- `GET /` - Get personalized food suggestions

## Security

### JWT Token
- Token expiration: 1 hour
- Token format: `Bearer <token>`
- Include in Authorization header

## Database Schema

### Tables
- `users` - User accounts
- `restaurant` - Restaurant information
- `menu_item` - Menu items
- `orders` - Customer orders
- `order_item` - Order items
- `restaurant_timing` - Restaurant operating hours
