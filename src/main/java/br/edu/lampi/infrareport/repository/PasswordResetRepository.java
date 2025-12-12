package br.edu.lampi.infrareport.repository;

import br.edu.lampi.infrareport.model.passwordReset.PasswordReset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PasswordResetRepository extends JpaRepository<PasswordReset, Long> {
    void deleteAllByExpiresAtBefore(LocalDateTime now);
    boolean existsByToken(int token);

    Optional<PasswordReset> findByTokenAndEmail(int token, String email);
}
