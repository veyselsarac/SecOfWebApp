Java Spring Security Project
This project demonstrates a basic Java Spring application, including user registration (signup), login (authentication), Spring Security configuration, and Flyway migration.

Table of Contents
Features
Setup
Prerequisites
Installation Steps
Running the Application
Project Structure
API Endpoints
Environment Variables (.env)
Resources
License
Features
Spring Boot for building a RESTful application.
Spring Security for basic authentication and authorization.
Flyway for database migration management (creating a users table, etc.).
JPA/Hibernate for database interactions.
Optional SQLite or PostgreSQL support.
Lombok support (optional).
Setup
Prerequisites
Java 17 or higher
Maven or Gradle (depending on the project)
Git (to clone the project)
PostgreSQL (or SQLite) installed or accessible
Installation Steps
Clone the Repository



git clone https://github.com/veyselsarac/SecOfWebApp.git
cd spring-security-app
Create and Configure the .env File

Create an .env file in the project root directory (see .env.example for guidance).
Fill in the database connection details (DB_URL, DB_USERNAME, DB_PASSWORD).
Install Dependencies and Build

If using Maven:

mvn clean install


If using Gradle:

gradle build


Flyway Migration
Flyway will automatically create the necessary database tables (e.g., users) upon application startup.

Running the Application
With Maven:

mvn spring-boot:run
With Gradle:

gradle bootRun
The application will start on http://localhost:8080 by default.

Project Structure

src
 ┣ main
 ┃ ┣ java
 ┃ ┃ ┗ com.example.security
 ┃ ┃    ┣ config
 ┃ ┃    ┣ users
 ┃ ┃    ┃ ┣ controller
 ┃ ┃    ┃ ┣ model
 ┃ ┃    ┃ ┣ repository
 ┃ ┃    ┃ ┗ service
 ┃ ┃    ┗ SecurityApplication.java
 ┃ ┣ resources
 ┃ ┃ ┣ db
 ┃ ┃ ┃ ┗ migration
 ┃ ┃ ┃    ┗ V1__init_db.sql
 ┃ ┃ ┗ application.properties
 ┗ test
SecurityApplication: Main class to start the Spring Boot application.
config: Spring Security configuration (SecurityConfig, etc.).
users/controller: HTTP endpoints for user operations.
users/model: User entity classes.
users/repository: Spring Data JPA repository interfaces.
users/service: Business logic classes (UserService, etc.).
db/migration: Flyway migration files (V1__init_db.sql).
application.properties: Application configuration.
API Endpoints
Method	Endpoint	Description
POST	/signup	Registers a new user.
POST	/login	Authenticates (login) a user.
GET	/me	Returns the currently authenticated user.
GET	/{username}	Fetches a user by their username.
For sample JSON requests, check the Controller methods in the code.

Environment Variables (.env)
The following environment variables must be set in your .env file:

DB_URL: Database connection URL
DB_USERNAME: Database username
DB_PASSWORD: Database password
And in your application.properties:

properties

spring.config.import=optional:file:.env[.properties]

spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
Resources
Spring Security Reference
Spring Data JPA
Flyway
OWASP Session Management Cheat Sheet
License
This project is for educational and demonstration purposes. Feel free to copy, modify, and distribute as needed.
