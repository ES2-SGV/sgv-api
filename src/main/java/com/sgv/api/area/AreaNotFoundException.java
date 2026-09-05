package com.sgv.api.area;

import com.sgv.api.shared.NotFoundException;

public class AreaNotFoundException extends NotFoundException {
  public AreaNotFoundException(Long id) {
    super("Área não encontrada: " + id);
  }
}
