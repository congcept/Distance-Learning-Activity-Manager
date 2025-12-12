package com.dlam.repository;

import com.dlam.model.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AnnouncementRepository extends JpaRepository<Announcement, Integer> {
    // Order by date descending so latest announcements appear first
    List<Announcement> findByCourseIdOrderByPostedDateDesc(int courseId);
}
