package com.sgv.api.viagem;

import com.sgv.api.colaborador.ColaboradorResponse;
import com.sgv.api.destino.DestinoResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import com.sgv.api.viagem.despesa.Despesa;

public class ViagemResponse {

  private Long id;
  private DestinoResponse destino;
  private ColaboradorResponse colaborador;
  private String motivo;
  private LocalDate dataSaida;
  private LocalDate dataRetorno;
  private MeioTransporte meioTransporte;
  private SituacaoViagem situacao;
  private String motivoAjuste;
  private BigDecimal valorTotal;

  public ViagemResponse(Viagem viagem) {
    this.id = viagem.getId();
    this.destino = new DestinoResponse(viagem.getDestino());
    // Não é o colaborador de hoje: é quem ele era quando solicitou. Em
    // RASCUNHO ainda não há lotação congelada, e aí vale a vigente.
    this.colaborador =
        new ColaboradorResponse(viagem.getColaborador(), viagem.getLotacaoSolicitante());
    this.motivo = viagem.getMotivo();
    this.dataSaida = viagem.getDataSaida();
    this.dataRetorno = viagem.getDataRetorno();
    this.meioTransporte = viagem.getMeioTransporte();
    this.situacao = viagem.getSituacao();
    this.motivoAjuste = viagem.getMotivoAjuste();
    this.valorTotal = viagem.getDespesas() != null 
        ? viagem.getDespesas().stream().map(Despesa::getValor).reduce(BigDecimal.ZERO, BigDecimal::add)
        : BigDecimal.ZERO;
  }

  public Long getId() {
    return id;
  }

  public DestinoResponse getDestino() {
    return destino;
  }

  public ColaboradorResponse getColaborador() {
    return colaborador;
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

  public String getMotivoAjuste() {
    return motivoAjuste;
  }

  public BigDecimal getValorTotal() {
    return valorTotal;
  }
}
