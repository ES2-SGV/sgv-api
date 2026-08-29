package com.sgv.api.colaborador;

import jakarta.validation.constraints.NotBlank;

public class ColaboradorRequest {

  @NotBlank(message = "matrícula é obrigatória")
  private String matricula;

  @NotBlank(message = "nome é obrigatório")
  private String nome;

  @NotBlank(message = "área é obrigatória")
  private String area;

  public String getMatricula() {
    return matricula;
  }

  public void setMatricula(String matricula) {
    this.matricula = matricula;
  }

  public String getNome() {
    return nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public String getArea() {
    return area;
  }

  public void setArea(String area) {
    this.area = area;
  }
}
