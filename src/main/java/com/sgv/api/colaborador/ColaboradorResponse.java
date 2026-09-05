package com.sgv.api.colaborador;

import com.sgv.api.area.AreaResponse;

public class ColaboradorResponse {

  private Long id;
  private String matricula;
  private String nome;
  private AreaResponse area;
  private Cargo cargo;

  public ColaboradorResponse(Colaborador colaborador) {
    this.id = colaborador.getId();
    this.matricula = colaborador.getMatricula();
    this.nome = colaborador.getNome();
    this.area = new AreaResponse(colaborador.getArea());
    this.cargo = colaborador.getCargo();
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
