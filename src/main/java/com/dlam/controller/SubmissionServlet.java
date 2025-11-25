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
import java.util.List;

@WebServlet(name = "SubmissionServlet", urlPatterns = { "/submit-activity", "/view-submissions", "/grade-submission" })
public class SubmissionServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String path = req.getServletPath();

        if ("/submit-activity".equals(path)) {
            String activityId = req.getParameter("activityId");
            req.setAttribute("activityId", activityId);
            req.getRequestDispatcher("submit-activity.jsp").forward(req, resp);
        } else if ("/view-submissions".equals(path)) {
            // Instructor viewing submissions
            HttpSession session = req.getSession();
            User user = (User) session.getAttribute("user");
            if (user == null || !"INSTRUCTOR".equals(user.getRole())) {
                resp.sendRedirect("login.jsp");
                return;
            }

            int activityId = Integer.parseInt(req.getParameter("activityId"));
            try (Connection conn = DatabaseConnection.initializeDatabase()) {
                SubmissionDAO dao = new SubmissionDAO(conn);
                List<Submission> submissions = dao.getSubmissionsByActivity(activityId);
                req.setAttribute("submissions", submissions);
                req.setAttribute("activityId", activityId);
                req.setAttribute("courseId", req.getParameter("courseId")); // Pass through
                req.getRequestDispatcher("view-submissions.jsp").forward(req, resp);
            } catch (Exception e) {
                throw new ServletException(e);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");

        String path = req.getServletPath();

        if ("/grade-submission".equals(path)) {
            if (user == null || !"INSTRUCTOR".equals(user.getRole())) {
                resp.sendRedirect("login.jsp");
                return;
            }
            int submissionId = Integer.parseInt(req.getParameter("submissionId"));
            String grade = req.getParameter("grade");
            String feedback = req.getParameter("feedback");
            String activityId = req.getParameter("activityId");
            String courseId = req.getParameter("courseId");

            try (Connection conn = DatabaseConnection.initializeDatabase()) {
                SubmissionDAO dao = new SubmissionDAO(conn);
                dao.updateGrade(submissionId, grade, feedback);
                resp.sendRedirect("view-submissions?activityId=" + activityId + "&courseId=" + courseId);
            } catch (Exception e) {
                throw new ServletException(e);
            }
            return;
        }

        // Student submitting activity
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
