package com.sgv.api.viagem;

import com.sgv.api.shared.NotFoundException;

public class ViagemNotFoundException extends NotFoundException {
  public ViagemNotFoundException(Long id) {
    super("Viagem não encontrada: " + id);
  }
}
