package br.edu.lampi.infrareport.service;

import br.edu.lampi.infrareport.config.SecurityConfig;
import br.edu.lampi.infrareport.controller.dto.user.ForgotPasswordNewRequestDTO;
import br.edu.lampi.infrareport.controller.dto.user.ValidatePasswordResetToken;
import br.edu.lampi.infrareport.model.passwordReset.PasswordReset;
import br.edu.lampi.infrareport.model.user.User;
import br.edu.lampi.infrareport.repository.PasswordResetRepository;
import br.edu.lampi.infrareport.repository.UserRepository;
import br.edu.lampi.infrareport.service.exceptions.TokenPasswordResetException;
import br.edu.lampi.infrareport.service.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class PasswordResetService {
    @Autowired
    private PasswordResetRepository passwordResetRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SecurityConfig securityConfig;

    @Autowired
    private EmailService emailService;

    private static final int EXPIRATION_TOKEN_HOURS = 6;
    private static final long TIME_CLEAN_EXPIRED_TOKEN = 3600000;

    public int generateUniqueToken() {
        Random random = new Random();
        int token;
        do {
            token = 100000 + random.nextInt(900000); // 6 digit token
        } while (passwordResetRepository.existsByToken(token));
        return token;
    }

    public void createPasswordReset(String email) {
        if (!userRepository.existsByEmail(email)) {
            throw new UserNotFoundException();
        }

        int token = generateUniqueToken();
        PasswordReset resetToken = new PasswordReset();
        resetToken.setToken(token);
        resetToken.setEmail(email);
        resetToken.setExpiresAt(LocalDateTime.now().plusHours(EXPIRATION_TOKEN_HOURS));

        passwordResetRepository.save(resetToken);

        try{
            emailService.sendPasswordResetEmail(email, token);
        } catch (MailException mailException) {
            throw new RuntimeException(mailException.getMessage());
        }
    }

    public void resetPassword(ForgotPasswordNewRequestDTO data) {
        Optional<PasswordReset> resetToken = validatePasswordResetToken(new ValidatePasswordResetToken(data.token(), data.email()));

        if (userRepository.findByEmail(data.email()) == null) {
            throw new UserNotFoundException();
        }

        UserDetails userDetails = userRepository.findByEmail(data.email());
        User user = (User) userDetails;
        user.setPassword(securityConfig.passwordEncoder().encode(data.newPassword()));
        userRepository.save(user);

        passwordResetRepository.delete(resetToken.get());
    }

    public Optional<PasswordReset> validatePasswordResetToken(ValidatePasswordResetToken data) {
        Optional<PasswordReset> resetToken = passwordResetRepository.findByTokenAndEmail(data.token(), data.email());

        if (resetToken.isEmpty() || resetToken.get().getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new TokenPasswordResetException();
        }

        return resetToken;
    }

    @Scheduled(fixedRate = TIME_CLEAN_EXPIRED_TOKEN)
    public void cleanExpiredTokens() {
        passwordResetRepository.deleteAllByExpiresAtBefore(LocalDateTime.now());
    }
}
