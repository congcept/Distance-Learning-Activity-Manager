package com.dlam.controller;

import com.dlam.model.Activity;
import com.dlam.model.Submission;
import com.dlam.model.User;
import com.dlam.repository.ActivityRepository;
import com.dlam.repository.SubmissionRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ActivityController {

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private com.dlam.repository.EnrollmentRepository enrollmentRepository;

    @Autowired
    private com.dlam.repository.CourseRepository courseRepository;

    @GetMapping("/activities")
    public String listActivities(@RequestParam("courseId") int courseId,
            HttpSession session,
            Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        if ("STUDENT".equals(user.getRole())) {
            if (!enrollmentRepository.existsByStudentIdAndCourseId(user.getId(), courseId)) {
                return "redirect:/courses?error=notEnrolled";
            }
        } else if ("INSTRUCTOR".equals(user.getRole())) {
            // Check if instructor owns the course
            com.dlam.model.Course course = courseRepository.findById(courseId).orElse(null);
            if (course == null || course.getInstructorId() != user.getId()) {
                return "redirect:/courses?error=accessDenied";
            }
        }

        List<Activity> activities = activityRepository.findByCourseId(courseId);
        model.addAttribute("courseId", courseId);
        model.addAttribute("activities", activities);

        if ("STUDENT".equals(user.getRole())) {
            List<Submission> submissions = submissionRepository.findByStudentId(user.getId());
            List<Integer> submittedActivityIds = submissions.stream()
                    .filter(s -> activities.stream().anyMatch(a -> a.getId() == s.getActivityId()))
                    .map(Submission::getActivityId)
                    .collect(Collectors.toList());
            model.addAttribute("submittedActivityIds", submittedActivityIds);
        }

        return "course-details";
    }

    @GetMapping("/create-activity")
    public String showCreateActivityForm(@RequestParam("courseId") String courseId,
            HttpSession session,
            Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"INSTRUCTOR".equals(user.getRole())) {
            return "redirect:/login";
        }

        // Check ownership
        int cId = Integer.parseInt(courseId);
        com.dlam.model.Course course = courseRepository.findById(cId).orElse(null);
        if (course == null || course.getInstructorId() != user.getId()) {
            return "redirect:/courses?error=accessDenied";
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

        // Check ownership
        com.dlam.model.Course course = courseRepository.findById(courseId).orElse(null);
        if (course == null || course.getInstructorId() != user.getId()) {
            return "redirect:/courses?error=accessDenied";
        }

        Date sqlDate = dueDate.isEmpty() ? null : Date.valueOf(dueDate);
        Activity activity = new Activity(courseId, title, description, sqlDate);
        activityRepository.save(activity);

        return "redirect:/activities?courseId=" + courseId;
    }
}
