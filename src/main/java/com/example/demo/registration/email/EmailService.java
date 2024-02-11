package com.example.demo.registration.email;


import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;


@Service
@RequiredArgsConstructor
public class EmailService implements    EmailSender{
    private  final JavaMailSender mailSender;
    private final static Logger LOGGER = LoggerFactory.
            getLogger(EmailService.class);

    @Override
    @Async
    public void send(String to, String email) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,true, "utf-8");
            helper.setText(email, true);
            helper.setTo(to);
            helper.setSubject("Confirm your email");
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            LOGGER.error("Failed to send email to {}", to, e);
            throw new IllegalStateException("Failed to send email to " + to, e);
        }
    }

}
