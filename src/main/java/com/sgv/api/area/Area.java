//Entity
package com.sgv.api.area;

import jakarta.persistence.*;

@Entity
@Table(name = "area")

public class Area {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String nome;

  public Area() {
  }

  public Area(String nome) {
    this.nome = nome;
  }

  public Long getId() {
    return this.id;
  }

  public String getNome() {
    return this.nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

}
