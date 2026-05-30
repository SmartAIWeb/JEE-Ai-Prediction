package beans;

import java.sql.ResultSet;
import java.sql.SQLException;

public class User {
  int userId;
  String firstName;
  String lastName;
  int age;
  String gender;
  String email;
  String password;
  String role;

  public User() {}
  public User(String firstName, String lastName, int age, String gender, String email, String password) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.age = age;
    this.gender = gender;
    this.email = email;
    this.password = password;
  }

  public User(ResultSet row) throws SQLException {
    this.userId = row.getInt("user_id");
    this.firstName = row.getString("firstName");
    this.lastName = row.getString("lastName");
    this.age = row.getInt("age");
    this.gender = row.getString("gender");
    this.email = row.getString("email");
    this.password = row.getString("password");
    this.role = row.getString("role");
  }

  public int getUserId() { return userId; }
  public String getFirstName() { return firstName; }
  public String getLastName() { return lastName; }
  public int getAge() { return age; }
  public String getGender() { return gender; }
  public String getEmail() { return email; }
  public String getPassword() { return password; }
  public String getRole() { return role; }

  public void setUserId(int userId) { this.userId = userId; }
  public void setFirstName(String value) { this.firstName = value; }
  public void setLastName(String value) { this.lastName = value; }
  public void setAge(int value) { this.age = value; }
  public void setGender(String value) { this.gender = value; }
  public void setEmail(String value) { this.email = value; }
  public void setPassword(String value) { this.password = value; }
  public void setRole(String role) { this.role = role; }
}
