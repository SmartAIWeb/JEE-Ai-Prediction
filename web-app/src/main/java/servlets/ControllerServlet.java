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
import java.net.*;
import java.util.*;


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
        rd = handleProfile(req,res);
      } else if("predict".equals(requestType)) {
        rd = handlePrediction(req, res);
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
      return req.getRequestDispatcher("/JSP/profile.jsp");
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
      return req.getRequestDispatcher("/JSP/profile.jsp");

    } else {
      req.setAttribute("error_msg", "Invalid Credentials");
    }
    return req.getRequestDispatcher("/JSP/login.jsp");
  }

  RequestDispatcher handleProfile(HttpServletRequest req, HttpServletResponse res) 
      throws SQLException {
    Cookie[] cookies =req.getCookies();
    String userToken=null;

    if(cookies != null){
      for(Cookie c : cookies){
        if("session_token".equals(c.getName())){
          userToken = c.getValue();
          break;
        }
      }
    }
    if (userToken!=null && loggedInUsers.containsKey(userToken)){
      User currentUser = loggedInUsers.get(userToken);
      req.setAttribute("user_info" , currentUser);

      return req.getRequestDispatcher("/JSP/profile.jsp");
    }else{
      req.setAttribute("error_msg", "Access denied , please log in first");
      return req.getRequestDispatcher("/JSP/login.jsp"); 
    }
}

RequestDispatcher handlePrediction(HttpServletRequest req , HttpServletResponse res) 
      throws SQLException {
    Cookie[] cookies =req.getCookies();
    String userToken=null;

    if(cookies != null){
      for(Cookie c : cookies){
        if("session_token".equals(c.getName())){
          userToken = c.getValue();
          break;
        }
      }
    }
    if (userToken!=null && loggedInUsers.containsKey(userToken)){
      User currentUser = loggedInUsers.get(userToken);
      req.setAttribute("user_info" , currentUser);
      try{
        String jsonPayload = req.getParameter("json_data");

        URL url = new URL("http://localhost:5000/predict");
        HttpURLConnection conn= (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        // Envoi du json
        conn.getOutputStream().write(jsonPayload.getBytes("UTF-8"));

        Scanner sc = new Scanner(conn.getInputStream()).useDelimiter("\\A");
        String predictionresult =sc.hasNext() ? sc.next() : "";
        sc.close();
        req.setAttribute("prediction_result",predictionresult);
        conn.disconnect();
      }catch (Exception e) {
          req.setAttribute("error_msg", "Error with the Flask API: " + e.getMessage());
      }
      return req.getRequestDispatcher("/JSP/predict.jsp");
    }else{
      req.setAttribute("error_msg", "Access denied , please log in first");
      return req.getRequestDispatcher("/JSP/login.jsp"); 
    }
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
