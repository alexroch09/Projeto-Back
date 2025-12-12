package br.edu.lampi.infrareport.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import br.edu.lampi.infrareport.controller.dto.callComment.CallCommentRequestDTO;
import br.edu.lampi.infrareport.controller.dto.callComment.CallCommentResponseDTO;
import br.edu.lampi.infrareport.model.call.Call;
import br.edu.lampi.infrareport.model.callComment.CallComment;
import br.edu.lampi.infrareport.model.user.User;
import br.edu.lampi.infrareport.repository.CallCommentRepository;
import br.edu.lampi.infrareport.repository.CallRepository;
import br.edu.lampi.infrareport.repository.UserRepository;
import br.edu.lampi.infrareport.service.exceptions.BadRequestException;
import br.edu.lampi.infrareport.service.exceptions.ResourceNotFoundException;

@Service
public class CallCommentService {
    @Autowired
    private CallCommentRepository callCommentRepository;

    @Autowired
    private CallRepository callRepository;

    @Autowired
    private UserRepository userRepository;


    public CallComment save(CallCommentRequestDTO callCommentRequestDTO) {
        if(callCommentRequestDTO.userId() != null && !this.userRepository.existsById(callCommentRequestDTO.userId())){
            throw new BadRequestException("user does not exists");
        }

        if(callCommentRequestDTO.call_id() == null){
            throw new BadRequestException("The given call id must not be null");
        }
        long call_id = callCommentRequestDTO.call_id();
        if(!this.callRepository.existsById(call_id)){
            throw new BadRequestException("call does not exists");
        }
    
        Call call = callRepository.getReferenceById(call_id);
        CallComment callComment = new CallComment();
        callComment.setCall(call);
        User user = userRepository.getReferenceById(callCommentRequestDTO.userId());
        callComment.setUser(user);
        callComment.setDateTime(callCommentRequestDTO.dateTime());
        callComment.setMessage(callCommentRequestDTO.message());
        callComment.setViewed(callCommentRequestDTO.viewed());
        return this.callCommentRepository.save(callComment);
        
    }

    public boolean existsById(Long id){
        if(id != null){
            return callCommentRepository.existsById(id);
        }
        return false;
    }

    public List<CallComment> getAll() {
       return this.callCommentRepository.findAll();
    }
    
    public Page<CallCommentResponseDTO> getAllPageable(Pageable pageable){
        if(pageable != null){
            return callCommentRepository.findAll(pageable).map(CallCommentResponseDTO::new);
        }
        return null;
    }

    public CallComment getById(@NonNull Long id) {
       return this.callCommentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("call comment not found by the given id"));
    }

    public List<CallComment> getByCallId(@NonNull Long callId) {
        return this.callCommentRepository.findByCallId(callId);
    }

    public void deleteById(@NonNull Long id) {
        CallComment c = this.getById(id);
        if(c == null){
            throw new BadRequestException("call comment not found by the given id");
        }
        this.callCommentRepository.delete(c);
    }

    public CallComment updateById(@NonNull Long id, CallCommentRequestDTO callCommentRequestDTO) {
        CallComment oldc = this.getById(id);

        if(callCommentRequestDTO.userId() != null && !this.userRepository.existsById(callCommentRequestDTO.userId())){
            throw new BadRequestException("user does not exists");
        }

        User user = userRepository.getReferenceById(callCommentRequestDTO.userId());
        oldc.setUser(user);
        
        if(callCommentRequestDTO.call_id() == null){
            throw new BadRequestException("The given id must not be null");
        }
        long call_id = callCommentRequestDTO.call_id();
        if(!this.callRepository.existsById(call_id)){
            throw new BadRequestException("call does not exists");
        }
    
        Call call = callRepository.getReferenceById(call_id);
        oldc.setCall(call);

        oldc.setDateTime(callCommentRequestDTO.dateTime());
        oldc.setMessage(callCommentRequestDTO.message());
        oldc.setViewed(callCommentRequestDTO.viewed());

        return this.callCommentRepository.save(oldc);
    }

    
    public void addViewedStatusToCallCommentByCallId(Long id) {
        Call call = this.callRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("call comment not found by the given id"));
        List<CallComment> comments = call.getComments();

        if(comments != null){
            comments.forEach(comment -> comment.setViewed(true));
            
            this.callCommentRepository.saveAll(comments);
        }
    }
}
