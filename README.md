# AgriSeva

AgriSeva is a full-stack web application that connects farmers with agricultural equipment owners and agricultural product sellers.

Farmers can browse equipment available for rent, search farming products, submit rental requests, and place product orders. Equipment owners can manage their equipment listings and rental requests. Product sellers can manage product listings and customer orders.

This project was developed as part of the Applied Computer Science Project / Capstone Project for the Master of Science in Computer Science programme delivered through Scaler Neovarsity and awarded by Woolf.

## Features

### Farmer

- Register a new account.
- Log in using email address or phone number.
- Browse agricultural equipment.
- Search equipment by keyword, category, district, village, and status.
- Submit equipment rental requests.
- View personal rental requests.
- Browse agricultural products.
- Search products by keyword, category, district, village, price range, and stock availability.
- Place product orders.
- View personal orders.

### Equipment Owner

- Create or update a provider profile as an equipment owner.
- Add equipment listings.
- Edit equipment details.
- Set equipment status as available, unavailable, or under maintenance.
- Deactivate equipment listings.
- View rental requests received from farmers.
- Accept or reject rental requests.
- Complete accepted rental requests.

### Product Seller

- Create or update a provider profile as a product seller.
- Add agricultural product listings.
- Edit product details.
- Update product price and stock quantity.
- Deactivate product listings.
- View product orders received from customers.
- Accept or reject orders.
- Complete accepted orders.

### Common Features

- JWT-based authentication.
- Role-based access control.
- Request validation.
- Centralized API error handling.
- PostgreSQL persistence.
- Flyway database migrations.
- Responsive React user interface.
- Fallback display for missing or invalid listing images.

## Technology Stack

### Backend

- Java
- Spring Boot
- Spring MVC
- Spring Data JPA
- Spring Security
- JWT
- PostgreSQL
- Flyway
- Maven
- JUnit

### Frontend

- React
- React Router
- Vite
- JavaScript
- CSS
- ESLint

### Development Tools

- Docker
- Postman
- Git
- GitHub

## Project Structure

```text
agriseva/
|-- backend/
|   |-- src/
|   |   |-- main/
|   |   |   |-- java/com/agriseva/
|   |   |   |   |-- auth/
|   |   |   |   |-- common/
|   |   |   |   |-- config/
|   |   |   |   |-- equipment/
|   |   |   |   |-- order/
|   |   |   |   |-- product/
|   |   |   |   |-- rental/
|   |   |   |   `-- user/
|   |   |   `-- resources/
|   |   |       `-- db/migration/
|   |   `-- test/
|   |-- mvnw
|   |-- mvnw.cmd
|   `-- pom.xml
|
|-- frontend/
|   |-- src/
|   |   |-- assets/
|   |   |-- components/
|   |   |-- context/
|   |   |-- pages/
|   |   `-- services/
|   |-- package.json
|   `-- vite.config.js
|
|-- docs/
|   |-- diagrams/
|   |-- report-notes/
|   `-- screenshots/
|
|-- postman/
`-- README.md
```

All repository paths are relative to the project root. The project does not require machine-specific paths such as Windows drive paths.

# Local Setup

The recommended local setup is:

1. Run PostgreSQL using Docker.
2. Run the Spring Boot backend locally.
3. Run the React frontend locally.

A local PostgreSQL installation can also be used instead of Docker.

## Prerequisites

Install the following software:

- Git
- Java 17 or later
- Node.js 20.19+ or 22.12+
- npm
- Docker Desktop, recommended for PostgreSQL

Vite 8 requires Node.js 20.19+ or 22.12+.

Maven does not need to be installed separately because the backend includes the Maven Wrapper.

Check the installed tools:

```bash
git --version
java -version
node --version
npm --version
docker --version
```

# 1. Clone the Repository

```bash
git clone https://github.com/sharathreddyb/agriseva.git
cd agriseva
```

# 2. Start PostgreSQL Using Docker

Docker is the recommended database setup because it creates the required PostgreSQL database and user consistently.

## Fresh Setup

### Windows PowerShell

Run this command from any terminal:

```powershell
docker run --name agriseva-postgres -e POSTGRES_DB=agriseva -e POSTGRES_USER=agriseva_user -e POSTGRES_PASSWORD=agriseva_password -p 5432:5432 -d postgres:16
```

### macOS / Linux

```bash
docker run --name agriseva-postgres -e POSTGRES_DB=agriseva -e POSTGRES_USER=agriseva_user -e POSTGRES_PASSWORD=agriseva_password -p 5432:5432 -d postgres:16
```

Verify that the container is running:

```bash
docker ps
```

The output should contain a container named:

```text
agriseva-postgres
```

The local development database configuration is:

```text
Host: localhost
Port: 5432
Database: agriseva
Username: agriseva_user
Password: agriseva_password
```

Flyway automatically creates and validates the required application tables when the backend starts.

## Existing Container

If the `agriseva-postgres` container already exists but is stopped, do not create another container.

Start it using:

```bash
docker start agriseva-postgres
```

# 3. PostgreSQL Setup Without Docker

Skip this section if PostgreSQL is running through Docker.

If PostgreSQL 16 is installed locally, connect as a PostgreSQL administrator and create the application user and database:

```sql
CREATE USER agriseva_user
WITH PASSWORD 'agriseva_password';

CREATE DATABASE agriseva
OWNER agriseva_user;
```

The database must be reachable at:

```text
localhost:5432
```

The backend will connect using:

```text
Database: agriseva
Username: agriseva_user
Password: agriseva_password
```

Flyway will create the application tables when the backend starts.

# 4. Backend Configuration

The backend configuration file is:

```text
backend/src/main/resources/application.properties
```

The project contains development defaults, so no additional environment variables are required for the standard local setup.

The supported environment variables are:

```text
DATABASE_URL
DATABASE_USERNAME
DATABASE_PASSWORD
JWT_SECRET
JWT_EXPIRATION_MILLISECONDS
```

The default local values are equivalent to:

```text
DATABASE_URL=jdbc:postgresql://localhost:5432/agriseva
DATABASE_USERNAME=agriseva_user
DATABASE_PASSWORD=agriseva_password
JWT_EXPIRATION_MILLISECONDS=3600000
```

The JWT secret included in the application configuration is a development default only. A separate secure `JWT_SECRET` should be supplied through the environment for a production deployment.

# 5. Run the Backend

Open a terminal from the project root.

## Windows PowerShell

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

## macOS / Linux

```bash
cd backend
chmod +x mvnw
./mvnw spring-boot:run
```

The backend runs on:

```text
http://localhost:8080
```

The REST API is available under:

```text
http://localhost:8080/api
```

Opening `http://localhost:8080` directly may return `403 Forbidden`. This is expected because the application is secured and the application functionality is exposed through API routes.

Keep this terminal running while using the application.

# 6. Run the Frontend

Open a second terminal from the project root.

```bash
cd frontend
npm install
npm run dev
```

Vite displays the frontend URL in the terminal. With the default local setup it is normally:

```text
http://localhost:5173
```

Open that URL in a web browser.

Keep this terminal running while using the application.

# 7. First-Time Use

No pre-created user account is required.

After opening the frontend:

1. Select **Register**.
2. Create a user account.
3. Log in using the registered email address or phone number.
4. Browse the **Equipment** and **Products** pages.
5. Use **Sell or Rent** after login to create a provider profile.
6. Select the required provider type:
   - Equipment Owner
   - Product Seller
   - Both
7. After the provider profile is created, the corresponding equipment-owner or product-seller options become available.

# Main Application Pages

| Page | URL |
|---|---|
| Home | `http://localhost:5173/` |
| Login | `http://localhost:5173/login` |
| Register | `http://localhost:5173/register` |
| Equipment | `http://localhost:5173/equipment` |
| Products | `http://localhost:5173/products` |
| My Rentals | `http://localhost:5173/rentals` |
| My Orders | `http://localhost:5173/orders` |
| Provider Profile | `http://localhost:5173/provider` |
| My Equipment | `http://localhost:5173/provider/equipment` |
| Owner Rental Requests | `http://localhost:5173/owner/rentals` |
| My Products | `http://localhost:5173/provider/products` |
| Received Orders | `http://localhost:5173/seller/orders` |

Some pages require authentication or the corresponding provider role.

# Main API Areas

## Authentication and User Management

The backend provides APIs for:

- user registration
- authentication
- user information
- provider profile management

Important API paths include:

```text
/api/auth
/api/users
/api/users/me/provider-profile
```

## Equipment and Rentals

Important API paths include:

```text
/api/equipment
/api/equipment/mine
/api/rentals
```

Equipment owners can create, update, view, and deactivate their listings. Farmers can search active equipment and create rental requests.

## Products and Orders

Important API paths include:

```text
/api/products
/api/products/mine
/api/orders
```

Product sellers can create, update, view, and deactivate their listings. Farmers can search active products and place product orders.

Protected endpoints require a valid JWT token.

# Database Migrations

AgriSeva uses Flyway for database schema versioning.

Migration files are stored in:

```text
backend/src/main/resources/db/migration/
```

The current migrations create the tables required for:

- users and roles
- provider profiles
- equipment
- equipment rentals
- products
- product orders

Spring JPA uses schema validation:

```text
spring.jpa.hibernate.ddl-auto=validate
```

This means the database structure is managed by Flyway rather than being generated automatically by Hibernate.

# Running Backend Tests

PostgreSQL must be running before the complete backend test suite is executed.

## Windows PowerShell

From the project root:

```powershell
cd backend
.\mvnw.cmd test
```

## macOS / Linux

```bash
cd backend
./mvnw test
```

A successful test run ends with:

```text
BUILD SUCCESS
```

# Running Frontend Checks

From the project root:

```bash
cd frontend
npm run lint
```

A successful lint run completes without ESLint errors.

To verify that the production frontend can be built:

```bash
npm run build
```

The generated frontend build is written to the Vite output directory.

# Stopping the Application

Stop the frontend and backend terminals using:

```text
Ctrl + C
```

Stop the PostgreSQL Docker container using:

```bash
docker stop agriseva-postgres
```

Start the same database again later using:

```bash
docker start agriseva-postgres
```

# Resetting the Docker Database

Use this only when a completely fresh local database is required.

Stop and remove the existing container:

```bash
docker stop agriseva-postgres
docker rm agriseva-postgres
```

Then repeat the Docker setup command from this README.

Removing the container deletes the database data stored inside that container.

# Troubleshooting

## Backend Cannot Connect to PostgreSQL

Check whether the database container is running:

```bash
docker ps
```

If the container exists but is stopped:

```bash
docker start agriseva-postgres
```

Also confirm that another PostgreSQL instance is not already using port `5432`.

## Docker Container Name Already Exists

If Docker reports that `agriseva-postgres` already exists, do not run the create command again.

Use:

```bash
docker start agriseva-postgres
```

## Port 8080 Is Already in Use

Another process is already using the backend port.

Stop that process and start the backend again.

## Port 5173 Is Already in Use

Another frontend development server may already be running.

Stop the existing process and run:

```bash
npm run dev
```

again.

Vite may choose another available port. Use the URL displayed in the terminal.

## Frontend Shows an API Error

Confirm that:

- PostgreSQL is running.
- The backend is running on port `8080`.
- The frontend development server is running.
- The backend can be reached at `http://localhost:8080`.

## Database Tables Are Missing

Restart the backend and inspect its startup logs for Flyway migration errors.

The migrations are located in:

```text
backend/src/main/resources/db/migration/
```

## Frontend Dependencies Are Missing

From the `frontend` directory, run:

```bash
npm install
```

Then restart the frontend:

```bash
npm run dev
```

# Application Architecture

AgriSeva follows a layered backend architecture.

```text
Client
  |
  v
REST Controller
  |
  v
Service Layer
  |
  v
Repository Layer
  |
  v
PostgreSQL
```

The main responsibilities are separated as follows:

- Controllers receive HTTP requests and return API responses.
- DTOs define request and response data.
- Services contain business rules and workflow logic.
- Repositories handle database access through Spring Data JPA.
- Spring Security protects authenticated and role-specific endpoints.
- JWT is used for stateless authentication.
- Flyway manages database schema changes.
- React provides the browser-based user interface.

# Main Application Workflows

## Equipment Rental Workflow

```text
Farmer
  |
  v
Browse / Search Equipment
  |
  v
Select Available Equipment
  |
  v
Submit Rental Request
  |
  v
Equipment Owner Reviews Request
  |
  +----> Accept
  |
  `----> Reject
          |
          v
    Rental Status Updated
```

## Product Order Workflow

```text
Farmer
  |
  v
Browse / Search Products
  |
  v
Select Product
  |
  v
Place Order
  |
  v
Product Seller Reviews Order
  |
  +----> Accept
  |
  `----> Reject
          |
          v
      Order Status Updated
```

# Security

The application uses:

- password hashing
- JWT-based authentication
- Spring Security
- role-based authorization
- request validation
- centralized exception handling

Provider-specific functions are available only when the authenticated user has the required provider role.

# Current Scope

The current application includes:

- user registration
- email and phone login
- JWT authentication
- provider profile and role management
- equipment listing management
- equipment search and filtering
- equipment rental requests
- owner rental-request management
- product listing management
- product search and filtering
- product orders
- seller order management
- responsive frontend navigation
- backend automated tests
- frontend lint and build support

# Future Improvements

Possible future enhancements include:

- online payments
- GPS-based equipment and delivery tracking
- transport partner integration
- multilingual support
- SMS or WhatsApp notifications
- weather-based farming information
- recommendation features
- ratings and reviews
- analytics dashboards
- production cloud deployment

These items are outside the current implemented project scope.

# Proposed Production Deployment

The current project is designed to run locally for development and academic evaluation.

For a production deployment, AgriSeva can be hosted on AWS using an architecture such as:

- Amazon VPC for network isolation
- Security Groups for controlled network access
- Amazon EC2 or a managed application platform for the Spring Boot backend
- Amazon RDS for PostgreSQL
- static web hosting for the React production build
- AWS Secrets Manager or environment variables for secrets
- HTTPS through a load balancer or reverse proxy

This section describes a proposed production deployment. The current project does not depend on AWS and can be fully evaluated using the local setup described in this README.

# Documentation

Project documentation is maintained under:

```text
docs/
```

The repository contains dedicated locations for:

```text
docs/diagrams/
docs/report-notes/
docs/screenshots/
```

Postman API material will be stored under:

```text
postman/
```

These folders are used during final project documentation and academic submission preparation.

# Author

**Sharath Reddy Bayyaram**

Master of Science in Computer Science

Scaler Neovarsity | Woolf

# Academic Project

AgriSeva is an applied software capstone project developed for academic evaluation.

The project has been developed iteratively using Git feature branches and periodic commits. The repository is structured so that the source code, database migrations, tests, documentation, diagrams, screenshots, and API material can be reviewed independently.

The project is intended to be reproducible locally using the setup instructions provided in this README.
