package br.edu.lampi.infrareport.service;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import br.edu.lampi.infrareport.controller.dto.callStatus.CallStatusRequestDTO;
import br.edu.lampi.infrareport.model.callstatus.CallStatus;
import br.edu.lampi.infrareport.repository.CallRepository;
import br.edu.lampi.infrareport.repository.CallStatusRepository;
import br.edu.lampi.infrareport.service.exceptions.ConflictException;
import br.edu.lampi.infrareport.service.exceptions.ResourceNotFoundException;

@Service
public class CallStatusService {

    private final CallStatusRepository callStatusRepository;

    private final CallRepository callRepository;

    public CallStatusService(CallStatusRepository callStatusRepository, CallRepository callRepository) {
        this.callStatusRepository = callStatusRepository;
        this.callRepository = callRepository;
    }

    public CallStatus save(CallStatusRequestDTO callStatusRequestDTO) {
        if(this.callStatusRepository.existsByCallStatusName(callStatusRequestDTO.callStatusName())){
            throw new ConflictException("call status name is already in use");
        }

        return this.callStatusRepository.save(new CallStatus(callStatusRequestDTO));
    }

    public List<CallStatus> search() {
        return this.callStatusRepository.findAll();
    }

    public CallStatus searchById(Long id) {
        return this.callStatusRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("call status not found by the given id."));
    }

    public void delete(Long id) {
        CallStatus callStatus = this.searchById(id);
        
        if(this.callRepository.existsByCallStatusId(id)){
            throw new ConflictException("there are calls associated with this status");
        }

        this.callStatusRepository.delete(callStatus);
    }

    public CallStatus update(Long id, CallStatusRequestDTO callStatusRequestDTO) {
        CallStatus oldCallStatus = this.searchById(id);
        
        if(this.callStatusRepository.existsByCallStatusName(callStatusRequestDTO.callStatusName()) 
            && !Objects.equals(callStatusRequestDTO.callStatusName(), oldCallStatus.getCallStatusName())){
            throw new ConflictException("call status name is already in use");
        }

        oldCallStatus.setCallStatusName(callStatusRequestDTO.callStatusName());
        
        return this.callStatusRepository.save(oldCallStatus);
    }
}
