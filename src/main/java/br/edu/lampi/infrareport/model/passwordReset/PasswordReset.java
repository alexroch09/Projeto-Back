package br.edu.lampi.infrareport.model.passwordReset;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Entity
@Table(name = "password_reset")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class PasswordReset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email")
    private String email;

    @Column(name = "token")
    private int token;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime expiresAt;
}
