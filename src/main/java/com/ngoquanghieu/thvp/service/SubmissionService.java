package com.ngoquanghieu.thvp.service;

import com.ngoquanghieu.thvp.entity.Assignment;
import com.ngoquanghieu.thvp.entity.Submission;
import com.ngoquanghieu.thvp.entity.User;
import com.ngoquanghieu.thvp.repository.SubmissionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SubmissionService {
    private final SubmissionRepository submissionRepository;
    private final AssignmentService assignmentService;

    // Thư mục lưu file bài nộp
    private static final String SUBMISSION_DIR = "src/main/resources/static/files/submissions/";

    // Học viên nộp bài
    public Submission submitAssignment(Long assignmentId, User student, String textSubmission, MultipartFile file) throws IOException {
        Assignment assignment = assignmentService.getAssignmentById(assignmentId);

        // Kiểm tra hạn nộp (nếu có)
        if (assignment.getDueDate() != null && LocalDateTime.now().isAfter(assignment.getDueDate())) {
            throw new RuntimeException("Đã hết hạn nộp bài!");
        }

        // Kiểm tra đã nộp chưa (mỗi học viên chỉ nộp 1 lần)
        boolean alreadySubmitted = submissionRepository.findByAssignmentId(assignmentId).stream()
                .anyMatch(s -> s.getUser().getId().equals(student.getId()));
        if (alreadySubmitted) {
            throw new RuntimeException("Bạn đã nộp bài tập này rồi!");
        }

        Submission submission = new Submission();
        submission.setAssignment(assignment);
        submission.setUser(student);
        submission.setTextSubmission(textSubmission);

        // Xử lý upload file
        if (file != null && !file.isEmpty()) {
            Files.createDirectories(Paths.get(SUBMISSION_DIR));
            String fileName = student.getId() + "_" + assignmentId + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path path = Paths.get(SUBMISSION_DIR + fileName);
            Files.write(path, file.getBytes());
            submission.setFileUrl("/files/submissions/" + fileName);
        }

        submission.setSubmittedAt(LocalDateTime.now());
        return submissionRepository.save(submission);
    }

    // Lấy danh sách bài nộp của một bài tập (Instructor xem)
    public List<Submission> getSubmissionsByAssignmentId(Long assignmentId, User currentUser) {
        Assignment assignment = assignmentService.getAssignmentById(assignmentId);

        if (!assignment.getLesson().getSection().getCourse().getInstructor().getId().equals(currentUser.getId())
                && currentUser.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Không có quyền xem bài nộp!");
        }

        return submissionRepository.findByAssignmentId(assignmentId);
    }

    // Chấm điểm bài nộp (Instructor/Admin)
    public Submission gradeSubmission(Long submissionId, Integer score, String feedback, User grader) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Bài nộp không tồn tại!"));

        Assignment assignment = submission.getAssignment();

        // Kiểm tra quyền chấm
        if (!assignment.getLesson().getSection().getCourse().getInstructor().getId().equals(grader.getId())
                && grader.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Bạn không có quyền chấm bài này!");
        }

        if (score < 0 || score > assignment.getMaxScore()) {
            throw new RuntimeException("Điểm không hợp lệ!");
        }

        submission.setScore(score);
        submission.setFeedback(feedback);
        submission.setGradedBy(grader);
        submission.setGradedAt(LocalDateTime.now());

        return submissionRepository.save(submission);
    }

    // Học viên xem bài nộp của mình
    public Submission getMySubmission(Long assignmentId, User student) {
        return submissionRepository.findByAssignmentId(assignmentId).stream()
                .filter(s -> s.getUser().getId().equals(student.getId()))
                .findFirst()
                .orElse(null);
    }
}
