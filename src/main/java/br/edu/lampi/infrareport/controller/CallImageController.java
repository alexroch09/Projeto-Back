package br.edu.lampi.infrareport.controller;

import br.edu.lampi.infrareport.controller.dto.callImage.CallImageRequestDTO;
import br.edu.lampi.infrareport.controller.dto.callImage.CallImageResponseDTO;
import br.edu.lampi.infrareport.model.callImage.CallImage;
import br.edu.lampi.infrareport.service.CallImageService;
import br.edu.lampi.infrareport.repository.CallRepository;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/image")
public class CallImageController {

    @Value("${infrareport.upload.dir}")
    private String uploadDir;

    @Autowired
    private CallImageService callImageService;

    @Autowired
    private CallRepository callRepository;

    @GetMapping("/{id}")
    @Operation(summary = "Return CallImage by ID", tags = "Image")
    public ResponseEntity<CallImageResponseDTO> getById(@PathVariable("id") Long id) {
        CallImage callImage = callImageService.getById(id);
        return new ResponseEntity<>(new CallImageResponseDTO(callImage), HttpStatusCode.valueOf(200));
    }

    @GetMapping
    @Operation(summary = "Return all CallImages", tags = "Image")
    public List<CallImageResponseDTO> getAll() {
        return callImageService.getAll()
                .stream()
                .map(CallImageResponseDTO::new)
                .toList();
    }

    @GetMapping("/call/{id}")
    @Operation(summary = "Return CallImages by Call ID", tags = "Image")
    public List<CallImageResponseDTO> getByIdCall(@PathVariable("id") Long id) {
        return callImageService.getByCallId(id)
                .stream()
                .map(CallImageResponseDTO::new)
                .toList();
    }

    @PostMapping
    @Operation(summary = "Save CallImage metadata", tags = "Image")
    public ResponseEntity<CallImageResponseDTO> saveCallImage(
            @RequestBody CallImageRequestDTO callImageRequestDTO,
            UriComponentsBuilder uriComponentsBuilder) {

        CallImage savedCallImage = callImageService.save(callImageRequestDTO);
        URI uri = uriComponentsBuilder.path("/image/{id}").buildAndExpand(savedCallImage.getId()).toUri();
        return ResponseEntity.created(uri).body(new CallImageResponseDTO(savedCallImage));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update CallImage metadata", tags = "Image")
    public ResponseEntity<CallImageResponseDTO> updateCallImage(
            @PathVariable("id") Long id,
            @RequestBody CallImageRequestDTO callImageRequestDTO) {

        CallImage updated = callImageService.updateById(id, callImageRequestDTO);
        return ResponseEntity.ok(new CallImageResponseDTO(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete CallImage by ID", tags = "Image")
    public ResponseEntity<Void> deleteById(@PathVariable("id") Long id) {
        callImageService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload image and store locally", tags = "Image")
    public ResponseEntity<CallImageResponseDTO> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("callId") Long callId,
            UriComponentsBuilder uriBuilder) {

        // Verifica se o callId existe
        if (!callRepository.existsById(callId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(null);
        }

        try {
            String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
            String upl = System.getProperty("user.dir") + "/uploads/";
            File found = new File(upl);

            if (!found.exists()) {
                found.mkdir();
            }

            Path filePath = Paths.get(upl + file.getOriginalFilename());

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            CallImageRequestDTO dto = new CallImageRequestDTO();
            dto.setFileName(fileName);
            dto.setPath(uploadDir + file.getOriginalFilename());
            dto.setCallId(callId);

            CallImage saved = callImageService.save(dto);

            URI uri = uriBuilder.path("/image/{id}").buildAndExpand(saved.getId()).toUri();
            return ResponseEntity.created(uri).body(new CallImageResponseDTO(saved));

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}