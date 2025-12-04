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
    public String loginPage(HttpSession session) {
        if (session.getAttribute("user") != null) {
            return "redirect:/courses";
        }
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
            HttpSession session,
            jakarta.servlet.http.HttpServletResponse response) {

        Optional<User> userOpt = userRepository.findByUsernameAndPassword(username, password);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            session.setAttribute("user", user);

            // Always set remember me cookie
            jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie("dlam_user_id",
                    String.valueOf(user.getId()));
            cookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
            cookie.setPath("/");
            response.addCookie(cookie);

            return "redirect:/courses";
        } else {
            return "redirect:/login?error=invalid";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, jakarta.servlet.http.HttpServletResponse response) {
        session.invalidate();

        // Clear remember me cookie
        jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie("dlam_user_id", "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);

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
