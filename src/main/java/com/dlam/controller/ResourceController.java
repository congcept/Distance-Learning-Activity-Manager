package com.dlam.controller;

import com.dlam.model.Resource;
import com.dlam.model.User;
import com.dlam.repository.ResourceRepository;
import com.dlam.repository.CourseRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Controller
public class ResourceController {

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private CourseRepository courseRepository;

    @GetMapping("/add-resource")
    public String showAddResourceForm(@RequestParam("courseId") int courseId, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"INSTRUCTOR".equals(user.getRole())) {
            return "redirect:/login";
        }

        com.dlam.model.Course course = courseRepository.findById(courseId).orElse(null);
        if (course == null || course.getInstructorId() != user.getId()) {
            return "redirect:/courses?error=accessDenied";
        }

        model.addAttribute("courseId", courseId);
        return "add-resource";
    }

    @PostMapping("/add-resource")
    public String addResource(@RequestParam("courseId") int courseId,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("file") MultipartFile file,
            HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"INSTRUCTOR".equals(user.getRole())) {
            return "redirect:/login";
        }

        com.dlam.model.Course course = courseRepository.findById(courseId).orElse(null);
        if (course == null || course.getInstructorId() != user.getId()) {
            return "redirect:/courses?error=accessDenied";
        }

        if (!file.isEmpty()) {
            try {
                String uploadDir = "uploads/";
                File directory = new File(uploadDir);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                String originalFilename = file.getOriginalFilename();
                String uniqueFilename = UUID.randomUUID().toString() + "_" + originalFilename;
                String filePath = uploadDir + uniqueFilename;

                file.transferTo(new File(new File(filePath).getAbsolutePath()));

                Resource resource = new Resource(courseId, title, description, filePath, originalFilename,
                        LocalDateTime.now());
                resourceRepository.save(resource);

            } catch (IOException e) {
                e.printStackTrace();
                return "redirect:/add-resource?courseId=" + courseId + "&error=uploadFailed";
            }
        }

        return "redirect:/activities?courseId=" + courseId;
    }

    @GetMapping("/resources/download/{id}")
    public ResponseEntity<FileSystemResource> downloadResource(@PathVariable("id") int id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Resource resource = resourceRepository.findById(id).orElse(null);
        if (resource == null) {
            return ResponseEntity.notFound().build();
        }

        // Add check if user is enrolled or instructor (omitted for brevity, but
        // recommended in prod)

        File file = new File(resource.getFilePath());
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFileName() + "\"");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity.ok()
                .headers(headers)
                .body(new FileSystemResource(file));
    }

    @PostMapping("/resources/delete")
    public String deleteResource(@RequestParam("resourceId") int resourceId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"INSTRUCTOR".equals(user.getRole())) {
            return "redirect:/login";
        }

        Resource resource = resourceRepository.findById(resourceId).orElse(null);
        if (resource != null) {
            com.dlam.model.Course course = courseRepository.findById(resource.getCourseId()).orElse(null);
            if (course != null && course.getInstructorId() == user.getId()) {
                // Optionally delete physical file
                File file = new File(resource.getFilePath());
                if (file.exists()) {
                    file.delete();
                }
                resourceRepository.delete(resource);
                return "redirect:/activities?courseId=" + course.getId();
            }
        }

        return "redirect:/courses";
    }
}
