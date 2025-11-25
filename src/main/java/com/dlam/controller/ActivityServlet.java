package com.dlam.controller;

import com.dlam.dao.ActivityDAO;
import com.dlam.model.Activity;
import com.dlam.model.User;
import com.dlam.config.DatabaseConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.List;

@WebServlet(name = "ActivityServlet", urlPatterns = { "/activities", "/create-activity" })
public class ActivityServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String path = req.getServletPath();

        try (Connection conn = DatabaseConnection.initializeDatabase()) {
            ActivityDAO dao = new ActivityDAO(conn);

            if ("/activities".equals(path)) {
                int courseId = Integer.parseInt(req.getParameter("courseId"));
                List<Activity> activities = dao.getActivitiesByCourse(courseId);

                req.setAttribute("courseId", courseId);
                req.setAttribute("activities", activities);

                req.getRequestDispatcher("course-details.jsp").forward(req, resp);

            } else if ("/create-activity".equals(path)) {
                HttpSession session = req.getSession();
                User user = (User) session.getAttribute("user");
                if (user == null || !"INSTRUCTOR".equals(user.getRole())) {
                    resp.sendRedirect("login.jsp");
                    return;
                }
                req.setAttribute("courseId", req.getParameter("courseId"));
                req.getRequestDispatcher("create-activity.jsp").forward(req, resp);
            }

        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null || !"INSTRUCTOR".equals(user.getRole())) {
            resp.sendRedirect("login.jsp");
            return;
        }

        int courseId = Integer.parseInt(req.getParameter("courseId"));
        String title = req.getParameter("title");
        String description = req.getParameter("description");
        String dueDate = req.getParameter("dueDate");

        try (Connection conn = DatabaseConnection.initializeDatabase()) {
            ActivityDAO dao = new ActivityDAO(conn);

            Date sqlDate = dueDate.isEmpty() ? null : Date.valueOf(dueDate);

            Activity a = new Activity(courseId, title, description, sqlDate);
            dao.addActivity(a);

            resp.sendRedirect("activities?courseId=" + courseId);

        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
