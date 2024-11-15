package com.bitescout.app.notificationservice.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Async
    public void sendReservationStatusEmail(
            String destinationEmail,
            String customerName,
            String restaurantName,
            LocalDateTime reservationTime
    ) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(
                mimeMessage,
                MimeMessageHelper.MULTIPART_MODE_RELATED,
                UTF_8.name());
        messageHelper.setFrom("noreply@bitescout.com");  //email subject to change

        final String templateName = EmailTemplates.RESERVATION_STATUS_NOTIFICATION.getTemplate();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd-MM");
        String formattedReservationTime = reservationTime.format(formatter);

        Map<String, Object> variables = new HashMap<>();
        variables.put("customer-name", customerName);
        variables.put("restaurant-name", restaurantName);
        variables.put("reservation-time", formattedReservationTime);

        Context context = new Context();
        context.setVariables(variables);
        messageHelper.setSubject(EmailTemplates.RESERVATION_STATUS_NOTIFICATION.getSubject());

        try{
            String htmlTemplate = templateEngine.process(templateName, context);
            messageHelper.setText(htmlTemplate, true);

            messageHelper.setTo(destinationEmail);
            mailSender.send(mimeMessage);
            log.info(String.format("INFO - Email successfully sent to %s with template %s",
                    destinationEmail, templateName));
        } catch (MessagingException e){
            log.warn(String.format("WARNING - Can not send email to %s", destinationEmail));
        }

    }

}
