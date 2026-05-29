package servlets;

import db.DAOClass;
import beans.User;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.RequestDispatcher;

import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;
import java.io.IOException;

@WebServlet("/controller")
public class ControllerServlet extends HttpServlet {
  DAOClass db = null;
  ConcurrentHashMap<String, User> loggedInUsers = new ConcurrentHashMap<>();

  @Override
  public void init() throws ServletException {
    try {
      db = new DAOClass();
    } catch(SQLException | ClassNotFoundException e) {
      throw new ServletException("DB init failed", e);
    }
  }

  public void doGet(HttpServletRequest req, HttpServletResponse res) 
      throws ServletException, IOException {

    RequestDispatcher rd = null;
    String requestType = req.getParameter("request_type");

    try {
      if("register".equals(requestType))
        rd = handleRegistering(req, res);
      else if("login".equals(requestType))
        rd = handleLogin(req, res);
      else if("profile".equals(requestType)) {
        
      } else if("predict".equals(requestType)) {

      } else if("history".equals(requestType)) {

      } else if("admin".equals(requestType)) {

      } else {
        rd = req.getRequestDispatcher("/JSP/error.jsp");
        req.setAttribute("error_msg", "Unsupported Request Type");
      }
      
    } catch(SQLException e) {
      throw new ServletException("DB quering failed", e);
    }
    if(rd != null)
      rd.forward(req, res);
  }

  RequestDispatcher handleRegistering(HttpServletRequest req , HttpServletResponse res) 
      throws SQLException {
    User newUser = parseUserInfo(req);
    newUser = db.registerNewUser(newUser);
    if(newUser != null) {
      newUser.setPassword(null);
      String sessionToken = generateSessionToken();
      loggedInUsers.put(sessionToken, newUser);
      res.addCookie(createSessionTokenCookie(sessionToken));
      req.setAttribute("user_info", newUser);
    } else {
      req.setAttribute("error_msg", "Email already linked to an account");
    }
    return req.getRequestDispatcher("/JSP/register.jsp");
  }

  RequestDispatcher handleLogin(HttpServletRequest req, HttpServletResponse res) 
      throws SQLException {
    User candidateUser = parseUserInfo(req); 
    candidateUser = db.validateLogin(candidateUser);
    if(candidateUser != null) {
      candidateUser.setPassword(null);
      String sessionToken = generateSessionToken();
      loggedInUsers.put(sessionToken, candidateUser);
      res.addCookie(createSessionTokenCookie(sessionToken));
      req.setAttribute("user_info", candidateUser);
    } else {
      req.setAttribute("error_msg", "Invalid Credentials");
    }
    return req.getRequestDispatcher("/JSP/login.jsp");
  }

  Cookie createSessionTokenCookie(String sessionToken) {
    Cookie sessionCookie = new Cookie("session_token", sessionToken);
    sessionCookie.setHttpOnly(true);
    return sessionCookie;
  }

  User parseUserInfo(HttpServletRequest req) {
    User user = new User();
    user.setFirstName(req.getParameter("firstName"));
    user.setLastName(req.getParameter("lastName"));
    String ageParam = req.getParameter("age");
    if (ageParam != null && !ageParam.isEmpty()) {
        user.setAge(Integer.parseInt(ageParam));
    }
    user.setGender(req.getParameter("gender"));
    user.setEmail(req.getParameter("email"));
    user.setPassword(req.getParameter("password"));
    return user;
  }

  static SecureRandom sr = new SecureRandom();
  String generateSessionToken() {
    byte[] randomBytes = new byte[24];
    sr.nextBytes(randomBytes);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
  }
}
