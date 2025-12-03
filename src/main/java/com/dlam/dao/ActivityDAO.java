package com.dlam.dao;

import com.dlam.model.Activity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class ActivityDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public boolean addActivity(Activity activity) {
        String sql = "INSERT INTO activities (course_id, title, description, due_date) VALUES (?, ?, ?, ?)";
        int rows = jdbcTemplate.update(sql, activity.getCourseId(), activity.getTitle(), activity.getDescription(),
                activity.getDueDate());
        return rows > 0;
    }

    public List<Activity> getActivitiesByCourse(int courseId) {
        String sql = "SELECT id, course_id, title, description, due_date FROM activities WHERE course_id = ?";
        return jdbcTemplate.query(sql, new ActivityRowMapper(), courseId);
    }

    private static class ActivityRowMapper implements RowMapper<Activity> {
        @Override
        public Activity mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Activity(
                    rs.getInt("id"),
                    rs.getInt("course_id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getDate("due_date"));
        }
    }
}
