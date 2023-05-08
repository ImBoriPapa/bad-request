package com.study.badrequest.utils.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
@RequiredArgsConstructor
@Slf4j
public class MailWriter {

    private final JavaMailSender javaMailSender;

    public boolean write(String email, String subject, String text) {

        MimeMessage javaMailSenderMimeMessage = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(javaMailSenderMimeMessage, false);
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(text, true);
            javaMailSender.send(javaMailSenderMimeMessage);
            log.info("회원 메일 발송 시작 수신인: {}", email);
            return true;
        } catch (MessagingException e) {
            log.info("회원 메일 발송 실패 수신인: {}", email);
            throw new IllegalArgumentException(e.getMessage());

        }
    }

}
