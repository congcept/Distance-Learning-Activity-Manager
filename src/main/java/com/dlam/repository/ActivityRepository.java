package com.dlam.repository;

import com.dlam.model.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Integer> {
        List<Activity> findByCourseId(int courseId);

        List<Activity> findByCourseIdOrderByIdDesc(int courseId);

        List<Activity> findByCourseIdInAndDueDateBetweenOrderByDueDateAsc(List<Integer> courseIds,
                        java.time.LocalDateTime start, java.time.LocalDateTime end);

        @org.springframework.data.jpa.repository.Query("SELECT a FROM Activity a JOIN Enrollment e ON a.courseId = e.courseId WHERE e.studentId = :studentId AND a.dueDate >= CURRENT_TIMESTAMP ORDER BY a.dueDate ASC")
        List<Activity> findUpcomingActivitiesByStudentId(
                        @org.springframework.data.repository.query.Param("studentId") int studentId);

        @org.springframework.data.jpa.repository.Query("SELECT DISTINCT a FROM Activity a JOIN Submission s ON a.id = s.activityId JOIN Course c ON a.courseId = c.id WHERE c.instructorId = :instructorId AND s.grade IS NULL")
        List<Activity> findActivitiesWithPendingSubmissionsByInstructorId(
                        @org.springframework.data.repository.query.Param("instructorId") int instructorId);
}
