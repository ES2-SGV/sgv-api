package com.sgv.api.viagem;

import com.sgv.api.colaborador.ColaboradorResponse;

import java.time.LocalDateTime;

public class ViagemHistoricoResponse {

  private Long id;
  private SituacaoViagem situacao;
  private ColaboradorResponse responsavel;
  private LocalDateTime registradoEm;
  private String observacao;

  public ViagemHistoricoResponse(ViagemHistorico historico) {
    this.id = historico.getId();
    this.situacao = historico.getSituacao();
    // Quem ele era ao agir, não quem é hoje.
    this.responsavel = new ColaboradorResponse(
        historico.getResponsavel(), historico.getLotacaoResponsavel());
    this.registradoEm = historico.getRegistradoEm();
    this.observacao = historico.getObservacao();
  }

  public Long getId() {
    return id;
  }

  public SituacaoViagem getSituacao() {
    return situacao;
  }

  public ColaboradorResponse getResponsavel() {
    return responsavel;
  }

  public LocalDateTime getRegistradoEm() {
    return registradoEm;
  }

  public String getObservacao() {
    return observacao;
  }
}
