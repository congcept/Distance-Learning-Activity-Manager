package com.dlam.controller;

import com.dlam.dao.ActivityDAO;
import com.dlam.dao.SubmissionDAO;
import com.dlam.model.Activity;
import com.dlam.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Date;
import java.util.List;

@Controller
public class ActivityController {

    @Autowired
    private ActivityDAO activityDAO;

    @Autowired
    private SubmissionDAO submissionDAO;

    @GetMapping("/activities")
    public String listActivities(@RequestParam("courseId") int courseId,
            HttpSession session,
            Model model) {
        List<Activity> activities = activityDAO.getActivitiesByCourse(courseId);
        model.addAttribute("courseId", courseId);
        model.addAttribute("activities", activities);

        User user = (User) session.getAttribute("user");
        if (user != null && "STUDENT".equals(user.getRole())) {
            List<Integer> submittedActivityIds = submissionDAO.getSubmittedActivityIds(user.getId(), courseId);
            model.addAttribute("submittedActivityIds", submittedActivityIds);
        }

        return "course-details"; // Maps to course-details.jsp
    }

    @GetMapping("/create-activity")
    public String showCreateActivityForm(@RequestParam("courseId") String courseId,
            HttpSession session,
            Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"INSTRUCTOR".equals(user.getRole())) {
            return "redirect:/login";
        }
        model.addAttribute("courseId", courseId);
        return "create-activity"; // Maps to create-activity.jsp
    }

    @PostMapping("/create-activity")
    public String createActivity(@RequestParam("courseId") int courseId,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("dueDate") String dueDate,
            HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"INSTRUCTOR".equals(user.getRole())) {
            return "redirect:/login";
        }

        Date sqlDate = dueDate.isEmpty() ? null : Date.valueOf(dueDate);
        Activity activity = new Activity(courseId, title, description, sqlDate);
        activityDAO.addActivity(activity);

        return "redirect:/activities?courseId=" + courseId;
    }
}
