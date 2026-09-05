package com.sgv.api.colaborador;

import com.sgv.api.area.AreaResponse;

import java.time.LocalDateTime;

public class LotacaoResponse {

  private Long id;
  private AreaResponse area;
  private Cargo cargo;
  private LocalDateTime inicio;
  private LocalDateTime fim;

  public LotacaoResponse(Lotacao lotacao) {
    this.id = lotacao.getId();
    this.area = new AreaResponse(lotacao.getArea());
    this.cargo = lotacao.getCargo();
    this.inicio = lotacao.getInicio();
    this.fim = lotacao.getFim();
  }

  public Long getId() {
    return id;
  }

  public AreaResponse getArea() {
    return area;
  }

  public Cargo getCargo() {
    return cargo;
  }

  public LocalDateTime getInicio() {
    return inicio;
  }

  /** Nulo enquanto for a lotação vigente. */
  public LocalDateTime getFim() {
    return fim;
  }
}
