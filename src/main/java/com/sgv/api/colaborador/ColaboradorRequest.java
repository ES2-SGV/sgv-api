package com.sgv.api.colaborador;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class ColaboradorRequest {

  @NotBlank(message = "matrícula é obrigatória")
  @Pattern(regexp = "\\d{4}-\\d", message = "matrícula deve estar no formato XXXX-X (somente dígitos)")
  private String matricula;

  @NotBlank(message = "nome é obrigatório")
  private String nome;

  @NotNull(message = "área é obrigatória")
  private Long areaId;

  @NotNull(message = "cargo é obrigatório")
  private Cargo cargo;

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

  public Cargo getCargo() {
    return cargo;
  }

  public void setCargo(Cargo cargo) {
    this.cargo = cargo;
  }
}
