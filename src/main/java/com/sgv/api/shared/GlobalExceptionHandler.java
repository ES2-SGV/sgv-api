package com.sgv.api.shared;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(NotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ApiError handleNotFound(NotFoundException ex) {
    return new ApiError(HttpStatus.NOT_FOUND.value(), ex.getMessage());
  }

  @ExceptionHandler(ForbiddenException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public ApiError handleForbidden(ForbiddenException ex) {
    return new ApiError(HttpStatus.FORBIDDEN.value(), ex.getMessage());
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

  // Sem estes dois, um header ausente ou não-numérico devolveria o corpo
  // padrão do Spring, e não o ApiError que o resto da API usa.
  @ExceptionHandler(MissingRequestHeaderException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApiError handleHeaderAusente(MissingRequestHeaderException ex) {
    return new ApiError(HttpStatus.BAD_REQUEST.value(),
        "header obrigatório ausente: " + ex.getHeaderName());
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApiError handleTipoInvalido(MethodArgumentTypeMismatchException ex) {
    return new ApiError(HttpStatus.BAD_REQUEST.value(),
        "valor inválido para " + ex.getName() + ": " + ex.getValue());
  }

  // Enum fora do conjunto ("situacao": "DIRETOR"), JSON malformado, data
  // impossível: o Jackson estoura antes do @Valid. Sem isto, viraria 500.
  @ExceptionHandler(HttpMessageNotReadableException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApiError handleCorpoIlegivel(HttpMessageNotReadableException ex) {
    return new ApiError(HttpStatus.BAD_REQUEST.value(), "corpo da requisição inválido");
  }
}
