package com.dlam.controller;

import com.dlam.model.Activity;
import com.dlam.model.Course;
import com.dlam.model.Enrollment;
import com.dlam.model.Submission;
import com.dlam.model.User;
import com.dlam.repository.ActivityRepository;
import com.dlam.repository.CourseRepository;
import com.dlam.repository.EnrollmentRepository;
import com.dlam.repository.SubmissionRepository;
import com.dlam.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class GradebookController {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    @GetMapping("/gradebook")
    public String showGradebook(@RequestParam("courseId") int courseId, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"INSTRUCTOR".equals(user.getRole())) {
            return "redirect:/login";
        }

        Course course = courseRepository.findById(courseId).orElse(null);
        if (course == null || course.getInstructorId() != user.getId()) {
            return "redirect:/courses?error=accessDenied";
        }

        // 1. Get all activities for the course
        List<Activity> activities = activityRepository.findByCourseId(courseId);

        // 2. Get all enrolled students
        List<Enrollment> enrollments = enrollmentRepository.findByCourseId(courseId);
        List<Integer> studentIds = enrollments.stream().map(Enrollment::getStudentId).collect(Collectors.toList());
        List<User> students = userRepository.findAllById(studentIds);

        // 3. Build the Grid: Map<StudentId, Map<ActivityId, Submission>>
        // We want to easily look up: "For Student S and Activity A, what is the Grade?"
        Map<Integer, Map<Integer, Submission>> gradeMap = new HashMap<>();

        for (User student : students) {
            Map<Integer, Submission> studentSubmissions = new HashMap<>();
            // Fetch all submissions for this student (inefficient query N+1, but simple for
            // now)
            // Better: Fetch all submissions for the course in one go?
            // Submission doesn't link to Course directly, only Activity.
            // So we can: Fetch all submissions for these activities.

            gradeMap.put(student.getId(), studentSubmissions);
        }

        // Optimization: Fetch all submissions for the activities in this course
        // Since we don't have a custom query for "Submissions where Activity.courseId =
        // X",
        // we can iterate activities or use the existing "findByActivityId" loop.
        // Given small scale, let's just fetch by Activity.

        for (Activity activity : activities) {
            List<Submission> submissions = submissionRepository.findByActivityId(activity.getId());
            for (Submission s : submissions) {
                if (gradeMap.containsKey(s.getStudentId())) {
                    // Store the submission. If multiple, we might want the latest or highest.
                    // Current logic often assumes latest.
                    gradeMap.get(s.getStudentId()).put(activity.getId(), s);
                }
            }
        }

        model.addAttribute("course", course);
        model.addAttribute("activities", activities);
        model.addAttribute("students", students);
        model.addAttribute("gradeMap", gradeMap);

        return "gradebook";
    }
}
