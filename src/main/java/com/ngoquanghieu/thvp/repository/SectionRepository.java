package com.ngoquanghieu.thvp.repository;

import com.ngoquanghieu.thvp.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {
    @Query("SELECT MAX(s.orderIndex) FROM Section s WHERE s.course.id = :courseId")
    Integer findMaxOrderByCourseId(@Param("courseId") Long courseId);
    List<Section> findByCourseIdOrderByOrderIndexAsc(Long courseId);
}