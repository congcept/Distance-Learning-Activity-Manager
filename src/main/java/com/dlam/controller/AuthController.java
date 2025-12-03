package com.dlam.controller;

import com.dlam.dao.UserDAO;
import com.dlam.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @Autowired
    private UserDAO userDAO;

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
        User newUser = new User(username, password, fullName, role);
        if (userDAO.registerUser(newUser)) {
            return "redirect:/login?success=registered";
        } else {
            return "redirect:/register?error=failed";
        }
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpSession session) {
        User user = userDAO.checkLogin(username, password);
        if (user != null) {
            session.setAttribute("user", user);
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
}
