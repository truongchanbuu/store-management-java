package com.finalproject.storemanagementproject.middleware;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.host}")
    public String HOST;

    @Value("${spring.mail.username}")
    private String USERNAME;

    @Value("${client.base.url}")
    private String clientBaseUrl;

    @Autowired
    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public boolean sendResetPasswordMail(String email, String token) {
        String subject = "Reset password";
        String url = clientBaseUrl + "/auth/reset-password?token=" + token;
        String content = "<p>Click this link to reset your password: <a href=\"" + url + "\">Reset Password</a></p>";
        return sendMail(email, subject, content);
    }

    public boolean sendMail(String email, String subject, String content) {

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

        try {
            mimeMessage.setContent(content, "text/html");
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setFrom(USERNAME);

            mailSender.send(mimeMessage);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
