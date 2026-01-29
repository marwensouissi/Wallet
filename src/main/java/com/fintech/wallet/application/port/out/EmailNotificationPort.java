package com.fintech.wallet.application.port.out;

/**
 * Output port for sending email notifications.
 * Implemented by infrastructure adapters.
 */
public interface EmailNotificationPort {

    /**
     * Sends an email notification.
     *
     * @param to recipient email address
     * @param subject email subject
     * @param body email body (HTML supported)
     */
    void sendEmail(String to, String subject, String body);

    /**
     * Sends an email notification with attachments.
     *
     * @param to recipient email address
     * @param subject email subject
     * @param body email body (HTML supported)
     * @param attachments map of filename to byte array content
     */
    void sendEmailWithAttachments(String to, String subject, String body, 
            java.util.Map<String, byte[]> attachments);
}
