package com.dlam.dao;

import com.dlam.model.Activity;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ActivityDAO {

    private Connection conn;

    public ActivityDAO(Connection conn) {
        this.conn = conn;
    }

    public boolean addActivity(Activity activity) throws SQLException {
        String sql = "INSERT INTO activities (course_id, title, description, due_date) VALUES (?, ?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, activity.getCourseId());
        ps.setString(2, activity.getTitle());
        ps.setString(3, activity.getDescription());
        ps.setDate(4, activity.getDueDate());
        return ps.executeUpdate() > 0;
    }

    public List<Activity> getActivitiesByCourse(int courseId) throws SQLException {
        String sql = "SELECT id, course_id, title, description, due_date FROM activities WHERE course_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, courseId);
        ResultSet rs = ps.executeQuery();

        List<Activity> list = new ArrayList<>();
        while (rs.next()) {
            list.add(new Activity(
                rs.getInt("id"),
                rs.getInt("course_id"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getDate("due_date")
            ));
        }

        return list;
    }
}
