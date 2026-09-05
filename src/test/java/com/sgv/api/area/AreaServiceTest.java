package com.sgv.api.area;

import com.sgv.api.colaborador.ColaboradorRepository;
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
class AreaServiceTest {

  @Mock
  private AreaRepository repository;

  @Mock
  private ColaboradorRepository colaboradorRepository;

  @InjectMocks
  private AreaService service;

  private AreaRequest request() {
    AreaRequest request = new AreaRequest();
    request.setNome("Comercial");
    return request;
  }

  @Test
  void createDeveSalvarERetornarResponse() {
    when(repository.existsByNome("Comercial")).thenReturn(false);
    when(repository.save(any(Area.class))).thenAnswer(invocation -> invocation.getArgument(0));

    AreaResponse response = service.create(request());

    assertThat(response.getNome()).isEqualTo("Comercial");
  }

  @Test
  void createComNomeDuplicadoDeveLancarConflito() {
    when(repository.existsByNome("Comercial")).thenReturn(true);

    assertThatThrownBy(() -> service.create(request()))
        .isInstanceOf(NomeAreaJaCadastradoException.class)
        .hasMessageContaining("Comercial");
    verify(repository, never()).save(any());
  }

  @Test
  void updateMantendoProprioNomeNaoDeveConflitar() {
    when(repository.findById(1L)).thenReturn(Optional.of(new Area("Comercial")));
    when(repository.existsByNomeAndIdNot("Comercial", 1L)).thenReturn(false);
    when(repository.save(any(Area.class))).thenAnswer(invocation -> invocation.getArgument(0));

    AreaResponse response = service.update(1L, request());

    assertThat(response.getNome()).isEqualTo("Comercial");
  }

  @Test
  void updateComNomeDeOutraAreaDeveLancarConflito() {
    when(repository.findById(1L)).thenReturn(Optional.of(new Area("Financeiro")));
    when(repository.existsByNomeAndIdNot("Comercial", 1L)).thenReturn(true);

    assertThatThrownBy(() -> service.update(1L, request()))
        .isInstanceOf(NomeAreaJaCadastradoException.class);
    verify(repository, never()).save(any());
  }

  @Test
  void findByIdInexistenteDeveLancarAreaNotFound() {
    when(repository.findById(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.findById(99L))
        .isInstanceOf(AreaNotFoundException.class)
        .hasMessageContaining("99");
  }

  @Test
  void deleteInexistenteDeveLancarAreaNotFound() {
    when(repository.existsById(99L)).thenReturn(false);

    assertThatThrownBy(() -> service.delete(99L))
        .isInstanceOf(AreaNotFoundException.class);
    verify(repository, never()).deleteById(any());
  }

  @Test
  void deleteComColaboradorVinculadoDeveLancarConflito() {
    when(repository.existsById(1L)).thenReturn(true);
    when(colaboradorRepository.existsByAreaId(1L)).thenReturn(true);

    assertThatThrownBy(() -> service.delete(1L))
        .isInstanceOf(AreaEmUsoException.class);
    verify(repository, never()).deleteById(any());
  }

  @Test
  void deleteSemColaboradorVinculadoDeveApagar() {
    when(repository.existsById(1L)).thenReturn(true);
    when(colaboradorRepository.existsByAreaId(1L)).thenReturn(false);

    service.delete(1L);

    verify(repository).deleteById(1L);
  }
}
