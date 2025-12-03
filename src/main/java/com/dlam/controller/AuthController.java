package com.dlam.controller;

import com.dlam.model.User;
import com.dlam.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam("username") String username,
            @RequestParam("fullname") String fullName,
            @RequestParam("password") String password,
            @RequestParam("role") String role) {

        if (userRepository.findByUsername(username).isPresent()) {
            return "redirect:/register?error=failed";
        }

        User newUser = new User(username, password, fullName, role);
        userRepository.save(newUser);
        return "redirect:/login?success=registered";
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpSession session) {

        Optional<User> userOpt = userRepository.findByUsernameAndPassword(username, password);

        if (userOpt.isPresent()) {
            session.setAttribute("user", userOpt.get());
            return "redirect:/courses";
        } else {
            return "redirect:/login?error=invalid";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/profile")
    public String profilePage(HttpSession session, org.springframework.ui.Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@RequestParam("fullname") String fullName,
            @RequestParam("password") String password,
            HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        userRepository.updateUser(fullName, password, user.getId());

        // Update session with new values
        user.setFullName(fullName);
        user.setPassword(password);
        session.setAttribute("user", user);

        return "redirect:/courses?success=profileUpdated";
    }
}
