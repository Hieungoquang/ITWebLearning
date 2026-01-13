package com.ngoquanghieu.thvp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendCourseApprovedEmail(String toEmail, String courseTitle) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Khóa học của bạn đã được duyệt");
        message.setText("Chúc mừng! Khóa học '" + courseTitle + "' đã được phê duyệt.");
        mailSender.send(message);
    }

    public void sendCourseRejectedEmail(String toEmail, String courseTitle, String reason) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Khóa học của bạn bị từ chối");
        message.setText("Khóa học '" + courseTitle + "' bị từ chối vì: " + reason);
        mailSender.send(message);
    }
}