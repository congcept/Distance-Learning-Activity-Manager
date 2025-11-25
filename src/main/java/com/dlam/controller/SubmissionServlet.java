package com.dlam.controller;

import com.dlam.dao.SubmissionDAO;
import com.dlam.model.Submission;
import com.dlam.model.User;
import com.dlam.config.DatabaseConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;

@WebServlet(name = "SubmissionServlet", urlPatterns = { "/submit-activity" })
public class SubmissionServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String activityId = req.getParameter("activityId");
        req.setAttribute("activityId", activityId);
        req.getRequestDispatcher("submit-activity.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null || !"STUDENT".equals(user.getRole())) {
            resp.sendRedirect("login.jsp");
            return;
        }

        int activityId = Integer.parseInt(req.getParameter("activityId"));
        String content = req.getParameter("content");

        try (Connection conn = DatabaseConnection.initializeDatabase()) {
            SubmissionDAO dao = new SubmissionDAO(conn);
            Submission submission = new Submission(activityId, user.getId(), content);
            dao.addSubmission(submission);

            // Redirect back to course details (we need courseId, but for now let's go to
            // dashboard or try to get courseId)
            // Ideally we should pass courseId in the form too to redirect back properly.
            String courseId = req.getParameter("courseId");
            if (courseId != null) {
                resp.sendRedirect("activities?courseId=" + courseId);
            } else {
                resp.sendRedirect("courses");
            }

        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
