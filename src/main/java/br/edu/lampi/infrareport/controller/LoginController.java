package br.edu.lampi.infrareport.controller;

import br.edu.lampi.infrareport.controller.dto.user.*;
import br.edu.lampi.infrareport.service.PasswordResetService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.lampi.infrareport.model.user.User;
import br.edu.lampi.infrareport.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/login")
@Tag(name = "Login", description = "This is api of login.")
public class LoginController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private PasswordResetService passwordResetService;

    @Operation(summary = "user login.", description = "release of resources to users.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User login success."),
                    @ApiResponse(responseCode = "400", description = "Email or password is blank.", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Invalid credentials or unauthenticated user.", content = @Content(schema = @Schema(hidden = true))),
            })
    @PostMapping
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO data){
        var authenticationToken = new UsernamePasswordAuthenticationToken(data.email(), data.password());
        var auth = this.authenticationManager.authenticate(authenticationToken);

        var token = this.tokenService.generateToken((User) auth.getPrincipal());
        
        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @Operation(summary = "Forgot password request.", description = "generate token to reset password.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Token generated and email sent."),
                    @ApiResponse(responseCode = "404", description = "User not found.", content = @Content),
            })
    @PostMapping("/forgotpasswordrequest")
    @Transactional
    public ResponseEntity<String> forgotPasswordRequest(@Valid @RequestBody ForgotPasswordRequestDTO data) {
        passwordResetService.createPasswordReset(data.email());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "user login.", description = "release of resources to users.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User login success."),
                    @ApiResponse(responseCode = "400", description = "Invalid or expired token.", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Email fail.", content = @Content)
               })
    @PostMapping("/forgotpasswordnew")
    @Transactional
    public ResponseEntity<String> forgotPasswordNew(@Valid @RequestBody ForgotPasswordNewRequestDTO data) {
        passwordResetService.resetPassword(data);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "user login.", description = "release of resources to users.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User login success."),
                    @ApiResponse(responseCode = "400", description = "Invalid or expired token.", content = @Content),
             })
    @PostMapping("/validatepasswordresettoken")
    @Transactional
    public ResponseEntity<String> validatePasswordResetToken(@Valid @RequestBody ValidatePasswordResetToken data) {
        passwordResetService.validatePasswordResetToken(data);
        return ResponseEntity.ok().build();
    }
}
