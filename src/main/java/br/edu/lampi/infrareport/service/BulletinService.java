package br.edu.lampi.infrareport.service;

import br.edu.lampi.infrareport.controller.dto.bulletin.BulletinResponseDTO;
import br.edu.lampi.infrareport.model.bulletin.Bulletin;
import br.edu.lampi.infrareport.repository.BulletinRepository;
import br.edu.lampi.infrareport.service.exceptions.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BulletinService {

    private final BulletinRepository bulletinRepository;

    public BulletinService(BulletinRepository bulletinRepository) {
        this.bulletinRepository = bulletinRepository;
    }

    public Bulletin create(Bulletin bulletin) {
        return bulletinRepository.save(bulletin);
    }

    public Optional<Bulletin> readById(Long id) {
        return Optional.ofNullable(bulletinRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Bulletin not found with the given id.")
        ));
    }

    public Page<BulletinResponseDTO> readAll(Pageable pageable) {
        return bulletinRepository.findAll(pageable).map(BulletinResponseDTO::new);
    }

    public void update(Bulletin bulletin) {
        bulletinRepository.save(bulletin);
    }

    public void delete(Bulletin bulletin) {
        bulletinRepository.delete(bulletin);
    }
}