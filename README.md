# Distance Learning Activity Manager (DLAM)

DLAM is a web-based application designed to manage distance learning activities. It provides a platform for instructors to manage courses and students to enroll and participate in learning activities.

## Features

- **User Authentication**: Secure login and registration system.
- **Role-Based Access**:
  - **Instructors**: Can manage courses and activities (Planned).
  - **Students**: Can enroll in courses and view activities (Planned).
- **Dashboard**: Personalized view based on user role.

## Technology Stack

- **Backend**: Java Servlets, JSP
- **Database**: MySQL
- **Build Tool**: Maven
- **Frontend**: HTML, CSS, JavaScript
- **Server**: Apache Tomcat (recommended)

## Prerequisites

- Java Development Kit (JDK) 8 or higher
- Apache Maven
- MySQL Server
- Apache Tomcat 9.0 or higher

## Database Setup

1. Create a MySQL database named `dlam_db`.
2. Execute the following SQL script to create the necessary tables:

```sql
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role ENUM('STUDENT', 'INSTRUCTOR') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

3. Configure the database connection in `src/main/java/com/dlam/config/DatabaseConnection.java` (ensure this file exists and has correct credentials).

## Installation & Running

1. **Clone the repository**:
   ```bash
   git clone <repository-url>
   cd "Final Project"
   ```

2. **Build the project**:
   ```bash
   mvn clean package
   ```

3. **Deploy**:
   - Copy the generated `.war` file from the `target/` directory to the `webapps/` directory of your Tomcat installation.
   - OR run with a Maven plugin if configured (e.g., `mvn tomcat7:run`).

4. **Access the application**:
   - Start the Tomcat server.
   - Open your browser and navigate to: `http://localhost:8080/dlam`

## Project Structure

```
src/
├── main/
│   ├── java/com/dlam/
│   │   ├── config/      # Database configuration
│   │   ├── controller/  # Servlets (AuthServlet, etc.)
│   │   ├── dao/         # Data Access Objects (UserDAO)
│   │   └── model/       # Java Beans (User)
│   ├── resources/       # Configuration files
│   └── webapp/          # JSP files, CSS, WEB-INF
│       ├── css/
│       ├── WEB-INF/
│       ├── login.jsp
│       ├── register.jsp
│       └── dashboard.jsp
```
