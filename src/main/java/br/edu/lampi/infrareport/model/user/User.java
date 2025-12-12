package br.edu.lampi.infrareport.model.user;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import br.edu.lampi.infrareport.controller.dto.user.UserRequestDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class User implements UserDetails{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_name")
    private String name;

    @Column(name = "user_type")
    @Enumerated(EnumType.STRING)
    private List<UserType> userType;

    @Column(name = "user_email")
    private String email;

    @Column(name = "user_password")
    private String password;

    @Column(name = "user_active")
    private boolean active = true;

    public User(UserRequestDTO data) {
        this.setName(data.name());
        this.setEmail(data.email());
        this.setPassword(data.password());
        this.setActive(true);
    }

    public void update(UserRequestDTO data) {
        this.setName(data.name());
        this.setEmail(data.email());
        this.setPassword(data.password());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return userType.stream()
                .map(p -> new SimpleGrantedAuthority(p.name()))
                .toList();
    }

    @Override
    public String getUsername() {
        return this.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.active;
    }

}
