package com.sgv.api.colaborador;

import com.sgv.api.area.Area;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * O exemplo do enunciado: WILLIAN muda de área e depois de cargo, e cada
 * lotação encerrada continua contando a verdade do período dela.
 */
class ColaboradorLotacaoTest {

  private static final Area INFORMATICA = new Area("INFORMATICA");
  private static final Area RH = new Area("RH");

  private static final LocalDateTime DIA_01 = LocalDateTime.of(2026, 9, 1, 8, 0);
  private static final LocalDateTime DIA_02 = LocalDateTime.of(2026, 9, 2, 8, 0);
  private static final LocalDateTime DIA_10 = LocalDateTime.of(2026, 9, 10, 8, 0);

  private Colaborador willian() {
    return new Colaborador("1001-2", "WILLIAN", INFORMATICA, Cargo.COLABORADOR, DIA_01);
  }

  @Test
  void colaboradorNasceComUmaLotacaoVigente() {
    Colaborador willian = willian();

    assertThat(willian.getLotacoes()).hasSize(1);
    assertThat(willian.getLotacaoVigente().isVigente()).isTrue();
    assertThat(willian.getArea()).isSameAs(INFORMATICA);
    assertThat(willian.getCargo()).isEqualTo(Cargo.COLABORADOR);
  }

  @Test
  void mudarDeAreaFechaAAnteriorEAbreOutra() {
    Colaborador willian = willian();

    willian.lotar(RH, Cargo.COLABORADOR, DIA_02);

    List<Lotacao> lotacoes = willian.getLotacoes();
    assertThat(lotacoes).hasSize(2);
    assertThat(lotacoes.get(0).getArea()).isSameAs(INFORMATICA);
    assertThat(lotacoes.get(0).getFim()).isEqualTo(DIA_02);
    assertThat(lotacoes.get(1).getArea()).isSameAs(RH);
    assertThat(lotacoes.get(1).getFim()).isNull();
    assertThat(willian.getArea()).isSameAs(RH);
  }

  @Test
  void haSempreExatamenteUmaLotacaoVigente() {
    Colaborador willian = willian();
    willian.lotar(RH, Cargo.COLABORADOR, DIA_02);
    willian.lotar(RH, Cargo.GESTOR, DIA_10);

    assertThat(willian.getLotacoes().stream().filter(Lotacao::isVigente)).hasSize(1);
  }

  @Test
  void oExemploDoEnunciado() {
    Colaborador willian = willian();
    Lotacao emInformatica = willian.getLotacaoVigente();

    // 02/09: muda para a área RH
    Lotacao noRh = willian.lotar(RH, Cargo.COLABORADOR, DIA_02);
    // 10/09: vira GESTOR da área RH
    Lotacao gestorDoRh = willian.lotar(RH, Cargo.GESTOR, DIA_10);

    // A viagem de 01/09 aponta para a primeira lotação, que segue dizendo
    // INFORMATICA/COLABORADOR mesmo com o WILLIAN já sendo gestor do RH.
    assertThat(emInformatica.getArea()).isSameAs(INFORMATICA);
    assertThat(emInformatica.getCargo()).isEqualTo(Cargo.COLABORADOR);
    assertThat(emInformatica.getFim()).isEqualTo(DIA_02);

    assertThat(noRh.getArea()).isSameAs(RH);
    assertThat(noRh.getCargo()).isEqualTo(Cargo.COLABORADOR);
    assertThat(noRh.getFim()).isEqualTo(DIA_10);

    assertThat(gestorDoRh.getCargo()).isEqualTo(Cargo.GESTOR);
    assertThat(gestorDoRh.isVigente()).isTrue();

    // E "quem é o WILLIAN hoje?" é a lotação aberta.
    assertThat(willian.getArea()).isSameAs(RH);
    assertThat(willian.getCargo()).isEqualTo(Cargo.GESTOR);
  }

  @Test
  void colaboradorSemLotacaoNaoTemAreaNemCargo() {
    Colaborador vazio = new Colaborador();

    assertThatThrownBy(vazio::getArea)
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("sem lotação vigente");
  }

  @Test
  void getLotacoesNaoDeveExporAListaInterna() {
    Colaborador willian = willian();

    assertThatThrownBy(() -> willian.getLotacoes().clear())
        .isInstanceOf(UnsupportedOperationException.class);
  }
}
