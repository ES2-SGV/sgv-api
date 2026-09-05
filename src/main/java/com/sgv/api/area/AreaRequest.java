package com.sgv.api.area;

import jakarta.validation.constraints.NotBlank;

public class AreaRequest {

  @NotBlank(message = "nome é obrigatório")
  private String nome;

  public String getNome() {
    return nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }
}
