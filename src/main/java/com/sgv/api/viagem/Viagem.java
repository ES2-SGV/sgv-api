//Entity
package com.sgv.api.viagem;

import com.sgv.api.destino.Destino;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "viagem")

public class Viagem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "destino_id", nullable = false)
  private Destino destino;

  @Column(nullable = false)
  private String motivo;

  @Column(name = "data_saida", nullable = false)
  private LocalDate dataSaida;

  @Column(name = "data_retorno", nullable = false)
  private LocalDate dataRetorno;

  @Enumerated(EnumType.STRING)
  @Column(name = "meio_transporte", nullable = false)
  private MeioTransporte meioTransporte;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private SituacaoViagem situacao;

  public Viagem() {
  }

  public Viagem(Destino destino, String motivo, LocalDate dataSaida, LocalDate dataRetorno,
      MeioTransporte meioTransporte, SituacaoViagem situacao) {
    this.destino = destino;
    this.motivo = motivo;
    this.dataSaida = dataSaida;
    this.dataRetorno = dataRetorno;
    this.meioTransporte = meioTransporte;
    this.situacao = situacao;
  }

  public Long getId() {
    return this.id;
  }

  public Destino getDestino() {
    return this.destino;
  }

  public void setDestino(Destino destino) {
    this.destino = destino;
  }

  public String getMotivo() {
    return this.motivo;
  }

  public void setMotivo(String motivo) {
    this.motivo = motivo;
  }

  public LocalDate getDataSaida() {
    return this.dataSaida;
  }

  public void setDataSaida(LocalDate dataSaida) {
    this.dataSaida = dataSaida;
  }

  public LocalDate getDataRetorno() {
    return this.dataRetorno;
  }

  public void setDataRetorno(LocalDate dataRetorno) {
    this.dataRetorno = dataRetorno;
  }

  public MeioTransporte getMeioTransporte() {
    return this.meioTransporte;
  }

  public void setMeioTransporte(MeioTransporte meioTransporte) {
    this.meioTransporte = meioTransporte;
  }

  public SituacaoViagem getSituacao() {
    return this.situacao;
  }

  public void setSituacao(SituacaoViagem situacao) {
    this.situacao = situacao;
  }

}
