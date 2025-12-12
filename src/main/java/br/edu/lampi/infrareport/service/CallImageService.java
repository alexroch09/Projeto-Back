package br.edu.lampi.infrareport.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import br.edu.lampi.infrareport.controller.dto.callImage.CallImageRequestDTO;
import br.edu.lampi.infrareport.controller.dto.callImage.CallImageResponseDTO;
import br.edu.lampi.infrareport.model.call.Call;
import br.edu.lampi.infrareport.model.callImage.CallImage;
import br.edu.lampi.infrareport.repository.CallImageRepository;
import br.edu.lampi.infrareport.repository.CallRepository;
import br.edu.lampi.infrareport.service.exceptions.BadRequestException;
import br.edu.lampi.infrareport.service.exceptions.ConflictException;
import br.edu.lampi.infrareport.service.exceptions.ResourceNotFoundException;

@Service
public class CallImageService {

    @Autowired
    private CallImageRepository callImageRepository;

    public List<CallImage> getAll() {
        return callImageRepository.findAll();
    }

    public CallImage getById(Long id) {
        Optional<CallImage> optional = callImageRepository.findById(id);
        return optional.orElseThrow(() -> new RuntimeException("Image not found"));
    }

    public List<CallImage> getByCallId(Long callId) {
        return callImageRepository.findByCallId(callId);
    }

    public CallImage save(CallImageRequestDTO dto) {
        CallImage callImage = new CallImage();
        callImage.setPath(dto.getPath());
        callImage.setFileName(dto.getFileName());
        callImage.setCallId(dto.getCallId());
        return callImageRepository.save(callImage);
    }

    public CallImage updateById(Long id, CallImageRequestDTO dto) {
        CallImage callImage = getById(id);
        callImage.setPath(dto.getPath());
        callImage.setFileName(dto.getFileName());
        callImage.setCallId(dto.getCallId());
        return callImageRepository.save(callImage);
    }

    public void deleteById(Long id) {
        if (!callImageRepository.existsById(id)) {
            throw new RuntimeException("Image not found");
        }
        callImageRepository.deleteById(id);
    }
}