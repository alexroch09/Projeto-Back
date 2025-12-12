package br.edu.lampi.infrareport.service.exceptions;

public class ResourceNotFoundException extends RuntimeException {
      public ResourceNotFoundException(String detail) {
            super(detail);
      }
}
