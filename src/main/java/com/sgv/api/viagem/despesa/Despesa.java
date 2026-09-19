package com.sgv.api.viagem.despesa;

import com.sgv.api.viagem.Viagem;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "despesa")
public class Despesa {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "viagem_id", nullable = false)
  private Viagem viagem;

  @Column(name = "data_despesa", nullable = false)
  private LocalDate dataDespesa;

  @Enumerated(EnumType.STRING)
  @Column(name = "tipo_despesa", nullable = false)
  private TipoDespesa tipoDespesa;

  @Column(nullable = false, length = 500)
  private String descricao;

  @Column(nullable = false, precision = 19, scale = 2)
  private BigDecimal valor;

  public Despesa() {
  }

  public Despesa(Viagem viagem, LocalDate dataDespesa, TipoDespesa tipoDespesa, String descricao, BigDecimal valor) {
    this.viagem = viagem;
    this.dataDespesa = dataDespesa;
    this.tipoDespesa = tipoDespesa;
    this.descricao = descricao;
    this.valor = valor;
  }

  public Long getId() {
    return id;
  }

  public Viagem getViagem() {
    return viagem;
  }

  public void setViagem(Viagem viagem) {
    this.viagem = viagem;
  }

  public LocalDate getDataDespesa() {
    return dataDespesa;
  }

  public void setDataDespesa(LocalDate dataDespesa) {
    this.dataDespesa = dataDespesa;
  }

  public TipoDespesa getTipoDespesa() {
    return tipoDespesa;
  }

  public void setTipoDespesa(TipoDespesa tipoDespesa) {
    this.tipoDespesa = tipoDespesa;
  }

  public String getDescricao() {
    return descricao;
  }

  public void setDescricao(String descricao) {
    this.descricao = descricao;
  }

  public BigDecimal getValor() {
    return valor;
  }

  public void setValor(BigDecimal valor) {
    this.valor = valor;
  }
}
