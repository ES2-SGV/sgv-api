package com.sgv.api.destino;

public class DestinoResponse {

  private Long id;
  private String nome;
  private String cidade;
  private String pais;

  public DestinoResponse(Destino destino) {
    this.id = destino.getId();
    this.nome = destino.getNome();
    this.cidade = destino.getCidade();
    this.pais = destino.getPais();
  }

  public Long getId() {
    return id;
  }

  public String getNome() {
    return nome;
  }

  public String getCidade() {
    return cidade;
  }

  public String getPais() {
    return pais;
  }
}
