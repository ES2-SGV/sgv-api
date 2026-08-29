package com.sgv.api.colaborador;

import com.sgv.api.shared.ConflictException;

public class MatriculaJaCadastradaException extends ConflictException {
  public MatriculaJaCadastradaException(String matricula) {
    super("Matrícula já cadastrada: " + matricula);
  }
}
