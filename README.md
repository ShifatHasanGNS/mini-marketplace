# Mini Marketplace

A Spring Boot project that provides a full-featured mini marketplace. The platform supports three roles: Admin, Seller, and Buyer, each with specific permissions and functionalities.

---

## Table of Contents

- [Project Description](#project-description)
- [Architecture](#architecture)
- [ER Diagram](#er-diagram)
- [Features](#features)
- [API Endpoints](#api-endpoints)
- [Tech Stack](#tech-stack)
- [Run Instructions](#run-instructions)
- [CI/CD Workflow](#cicd-workflow)
- [Pipeline](#pipeline)
- [Branch Strategy](#branch-strategy)
- [Render Deployment](#render-deployment)
- [License](#license)

---

## Project Description

Mini Marketplace is a web-based application that allows:

- Admins ->  can manage the whole marketplace , can see all products available in the marketplace , can add coupon ( edit/delete) ,manage users
- Sellers -> can add, update, delete products and view orders.
- Buyers -> can browse products, place orders, and apply coupons.

The application is developed with Spring Boot, Java, and Maven, and uses PostgreSQL for data persistence. Additionally, it features Git-based CI/CD pipelines, Spring Security for authentication and authorization, Data Transfer Objects (DTOs) for clean data handling, and comprehensive unit and integration tests.

---

## Architecture

The project follows a standard Spring Boot layered architecture:

1. Controller Layer – Handles HTTP requests and routes them to services.
2. Service Layer – Implements business logic.
3. Repository Layer – Interacts with the database.
4. Model Layer – Represents entities and data structures.

Architecture Diagram:


+-------------------+
| Browser / Client  |
| - Thymeleaf pages |
| - JS fetch calls  |
+---------+---------+
          |
          v
+------------------------------+
| Spring Security Filter Chain |
| - JWT Authentication Filter  |
| - Role-based method guards   |
+---------------+--------------+
                |
                v
+----------------------------------------------+
| Controllers                                   |
| - PageController (server-rendered pages)      |
| - REST Controllers (/api/**)                  |
+--------------------------+-------------------+
                           |
                           v
+----------------------------------------------+
| Service Layer                                  |
| - AuthService                                  |
| - ProductService                               |
| - OrderService                                 |
| - CouponService                                |
+--------------------------+-------------------+
                           |
                           v
+--------------------------------------------+
| Repository Layer (Spring Data JPA)         |
| - LoginRepository                          |
| - ProductRepository                        |
| - OrdersRepository                         |
| - CouponRepository                         |
+--------------------------+-----------------+
                           |
                           v
+----------------------------------------------+
| H2 / MySQL Database                           |
| - schema migrations (if using Liquibase)      |
| - persistent marketplace data                 |
+----------------------------------------------+

## ER Diagram

The database structure consists of the following key entities:

- User – Represents Admin, Buyer, Seller
- Product – Added by Seller
- Order – Placed by Buyer
- Coupon – Managed by Admin

ER Diagram:


+-------------------+       +-------------------+
| login             |1-----<| product           |
|-------------------|       |-------------------|
| id PK             |       | id PK             |
| name              |       | name              |
| password          |       | price             |
| role              |       | origin            |
|                   |       | pic               |
|                   |       | seller_id FK      |
+-------------------+       +-------------------+
        |   ^
        |   |
        |   |
        v   |
+-------------------+       +-------------------+
| orders            |       | coupon            |
|-------------------|       |-------------------|
| id PK             |       | id PK             |
| buyer_id FK       |>-----1| code              |
| product_id FK     |       | discount_percentage|
| status            |       | valid_until       |
| total_amount      |       +-------------------+
| tracking_number   |
| discount_percentage|
| coupon_id FK      |
| order_date        |
+-------------------+

---

## Features

- User signup/login with role-based access
- Admin dashboard for managing users, products , orders, and coupons
- Seller dashboard for managing products and viewing orders
- Buyer functionality for browsing products, managing cart, placing orders, and using coupons
- Session-based cart for buyers
- Dashboard stats like total sales, top sellers, and total spent

---

## API Endpoints

| Method | Path                                     | Auth     | Role                  | Description                                           
|--------|-----------------------------------------|----------|----------------------|-------------------------------------------------------|
| GET    | /login                                  | Public   | All                  | Show login page                                       |
| GET    | /signup                                 | Public   | All                  | Show signup page                                      |
| POST   | /signup                                 | Public   | All                  | Register a new user (buyer or seller)                 |
| GET    | /redirect-dashboard                     | Required | All                  | Redirect user to dashboard based on role              |

| **Admin Endpoints** |                                                                                                                              
| GET    | /admin                                   | Required | ADMIN                | Redirect to admin dashboard                          |
| GET    | /admin/dashboard                         | Required | ADMIN                | View admin dashboard                                 |
| GET    | /admin/users                             | Required | ADMIN                | View all users                                       |
| DELETE | /admin/users/{id}                        | Required | ADMIN                | Delete a user by ID                                  |
| GET    | /admin/coupons                           | Required | ADMIN                | View all coupons                                     |
| POST   | /admin/coupons                           | Required | ADMIN                | Create a new coupon                                  |
| GET    | /admin/coupons/{id}                      | Required | ADMIN                | View coupon details for editing                      |
| PUT    | /admin/coupons/{id}                      | Required | ADMIN                | Update coupon details                                |
| DELETE | /admin/coupons/{id}                      | Required | ADMIN                | Delete a coupon by ID                                |
| GET    | /admin/sales                             | Required | ADMIN                | View sales summary and top sellers                   |

| **Seller Endpoints** |                                                                                                                             
| GET    | /seller/dashboard/{username}             | Required | SELLER               | View seller dashboard                                |
| GET    | /seller/products/{username}              | Required | SELLER               | View seller's products                               |
| POST   | /seller/products                         | Required | SELLER               | Add a new product                                    |
| PUT    | /seller/products/{id}                    | Required | SELLER               | Update an existing product                           |
| DELETE | /seller/products/{id}                    | Required | SELLER               | Delete a product by ID                               |
| GET    | /seller/orders/{username}                | Required | SELLER               | View orders received for the seller                  |

| **Buyer Endpoints**  |                                                                                                                             
| GET    | /buyer/dashboard/{username}              | Required | BUYER                | View buyer dashboard                                  |
| GET    | /buyer/products/{username}               | Required | BUYER                | Browse all products                                   |
| GET    | /buyer/cart/{username}                   | Required | BUYER                | View cart contents                                    |
| GET    | /buyer/add-to-cart/{id}/{username}       | Required | BUYER                | Add product to cart                                   |
| GET    | /buyer/remove-from-cart/{id}/{username}  | Required | BUYER                | Remove product from cart                              |
| GET    | /buyer/orders/{username}                 | Required | BUYER                | View buyer's order history                            |
| GET    | /buyer/check-coupon?code={code}          | Required | BUYER                | Validate a coupon code                                |
| POST   | /buyer/checkout                          | Required | BUYER                | Checkout cart and place orders                        |

---

## Tech Stack

| Layer           | Technology                                        |
|-----------------|---------------------------------------------------|
| Language        | Java 17                                           |
| Framework       | Spring Boot 3.3.4                                 |
| Security        | Spring Security, JWT, BCrypt                      |
| Database        | PostgreSQL (runtime), H2 (tests)                  |
| ORM             | Spring Data JPA / Hibernate                       |
| Migrations      | Liquibase                                         |
| Views           | Thymeleaf + Spring Security Extras                |
| API Docs        | springdoc-openapi / Swagger UI                    |
| Testing         | JUnit 5, Mockito, MockMvc                         |
| Containerization| Docker + Docker Compose                           |
| CI/CD           | GitHub Actions + Render deploy hook               |



## Run Instructions 

(Docker-based)
 
 1. Clone the Repository

git clone https://github.com/ShifatHasanGNS/mini-marketplace.git
cd mini-marketplace


2. Build the Docker Image

  docker build -t mini-marketplace .

3. Run the Application with Docker

  docker run -p 8080:8080 mini-marketplace

  The application will start at: http://localhost:8080
