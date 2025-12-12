package br.edu.lampi.infrareport.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import br.edu.lampi.infrareport.model.callComment.CallComment;
import br.edu.lampi.infrareport.model.floor.Floor;
import br.edu.lampi.infrareport.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import br.edu.lampi.infrareport.controller.dto.call.CallFilterDTO;
import br.edu.lampi.infrareport.controller.dto.call.CallUserRequestDTO;
import br.edu.lampi.infrareport.controller.dto.call.CallUserResponseDTO;
import br.edu.lampi.infrareport.controller.dto.call.ClassifyCallDTO;
import br.edu.lampi.infrareport.model.call.Call;
import br.edu.lampi.infrareport.model.callstatus.CallStatus;
import br.edu.lampi.infrareport.model.category.Category;
import br.edu.lampi.infrareport.model.user.User;
import br.edu.lampi.infrareport.service.exceptions.BadRequestException;
import br.edu.lampi.infrareport.service.exceptions.ResourceNotFoundException;
import br.edu.lampi.infrareport.specifications.CallSpecification;
import org.springframework.util.StringUtils;

@Service
public class CallService {
    @Autowired
    private CallRepository callRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CallCommentRepository callCommentRepository;

    @Autowired
    private CallImageRepository callImageRepository;

    @Autowired
    private FloorService floorService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CallStatusService callStatusService;

    public Call save(CallUserRequestDTO callUserRequestDTO) {

        if(callUserRequestDTO.userId() != null && !this.userRepository.existsById(callUserRequestDTO.userId())){
            throw new BadRequestException("user does not exists");
        }

        Floor floor = floorService.getFloorByID(callUserRequestDTO.floorId());

        Call call = new Call();
        call.setTitle(callUserRequestDTO.title());
        call.setDescription(callUserRequestDTO.description());
        call.setDateTime(callUserRequestDTO.dateTime());
        User user = userRepository.findById(callUserRequestDTO.userId()).get();
        call.setUser(user);
        call.setFloor(floor);
        call.setActive(true);
        return this.callRepository.save(call);
    }

    public boolean existsById(Long id){
        if(id != null){
            return callRepository.existsById(id);
        }
        return false;
    }

    // Atenção, esse método não deve ser utilizado em produção.
    public List<Call> getAll() {
        return this.callRepository.findAll();
    }
    
    public Page<CallUserResponseDTO> getAllPageable(Pageable pageable){
        if(pageable != null){
            return callRepository.findAll(pageable).map(CallUserResponseDTO::new);
        }
        return null;
    }

    public Call getById(@NonNull Long id) {
        return this.callRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("call not found by the given id"));
    }

    public void deleteById(@NonNull Long id) {
        Call call = this.getById(id);
        if(call == null){
            throw new BadRequestException("call not found by the given id");
        }
        if(callCommentRepository.existsByCallId(id)){
            throw new BadRequestException("call is associated with existing comments");
        }
        if(callImageRepository.existsByCallId(id)){
            throw new BadRequestException("call is associated with existing images");
        }
        
        this.callRepository.delete(call);
    }

    public void deactivateById(Long id, String justification) {
        try {
            Optional<Call> optionalCall = callRepository.findById(id);

            Call call = optionalCall.orElseThrow(() -> new BadRequestException("call not found by the given id"));

            call.setActive(false);
            call.setJustification(StringUtils.hasText(justification) ? justification : "Chamada deletada pelo administrador");
            callRepository.save(call);
        } catch(BadRequestException e) {
            throw e;
        } catch(Exception e) {
            throw new RuntimeException("An error occurred while trying to deactivate the call", e);
        }
    }
    public void activeCall(Long id) {
        try {
            Optional<Call> optionalCall = callRepository.findById(id);

            Call call = optionalCall.orElseThrow(() -> new BadRequestException("call not found by the given id"));

            call.setActive(true);
            call.setJustification(null);
            callRepository.save(call);
        } catch(BadRequestException e) {
            throw e;
        } catch(Exception e) {
            throw new RuntimeException("An error occurred while trying to deactivate the call", e);
        }
    }

    public Call updateById(@NonNull Long id, CallUserRequestDTO callUserRequestDTO) {
        Call oldCall = this.getById(id);

        if(callUserRequestDTO.userId() != null && !this.userRepository.existsById(callUserRequestDTO.userId())){
            throw new BadRequestException("user does not exists");
        }

        Floor floor = floorService.getFloorByID(callUserRequestDTO.floorId());

        oldCall.setTitle(callUserRequestDTO.title());
        oldCall.setDescription(callUserRequestDTO.description());
        oldCall.setDateTime(callUserRequestDTO.dateTime());
        User user = userRepository.findById(callUserRequestDTO.userId()).get();
        oldCall.setUser(user);
        oldCall.setFloor(floor);

        return this.callRepository.save(oldCall);
    }

    public Page<Call> searchFilter(CallFilterDTO callFilter, Pageable pageable) {
        if(callFilter.initDateTime() != null && callFilter.endDateTime() != null && callFilter.initDateTime().isAfter(callFilter.endDateTime())){
            throw new BadRequestException("the initDatetime parameter must not be after endDateTime parameter");
        }
        
        return this.callRepository.findAll(Specification
                        .where(CallSpecification.callsBetween(callFilter.initDateTime(), callFilter.endDateTime()))
                        .and(Objects.equals(callFilter.classified(), true) ? CallSpecification.callHasTeam(callFilter.teamIdList()) : null)
                        .and(Objects.equals(callFilter.classified(), true) ? CallSpecification.callHasCategory(callFilter.categoryIdList()) : null)
                        .and(Objects.equals(callFilter.classified(), true) ? CallSpecification.callHasCallStatus(callFilter.callStatusIdList()) : null)
                        .and(CallSpecification.callHasFloor(callFilter.floorIdList()))
                        .and(Objects.equals(callFilter.classified(), true) ? CallSpecification.callHasCallPriority(callFilter.callPriorityList()) : null)
                        .and(CallSpecification.callHasActive(callFilter.active()))
                        .and(CallSpecification.callHasUser(callFilter.userIdList()))
                        .and(CallSpecification.isCallClassified(callFilter.classified())),
                pageable
        );
    }

    public byte[] generateCSV(CallFilterDTO callFilter) {
        String csvHeaders = "id,title,description,dateTime,floor,priority,active,team,category,callStatus,username\n";
        List<Call> callList = this.searchFilter(callFilter);
        StringBuilder csvBuilder = new StringBuilder();

        csvBuilder.append(csvHeaders);

        if(callList.isEmpty()) {
            return csvBuilder.toString().getBytes();
        }

        for (Call call : callList) {
            csvBuilder.append(call.getId())
                    .append(",")
                    .append(call.getTitle())
                    .append(",")
                    .append(call.getDescription())
                    .append(",")
                    .append(call.getDateTime())
                    .append(",")
                    .append(call.getFloor().getName())
                    .append(",")
                    .append(call.getPriority() != null ? call.getPriority().name() : call.getPriority())
                    .append(",")
                    .append(call.getActive())
                    .append(",")
                    .append(call.getTeam() != null ? call.getTeam().getName() : call.getTeam())
                    .append(",")
                    .append(call.getCategory() != null ? call.getCategory().getName() : call.getCategory())
                    .append(",")
                    .append(call.getCallStatus() != null ? call.getCallStatus().getCallStatusName() : call.getCallStatus())
                    .append(",")
                    .append(call.getUser() != null ? call.getUser().getEmail() : call.getUser())
                    .append("\n");
        }

        return csvBuilder.toString().getBytes();
    }

    private List<Call> searchFilter(CallFilterDTO callFilter) {
        if(callFilter.initDateTime() != null && callFilter.endDateTime() != null && callFilter.initDateTime().isAfter(callFilter.endDateTime())){
            throw new BadRequestException("the initDatetime parameter must not be after endDateTime parameter");
        }

        return this.callRepository.findAll(Specification
                        .where(CallSpecification.callsBetween(callFilter.initDateTime(), callFilter.endDateTime()))
                        .and(Objects.equals(callFilter.classified(), true) ? CallSpecification.callHasTeam(callFilter.teamIdList()) : null)
                        .and(Objects.equals(callFilter.classified(), true) ? CallSpecification.callHasCategory(callFilter.categoryIdList()) : null)
                        .and(Objects.equals(callFilter.classified(), true) ? CallSpecification.callHasCallStatus(callFilter.callStatusIdList()) : null)
                        .and(CallSpecification.callHasFloor(callFilter.floorIdList()))
                        .and(Objects.equals(callFilter.classified(), true) ? CallSpecification.callHasCallPriority(callFilter.callPriorityList()) : null)
                        .and(CallSpecification.callHasActive(callFilter.active()))
                        .and(CallSpecification.callHasUser(callFilter.userIdList()))
                        .and(CallSpecification.isCallClassified(callFilter.classified()))
                        );
    }

    public void classifyCall(Long id, ClassifyCallDTO classifyCallDTO) {
        Call call = this.getById(id);

        call.setTeam(this.teamService.getTeamsById(classifyCallDTO.teamId()));
        call.setCategory(findCategory(classifyCallDTO.categoryId()));
        call.setCallStatus(findCallStatus(classifyCallDTO.callStatusId()));
        call.setPriority(classifyCallDTO.priority());

        this.callRepository.save(call);
    }

    private Category findCategory(Long id){
        if(id != null) 
            return this.categoryService.getById(id);

        return null;
    }

    private CallStatus findCallStatus(Long id){
        if(id != null) 
            return this.callStatusService.searchById(id);

        return null;
    }

    public Page<Call> searchCallsByUserId(Long userId, Pageable pageable) {
        Page<Call> callsPage = callRepository.findByUserId(userId, pageable);
        List<Call> filteredCalls = callsPage.getContent().stream()
                .map(this::filterUnviewedComments)
                .collect(Collectors.toList());
        return new PageImpl<>(filteredCalls, pageable, callsPage.getTotalElements());
    }

    private Call filterUnviewedComments(Call call) {
        if (call.getComments() == null) {
            call.setComments(List.of());
        } else {
            List<CallComment> unviewedComments = call.getComments().stream()
                    .filter(comment -> !comment.getViewed())
                    .collect(Collectors.toList());
            call.setComments(unviewedComments);
        }
        return call;
    }
}
