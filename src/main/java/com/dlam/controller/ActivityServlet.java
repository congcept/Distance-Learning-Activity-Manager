package com.dlam.controller;

import com.dlam.dao.ActivityDAO;
import com.dlam.model.Activity;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.List;

@WebServlet(name = "ActivityServlet", urlPatterns = {"/activities", "/create-activity"})
public class ActivityServlet extends HttpServlet {

    private Connection getConn() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/dlam_db?useSSL=false";
        return DriverManager.getConnection(url, "root", "");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String path = req.getServletPath();

        try (Connection conn = getConn()) {
            ActivityDAO dao = new ActivityDAO(conn);

            if ("/activities".equals(path)) {
                int courseId = Integer.parseInt(req.getParameter("courseId"));
                List<Activity> activities = dao.getActivitiesByCourse(courseId);

                req.setAttribute("courseId", courseId);
                req.setAttribute("activities", activities);

                req.getRequestDispatcher("course-details.jsp").forward(req, resp);

            } else if ("/create-activity".equals(path)) {
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

        int courseId = Integer.parseInt(req.getParameter("courseId"));
        String title = req.getParameter("title");
        String description = req.getParameter("description");
        String dueDate = req.getParameter("dueDate");

        try (Connection conn = getConn()) {
            ActivityDAO dao = new ActivityDAO(conn);

            Date sqlDate = dueDate.isEmpty() ? null : Date.valueOf(dueDate);

            Activity a = new Activity(courseId, title, description, sqlDate);
            dao.addActivity(a);

            resp.sendRedirect("activities?courseId=" + courseId);

        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
