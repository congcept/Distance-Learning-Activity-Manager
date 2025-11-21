<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <!DOCTYPE html>
    <html>

    <head>
        <title>Register - DLAM</title>
        <link rel="stylesheet" href="style.css">
    </head>

    <body>
        <div class="container">
            <h2>Register</h2>
            <form action="register" method="post">
                <label>Full Name:</label>
                <input type="text" name="username" required><br>

                <label>Username:</label>
                <input type="text" name="username" required><br>

                <label>Password:</label>
                <input type="password" name="password" required><br>

                <label>Role:</label>
                <select name="role">
                    <option value="STUDENT">Student</option>
                    <option value="INSTRUCTOR">Instructor</option>
                </select><br>

                <button type="submit">Register</button>
            </form>
            <a href="login.jsp">Already have an account? Login</a>
        </div>
    </body>

    </html>