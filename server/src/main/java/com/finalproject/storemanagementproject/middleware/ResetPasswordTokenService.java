package com.finalproject.storemanagementproject.middleware;

import com.finalproject.storemanagementproject.models.ResetPasswordToken;
import com.finalproject.storemanagementproject.repositories.ResetPasswordTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ResetPasswordTokenService {

    private final ResetPasswordTokenRepository resetPasswordTokenRepository;

    @Autowired
    public ResetPasswordTokenService(ResetPasswordTokenRepository resetPasswordTokenRepository) {
        this.resetPasswordTokenRepository = resetPasswordTokenRepository;
    }

    public String createToken(String userId) {
        ResetPasswordToken resetPasswordToken = new ResetPasswordToken(null, userId, LocalDateTime.now().plusMinutes(1));

        return resetPasswordTokenRepository.save(resetPasswordToken).getId();
    }

    public String getUserIdFromToken(String token) {
        clearExpiredToken();

        ResetPasswordToken resetPasswordToken = resetPasswordTokenRepository.findById(token).orElse(null);
        return resetPasswordToken != null ? resetPasswordToken.getUserId() : null;
    }

    private void clearExpiredToken() {
        Iterable<ResetPasswordToken> resetPasswordTokens = resetPasswordTokenRepository.findAll();
        for (ResetPasswordToken resetPasswordToken : resetPasswordTokens) {
            if (resetPasswordToken.getExpiryDate().isBefore(LocalDateTime.now()))
                resetPasswordTokenRepository.delete(resetPasswordToken);
        }
    }
}
