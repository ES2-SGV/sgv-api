package com.sgv.api.area;

import com.sgv.api.shared.ConflictException;

public class NomeAreaJaCadastradoException extends ConflictException {
  public NomeAreaJaCadastradoException(String nome) {
    super("Área já cadastrada: " + nome);
  }
}
