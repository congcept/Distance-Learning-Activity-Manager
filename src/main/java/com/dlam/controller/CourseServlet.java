package com.dlam.controller;

import com.dlam.dao.CourseDAO;
import com.dlam.model.Course;
import com.dlam.model.User;
import com.dlam.config.DatabaseConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;

@WebServlet(name = "CourseServlet", urlPatterns = { "/courses", "/create-course" })
public class CourseServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String path = req.getServletPath();

        if ("/create-course".equals(path)) {
            req.getRequestDispatcher("create-course.jsp").forward(req, resp);
            return;
        }

        try (Connection conn = DatabaseConnection.initializeDatabase()) {
            CourseDAO dao = new CourseDAO(conn);
            List<Course> courses = dao.getAllCourses();

            req.setAttribute("courses", courses);
            req.getRequestDispatcher("dashboard.jsp").forward(req, resp);

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

        String title = req.getParameter("title");
        String description = req.getParameter("description");

        try (Connection conn = DatabaseConnection.initializeDatabase()) {
            CourseDAO dao = new CourseDAO(conn);
            Course course = new Course(title, description, user.getId());
            dao.addCourse(course);

            resp.sendRedirect("courses");

        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
