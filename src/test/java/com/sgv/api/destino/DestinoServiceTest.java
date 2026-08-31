package com.sgv.api.destino;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DestinoServiceTest {

  @Mock
  private DestinoRepository repository;

  @InjectMocks
  private DestinoService service;

  private DestinoRequest request() {
    DestinoRequest request = new DestinoRequest();
    request.setNome("Matriz SP");
    request.setCidade("São Paulo");
    request.setPais("Brasil");
    return request;
  }

  @Test
  void findAllDeveMapearParaResponse() {
    when(repository.findAll()).thenReturn(List.of(new Destino("Matriz SP", "São Paulo", "Brasil")));

    List<DestinoResponse> destinos = service.findAll();

    assertThat(destinos).hasSize(1);
    assertThat(destinos.get(0).getCidade()).isEqualTo("São Paulo");
  }

  @Test
  void createDeveSalvarERetornarResponse() {
    when(repository.save(any(Destino.class))).thenAnswer(invocation -> invocation.getArgument(0));

    DestinoResponse response = service.create(request());

    assertThat(response.getNome()).isEqualTo("Matriz SP");
    assertThat(response.getPais()).isEqualTo("Brasil");
  }

  @Test
  void findByIdInexistenteDeveLancarDestinoNotFound() {
    when(repository.findById(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.findById(99L))
        .isInstanceOf(DestinoNotFoundException.class)
        .hasMessageContaining("99");
  }

  @Test
  void deleteInexistenteDeveLancarDestinoNotFound() {
    when(repository.existsById(99L)).thenReturn(false);

    assertThatThrownBy(() -> service.delete(99L))
        .isInstanceOf(DestinoNotFoundException.class);
    verify(repository, never()).deleteById(any());
  }
}
