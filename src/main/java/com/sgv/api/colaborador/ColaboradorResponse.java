package com.sgv.api.colaborador;

import com.sgv.api.area.AreaResponse;

public class ColaboradorResponse {

  private Long id;
  private String matricula;
  private String nome;
  private AreaResponse area;
  private Cargo cargo;

  /** Área e cargo de hoje. */
  public ColaboradorResponse(Colaborador colaborador) {
    this(colaborador, colaborador.getLotacaoVigente());
  }

  /**
   * Área e cargo de uma lotação específica — a de quando a viagem foi
   * solicitada, por exemplo. Passar {@code null} cai na vigente.
   */
  public ColaboradorResponse(Colaborador colaborador, Lotacao lotacao) {
    Lotacao efetiva = lotacao != null ? lotacao : colaborador.getLotacaoVigente();
    this.id = colaborador.getId();
    this.matricula = colaborador.getMatricula();
    this.nome = colaborador.getNome();
    this.area = new AreaResponse(efetiva.getArea());
    this.cargo = efetiva.getCargo();
  }

  public Long getId() {
    return id;
  }

  public String getMatricula() {
    return matricula;
  }

  public String getNome() {
    return nome;
  }

  public AreaResponse getArea() {
    return area;
  }

  public Cargo getCargo() {
    return cargo;
  }
}
