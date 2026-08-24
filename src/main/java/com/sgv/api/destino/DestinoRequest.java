package com.sgv.api.destino;

import jakarta.validation.constraints.NotBlank;

public class DestinoRequest {

  @NotBlank(message = "nome é obrigatório")
  private String nome;

  @NotBlank(message = "cidade é obrigatória")
  private String cidade;

  @NotBlank(message = "país é obrigatório")
  private String pais;

  public String getNome() {
    return nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public String getCidade() {
    return cidade;
  }

  public void setCidade(String cidade) {
    this.cidade = cidade;
  }

  public String getPais() {
    return pais;
  }

  public void setPais(String pais) {
    this.pais = pais;
  }
}
