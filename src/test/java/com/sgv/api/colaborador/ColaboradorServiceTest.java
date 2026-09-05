package com.sgv.api.colaborador;

import com.sgv.api.area.Area;
import com.sgv.api.area.AreaNotFoundException;
import com.sgv.api.area.AreaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ColaboradorServiceTest {

  @Mock
  private ColaboradorRepository repository;

  @Mock
  private AreaRepository areaRepository;

  @InjectMocks
  private ColaboradorService service;

  private ColaboradorRequest request() {
    ColaboradorRequest request = new ColaboradorRequest();
    request.setMatricula("1001-2");
    request.setCargo(Cargo.GESTOR);
    request.setNome("Ana Souza");
    request.setAreaId(1L);
    return request;
  }

  private void areaComercialExiste() {
    when(areaRepository.findById(1L)).thenReturn(Optional.of(new Area("Comercial")));
  }

  @Test
  void createDeveSalvarERetornarResponse() {
    when(repository.existsByMatricula("1001-2")).thenReturn(false);
    areaComercialExiste();
    when(repository.save(any(Colaborador.class))).thenAnswer(invocation -> invocation.getArgument(0));

    ColaboradorResponse response = service.create(request());

    assertThat(response.getMatricula()).isEqualTo("1001-2");
    assertThat(response.getArea().getNome()).isEqualTo("Comercial");
    assertThat(response.getCargo()).isEqualTo(Cargo.GESTOR);
  }

  @Test
  void createComAreaInexistenteDeveLancarAreaNotFound() {
    when(repository.existsByMatricula("1001-2")).thenReturn(false);
    when(areaRepository.findById(1L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.create(request()))
        .isInstanceOf(AreaNotFoundException.class)
        .hasMessageContaining("1");
    verify(repository, never()).save(any());
  }

  @Test
  void createComMatriculaDuplicadaDeveLancarConflito() {
    when(repository.existsByMatricula("1001-2")).thenReturn(true);

    assertThatThrownBy(() -> service.create(request()))
        .isInstanceOf(MatriculaJaCadastradaException.class)
        .hasMessageContaining("1001-2");
    verify(repository, never()).save(any());
  }

  @Test
  void updateMantendoPropriaMatriculaNaoDeveConflitar() {
    Colaborador existente = new Colaborador("1001-2", "Ana", new Area("Financeiro"), Cargo.COLABORADOR);
    when(repository.findById(1L)).thenReturn(Optional.of(existente));
    when(repository.existsByMatriculaAndIdNot("1001-2", 1L)).thenReturn(false);
    areaComercialExiste();
    when(repository.save(any(Colaborador.class))).thenAnswer(invocation -> invocation.getArgument(0));

    ColaboradorResponse response = service.update(1L, request());

    assertThat(response.getNome()).isEqualTo("Ana Souza");
    assertThat(response.getArea().getNome()).isEqualTo("Comercial");
    assertThat(response.getCargo()).isEqualTo(Cargo.GESTOR);
  }

  @Test
  void updateComMatriculaDeOutroColaboradorDeveLancarConflito() {
    when(repository.findById(1L)).thenReturn(Optional.of(new Colaborador("2002-3", "Ana", new Area("Financeiro"), Cargo.COLABORADOR)));
    when(repository.existsByMatriculaAndIdNot("1001-2", 1L)).thenReturn(true);

    assertThatThrownBy(() -> service.update(1L, request()))
        .isInstanceOf(MatriculaJaCadastradaException.class);
    verify(repository, never()).save(any());
  }

  @Test
  void findByIdInexistenteDeveLancarColaboradorNotFound() {
    when(repository.findById(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.findById(99L))
        .isInstanceOf(ColaboradorNotFoundException.class)
        .hasMessageContaining("99");
  }

  @Test
  void deleteInexistenteDeveLancarColaboradorNotFound() {
    when(repository.existsById(99L)).thenReturn(false);

    assertThatThrownBy(() -> service.delete(99L))
        .isInstanceOf(ColaboradorNotFoundException.class);
    verify(repository, never()).deleteById(any());
  }

  // --- Lotação ---

  @Test
  void updateMudandoDeAreaDeveFecharALotacaoAtualEAbrirOutra() {
    Area rh = new Area("RH");
    ReflectionTestUtils.setField(rh, "id", 9L);
    Colaborador existente = new Colaborador("1001-2", "Ana", areaComId(1L, "Comercial"),
        Cargo.COLABORADOR, LocalDateTime.of(2026, 9, 1, 8, 0));
    when(repository.findById(1L)).thenReturn(Optional.of(existente));
    when(repository.existsByMatriculaAndIdNot("1001-2", 1L)).thenReturn(false);
    when(areaRepository.findById(1L)).thenReturn(Optional.of(rh));
    when(repository.save(any(Colaborador.class))).thenAnswer(invocation -> invocation.getArgument(0));

    ColaboradorResponse response = service.update(1L, request());

    assertThat(existente.getLotacoes()).hasSize(2);
    assertThat(existente.getLotacoes().get(0).getFim()).isNotNull();
    assertThat(response.getArea().getNome()).isEqualTo("RH");
    assertThat(response.getCargo()).isEqualTo(Cargo.GESTOR);
  }

  @Test
  void updateSemMudarAreaNemCargoNaoDeveCriarLotacaoNova() {
    Area comercial = areaComId(1L, "Comercial");
    Colaborador existente = new Colaborador("1001-2", "Ana", comercial, Cargo.GESTOR,
        LocalDateTime.of(2026, 9, 1, 8, 0));
    when(repository.findById(1L)).thenReturn(Optional.of(existente));
    when(repository.existsByMatriculaAndIdNot("1001-2", 1L)).thenReturn(false);
    when(areaRepository.findById(1L)).thenReturn(Optional.of(comercial));
    when(repository.save(any(Colaborador.class))).thenAnswer(invocation -> invocation.getArgument(0));

    service.update(1L, request());

    assertThat(existente.getLotacoes()).hasSize(1);
    assertThat(existente.getLotacaoVigente().getFim()).isNull();
  }

  @Test
  void lotacoesDeveDevolverOHistoricoEmOrdem() {
    Colaborador ana = new Colaborador("1001-2", "Ana", areaComId(1L, "Comercial"),
        Cargo.COLABORADOR, LocalDateTime.of(2026, 9, 1, 8, 0));
    ana.lotar(areaComId(9L, "RH"), Cargo.GESTOR, LocalDateTime.of(2026, 9, 10, 8, 0));
    when(repository.findById(1L)).thenReturn(Optional.of(ana));

    List<LotacaoResponse> lotacoes = service.lotacoes(1L);

    assertThat(lotacoes).hasSize(2);
    assertThat(lotacoes.get(0).getArea().getNome()).isEqualTo("Comercial");
    assertThat(lotacoes.get(0).getFim()).isEqualTo(LocalDateTime.of(2026, 9, 10, 8, 0));
    assertThat(lotacoes.get(1).getArea().getNome()).isEqualTo("RH");
    assertThat(lotacoes.get(1).getCargo()).isEqualTo(Cargo.GESTOR);
    assertThat(lotacoes.get(1).getFim()).isNull();
  }

  @Test
  void lotacoesDeColaboradorInexistenteDeveLancarNotFound() {
    when(repository.findById(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.lotacoes(99L))
        .isInstanceOf(ColaboradorNotFoundException.class);
  }

  private Area areaComId(long id, String nome) {
    Area area = new Area(nome);
    ReflectionTestUtils.setField(area, "id", id);
    return area;
  }
}
