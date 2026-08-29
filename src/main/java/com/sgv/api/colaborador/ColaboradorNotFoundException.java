package com.sgv.api.colaborador;

import com.sgv.api.shared.NotFoundException;

public class ColaboradorNotFoundException extends NotFoundException {
  public ColaboradorNotFoundException(Long id) {
    super("Colaborador não encontrado: " + id);
  }
}
