package com.dlam.controller;

import com.dlam.model.Announcement;
import com.dlam.model.User;
import com.dlam.repository.AnnouncementRepository;
import com.dlam.repository.CourseRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

@Controller
public class AnnouncementController {

    @Autowired
    private AnnouncementRepository announcementRepository;

    @Autowired
    private CourseRepository courseRepository;

    @GetMapping("/create-announcement")
    public String showCreateAnnouncementForm(@RequestParam("courseId") int courseId, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"INSTRUCTOR".equals(user.getRole())) {
            return "redirect:/login";
        }

        com.dlam.model.Course course = courseRepository.findById(courseId).orElse(null);
        if (course == null || course.getInstructorId() != user.getId()) {
            return "redirect:/courses?error=accessDenied";
        }

        model.addAttribute("courseId", courseId);
        return "create-announcement";
    }

    @PostMapping("/create-announcement")
    public String createAnnouncement(@RequestParam("courseId") int courseId,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"INSTRUCTOR".equals(user.getRole())) {
            return "redirect:/login";
        }

        com.dlam.model.Course course = courseRepository.findById(courseId).orElse(null);
        if (course == null || course.getInstructorId() != user.getId()) {
            return "redirect:/courses?error=accessDenied";
        }

        Announcement announcement = new Announcement(courseId, title, content, LocalDateTime.now());
        announcementRepository.save(announcement);

        return "redirect:/activities?courseId=" + courseId;
    }

    @PostMapping("/delete-announcement")
    public String deleteAnnouncement(@RequestParam("announcementId") int announcementId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"INSTRUCTOR".equals(user.getRole())) {
            return "redirect:/login";
        }

        Announcement announcement = announcementRepository.findById(announcementId).orElse(null);
        if (announcement != null) {
            com.dlam.model.Course course = courseRepository.findById(announcement.getCourseId()).orElse(null);
            if (course != null && course.getInstructorId() == user.getId()) {
                announcementRepository.delete(announcement);
                return "redirect:/activities?courseId=" + course.getId();
            }
        }

        return "redirect:/courses";
    }
}
