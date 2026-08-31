package com.sgv.api.colaborador;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

  @InjectMocks
  private ColaboradorService service;

  private ColaboradorRequest request() {
    ColaboradorRequest request = new ColaboradorRequest();
    request.setMatricula("M-1001");
    request.setNome("Ana Souza");
    request.setArea("Comercial");
    return request;
  }

  @Test
  void createDeveSalvarERetornarResponse() {
    when(repository.existsByMatricula("M-1001")).thenReturn(false);
    when(repository.save(any(Colaborador.class))).thenAnswer(invocation -> invocation.getArgument(0));

    ColaboradorResponse response = service.create(request());

    assertThat(response.getMatricula()).isEqualTo("M-1001");
    assertThat(response.getArea()).isEqualTo("Comercial");
  }

  @Test
  void createComMatriculaDuplicadaDeveLancarConflito() {
    when(repository.existsByMatricula("M-1001")).thenReturn(true);

    assertThatThrownBy(() -> service.create(request()))
        .isInstanceOf(MatriculaJaCadastradaException.class)
        .hasMessageContaining("M-1001");
    verify(repository, never()).save(any());
  }

  @Test
  void updateMantendoPropriaMatriculaNaoDeveConflitar() {
    Colaborador existente = new Colaborador("M-1001", "Ana", "Financeiro");
    when(repository.findById(1L)).thenReturn(Optional.of(existente));
    when(repository.existsByMatriculaAndIdNot("M-1001", 1L)).thenReturn(false);
    when(repository.save(any(Colaborador.class))).thenAnswer(invocation -> invocation.getArgument(0));

    ColaboradorResponse response = service.update(1L, request());

    assertThat(response.getNome()).isEqualTo("Ana Souza");
    assertThat(response.getArea()).isEqualTo("Comercial");
  }

  @Test
  void updateComMatriculaDeOutroColaboradorDeveLancarConflito() {
    when(repository.findById(1L)).thenReturn(Optional.of(new Colaborador("M-2002", "Ana", "Financeiro")));
    when(repository.existsByMatriculaAndIdNot("M-1001", 1L)).thenReturn(true);

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
}
