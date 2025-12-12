package com.dlam.controller;

import com.dlam.dto.StudentGradeDTO;
import com.dlam.model.Activity;
import com.dlam.model.Course;
import com.dlam.model.Enrollment;
import com.dlam.model.Submission;
import com.dlam.model.User;
import com.dlam.repository.ActivityRepository;
import com.dlam.repository.CourseRepository;
import com.dlam.repository.EnrollmentRepository;
import com.dlam.repository.SubmissionRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class ProgressController {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    @GetMapping("/my-grades")
    public String viewMyGrades(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"STUDENT".equals(user.getRole())) {
            return "redirect:/login";
        }

        // 1. Get Enrolled Courses
        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(user.getId());
        List<Integer> courseIds = enrollments.stream().map(Enrollment::getCourseId).collect(Collectors.toList());
        List<Course> courses = courseRepository.findAllById(courseIds);
        Map<Integer, String> courseTitleMap = courses.stream()
                .collect(Collectors.toMap(Course::getId, Course::getTitle));

        // 2. Get Activities for these courses
        List<Activity> allActivities = new ArrayList<>();
        for (Integer cid : courseIds) { // Optimization: Use findByCourseIdIn if available in repo, else loop
            allActivities.addAll(activityRepository.findByCourseId(cid));
        }

        // 3. Get Submissions
        // Optimization: fetch all by studentId
        List<Submission> submissions = submissionRepository.findByStudentId(user.getId());
        Map<Integer, Submission> submissionMap = submissions.stream()
                .collect(Collectors.toMap(Submission::getActivityId, s -> s, (s1, s2) -> s1)); // Handle potential
                                                                                               // duplicates

        // 4. Build DTOs
        List<StudentGradeDTO> gradeList = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (Activity activity : allActivities) {
            Submission sub = submissionMap.get(activity.getId());

            String status;
            String grade = "-";
            String feedback = "";
            LocalDateTime submittedDate = null;

            if (sub != null) {
                submittedDate = sub.getSubmissionDate().toLocalDateTime();
                if (sub.getGrade() != null && !sub.getGrade().isEmpty()) {
                    status = "Graded";
                    grade = sub.getGrade();
                    feedback = sub.getFeedback();
                } else {
                    status = "Submitted";
                    grade = "Pending";
                }
            } else {
                if (activity.getDueDate() != null && activity.getDueDate().isBefore(now)) {
                    status = "Missing";
                } else {
                    status = "Pending"; // Not yet submitted, but not due yet
                }
            }

            StudentGradeDTO dto = new StudentGradeDTO(
                    courseTitleMap.get(activity.getCourseId()),
                    activity.getTitle(),
                    activity.getDueDate(),
                    submittedDate,
                    grade,
                    feedback,
                    status);
            gradeList.add(dto);
        }

        // Group by Course Name for display
        Map<String, List<StudentGradeDTO>> gradesByCourse = gradeList.stream()
                .collect(Collectors.groupingBy(StudentGradeDTO::getCourseTitle));

        model.addAttribute("gradesByCourse", gradesByCourse);

        return "my-grades";
    }
}
