//Entity
package com.sgv.api.viagem;

import com.sgv.api.colaborador.Colaborador;
import com.sgv.api.colaborador.Lotacao;
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

  /**
   * A lotação do responsável no momento em que ele agiu — mesmo motivo do
   * {@code lotacao_id} da viagem: sem isto, promover alguém reescreveria o
   * cargo com que ele aprovou tudo o que já aprovou. Nula só nos registros
   * gravados antes desta coluna existir.
   */
  @ManyToOne
  @JoinColumn(name = "lotacao_id")
  private Lotacao lotacaoResponsavel;

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
    this.lotacaoResponsavel = responsavel.getLotacaoVigente();
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

  public Lotacao getLotacaoResponsavel() {
    return this.lotacaoResponsavel;
  }

  public LocalDateTime getRegistradoEm() {
    return this.registradoEm;
  }

  public String getObservacao() {
    return this.observacao;
  }
}
