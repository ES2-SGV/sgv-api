package com.sgv.api.shared;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(NotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ApiError handleNotFound(NotFoundException ex) {
    return new ApiError(HttpStatus.NOT_FOUND.value(), ex.getMessage());
  }

  @ExceptionHandler(ConflictException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ApiError handleConflict(ConflictException ex) {
    return new ApiError(HttpStatus.CONFLICT.value(), ex.getMessage());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApiError handleValidation(MethodArgumentNotValidException ex) {
    Map<String, String> campos = new LinkedHashMap<>();
    for (FieldError erro : ex.getBindingResult().getFieldErrors()) {
      campos.putIfAbsent(erro.getField(), erro.getDefaultMessage());
    }
    return new ApiError(HttpStatus.BAD_REQUEST.value(), "dados inválidos", campos);
  }
}
