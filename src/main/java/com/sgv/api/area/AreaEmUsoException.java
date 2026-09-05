package com.sgv.api.area;

import com.sgv.api.shared.ConflictException;

public class AreaEmUsoException extends ConflictException {
  public AreaEmUsoException(Long id) {
    super("Área possui colaboradores vinculados: " + id);
  }
}
