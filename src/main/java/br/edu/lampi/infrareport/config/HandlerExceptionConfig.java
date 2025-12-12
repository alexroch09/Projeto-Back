package br.edu.lampi.infrareport.config;

import br.edu.lampi.infrareport.service.exceptions.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class HandlerExceptionConfig {

      @ExceptionHandler(BadRequestException.class)
      public ResponseEntity<ExceptionDetails> handlerBadRequestException(BadRequestException exception) {
            ExceptionDetails details = new ExceptionDetails("Bad Request Exception. Please, Submit a Valid Request",
                        exception.getMessage(), HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(details);
      }

      @ExceptionHandler(ResourceNotFoundException.class)
      public ResponseEntity<ExceptionDetails> handlerResourceNotFoundException(ResourceNotFoundException exception) {
            ExceptionDetails details = new ExceptionDetails("Resource Not Found in Database", exception.getMessage(),
                        HttpStatus.NOT_FOUND.value());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(details);
      }

      @ExceptionHandler(ConflictException.class)
      public ResponseEntity<ExceptionDetails> handlerConflictException(ConflictException exception) {
            ExceptionDetails details = new ExceptionDetails("Data Conflict", exception.getMessage(),
                        HttpStatus.CONFLICT.value());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(details);
      }

      @ExceptionHandler(HttpMessageNotReadableException.class)
      public ResponseEntity<ExceptionDetails> handlerHttpMessageNotReadableException(
                  HttpMessageNotReadableException exception) {
            ExceptionDetails details = new ExceptionDetails("Bad request. syntax error",
                        exception.getMessage(),
                        HttpStatus.BAD_REQUEST.value());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(details);
      }

      @ExceptionHandler(EmailAlreadyRegisteredException.class)
      public ResponseEntity<ExceptionDetails> handlerEmailAlreadyRegisteredException(
            EmailAlreadyRegisteredException exception) {
            ExceptionDetails details = new ExceptionDetails("Email already registered.",
                  exception.getMessage(),
                  HttpStatus.CONFLICT.value());

            return ResponseEntity.status(HttpStatus.CONFLICT).body(details);
      }

      @ExceptionHandler(UserNotFoundException.class)
      public ResponseEntity<ExceptionDetails> handlerUserNotFoundException(UserNotFoundException exception) {
            ExceptionDetails details = new ExceptionDetails("User not found.",
                  exception.getMessage(),
                  HttpStatus.NOT_FOUND.value());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(details);
      }

      @ExceptionHandler(MethodArgumentNotValidException.class)
      public ResponseEntity<ExceptionDetails> handlerMethodArgumentNotValidException(
                  MethodArgumentNotValidException exception) {
            List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
            ExceptionDetails details = new ExceptionDetails("Bad request, fields are not filled in correctly",
                        "the following fields were filled in incorrectly", HttpStatus.BAD_REQUEST.value());  
            details.setFields(fieldErrors
                  .stream()
                  .map(ArgumentNotValidDetails::new)
                  .collect(Collectors.toList())
                  );

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(details);
      }

      @ExceptionHandler(MethodArgumentTypeMismatchException.class)
      public ResponseEntity<ExceptionDetails> handlerMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception) {
            ExceptionDetails details = new ExceptionDetails("Conversion error",
                  exception.getMessage(),
                  HttpStatus.BAD_REQUEST.value());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(details);
      }

      @ExceptionHandler(UnauthorizedException.class)
      public ResponseEntity<ExceptionDetails> handlerUnauthorizedException(UnauthorizedException exception) {
            ExceptionDetails details = new ExceptionDetails("Unauthorized",
                  exception.getMessage(),
                  HttpStatus.UNAUTHORIZED.value());

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(details);
      }

      @ExceptionHandler(TokenPasswordResetException.class)
      public ResponseEntity<ExceptionDetails> handlerTokenPasswordResetException(TokenPasswordResetException exception) {
            ExceptionDetails details = new ExceptionDetails("Invalid or expired token",
                    exception.getMessage(),
                    HttpStatus.BAD_REQUEST.value());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(details);
      }
}
