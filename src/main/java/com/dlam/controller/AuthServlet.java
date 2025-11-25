package com.dlam.controller;

import com.dlam.dao.UserDAO;
import com.dlam.model.User;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "AuthServlet", urlPatterns = { "/login", "/register", "/logout" })
public class AuthServlet extends HttpServlet {
    private UserDAO userDAO;

    public void init() {
        userDAO = new UserDAO();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getServletPath();
        if ("/register".equals(action)) {
            registerUser(request, response);
        } else if ("/login".equals(action)) {
            loginUser(request, response);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getServletPath();
        if ("/logout".equals(action)) {
            HttpSession session = request.getSession();
            session.invalidate();
            response.sendRedirect("login.jsp");
        }
    }

    private void registerUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String fullName = request.getParameter("fullname");
        String password = request.getParameter("password");
        String role = request.getParameter("role");
        User newUser = new User(username, password, fullName, role);
        if (userDAO.registerUser(newUser)) {
            response.sendRedirect("login.jsp?success=registered");
        } else {
            response.sendRedirect("register.jsp?error=failed");
        }
    }

    private void loginUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        User user = userDAO.checkLogin(username, password);
        if (user != null) {
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            response.sendRedirect("courses");
        } else {
            response.sendRedirect("login.jsp?error=invalid");
        }
    }
}
