//Entity
package com.sgv.api.viagem;

import com.sgv.api.colaborador.Colaborador;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Uma linha por mudança de situação — inclusive a criação em RASCUNHO.
 * Registro de auditoria: nasce e não muda mais.
 */
@Entity
@Table(name = "viagem_historico")
public class ViagemHistorico {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "viagem_id", nullable = false)
  private Viagem viagem;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private SituacaoViagem situacao;

  @ManyToOne(optional = false)
  @JoinColumn(name = "responsavel_id", nullable = false)
  private Colaborador responsavel;

  @Column(name = "registrado_em", nullable = false)
  private LocalDateTime registradoEm;

  /** O que o gestor mandou ajustar. Nulo nas demais transições. */
  @Column(length = 500)
  private String observacao;

  public ViagemHistorico() {
  }

  public ViagemHistorico(Viagem viagem, Colaborador responsavel, LocalDateTime registradoEm,
      String observacao) {
    this.viagem = viagem;
    this.situacao = viagem.getSituacao();
    this.responsavel = responsavel;
    this.registradoEm = registradoEm;
    this.observacao = observacao;
  }

  public Long getId() {
    return this.id;
  }

  public Viagem getViagem() {
    return this.viagem;
  }

  public SituacaoViagem getSituacao() {
    return this.situacao;
  }

  public Colaborador getResponsavel() {
    return this.responsavel;
  }

  public LocalDateTime getRegistradoEm() {
    return this.registradoEm;
  }

  public String getObservacao() {
    return this.observacao;
  }
}
