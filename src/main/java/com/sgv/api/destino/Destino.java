//Entity
package com.sgv.api.destino;

import jakarta.persistence.*;

@Entity
@Table(name = "destino")

public class Destino {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(nullable = false)
  private String nome;

  @Column(nullable = false)
  private String cidade;

  @Column(nullable = false)
  private String pais;

  public Destino() {
  }

  public Destino(String nome, String cidade, String pais) {
    this.nome = nome;
    this.cidade = cidade;
    this.pais = pais;
  }

  public Long getId() {
    return id;
  };

  public String getNome() {
    return this.nome;
  };

  public void setNome(String nome) {
    this.nome = nome;
  };

  public String getCidade() {
    return this.cidade;
  };

  public void setCidade(String cidade) {
    this.cidade = cidade;
  };

  public String getPais() {
    return this.pais;
  };

  public void setPais(String pais) {
    this.pais = pais;
  }

}
