package com.dlam.controller;

import com.dlam.dao.CourseDAO;
import com.dlam.model.Course;
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
public class CourseController {

    @Autowired
    private CourseDAO courseDAO;

    @GetMapping("/courses")
    public String listCourses(Model model) {
        List<Course> courses = courseDAO.getAllCourses();
        model.addAttribute("courses", courses);
        return "dashboard"; // Maps to dashboard.jsp
    }

    @GetMapping("/create-course")
    public String showCreateCourseForm() {
        return "create-course"; // Maps to create-course.jsp
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
        courseDAO.addCourse(course);
        return "redirect:/courses";
    }
}
