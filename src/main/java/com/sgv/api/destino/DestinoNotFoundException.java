package com.sgv.api.destino;

import com.sgv.api.shared.NotFoundException;

public class DestinoNotFoundException extends NotFoundException {
  public DestinoNotFoundException(Long id) {
    super("Destino não encontrado: " + id);
  }
}
