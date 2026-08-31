package com.sgv.api.colaborador;

public class ColaboradorResponse {

  private Long id;
  private String matricula;
  private String nome;
  private String area;

  public ColaboradorResponse(Colaborador colaborador) {
    this.id = colaborador.getId();
    this.matricula = colaborador.getMatricula();
    this.nome = colaborador.getNome();
    this.area = colaborador.getArea();
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

  public String getArea() {
    return area;
  }
}
