package com.ngoquanghieu.thvp.repository;

import com.ngoquanghieu.thvp.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Integer> {
    List<Lesson> findBySectionIdOrderByOrderIndexAsc(Long sectionId);

    @Query("SELECT COALESCE(MAX(l.orderIndex), 0) FROM Lesson l WHERE l.section.id = :moduleId")
    Integer findMaxOrderByModuleId(Long moduleId);
}
