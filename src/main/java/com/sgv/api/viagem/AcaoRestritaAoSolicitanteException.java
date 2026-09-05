package com.sgv.api.viagem;

import com.sgv.api.shared.ForbiddenException;

public class AcaoRestritaAoSolicitanteException extends ForbiddenException {
  public AcaoRestritaAoSolicitanteException(Long viagemId, Long atorId) {
    super("colaborador " + atorId + " não é o solicitante da viagem " + viagemId);
  }
}
