//Entity
package com.sgv.api.viagem;

import com.sgv.api.colaborador.Colaborador;
import com.sgv.api.colaborador.Lotacao;
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

  @ManyToOne(optional = false)
  @JoinColumn(name = "colaborador_id", nullable = false)
  private Colaborador colaborador;

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

  /**
   * A lotação do solicitante no instante em que a viagem foi solicitada.
   * Nula enquanto ela nunca saiu de RASCUNHO — rascunho não é solicitação.
   * A linha apontada nunca tem área nem cargo alterados (só ganha `fim`), e é
   * isso que faz esta viagem continuar dizendo a verdade depois de a pessoa
   * mudar de área.
   */
  @ManyToOne
  @JoinColumn(name = "lotacao_id")
  private Lotacao lotacaoSolicitante;

  /** O que o gestor pediu para ajustar. Preenchido só enquanto EM_AJUSTE. */
  @Column(name = "motivo_ajuste", length = 500)
  private String motivoAjuste;

  public Viagem() {
  }

  public Viagem(Destino destino, Colaborador colaborador, String motivo, LocalDate dataSaida,
      LocalDate dataRetorno, MeioTransporte meioTransporte, SituacaoViagem situacao) {
    this.destino = destino;
    this.colaborador = colaborador;
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

  public Colaborador getColaborador() {
    return this.colaborador;
  }

  public void setColaborador(Colaborador colaborador) {
    this.colaborador = colaborador;
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

  public Lotacao getLotacaoSolicitante() {
    return this.lotacaoSolicitante;
  }

  public void setLotacaoSolicitante(Lotacao lotacaoSolicitante) {
    this.lotacaoSolicitante = lotacaoSolicitante;
  }

  public String getMotivoAjuste() {
    return this.motivoAjuste;
  }

  public void setMotivoAjuste(String motivoAjuste) {
    this.motivoAjuste = motivoAjuste;
  }

}
