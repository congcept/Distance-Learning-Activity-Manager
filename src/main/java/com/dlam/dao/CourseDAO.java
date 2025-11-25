package com.dlam.dao;

import com.dlam.model.Course;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {
    private Connection conn;

    public CourseDAO(Connection conn) {
        this.conn = conn;
    }

    public boolean addCourse(Course course) throws SQLException {
        String sql = "INSERT INTO courses (title, description, instructor_id) VALUES (?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, course.getTitle());
        ps.setString(2, course.getDescription());
        ps.setInt(3, course.getInstructorId());
        return ps.executeUpdate() > 0;
    }

    public List<Course> getAllCourses() throws SQLException {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT * FROM courses";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            list.add(new Course(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getInt("instructor_id")));
        }
        return list;
    }

    public List<Course> getCoursesByInstructor(int instructorId) throws SQLException {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT * FROM courses WHERE instructor_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, instructorId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            list.add(new Course(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getInt("instructor_id")));
        }
        return list;
    }
}
