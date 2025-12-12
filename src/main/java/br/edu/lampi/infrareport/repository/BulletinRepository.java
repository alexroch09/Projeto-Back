package br.edu.lampi.infrareport.repository;

import br.edu.lampi.infrareport.model.bulletin.Bulletin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BulletinRepository extends JpaRepository<Bulletin, Long> {
}