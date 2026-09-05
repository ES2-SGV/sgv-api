//Entity
package com.sgv.api.colaborador;

import com.sgv.api.area.Area;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Onde o colaborador esteve alocado e em que cargo, durante um período.
 *
 * <p>Linha fechada não muda mais: é o que permite uma viagem antiga continuar
 * dizendo a área e o cargo de quem a solicitou, mesmo depois de a pessoa mudar
 * de área ou ser promovida. {@code fim} nulo significa "vale até hoje".
 */
@Entity
@Table(name = "colaborador_lotacao")
public class Lotacao {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "colaborador_id", nullable = false)
  private Colaborador colaborador;

  @ManyToOne(optional = false)
  @JoinColumn(name = "area_id", nullable = false)
  private Area area;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Cargo cargo;

  @Column(nullable = false)
  private LocalDateTime inicio;

  private LocalDateTime fim;

  public Lotacao() {
  }

  public Lotacao(Colaborador colaborador, Area area, Cargo cargo, LocalDateTime inicio) {
    this.colaborador = colaborador;
    this.area = area;
    this.cargo = cargo;
    this.inicio = inicio;
  }

  public boolean isVigente() {
    return fim == null;
  }

  void encerrar(LocalDateTime quando) {
    this.fim = quando;
  }

  public Long getId() {
    return this.id;
  }

  public Colaborador getColaborador() {
    return this.colaborador;
  }

  public Area getArea() {
    return this.area;
  }

  public Cargo getCargo() {
    return this.cargo;
  }

  public LocalDateTime getInicio() {
    return this.inicio;
  }

  public LocalDateTime getFim() {
    return this.fim;
  }
}
