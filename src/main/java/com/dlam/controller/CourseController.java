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
    public String listCourses(@RequestParam(value = "search", required = false) String search, HttpSession session,
            Model model) {
        User user = null;
        try {
            user = (User) session.getAttribute("user");
        } catch (Exception e) {
            session.invalidate();
            return "redirect:/login";
        }

        List<Course> allCourses;
        if (search != null && !search.isEmpty()) {
            allCourses = courseRepository.findByTitleContainingIgnoreCase(search);
            model.addAttribute("search", search);
        } else {
            allCourses = courseRepository.findAll();
        }
        if (allCourses == null) {
            allCourses = java.util.Collections.emptyList();
        }

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

            // If searching, show result in available (or both? stick to split for now)
            // But if I search "Math", and I am enrolled in "Math 101", it should be in
            // myCourses.
            // If I am not enrolled, it should be in availableCourses.
            // The filtering logic above handles this correctly regardless of how allCourses
            // is sourced.

            model.addAttribute("myCourses", myCourses);
            model.addAttribute("availableCourses", availableCourses);

            // Widget: Due Soon (Next 7 days)
            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            java.time.LocalDateTime nextWeek = now.plusDays(7);

            // Check if we have enrolled courses before querying
            List<com.dlam.model.Activity> upcoming = java.util.Collections.emptyList();
            if (!enrolledCourseIds.isEmpty()) {
                upcoming = activityRepository.findByCourseIdInAndDueDateBetweenOrderByDueDateAsc(enrolledCourseIds, now,
                        nextWeek);
            }

            model.addAttribute("upcomingActivities", upcoming);
        } else if (user != null && "INSTRUCTOR".equals(user.getRole())) {
            // Instructors only see courses they created
            List<Course> myCourses;
            if (search != null && !search.isEmpty()) {
                // Filter instructor's courses by search
                // Efficient way: findByInstructorIdAndTitleContaining? No such method yet.
                // Just filter in memory for now or update repo.
                // Repo update is better but I will do in memory for simplicity as I just added
                // generic search.
                List<Course> instructorCourses = courseRepository.findByInstructorId(user.getId());
                myCourses = instructorCourses.stream()
                        .filter(c -> c.getTitle().toLowerCase().contains(search.toLowerCase()))
                        .collect(java.util.stream.Collectors.toList());
            } else {
                myCourses = courseRepository.findByInstructorId(user.getId());
            }
            model.addAttribute("myCourses", myCourses);
            model.addAttribute("availableCourses", java.util.Collections.emptyList());
            if (search != null) {
                model.addAttribute("search", search);
            }

            // Widget: Pending Grading Grouped by Course
            // 1. Fetch all activities with pending submissions
            List<com.dlam.model.Activity> pendingActivities = activityRepository
                    .findActivitiesWithPendingSubmissionsByInstructorId(user.getId());

            // 2. Group by Course Title. We know myCourses contains all instructor courses.
            // Map<CourseID, List<Activity>>
            java.util.Map<Integer, List<com.dlam.model.Activity>> pendingMap = pendingActivities.stream()
                    .collect(java.util.stream.Collectors.groupingBy(com.dlam.model.Activity::getCourseId));

            // We need to pass Course objects or Titles to the view.
            // Only include courses that HAVE pending activities.
            java.util.Map<Course, List<com.dlam.model.Activity>> pendingGradingMap = new java.util.HashMap<>();

            for (Course c : myCourses) {
                if (pendingMap.containsKey(c.getId())) {
                    pendingGradingMap.put(c, pendingMap.get(c.getId()));
                }
            }

            model.addAttribute("pendingGradingMap", pendingGradingMap);

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

    @PostMapping("/unenroll")
    public String unenroll(@RequestParam("courseId") int courseId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"STUDENT".equals(user.getRole())) {
            return "redirect:/login";
        }
        enrollmentRepository.deleteByStudentIdAndCourseId(user.getId(), courseId);
        return "redirect:/courses";
    }

    @Autowired
    private com.dlam.repository.ActivityRepository activityRepository;

    @Autowired
    private com.dlam.repository.SubmissionRepository submissionRepository;

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

    @GetMapping("/edit-course")
    public String showEditCourseForm(@RequestParam("courseId") int courseId, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"INSTRUCTOR".equals(user.getRole())) {
            return "redirect:/login";
        }

        Course course = courseRepository.findById(courseId).orElse(null);
        if (course == null || course.getInstructorId() != user.getId()) {
            return "redirect:/courses?error=accessDenied";
        }

        model.addAttribute("course", course);
        return "edit-course";
    }

    @PostMapping("/edit-course")
    public String editCourse(@RequestParam("courseId") int courseId,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"INSTRUCTOR".equals(user.getRole())) {
            return "redirect:/login";
        }

        Course course = courseRepository.findById(courseId).orElse(null);
        if (course == null || course.getInstructorId() != user.getId()) {
            return "redirect:/courses?error=accessDenied";
        }

        course.setTitle(title);
        course.setDescription(description);
        courseRepository.save(course);

        return "redirect:/courses?success=updated";
    }

    @PostMapping("/delete-course")
    public String deleteCourse(@RequestParam("courseId") int courseId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"INSTRUCTOR".equals(user.getRole())) {
            return "redirect:/login";
        }

        Course course = courseRepository.findById(courseId).orElse(null);
        if (course == null || course.getInstructorId() != user.getId()) {
            return "redirect:/courses?error=accessDenied";
        }

        // Cascading delete manually
        // 1. Delete Enrollments
        enrollmentRepository.deleteByCourseId(courseId);

        // 2. Delete Activities and Submissions
        List<com.dlam.model.Activity> activities = activityRepository.findByCourseId(courseId);
        for (com.dlam.model.Activity activity : activities) {
            List<com.dlam.model.Submission> submissions = submissionRepository.findByActivityId(activity.getId());
            submissionRepository.deleteAll(submissions);
            activityRepository.delete(activity);
        }

        // 3. Delete Course
        courseRepository.delete(course);

        return "redirect:/courses?success=deleted";
    }
}
