# Development Environment

This repository is a fresh implementation. Do not use the old application archive or existing databases as a code template.

## Target stack

- Windows 11 x64
- Eclipse Temurin JDK 25.0.4.1
- Apache Maven 3.10.0
- Apache Tomcat 10.1.61
- Jakarta Servlet APIs (Tomcat 10.1 is not compatible with applications using the old `javax.servlet` package unchanged)
- MySQL-compatible SQL targeting MySQL 8.0 and 8.4
- MySQL Connector/J JDBC driver

## Database isolation

The application will use its own database, `college_event_db`. Do not drop, alter, or reuse an existing database such as `college_events`. Database credentials must be configured locally and must not be committed to Git.

## Portability rule

Use only SQL features supported by MySQL 8.0. Test the schema on both MySQL 8.0 and MySQL 8.4 before calling the delivery validated.

## Build and delivery status

Environment versions have been checked on the developer's Windows PC. Tomcat 10.1.61 responds on localhost. The first application implementation is now committed. A GitHub Actions Maven build has been added; local database and browser workflow testing are still required before final delivery.
