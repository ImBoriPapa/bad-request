package com.study.badrequest.service.mail;

import com.study.badrequest.domain.mail.MemberMail;
import com.study.badrequest.domain.member.Member;
import com.study.badrequest.repository.mail.MemberMailRepository;
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

import static com.study.badrequest.domain.mail.MemberMail.createTemporaryPasswordMail;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MemberMailServiceImpl implements MemberMailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final MemberMailRepository mailRepository;
    @Value("${mail.temporary-password}")
    public String temporaryPasswordSubject;


    @Transactional
    @Override
    public void sendTemporaryPassword(Member member, String temporaryPassword) {
        log.info("회원 임시 비밀번호 메일 발송 시작 수신인: {}", member.getEmail());

        sendMail(
                member.getEmail(),
                temporaryPasswordSubject,
                mailSender.createMimeMessage(),
                createTemporaryPasswordMailBody(temporaryPassword),
                createTemporaryPasswordMail(member, temporaryPasswordSubject)
        );
        log.info("회원 임시 비밀번호 메일 발송 완료 수신인: {}", member.getEmail());
    }

    private void sendMail(String email, String subject, MimeMessage mimeMessage, String mailText, MemberMail memberMail) {
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false);
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(mailText, true);
            mailSender.send(mimeMessage);
            mailRepository.save(memberMail);
        } catch (MessagingException e) {
            memberMail.sentFail();
            mailRepository.save(memberMail);
            log.info("회원 {} 메일 발송 실패 수신인: {}", subject, email);
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private String createTemporaryPasswordMailBody(String temporaryPassword) {
        Context context = new Context();
        context.setVariable("temporaryPassword", temporaryPassword);
        return templateEngine.process("mail/temporary-password", context);
    }

}
