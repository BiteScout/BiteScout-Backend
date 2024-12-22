package com.bitescout.app.authenticationservice.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${auth-service.url}")
    private String authServiceUrl;
    @Async
    public void sendVerificationEmail(String destinationEmail, String token) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(
                mimeMessage,
                MimeMessageHelper.MULTIPART_MODE_RELATED,
                UTF_8.name());
        messageHelper.setFrom("bite_scout@gmail.com");

        String templateName = "verification-email.html";
        Map<String, Object> variables = new HashMap<>();
        variables.put("token", token);
        variables.put("verificationUrl", authServiceUrl + "/v1/auth/verify?token=" + token);
        variables.put("currentYear", String.valueOf(java.time.Year.now().getValue()));

        Context context = new Context();
        context.setVariables(variables);
        messageHelper.setSubject("Email Verification");

        try {
            String htmlTemplate = templateEngine.process(templateName, context);
            messageHelper.setText(htmlTemplate, true);

            messageHelper.setTo(destinationEmail);
            mailSender.send(mimeMessage);
            log.info(String.format("INFO - Verification email successfully sent to %s", destinationEmail));
        } catch (MessagingException e) {
            log.warn(String.format("WARNING - Cannot send email to %s", destinationEmail));
        }
    }
}