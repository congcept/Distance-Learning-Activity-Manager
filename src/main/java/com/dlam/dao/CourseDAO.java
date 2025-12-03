package com.dlam.dao;

import com.dlam.model.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class CourseDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public boolean addCourse(Course course) {
        String sql = "INSERT INTO courses (title, description, instructor_id) VALUES (?, ?, ?)";
        int rows = jdbcTemplate.update(sql, course.getTitle(), course.getDescription(), course.getInstructorId());
        return rows > 0;
    }

    public List<Course> getAllCourses() {
        String sql = "SELECT * FROM courses";
        return jdbcTemplate.query(sql, new CourseRowMapper());
    }

    public List<Course> getCoursesByInstructor(int instructorId) {
        String sql = "SELECT * FROM courses WHERE instructor_id = ?";
        return jdbcTemplate.query(sql, new CourseRowMapper(), instructorId);
    }

    private static class CourseRowMapper implements RowMapper<Course> {
        @Override
        public Course mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Course(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getInt("instructor_id"));
        }
    }
}
