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
}
