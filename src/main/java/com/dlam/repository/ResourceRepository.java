package com.dlam.repository;

import com.dlam.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ResourceRepository extends JpaRepository<Resource, Integer> {
    List<Resource> findByCourseId(int courseId);
}
