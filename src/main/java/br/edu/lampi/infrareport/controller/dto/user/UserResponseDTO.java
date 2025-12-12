package br.edu.lampi.infrareport.controller.dto.user;

import java.util.List;

import br.edu.lampi.infrareport.model.user.User;
import br.edu.lampi.infrareport.model.user.UserType;

public record UserResponseDTO(Long id, String name, List<UserType> userType, String email){
    public UserResponseDTO(User user) {
        this(user.getId(), user.getName(), user.getUserType(), user.getEmail());
    }
}
