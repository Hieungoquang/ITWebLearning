package com.ngoquanghieu.thvp.repository;

import com.ngoquanghieu.thvp.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    List<Submission> findByAssignmentId(Long assignmentId);
    List<Submission> findByUserId(Long userId);
    Optional<Submission> findByAssignmentIdAndUserId(Long assignmentId, Long userId);
}
