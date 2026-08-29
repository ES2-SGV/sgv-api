package com.sgv.api.viagem;

import com.sgv.api.destino.DestinoResponse;

import java.time.LocalDate;

public class ViagemResponse {

  private Long id;
  private DestinoResponse destino;
  private String motivo;
  private LocalDate dataSaida;
  private LocalDate dataRetorno;
  private MeioTransporte meioTransporte;
  private SituacaoViagem situacao;

  public ViagemResponse(Viagem viagem) {
    this.id = viagem.getId();
    this.destino = new DestinoResponse(viagem.getDestino());
    this.motivo = viagem.getMotivo();
    this.dataSaida = viagem.getDataSaida();
    this.dataRetorno = viagem.getDataRetorno();
    this.meioTransporte = viagem.getMeioTransporte();
    this.situacao = viagem.getSituacao();
  }

  public Long getId() {
    return id;
  }

  public DestinoResponse getDestino() {
    return destino;
  }

  public String getMotivo() {
    return motivo;
  }

  public LocalDate getDataSaida() {
    return dataSaida;
  }

  public LocalDate getDataRetorno() {
    return dataRetorno;
  }

  public MeioTransporte getMeioTransporte() {
    return meioTransporte;
  }

  public SituacaoViagem getSituacao() {
    return situacao;
  }
}
