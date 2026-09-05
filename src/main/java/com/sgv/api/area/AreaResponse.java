package com.sgv.api.area;

public class AreaResponse {

  private Long id;
  private String nome;

  public AreaResponse(Area area) {
    this.id = area.getId();
    this.nome = area.getNome();
  }

  public Long getId() {
    return id;
  }

  public String getNome() {
    return nome;
  }
}
