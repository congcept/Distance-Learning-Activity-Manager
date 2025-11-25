package com.dlam.dao;

import com.dlam.model.Submission;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SubmissionDAO {
    private Connection conn;

    public SubmissionDAO(Connection conn) {
        this.conn = conn;
    }

    public boolean addSubmission(Submission submission) throws SQLException {
        String sql = "INSERT INTO submissions (activity_id, student_id, content) VALUES (?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, submission.getActivityId());
        ps.setInt(2, submission.getStudentId());
        ps.setString(3, submission.getContent());
        return ps.executeUpdate() > 0;
    }

    public List<Submission> getSubmissionsByActivity(int activityId) throws SQLException {
        List<Submission> list = new ArrayList<>();
        String sql = "SELECT * FROM submissions WHERE activity_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, activityId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }

    public Submission getSubmissionByStudent(int activityId, int studentId) throws SQLException {
        String sql = "SELECT * FROM submissions WHERE activity_id = ? AND student_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, activityId);
        ps.setInt(2, studentId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return mapRow(rs);
        }
        return null;
    }

    private Submission mapRow(ResultSet rs) throws SQLException {
        return new Submission(
                rs.getInt("id"),
                rs.getInt("activity_id"),
                rs.getInt("student_id"),
                rs.getTimestamp("submission_date"),
                rs.getString("content"),
                rs.getString("grade"),
                rs.getString("feedback"));
    }
}
