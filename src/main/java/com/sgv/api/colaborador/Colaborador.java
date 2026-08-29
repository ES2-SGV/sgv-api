//Entity
package com.sgv.api.colaborador;

import jakarta.persistence.*;

@Entity
@Table(name = "colaborador")

public class Colaborador {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String matricula;

  @Column(nullable = false)
  private String nome;

  @Column(nullable = false)
  private String area;

  public Colaborador() {
  }

  public Colaborador(String matricula, String nome, String area) {
    this.matricula = matricula;
    this.nome = nome;
    this.area = area;
  }

  public Long getId() {
    return this.id;
  }

  public String getMatricula() {
    return this.matricula;
  }

  public void setMatricula(String matricula) {
    this.matricula = matricula;
  }

  public String getNome() {
    return this.nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public String getArea() {
    return this.area;
  }

  public void setArea(String area) {
    this.area = area;
  }

}
