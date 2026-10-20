# College Event Management System

A fresh Java web application for managing college events. The target stack is Java 25, Jakarta Servlets/JSP, Maven, Tomcat 10.1.61, JDBC, and MySQL.

> **Implementation status:** Project environment and repository setup are established. The full application is not yet implemented or tested. Do not treat this README as evidence that the final application is ready.

## Target environment

- Eclipse Temurin JDK 25.0.4.1
- Apache Maven 3.10.0
- Apache Tomcat 10.1.61
- MySQL 8.0-compatible SQL (should also work on MySQL 8.4)
- Jakarta Servlet APIs (not the older `javax.servlet` package)

See [docs/ENVIRONMENT.md](docs/ENVIRONMENT.md).

## Database safety

The application will use a new database named `college_event_db`. Existing databases, including any database named `college_events`, must remain untouched.

1. Create a local configuration from `config/db.properties.example`.
2. Enter your own MySQL username and password in the local file.
3. Do not commit real credentials to Git.
4. The database setup script will create/use only `college_event_db`.

## Planned delivery

The final package is intended to contain Maven source code, a deployable WAR, database initialization SQL, Windows setup/run scripts, a configuration template, a setup guide, and a test checklist.

## Current verification

- JDK 25.0.4.1 and its compiler run on the developer PC.
- Maven 3.10.0 reports Java 25 when invoked directly.
- Tomcat 10.1.61 has been started and its welcome page was displayed.
- The MySQL service runs on the developer PC. Database integration testing remains to be done.

Application features and acceptance tests will be finalized against the assignment brief before implementation is considered complete.
