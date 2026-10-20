# College Event Management System

A fresh Java web application for campus event discovery and registration, built with Java, Jakarta Servlets/JSP, Maven, Tomcat 10.1.61, JDBC, and MySQL.

## Implemented in this repository

- Student account creation and sign-in
- Administrator sign-in with an initial admin account created from local environment variables
- Event catalogue with dates, venues, categories, registration counts, and capacity
- Student event registration with duplicate-registration protection
- Admin event creation and attendee lists
- Isolated database schema named `college_event_db`
- Responsive UI for student and admin views
- Windows setup instructions

## Environment

- Eclipse Temurin JDK 25.0.4.1 for development
- Apache Maven 3.10.0
- Apache Tomcat 10.1.61
- MySQL 8.0-compatible schema, intended to run on MySQL 8.4 too
- Jakarta Servlet 6.0 / JSP, not the old `javax.servlet` namespace

## Start here

Read [SETUP.md](SETUP.md) for database initialization, environment variables, building, deploying, and first-run steps. The database script creates `college_event_db` only and does not drop or alter any other database.

## Build and test status

The implementation has been committed to GitHub. It has **not yet been confirmed by a clean Maven build or end-to-end local testing**. Treat it as an implementation in progress until the build, login, admin event creation, student registration, and admin participant report have been tested on Tomcat and MySQL.
