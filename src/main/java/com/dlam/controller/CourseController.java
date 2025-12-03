package com.dlam.controller;

import com.dlam.model.Course;
import com.dlam.model.User;
import com.dlam.repository.CourseRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class CourseController {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private com.dlam.repository.EnrollmentRepository enrollmentRepository;

    @GetMapping("/courses")
    public String listCourses(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        List<Course> allCourses = courseRepository.findAll();

        if (user != null && "STUDENT".equals(user.getRole())) {
            List<com.dlam.model.Enrollment> enrollments = enrollmentRepository.findByStudentId(user.getId());
            List<Integer> enrolledCourseIds = enrollments.stream()
                    .map(com.dlam.model.Enrollment::getCourseId)
                    .collect(java.util.stream.Collectors.toList());

            List<Course> myCourses = allCourses.stream()
                    .filter(c -> enrolledCourseIds.contains(c.getId()))
                    .collect(java.util.stream.Collectors.toList());

            List<Course> availableCourses = allCourses.stream()
                    .filter(c -> !enrolledCourseIds.contains(c.getId()))
                    .collect(java.util.stream.Collectors.toList());

            model.addAttribute("myCourses", myCourses);
            model.addAttribute("availableCourses", availableCourses);
            model.addAttribute("myCourses", myCourses);
            model.addAttribute("availableCourses", availableCourses);
        } else if (user != null && "INSTRUCTOR".equals(user.getRole())) {
            // Instructors only see courses they created
            List<Course> myCourses = courseRepository.findByInstructorId(user.getId());
            model.addAttribute("myCourses", myCourses);
            model.addAttribute("availableCourses", java.util.Collections.emptyList());
        } else {
            // Fallback for other roles (if any)
            model.addAttribute("myCourses", java.util.Collections.emptyList());
            model.addAttribute("availableCourses", java.util.Collections.emptyList());
        }

        return "dashboard";
    }

    @PostMapping("/enroll")
    public String enroll(@RequestParam("courseId") int courseId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"STUDENT".equals(user.getRole())) {
            return "redirect:/login";
        }

        if (!enrollmentRepository.existsByStudentIdAndCourseId(user.getId(), courseId)) {
            com.dlam.model.Enrollment enrollment = new com.dlam.model.Enrollment(user.getId(), courseId);
            enrollmentRepository.save(enrollment);
        }

        return "redirect:/courses";
    }

    @GetMapping("/create-course")
    public String showCreateCourseForm() {
        return "create-course";
    }

    @PostMapping("/create-course")
    public String createCourse(@RequestParam("title") String title,
            @RequestParam("description") String description,
            HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"INSTRUCTOR".equals(user.getRole())) {
            return "redirect:/login";
        }

        Course course = new Course(title, description, user.getId());
        courseRepository.save(course);
        return "redirect:/courses";
    }
}
