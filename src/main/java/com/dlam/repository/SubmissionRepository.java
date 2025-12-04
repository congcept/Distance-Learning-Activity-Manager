package com.dlam.repository;

import com.dlam.model.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Integer> {
    List<Submission> findByActivityId(int activityId);

    List<Submission> findByActivityIdAndStudentId(int activityId, int studentId);

    List<Submission> findByStudentId(int studentId);

    @org.springframework.data.jpa.repository.Query("SELECT s FROM Submission s WHERE s.activityId = :activityId AND s.id IN (SELECT MAX(s2.id) FROM Submission s2 WHERE s2.activityId = :activityId GROUP BY s2.studentId)")
    List<Submission> findLatestSubmissionsByActivityId(
            @org.springframework.data.repository.query.Param("activityId") int activityId);
}
