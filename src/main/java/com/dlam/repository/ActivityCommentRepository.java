package com.dlam.repository;

import com.dlam.model.ActivityComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ActivityCommentRepository extends JpaRepository<ActivityComment, Integer> {
    List<ActivityComment> findByActivityIdOrderByPostedAtAsc(int activityId);
}
