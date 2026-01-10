package com.ngoquanghieu.thvp.service;

import com.ngoquanghieu.thvp.entity.*;
import com.ngoquanghieu.thvp.repository.QuestionRepository;
import com.ngoquanghieu.thvp.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class QuizService {
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;

    // Tạo quiz cho lesson
    public Quiz createQuiz(Quiz quiz, Lesson lesson, User currentUser) {
        if (!lesson.getSection().getCourse().getInstructor().getId().equals(currentUser.getId()) && currentUser.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Không có quyền tạo quiz!");
        }

        quiz.setLesson(lesson);
        return quizRepository.save(quiz);
    }

    public Question addQuestion(Question question, Long quizId, User currentUser) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz không tồn tại!"));

        if (!quiz.getLesson().getSection().getCourse().getInstructor().getId().equals(currentUser.getId()) && currentUser.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Không có quyền thêm câu hỏi!");
        }

        question.setQuiz(quiz);
        return questionRepository.save(question);
    }

    public Quiz getQuizByLessonId(Long lessonId) {
        return quizRepository.findByLessonId(lessonId)
                .orElse(null);
    }
}
