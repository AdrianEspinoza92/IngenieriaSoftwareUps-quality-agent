package org.citasalud.infrastructure.web.controller;

import org.citasalud.api.model.ErrorResponse;
import org.citasalud.domain.exception.FranjaOcupadaException;
import org.citasalud.domain.exception.RecursoNoEncontradoException;
import org.citasalud.infrastructure.web.mapper.ApiMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FranjaOcupadaException.class)
    public ResponseEntity<ErrorResponse> handleFranjaOcupada(FranjaOcupadaException ex) {
        ErrorResponse error = new ErrorResponse();
        error.setCodigo("FRANJA_OCUPADA");
        error.setMensaje(ex.getMessage());
        if (!ex.getAlternativas().isEmpty()) {
            error.setFranjasAlternativas(
                ex.getAlternativas().stream()
                    .filter(f -> f.estaDisponible())
                    .map(ApiMapper::toFranjaResponse)
                    .toList()
            );
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> handleOptimisticLock(ObjectOptimisticLockingFailureException ex) {
        ErrorResponse error = new ErrorResponse();
        error.setCodigo("FRANJA_OCUPADA");
        error.setMensaje("La franja horaria fue reservada por otro usuario simultáneamente");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(RecursoNoEncontradoException ex) {
        ErrorResponse error = new ErrorResponse();
        error.setCodigo("RECURSO_NO_ENCONTRADO");
        error.setMensaje(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        org.slf4j.LoggerFactory.getLogger(GlobalExceptionHandler.class).error("Unexpected error", ex);
        ErrorResponse error = new ErrorResponse();
        error.setCodigo("ERROR_INTERNO");
        error.setMensaje("Ha ocurrido un error interno");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
