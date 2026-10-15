package com.lakshan.user_service.service;

import com.lakshan.user_service.entity.PasswordResetToken;
import com.lakshan.user_service.entity.User;
import com.lakshan.user_service.exceptions.InvalidCredentialsException;
import com.lakshan.user_service.exceptions.UserNotFoundException;
import com.lakshan.user_service.models.ApiResponse;
import com.lakshan.user_service.models.VerifyOtpResponse;
import com.lakshan.user_service.repository.PasswordResetTokenRepository;
import com.lakshan.user_service.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class PasswordResetService {

    private static final int OTP_EXPIRATION_MINUTES = 10;
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();

    @Autowired
    public PasswordResetService(
            UserRepository userRepository,
            PasswordResetTokenRepository tokenRepository,
            EmailService emailService,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public ApiResponse sendOtp(String email) {
        String cleanEmail = email.trim().toLowerCase();

        // Check if user exists (generic response returned to prevent user enumeration)
        Optional<User> userOptional = userRepository.findByEmail(cleanEmail);
        if (userOptional.isEmpty()) {
            log.warn("Password reset requested for non-existent email: {}", cleanEmail);
            return ApiResponse.ok("If an account with that email exists, a verification code has been sent.");
        }

        // Generate 6-digit OTP (100000 - 999999)
        String otp = String.format("%06d", secureRandom.nextInt(1_000_000));

        // Create or update reset token record
        PasswordResetToken resetToken = tokenRepository.findByEmail(cleanEmail)
                .orElse(PasswordResetToken.builder().email(cleanEmail).build());

        resetToken.setOtp(otp);
        resetToken.setExpiryTime(LocalDateTime.now().plusMinutes(OTP_EXPIRATION_MINUTES));
        resetToken.setVerified(false);
        resetToken.setResetToken(null);
        tokenRepository.save(resetToken);

        // Send HTML email with OTP
        emailService.sendPasswordResetOtp(cleanEmail, otp);

        return ApiResponse.ok("If an account with that email exists, a verification code has been sent.");
    }

    @Transactional
    public VerifyOtpResponse verifyOtp(String email, String otp) {
        String cleanEmail = email.trim().toLowerCase();
        String cleanOtp = otp.trim();

        PasswordResetToken token = tokenRepository.findByEmail(cleanEmail)
                .orElseThrow(() -> new InvalidCredentialsException("No password reset request found for this email."));

        if (token.isExpired()) {
            throw new InvalidCredentialsException("The verification code has expired. Please request a new code.");
        }

        if (!token.getOtp().equals(cleanOtp)) {
            throw new InvalidCredentialsException("Invalid verification code. Please check and try again.");
        }

        // Mark verified and generate a secure single-use resetToken
        String resetToken = UUID.randomUUID().toString();
        token.setVerified(true);
        token.setResetToken(resetToken);
        tokenRepository.save(token);

        return new VerifyOtpResponse("Verification successful.", resetToken);
    }

    @Transactional
    public ApiResponse resetPassword(String email, String resetToken, String newPassword) {
        String cleanEmail = email.trim().toLowerCase();

        PasswordResetToken token = tokenRepository.findByEmailAndResetToken(cleanEmail, resetToken)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid or expired password reset session."));

        if (!token.isVerified() || token.isExpired()) {
            throw new InvalidCredentialsException("Password reset session is invalid or has expired. Please start over.");
        }

        User user = userRepository.findByEmail(cleanEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Invalidate and remove the reset token
        tokenRepository.delete(token);

        log.info("Password successfully updated for user {}", cleanEmail);
        return ApiResponse.ok("Password has been reset successfully. You can now sign in.");
    }
}
