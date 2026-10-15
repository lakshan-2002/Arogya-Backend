package com.lakshan.user_service.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendPasswordResetOtp(String toEmail, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name()
            );

            helper.setTo(toEmail);
            helper.setFrom(fromEmail != null && !fromEmail.isBlank() ? fromEmail : "no-reply@arogya.health");
            helper.setSubject("Arogya Healthcare - Password Reset Verification Code");

            String htmlContent = buildOtpHtmlTemplate(otp);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Password reset OTP email sent successfully to {}", toEmail);
        } catch (MessagingException ex) {
            log.error("Failed to send OTP email to {}: {}", toEmail, ex.getMessage(), ex);
            throw new RuntimeException("Failed to send password reset email. Please verify mail settings.", ex);
        }
    }

    private String buildOtpHtmlTemplate(String otp) {
        return "<!DOCTYPE html>"
                + "<html>"
                + "<head>"
                + "<meta charset='UTF-8'>"
                + "<meta name='viewport' content='width=device-width, initial-scale=1.0'>"
                + "<title>Password Reset</title>"
                + "</head>"
                + "<body style='margin:0;padding:0;background-color:#F3F4F6;font-family:-apple-system,BlinkMacSystemFont,\"Segoe UI\",Roboto,Helvetica,Arial,sans-serif;'>"
                + "<table border='0' cellpadding='0' cellspacing='0' width='100%' style='background-color:#F3F4F6;padding:40px 16px;'>"
                + "<tr>"
                + "<td align='center'>"
                + "<table border='0' cellpadding='0' cellspacing='0' width='100%' style='max-width:540px;background-color:#FFFFFF;border-radius:16px;overflow:hidden;box-shadow:0 4px 12px rgba(0,0,0,0.06);'>"
                + "<!-- Header -->"
                + "<tr>"
                + "<td style='background:linear-gradient(135deg,#38A3A5 0%,#22577A 100%);padding:32px 24px;text-align:center;'>"
                + "<h1 style='margin:0;color:#FFFFFF;font-size:26px;font-weight:700;letter-spacing:0.5px;'>Arogya Healthcare</h1>"
                + "<p style='margin:6px 0 0;color:#C7F9CC;font-size:14px;'>Secure Account Recovery</p>"
                + "</td>"
                + "</tr>"
                + "<!-- Body -->"
                + "<tr>"
                + "<td style='padding:36px 32px;color:#1F2937;'>"
                + "<h2 style='margin:0 0 16px;color:#111827;font-size:20px;font-weight:600;'>Password Reset Request</h2>"
                + "<p style='margin:0 0 20px;color:#4B5563;font-size:15px;line-height:1.6;'>"
                + "We received a request to reset the password for your Arogya account. Use the verification code below to complete the reset process."
                + "</p>"
                + "<!-- OTP Box -->"
                + "<div style='background-color:#F0FDF4;border:2px dashed #38A3A5;border-radius:12px;padding:20px;text-align:center;margin:28px 0;'>"
                + "<span style='display:block;font-size:13px;text-transform:uppercase;color:#047857;font-weight:600;letter-spacing:1px;margin-bottom:8px;'>Your Verification Code</span>"
                + "<span style='font-family:monospace,Consolas,Courier;font-size:36px;font-weight:800;letter-spacing:8px;color:#111827;'>" + otp + "</span>"
                + "</div>"
                + "<p style='margin:0 0 16px;color:#6B7280;font-size:13px;line-height:1.5;'>"
                + "⏱ This code will expire in <strong>10 minutes</strong>. Never share this code with anyone."
                + "</p>"
                + "<p style='margin:24px 0 0;color:#9CA3AF;font-size:12px;line-height:1.5;border-top:1px solid #E5E7EB;padding-top:20px;'>"
                + "If you did not request a password reset, please ignore this email or contact support. Your password will remain unchanged."
                + "</p>"
                + "</td>"
                + "</tr>"
                + "<!-- Footer -->"
                + "<tr>"
                + "<td style='background-color:#F9FAFB;padding:20px 24px;text-align:center;color:#9CA3AF;font-size:12px;'>"
                + "&copy; " + java.time.Year.now().getValue() + " Arogya Healthcare Platform. All rights reserved."
                + "</td>"
                + "</tr>"
                + "</table>"
                + "</td>"
                + "</tr>"
                + "</table>"
                + "</body>"
                + "</html>";
    }
}
