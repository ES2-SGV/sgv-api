package com.sgv.api.viagem;

import com.sgv.api.shared.ConflictException;

import java.util.Arrays;
import java.util.stream.Collectors;

public class TransicaoInvalidaException extends ConflictException {

  public TransicaoInvalidaException(String acao, SituacaoViagem atual, SituacaoViagem... permitidas) {
    super("não é possível " + acao + " uma viagem " + atual
        + ": a situação precisa ser "
        + Arrays.stream(permitidas).map(Enum::name).collect(Collectors.joining(" ou ")));
  }
}
