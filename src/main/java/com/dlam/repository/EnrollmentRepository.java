package com.dlam.repository;

import com.dlam.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {
    List<Enrollment> findByStudentId(int studentId);

    boolean existsByStudentIdAndCourseId(int studentId, int courseId);

    @org.springframework.transaction.annotation.Transactional
    void deleteByStudentIdAndCourseId(int studentId, int courseId);

    @org.springframework.transaction.annotation.Transactional
    void deleteByCourseId(int courseId);
}
