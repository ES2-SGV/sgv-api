package com.sgv.api.colaborador;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ColaboradorRequest {

  @NotBlank(message = "matrícula é obrigatória")
  private String matricula;

  @NotBlank(message = "nome é obrigatório")
  private String nome;

  @NotNull(message = "área é obrigatória")
  private Long areaId;

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

  public Long getAreaId() {
    return areaId;
  }

  public void setAreaId(Long areaId) {
    this.areaId = areaId;
  }
}
