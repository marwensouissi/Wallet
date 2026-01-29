package com.fintech.wallet.infrastructure.adapter;

import com.fintech.wallet.application.port.out.EmailNotificationPort;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Email notification adapter using Spring Mail.
 */
@Component
public class EmailNotificationAdapter implements EmailNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationAdapter.class);

    private final JavaMailSender mailSender;
    private final String fromAddress;
    private final boolean enabled;

    public EmailNotificationAdapter(
            JavaMailSender mailSender,
            @Value("${notification.email.from:noreply@wallet.fintech.com}") String fromAddress,
            @Value("${notification.email.enabled:false}") boolean enabled) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
        this.enabled = enabled;
    }

    @Override
    @Async
    public void sendEmail(String to, String subject, String body) {
        if (!enabled) {
            log.debug("Email notifications disabled. Would send to: {}, subject: {}", to, subject);
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
            
            helper.setFrom(fromAddress);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true); // true = HTML

            mailSender.send(message);
            log.info("Email sent successfully to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }

    @Override
    @Async
    public void sendEmailWithAttachments(String to, String subject, String body, 
            Map<String, byte[]> attachments) {
        if (!enabled) {
            log.debug("Email notifications disabled. Would send to: {} with {} attachments", 
                    to, attachments.size());
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromAddress);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);

            for (Map.Entry<String, byte[]> attachment : attachments.entrySet()) {
                helper.addAttachment(attachment.getKey(), 
                        new ByteArrayResource(attachment.getValue()));
            }

            mailSender.send(message);
            log.info("Email with attachments sent successfully to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send email with attachments to {}: {}", to, e.getMessage());
        }
    }
}
