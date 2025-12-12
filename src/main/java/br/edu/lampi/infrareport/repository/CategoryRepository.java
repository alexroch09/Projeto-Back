package br.edu.lampi.infrareport.repository;

import br.edu.lampi.infrareport.model.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByName(String name);
}