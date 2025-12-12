package br.edu.lampi.infrareport.service.exceptions;

public class ConflictException extends RuntimeException {
      public ConflictException(String detail) {
            super(detail);
      }
}
