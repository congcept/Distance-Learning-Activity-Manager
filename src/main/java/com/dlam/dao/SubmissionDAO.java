package com.dlam.dao;

import com.dlam.model.Submission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class SubmissionDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public boolean addSubmission(Submission submission) {
        String sql = "INSERT INTO submissions (activity_id, student_id, content) VALUES (?, ?, ?)";
        int rows = jdbcTemplate.update(sql, submission.getActivityId(), submission.getStudentId(),
                submission.getContent());
        return rows > 0;
    }

    public List<Submission> getSubmissionsByActivity(int activityId) {
        String sql = "SELECT * FROM submissions WHERE activity_id = ?";
        return jdbcTemplate.query(sql, new SubmissionRowMapper(), activityId);
    }

    public Submission getSubmissionByStudent(int activityId, int studentId) {
        String sql = "SELECT * FROM submissions WHERE activity_id = ? AND student_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new SubmissionRowMapper(), activityId, studentId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<Submission> getAllSubmissionsByStudent(int activityId, int studentId) {
        String sql = "SELECT * FROM submissions WHERE activity_id = ? AND student_id = ? ORDER BY submission_date DESC";
        return jdbcTemplate.query(sql, new SubmissionRowMapper(), activityId, studentId);
    }

    public List<Integer> getSubmittedActivityIds(int studentId, int courseId) {
        String sql = "SELECT DISTINCT s.activity_id FROM submissions s " +
                "JOIN activities a ON s.activity_id = a.id " +
                "WHERE s.student_id = ? AND a.course_id = ?";
        return jdbcTemplate.queryForList(sql, Integer.class, studentId, courseId);
    }

    public boolean updateGrade(int submissionId, String grade, String feedback) {
        String sql = "UPDATE submissions SET grade = ?, feedback = ? WHERE id = ?";
        int rows = jdbcTemplate.update(sql, grade, feedback, submissionId);
        return rows > 0;
    }

    private static class SubmissionRowMapper implements RowMapper<Submission> {
        @Override
        public Submission mapRow(ResultSet rs, int rowNum) throws SQLException {
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
}
