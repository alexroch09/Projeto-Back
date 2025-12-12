package br.edu.lampi.infrareport.controller.dto.category;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryResponseDTO {
    private Long id;
    private String name;

    public CategoryResponseDTO(Long id, String name){
        this.id = id;
        this.name = name;
    }
}
