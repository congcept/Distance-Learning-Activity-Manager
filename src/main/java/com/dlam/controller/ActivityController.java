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
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file,
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

        if (!file.isEmpty()) {
            try {
                // Define upload directory (Project Root/uploads)
                String uploadDir = "uploads/";
                java.io.File directory = new java.io.File(uploadDir);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                // Generate unique filename
                String originalFilename = file.getOriginalFilename();
                String uniqueFilename = java.util.UUID.randomUUID().toString() + "_" + originalFilename;
                String filePath = uploadDir + uniqueFilename;

                // Save file
                file.transferTo(new java.io.File(new java.io.File(filePath).getAbsolutePath()));

                // Set file info in activity
                activity.setFilePath("/uploads/" + uniqueFilename);
                activity.setOriginalFilename(originalFilename);
            } catch (java.io.IOException e) {
                e.printStackTrace();
                return "redirect:/create-activity?courseId=" + courseId + "&error=uploadFailed";
            }
        }

        activityRepository.save(activity);

        return "redirect:/activities?courseId=" + courseId;
    }

    @GetMapping("/edit-activity")
    public String showEditActivityForm(@RequestParam("activityId") int activityId,
            @RequestParam("courseId") int courseId,
            HttpSession session,
            Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"INSTRUCTOR".equals(user.getRole())) {
            return "redirect:/login";
        }

        // Check ownership
        com.dlam.model.Course course = courseRepository.findById(courseId).orElse(null);
        if (course == null || course.getInstructorId() != user.getId()) {
            return "redirect:/courses?error=accessDenied";
        }

        Activity activity = activityRepository.findById(activityId).orElse(null);
        if (activity == null || activity.getCourseId() != courseId) {
            return "redirect:/activities?courseId=" + courseId + "&error=notFound";
        }

        model.addAttribute("activity", activity);
        model.addAttribute("courseId", courseId);
        return "edit-activity";
    }

    @PostMapping("/edit-activity")
    public String editActivity(@RequestParam("activityId") int activityId,
            @RequestParam("courseId") int courseId,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("dueDate") String dueDate,
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file,
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

        Activity activity = activityRepository.findById(activityId).orElse(null);
        if (activity == null || activity.getCourseId() != courseId) {
            return "redirect:/activities?courseId=" + courseId + "&error=notFound";
        }

        activity.setTitle(title);
        activity.setDescription(description);
        activity.setDueDate(dueDate.isEmpty() ? null : Date.valueOf(dueDate));

        if (!file.isEmpty()) {
            try {
                // Define upload directory (Project Root/uploads)
                String uploadDir = "uploads/";
                java.io.File directory = new java.io.File(uploadDir);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                // Generate unique filename
                String originalFilename = file.getOriginalFilename();
                String uniqueFilename = java.util.UUID.randomUUID().toString() + "_" + originalFilename;
                String filePath = uploadDir + uniqueFilename;

                // Save file
                file.transferTo(new java.io.File(new java.io.File(filePath).getAbsolutePath()));

                // Update file info in activity
                activity.setFilePath("/uploads/" + uniqueFilename);
                activity.setOriginalFilename(originalFilename);
            } catch (java.io.IOException e) {
                e.printStackTrace();
                return "redirect:/edit-activity?activityId=" + activityId + "&courseId=" + courseId
                        + "&error=uploadFailed";
            }
        }

        activityRepository.save(activity);

        return "redirect:/activities?courseId=" + courseId;
    }
}
