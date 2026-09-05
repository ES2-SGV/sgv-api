//Entity
package com.sgv.api.colaborador;

import com.sgv.api.area.Area;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

  /**
   * Área e cargo não são campos daqui: são a lotação vigente. Guardá-los na
   * tabela faria um UPDATE reescrever o passado das viagens já solicitadas.
   */
  @OneToMany(mappedBy = "colaborador", cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy("inicio ASC, id ASC")
  private List<Lotacao> lotacoes = new ArrayList<>();

  public Colaborador() {
  }

  public Colaborador(String matricula, String nome, Area area, Cargo cargo) {
    this(matricula, nome, area, cargo, LocalDateTime.now());
  }

  public Colaborador(String matricula, String nome, Area area, Cargo cargo, LocalDateTime desde) {
    this.matricula = matricula;
    this.nome = nome;
    lotar(area, cargo, desde);
  }

  /** Fecha a lotação vigente, se houver, e abre outra. */
  public Lotacao lotar(Area area, Cargo cargo, LocalDateTime quando) {
    lotacoes.stream().filter(Lotacao::isVigente).forEach(l -> l.encerrar(quando));
    Lotacao nova = new Lotacao(this, area, cargo, quando);
    lotacoes.add(nova);
    return nova;
  }

  public Lotacao getLotacaoVigente() {
    return lotacoes.stream()
        .filter(Lotacao::isVigente)
        .findFirst()
        .orElseThrow(() -> new IllegalStateException(
            "colaborador " + id + " está sem lotação vigente"));
  }

  public List<Lotacao> getLotacoes() {
    return List.copyOf(lotacoes);
  }

  public Area getArea() {
    return getLotacaoVigente().getArea();
  }

  public Cargo getCargo() {
    return getLotacaoVigente().getCargo();
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

}
