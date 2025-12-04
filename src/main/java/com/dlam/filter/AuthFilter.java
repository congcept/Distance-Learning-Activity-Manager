package com.dlam.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

import jakarta.servlet.annotation.WebFilter;

@WebFilter(urlPatterns = "/*")
public class AuthFilter implements Filter {

    @org.springframework.beans.factory.annotation.Autowired
    private com.dlam.repository.UserRepository userRepository;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization code if needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(true); // Create session if not exists to store user if cookie found

        String path = req.getServletPath();

        // Allow access to public resources
        boolean isAuthPath = path.equals("/login") || path.equals("/register") || path.equals("/logout");
        boolean isCss = path.startsWith("/css/");
        boolean isUploads = path.startsWith("/uploads/"); // Allow access to uploaded files

        boolean isLoggedIn = (session != null && session.getAttribute("user") != null);

        if (!isLoggedIn) {
            // Check for remember me cookie
            jakarta.servlet.http.Cookie[] cookies = req.getCookies();
            if (cookies != null) {
                for (jakarta.servlet.http.Cookie cookie : cookies) {
                    if ("dlam_user_id".equals(cookie.getName())) {
                        try {
                            int userId = Integer.parseInt(cookie.getValue());
                            com.dlam.model.User user = userRepository.findById(userId).orElse(null);
                            if (user != null) {
                                session.setAttribute("user", user);
                                isLoggedIn = true;
                            }
                        } catch (NumberFormatException e) {
                            // Invalid cookie value, ignore
                        }
                        break;
                    }
                }
            }
        }

        if (isLoggedIn || isAuthPath || isCss || isUploads) {
            chain.doFilter(request, response);
        } else {
            res.sendRedirect(req.getContextPath() + "/login");
        }
    }

    @Override
    public void destroy() {
        // Cleanup code if needed
    }
}
