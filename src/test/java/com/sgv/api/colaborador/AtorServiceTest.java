package com.sgv.api.colaborador;

import com.sgv.api.area.Area;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AtorServiceTest {

  @Mock
  private ColaboradorRepository repository;

  @InjectMocks
  private AtorService service;

  private Colaborador colaborador(Cargo cargo) {
    return new Colaborador("1001-2", "Ana Souza", new Area("Comercial"), cargo);
  }

  @Test
  void resolverDeveRetornarOColaboradorDoHeader() {
    Colaborador ana = colaborador(Cargo.COLABORADOR);
    when(repository.findById(1L)).thenReturn(Optional.of(ana));

    assertThat(service.resolver(1L)).isSameAs(ana);
  }

  @Test
  void resolverAtorInexistenteDeveLancarColaboradorNotFound() {
    when(repository.findById(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.resolver(99L))
        .isInstanceOf(ColaboradorNotFoundException.class)
        .hasMessageContaining("99");
  }

  @Test
  void exigirGestorDeveAceitarGestor() {
    Colaborador gestora = colaborador(Cargo.GESTOR);
    when(repository.findById(1L)).thenReturn(Optional.of(gestora));

    assertThat(service.exigirGestor(1L)).isSameAs(gestora);
  }

  @Test
  void exigirGestorDeveRecusarColaborador() {
    when(repository.findById(1L)).thenReturn(Optional.of(colaborador(Cargo.COLABORADOR)));

    assertThatThrownBy(() -> service.exigirGestor(1L))
        .isInstanceOf(AcaoRestritaAGestorException.class)
        .hasMessageContaining("COLABORADOR");
  }

  @Test
  void exigirGestorComAtorInexistenteDeveLancarNotFoundAntesDe403() {
    when(repository.findById(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.exigirGestor(99L))
        .isInstanceOf(ColaboradorNotFoundException.class);
  }
}
