package com.sgv.api.viagem.despesa;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DespesaResponse(
    Long id,
    LocalDate dataDespesa,
    TipoDespesa tipoDespesa,
    String descricao,
    BigDecimal valor
) {
  public DespesaResponse(Despesa d) {
    this(d.getId(), d.getDataDespesa(), d.getTipoDespesa(), d.getDescricao(), d.getValor());
  }
}
