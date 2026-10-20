CREATE DATABASE IF NOT EXISTS college_event_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE college_event_db;

CREATE TABLE IF NOT EXISTS users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  full_name VARCHAR(120) NOT NULL,
  email VARCHAR(190) NOT NULL UNIQUE,
  student_id VARCHAR(80) NULL UNIQUE,
  department VARCHAR(120) NULL,
  password_hash VARCHAR(255) NOT NULL,
  password_salt VARCHAR(255) NOT NULL,
  role ENUM('ADMIN','STUDENT') NOT NULL DEFAULT 'STUDENT',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS events (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(180) NOT NULL,
  category VARCHAR(80) NOT NULL,
  description TEXT NOT NULL,
  venue VARCHAR(180) NOT NULL,
  event_date DATETIME NOT NULL,
  registration_deadline DATETIME NULL,
  capacity INT NULL,
  created_by BIGINT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_events_creator FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,
  INDEX idx_events_date (event_date)
);

CREATE TABLE IF NOT EXISTS registrations (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  event_id BIGINT NOT NULL,
  student_user_id BIGINT NOT NULL,
  full_name VARCHAR(120) NOT NULL,
  email VARCHAR(190) NOT NULL,
  student_id VARCHAR(80) NOT NULL,
  department VARCHAR(120) NOT NULL,
  registered_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_reg_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
  CONSTRAINT fk_reg_student FOREIGN KEY (student_user_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT uq_event_student UNIQUE (event_id, student_user_id),
  INDEX idx_reg_event (event_id)
);
