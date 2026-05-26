CREATE DATABASE IF NOT EXISTS prediction_app_db;
USE prediction_app_db;

CREATE TABLE History (
  history_id INT AUTO_INCREMENT,
  user_id INT,
  input_data JSON,
  prediction_res JSON,
  date DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (history_id)
);

CREATE TABLE Users (
  user_id INT AUTO_INCREMENT,
  history_id INT,
  firstName VARCHAR(100),
  lastName VARCHAR(100),
  age INT,
  gender ENUM('male', 'female'),
  email VARCHAR(100),
  password VARCHAR(255),
  role ENUM('user', 'admin'),
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (user_id),
  FOREIGN KEY (history_id) REFERENCES History(history_id)
);
