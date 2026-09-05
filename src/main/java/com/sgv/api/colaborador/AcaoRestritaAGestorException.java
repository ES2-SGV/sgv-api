package com.sgv.api.colaborador;

import com.sgv.api.shared.ForbiddenException;

public class AcaoRestritaAGestorException extends ForbiddenException {
  public AcaoRestritaAGestorException(Colaborador ator) {
    super("Ação restrita a gestores: colaborador " + ator.getId()
        + " tem o cargo " + ator.getCargo());
  }
}
