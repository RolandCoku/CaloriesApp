Group:  Roland Çoku 
       Idna Ademi 
       Ikeda Laze 
       Laura Tafilaj 


Calories Calculation Application
Overview
The Calories Calculation Application is a Java-based project that enables users to track their daily calorie intake, food expenses, and receive warnings when exceeding predefined thresholds. 
The application supports both user and admin functionalities, with detailed reporting and food entry management.

**Features**

**User Features**

Account Management:

User Registration: Create an account with name, email, and password.
User Login: Securely log in to access personalized dashboards.

Food Entry Management:

Add food entries with details such as name, calorie value, price, and date.
View and filter food entries by date range.

Calorie and Expense Warnings:

Daily calorie threshold of 2,500 calories.
Monthly expense threshold of €1,000.
Visual warnings and highlights when thresholds are exceeded.

Weekly Summary Report:

A dashboard summary of total calories per day, days the threshold was exceeded, and total weekly expenses.

Admin Features
Manage all user food entries:
CRUD operations (Create, Read, Update, Delete).
Advanced reporting:
Number of entries added in the last 7 days.
Average calories per user over the last week.
Users exceeding the monthly spending limit.
User Stories
Refer to the User Stories and Sprint Backlog sections for detailed features and acceptance criteria.

**Technical Requirements**

**Functional Requirements**

User registration and login system with password security.
Food entry tracking, filtering, and reporting.
Calorie and spending threshold warnings.

**Non-Functional Requirements**
Intuitive and user-friendly interface.
Three-click interaction for main features.
Java-based implementation with a relational database backend.
GitHub-hosted version control.
Target Platform
Web-based or desktop application using Java.

**Architecture and Design**

The application follows a layered architecture:

**Presentation Layer:** HTML, CSS, JavaScript, and Bootstrap for UI.
**Service Layer:** Business logic in the form of Java services.
**Data Access Layer:** Integration with a relational database using JPA and Hibernate.


**Installation and Setup**
Java 17 or higher.
Maven for dependency management.
A relational database (e.g., MySQL or H2 for local development).

Admin user default credentials: "username": admin, "password": admin123
