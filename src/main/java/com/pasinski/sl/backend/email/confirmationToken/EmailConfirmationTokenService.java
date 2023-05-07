package com.pasinski.sl.backend.email.confirmationToken;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class EmailConfirmationTokenService {
    private final EmailConfirmationTokenRepository emailConfirmationTokenRepository;

    public void saveEmailConfirmationToken(EmailConfirmationToken emailConfirmationToken) {
        emailConfirmationTokenRepository.save(emailConfirmationToken);
    }

    public Optional<EmailConfirmationToken> findEmailConfirmationTokenByToken(String token) {
        return emailConfirmationTokenRepository.findByToken(token);
    }
}
