package db;

import beans.User;
import beans.Prediction;

import org.mindrot.jbcrypt.BCrypt;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.*;

import java.util.ArrayList;
public class DAOClass {
  String dbHost = System.getenv("DB_HOST");
  String dbName = System.getenv("DB_NAME");
  String dbPort = System.getenv("DB_PORT");
  String dbUser = System.getenv("DB_USER");
  String dbPassword = System.getenv("DB_PASSWORD");
  Connection connection = null;

  public DAOClass() 
      throws SQLException, ClassNotFoundException {
    Class.forName("org.mariadb.jdbc.Driver");
    String dbUrl = String.format("jdbc:mariadb://%s:%s/%s", dbHost, dbPort, dbName);
    connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
  }

  ResultSet findUserByEmail(User targetInfo) 
      throws SQLException{
    String sql = "SELECT * FROM Users WHERE email=?";
    PreparedStatement stmt = connection.prepareStatement(sql);
    stmt.setString(1, targetInfo.getEmail());
    return stmt.executeQuery();
  }

  boolean insertNewUser(User newUser)
      throws SQLException {
    String sql =  
      "INSERT INTO Users (firstName, lastName, age, gender, email, password) " +
      "VALUES (?, ?, ?, ?, ?, ?)";
    try(PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setString(1, newUser.getFirstName());
      stmt.setString(2, newUser.getLastName());
      stmt.setInt(3, newUser.getAge());
      stmt.setString(4, newUser.getGender());
      stmt.setString(5, newUser.getEmail());
      stmt.setString(6, BCrypt.hashpw(newUser.getPassword(), BCrypt.gensalt()));
      return stmt.executeUpdate() != 0;
    }
  }

  public User validateLogin(User targetInfo)
      throws SQLException {
    ResultSet row = findUserByEmail(targetInfo);
    try {
      if(row.next()) {
        User fetchedUser = new User(row);
        if(BCrypt.checkpw(targetInfo.getPassword(), fetchedUser.getPassword())) {
          return fetchedUser;
        }
      }
      return null;
    } finally {
      row.getStatement().close();
    }
  }

  public User registerNewUser(User targetInfo)
      throws SQLException {
    ResultSet row = findUserByEmail(targetInfo);
    try {
      if(!row.next()) {
        if(insertNewUser(targetInfo))
          return targetInfo;
      }
      return null;
    } finally {
      row.getStatement().close();
    }
  }

  public boolean updateUserInfo(User targetInfo) 
      throws SQLException {
    String sql =
      "UPDATE Users SET firstName = ?, lastName = ?, age = ?, gender = ? " +
      "WHERE email = ?";
    try(PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setString(1, targetInfo.getFirstName());
      stmt.setString(2, targetInfo.getLastName());
      stmt.setInt(3, targetInfo.getAge());
      stmt.setString(4, targetInfo.getGender());
      stmt.setString(5, targetInfo.getEmail());
      return stmt.executeUpdate() != 0;
    }
  }
  public ArrayList<Prediction> getUserHistory(int userId) throws SQLException {
    ArrayList<Prediction> historyList = new ArrayList<>();
    String sql = "SELECT * FROM History WHERE user_id = ? ORDER BY date DESC";
    try{
      PreparedStatement stmt = connection.prepareStatement(sql);
      stmt.setInt(1, userId);
      try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          Prediction pre = new Prediction();
          pre.setHistoryId(rs.getInt("history_id"));
          pre.setUserId(rs.getInt("user_id"));
          pre.setInputData(rs.getString("input_data"));
          pre.setPredictionRes(rs.getString("prediction_res"));
          pre.setDate(rs.getString("date"));
          historyList.add(pre);
        }
      }
    }
    return historyList; 
  }
  public ArrayList<User> getAllUsers() throws SQLException {
    ArrayList<User> allusers = new ArrayList<>();
    String sql = "SELECT * FROM Users ORDER BY created_at DESC";
    try {
      Statement stmt = connection.createStatement();
      try (ResultSet rs = stmt.executeQuery(sql);) {
        while (rs.next()) {
          User user = new User();
          user.setUserId(rs.getInt("user_id"));
          user.setFirstName(rs.getString("firstName"));
          user.setLastName(rs.getString("lastName"));
          user.setAge(rs.getInt("age"));
          user.setGender(rs.getString("gender"));
          user.setEmail(rs.getString("email"));
          allusers.add(user);
        }
      }
    }
    return allusers ; 
  }

}
