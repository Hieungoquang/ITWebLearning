package com.ngoquanghieu.thvp.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "question_options")
@Data
public class QuestionOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    @Column(name = "option_text", length = 500)
    private String optionText;

    @Column(name = "is_correct")
    private boolean isCorrect = false;
}
