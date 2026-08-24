package com.sgv.api.destino;

public class DestinoNotFoundException extends RuntimeException {
  public DestinoNotFoundException(Long id) {
    super("Destino não encontrado: " + id);
  }
}
