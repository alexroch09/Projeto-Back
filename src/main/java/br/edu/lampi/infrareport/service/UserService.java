package br.edu.lampi.infrareport.service;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.edu.lampi.infrareport.config.SecurityConfig;
import br.edu.lampi.infrareport.controller.dto.user.UserRequestDTO;
import br.edu.lampi.infrareport.model.user.User;
import br.edu.lampi.infrareport.model.user.UserType;
import br.edu.lampi.infrareport.repository.UserRepository;
import br.edu.lampi.infrareport.service.exceptions.EmailAlreadyRegisteredException;
import br.edu.lampi.infrareport.service.exceptions.UserNotFoundException;

@Service
public class UserService{
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SecurityConfig securityConfig;

    public User searchById(Long id) {
        return this.userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found."));
    }

    public User searchByEmail(String email) {
        return (User) this.userRepository.findByEmail(email);
    }

    public User save(UserRequestDTO data) {

        User existingUser = (User) userRepository.findByEmail(data.email());

        if(existingUser != null) {
            if(!existingUser.isActive()) {
                existingUser.setName(data.name());
                existingUser.setPassword(securityConfig.passwordEncoder().encode(data.password()));
                existingUser.setActive(true);
                userRepository.save(existingUser);
                return existingUser;
            } else {
                throw new EmailAlreadyRegisteredException("Email already registered.");
            }
        }

        var user = new User(data);
        user.setUserType(List.of(UserType.ROLE_COMMON));
        user.setPassword(securityConfig.passwordEncoder().encode(user.getPassword()));

        userRepository.save(user);

        return user;
    }

    /* TODO: esse método só serve para criar rapidamentes administradores durante o teste. 
     * Ainda não está disponível no Controller.
     * É possível remover esse método caso UserRequestDTO seja corrigido para conter o papel do usuário salvo.
     * Entretanto, o ideal é que continue separado para que possa ser acessado apenas por outros administradores.
     */
    public User saveAdmin(UserRequestDTO data) {
        System.out.println(userRepository.findByEmail(data.email()));
        if (userRepository.findByEmail(data.email()) != null) {
            throw new EmailAlreadyRegisteredException("Email already registered.");
        }

        var user = new User(data);
        user.setUserType(List.of(UserType.ROLE_ADMIN, UserType.ROLE_COMMON));
        user.setPassword(securityConfig.passwordEncoder().encode(user.getPassword()));

        userRepository.save(user);

        return user;
    }

    public User update(Long id, UserRequestDTO data) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found."));

        if(!Objects.equals(user.getEmail(), data.email())
            && userRepository.findByEmail(data.email()) != null) {
            throw new EmailAlreadyRegisteredException("Email already registered.");
        }

        user.update(data);
        user.setPassword(securityConfig.passwordEncoder().encode(user.getPassword()));

        return this.userRepository.save(user);
    }

    public void delete(Long id) {
        User user = this.userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found."));

        if(!user.isEnabled()) {
            throw new IllegalStateException("Cannot deactivate an already deactivated account.");
        }

        user.setActive(false);
        this.userRepository.save(user);
    }

}
