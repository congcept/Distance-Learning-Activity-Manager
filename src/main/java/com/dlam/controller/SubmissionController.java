package com.dlam.controller;

import com.dlam.dao.SubmissionDAO;
import com.dlam.model.Submission;
import com.dlam.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class SubmissionController {

    @Autowired
    private SubmissionDAO submissionDAO;

    @GetMapping("/submit-activity")
    public String showSubmitForm(@RequestParam("activityId") String activityId,
            HttpSession session,
            Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"STUDENT".equals(user.getRole())) {
            return "redirect:/login";
        }
        model.addAttribute("activityId", activityId);
        return "submit-activity"; // Maps to submit-activity.jsp
    }

    @PostMapping("/submit-activity")
    public String submitActivity(@RequestParam("activityId") int activityId,
            @RequestParam("content") String content,
            @RequestParam(value = "courseId", required = false) String courseId,
            HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"STUDENT".equals(user.getRole())) {
            return "redirect:/login";
        }

        Submission submission = new Submission(activityId, user.getId(), content);
        submissionDAO.addSubmission(submission);

        if (courseId != null) {
            return "redirect:/activities?courseId=" + courseId;
        } else {
            return "redirect:/courses";
        }
    }

    @GetMapping("/view-my-submissions")
    public String viewMySubmissions(@RequestParam("activityId") int activityId,
            @RequestParam(value = "courseId", required = false) String courseId,
            HttpSession session,
            Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"STUDENT".equals(user.getRole())) {
            return "redirect:/login";
        }

        List<Submission> submissions = submissionDAO.getAllSubmissionsByStudent(activityId, user.getId());
        model.addAttribute("submissions", submissions);
        model.addAttribute("activityId", activityId);
        model.addAttribute("courseId", courseId);
        return "view-my-submissions"; // Maps to view-my-submissions.jsp
    }

    @GetMapping("/view-submissions")
    public String viewSubmissions(@RequestParam("activityId") int activityId,
            @RequestParam(value = "courseId", required = false) String courseId,
            HttpSession session,
            Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"INSTRUCTOR".equals(user.getRole())) {
            return "redirect:/login";
        }

        List<Submission> submissions = submissionDAO.getSubmissionsByActivity(activityId);
        model.addAttribute("submissions", submissions);
        model.addAttribute("activityId", activityId);
        model.addAttribute("courseId", courseId);
        return "view-submissions"; // Maps to view-submissions.jsp
    }

    @PostMapping("/grade-submission")
    public String gradeSubmission(@RequestParam("submissionId") int submissionId,
            @RequestParam("grade") String grade,
            @RequestParam("feedback") String feedback,
            @RequestParam("activityId") String activityId,
            @RequestParam("courseId") String courseId,
            HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"INSTRUCTOR".equals(user.getRole())) {
            return "redirect:/login";
        }

        submissionDAO.updateGrade(submissionId, grade, feedback);
        return "redirect:/view-submissions?activityId=" + activityId + "&courseId=" + courseId;
    }
}
