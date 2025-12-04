package com.dlam.controller;

import com.dlam.model.Submission;
import com.dlam.model.User;
import com.dlam.repository.SubmissionRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
public class SubmissionController {

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private com.dlam.repository.ActivityRepository activityRepository;

    @GetMapping("/submit-activity")
    public String showSubmitForm(@RequestParam("activityId") String activityId,
            @RequestParam(value = "courseId", required = false) String courseId,
            HttpSession session,
            Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"STUDENT".equals(user.getRole())) {
            return "redirect:/login";
        }
        model.addAttribute("activityId", activityId);
        model.addAttribute("courseId", courseId);
        return "submit-activity"; // Maps to submit-activity.jsp
    }

    @PostMapping("/submit-activity")
    public String submitActivity(@RequestParam("activityId") int activityId,
            @RequestParam("content") String content,
            @RequestParam(value = "courseId", required = false) String courseId,
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file,
            HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"STUDENT".equals(user.getRole())) {
            return "redirect:/login";
        }

        if (content.trim().isEmpty() && file.isEmpty()) {
            return "redirect:/submit-activity?activityId=" + activityId + "&courseId=" + courseId
                    + "&error=emptySubmission";
        }

        // Check for single submission limit
        com.dlam.model.Activity activity = activityRepository.findById(activityId).orElse(null);
        if (activity != null && activity.isSingleSubmission()) {
            List<Submission> existingSubmissions = submissionRepository.findByActivityIdAndStudentId(activityId,
                    user.getId());
            if (!existingSubmissions.isEmpty()) {
                return "redirect:/activities?courseId=" + courseId + "&error=singleSubmissionLimit";
            }
        }

        Submission submission = new Submission(activityId, user.getId(), content);

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

                // Set file info in submission
                submission.setFilePath("/uploads/" + uniqueFilename);
                submission.setOriginalFilename(originalFilename);
            } catch (java.io.IOException e) {
                e.printStackTrace();
                return "redirect:/submit-activity?activityId=" + activityId + "&courseId=" + courseId
                        + "&error=uploadFailed";
            }
        }

        submissionRepository.save(submission);

        if (courseId != null) {
            return "redirect:/activities?courseId=" + courseId;
        } else {
            return "redirect:/courses";
        }
    }

    @GetMapping("/view-my-submissions")
    public String viewMySubmissions(@RequestParam("activityId") int activityId,
            @RequestParam(value = "courseId", required = false) String courseId,
            HttpSession session,
            Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"STUDENT".equals(user.getRole())) {
            return "redirect:/login";
        }

        List<Submission> submissions = submissionRepository.findByActivityIdAndStudentId(activityId, user.getId());
        model.addAttribute("submissions", submissions);
        model.addAttribute("activityId", activityId);
        model.addAttribute("courseId", courseId);
        return "view-my-submissions"; // Maps to view-my-submissions.jsp
    }

    @GetMapping("/view-submissions")
    public String viewSubmissions(@RequestParam("activityId") int activityId,
            @RequestParam(value = "courseId", required = false) String courseId,
            HttpSession session,
            Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"INSTRUCTOR".equals(user.getRole())) {
            return "redirect:/login";
        }

        List<Submission> submissions = submissionRepository.findLatestSubmissionsByActivityId(activityId);
        model.addAttribute("submissions", submissions);
        model.addAttribute("activityId", activityId);
        model.addAttribute("courseId", courseId);
        return "view-submissions"; // Maps to view-submissions.jsp
    }

    @PostMapping("/grade-submission")
    public String gradeSubmission(@RequestParam("submissionId") int submissionId,
            @RequestParam("grade") String grade,
            @RequestParam("feedback") String feedback,
            @RequestParam("activityId") String activityId,
            @RequestParam("courseId") String courseId,
            HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"INSTRUCTOR".equals(user.getRole())) {
            return "redirect:/login";
        }

        Optional<Submission> submissionOpt = submissionRepository.findById(submissionId);
        if (submissionOpt.isPresent()) {
            Submission submission = submissionOpt.get();
            submission.setGrade(grade);
            submission.setFeedback(feedback);
            submissionRepository.save(submission);
        }

        return "redirect:/view-submissions?activityId=" + activityId + "&courseId=" + courseId;
    }
}
