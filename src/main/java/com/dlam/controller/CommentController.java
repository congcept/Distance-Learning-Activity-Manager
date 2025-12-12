package com.dlam.controller;

import com.dlam.model.ActivityComment;
import com.dlam.model.User;
import com.dlam.repository.ActivityCommentRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

@Controller
public class CommentController {

    @Autowired
    private ActivityCommentRepository commentRepository;

    @PostMapping("/add-comment")
    public String addComment(@RequestParam("activityId") int activityId,
            @RequestParam("courseId") int courseId,
            @RequestParam("content") String content,
            @RequestParam("redirectUrl") String redirectUrl,
            HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        ActivityComment comment = new ActivityComment(activityId, user.getId(), content, LocalDateTime.now());
        commentRepository.save(comment);

        // Redirect back to where the user came from (Student submit view or Instructor
        // grade view)
        return "redirect:" + redirectUrl;
    }
}
