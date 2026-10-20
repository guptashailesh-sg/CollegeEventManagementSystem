# Windows Setup and Run Guide

This is a fresh app. It does not read, modify, or delete any other database. It uses only `college_event_db`.

## Requirements

- JDK 25 (JDK 17+ is sufficient for the compiled Java 17 bytecode, but JDK 25 is the agreed development environment)
- Maven 3.10.0
- Tomcat 10.1.61
- MySQL Server 8.0 or 8.4

## 1. Create the app database

Open the MySQL command-line client and run the SQL in `database/schema.sql`. It creates and selects `college_event_db` only. It does not drop or change your other databases.

## 2. Configure connection details

Set environment variables in the same Windows Command Prompt before starting Tomcat. Replace the example values with your local MySQL credentials:

```bat
set "CE_DB_URL=jdbc:mysql://localhost:3306/college_event_db?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Kolkata"
set "CE_DB_USER=root"
set "CE_DB_PASSWORD=YOUR_MYSQL_PASSWORD"
set "CE_ADMIN_EMAIL=admin@college.local"
set "CE_ADMIN_PASSWORD=Choose-A-Unique-Password-At-Least-12-Chars"
```

Use a private password. Do not commit passwords or a file containing real credentials to Git. The initial admin account is created on application startup if that email does not already exist. If the admin email already exists as a student account, use a different unused email; the bootstrap will not silently elevate an existing account.

## 3. Build the WAR

From the project root:

```bat
mvn clean package
```

The expected artifact is `target\college-event-management.war`.

## 4. Deploy to Tomcat

Copy the WAR into Tomcat 10.1.61's `webapps` folder, start Tomcat with `bin\startup.bat`, and open:

`http://localhost:8080/college-event-management/`

Keep the Command Prompt that contains the environment variables open while starting Tomcat. If you start Tomcat as a Windows service, configure those variables for that service separately.

## 5. Use the application

1. Sign in with the configured admin email and password.
2. Create an event in the admin dashboard.
3. Sign out and create a student account.
4. Sign in as a student and register for the event.
5. Sign back in as admin and use **View list** to inspect registration data.

## Troubleshooting

- **Database connection failed:** verify the MySQL service is running, database schema was imported, and the three `CE_DB_*` variables are correct.
- **Admin account wasn't created:** confirm the schema exists, set `CE_ADMIN_EMAIL` and a password of at least 12 characters, then restart the web application.
- **404:** confirm the WAR filename and deployed context path are `college-event-management`.
- **Port 8080 is occupied:** stop the other Tomcat process or configure this Tomcat instance to use a different port.
- **Java/Jakarta class errors:** use Tomcat 10.1.61 and do not replace Jakarta Servlet dependencies with `javax.servlet` dependencies.

## Validation status

The source has been committed to GitHub, but a clean Maven build, database integration test, and end-to-end browser test still need to be run before calling the package fully debugged.
