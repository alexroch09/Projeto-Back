package br.edu.lampi.infrareport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import br.edu.lampi.infrareport.model.user.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>{
    UserDetails findByEmail(String email);

    boolean existsByEmail(String email);
}
