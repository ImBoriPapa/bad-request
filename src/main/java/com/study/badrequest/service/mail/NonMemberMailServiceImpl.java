package com.study.badrequest.service.mail;

import com.study.badrequest.domain.mail.NonMemberMail;
import com.study.badrequest.domain.member.EmailAuthenticationCode;
import com.study.badrequest.exception.CustomRuntimeException;
import com.study.badrequest.repository.mail.NonMemberMailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import static com.study.badrequest.commons.response.ApiResponseStatus.FAIL_SEND_MAIL;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class NonMemberMailServiceImpl implements NonMemberMailService {

    private final JavaMailSender mailSender;

    private final SpringTemplateEngine templateEngine;

    private final NonMemberMailRepository nonMemberMailRepository;
    @Value("${mail.authentication-subject}")
    public String authenticationSubject;

    @Override
    @Transactional
    public void sendAuthenticationMail(String email,String code) {
        log.info("비회원 이메일 인증 메일 발송 시작 수신인: {}", email);

        sendMail(
                email,
                authenticationSubject,
                mailSender.createMimeMessage(),
                createAuthenticationMailBody(code),
                NonMemberMail.createAuthenticationMail(email, authenticationSubject)
        );

        log.info("비회원 이메일 인증 메일 발송 완료");
    }

    private void sendMail(String email, String subject, MimeMessage mimeMessage, String mailText, NonMemberMail nonMemberMail) {
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false);
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(mailText, true);
            mailSender.send(mimeMessage);
            nonMemberMailRepository.save(nonMemberMail);
        } catch (MessagingException e) {
            nonMemberMail.sentFail();
            log.error("비회원 {} 메일 발송 실패 수신인: {}, Message: {}", subject, email,e.getLocalizedMessage());
            throw CustomRuntimeException.createWithApiResponseStatus(FAIL_SEND_MAIL);
        }
    }

    private String createAuthenticationMailBody(String authenticationCode) {
        Context context = new Context();
        context.setVariable("authenticationCode", authenticationCode);
        return templateEngine.process("mail/email-authentication", context);
    }

}
