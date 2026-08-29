package com.sgv.api.viagem;

import com.sgv.api.destino.Destino;
import com.sgv.api.destino.DestinoNotFoundException;
import com.sgv.api.destino.DestinoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ViagemServiceTest {

  @Mock
  private ViagemRepository repository;

  @Mock
  private DestinoRepository destinoRepository;

  @InjectMocks
  private ViagemService service;

  private Destino destino;

  @BeforeEach
  void setUp() {
    destino = new Destino("Matriz SP", "São Paulo", "Brasil");
  }

  private ViagemRequest request() {
    ViagemRequest request = new ViagemRequest();
    request.setDestinoId(1L);
    request.setMotivo("Reunião com cliente");
    request.setDataSaida(LocalDate.of(2026, 9, 10));
    request.setDataRetorno(LocalDate.of(2026, 9, 12));
    request.setMeioTransporte(MeioTransporte.AEREO);
    return request;
  }

  @Test
  void createDeveSalvarViagemEmRascunho() {
    when(destinoRepository.findById(1L)).thenReturn(Optional.of(destino));
    when(repository.save(any(Viagem.class))).thenAnswer(invocation -> invocation.getArgument(0));

    ViagemResponse response = service.create(request());

    ArgumentCaptor<Viagem> captor = ArgumentCaptor.forClass(Viagem.class);
    verify(repository).save(captor.capture());
    assertThat(captor.getValue().getSituacao()).isEqualTo(SituacaoViagem.RASCUNHO);
    assertThat(captor.getValue().getDestino()).isSameAs(destino);
    assertThat(response.getSituacao()).isEqualTo(SituacaoViagem.RASCUNHO);
    assertThat(response.getDestino().getNome()).isEqualTo("Matriz SP");
  }

  @Test
  void createComDestinoInexistenteDeveLancarDestinoNotFound() {
    when(destinoRepository.findById(1L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.create(request()))
        .isInstanceOf(DestinoNotFoundException.class);
    verify(repository, never()).save(any());
  }

  @Test
  void findByIdInexistenteDeveLancarViagemNotFound() {
    when(repository.findById(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.findById(99L))
        .isInstanceOf(ViagemNotFoundException.class)
        .hasMessageContaining("99");
  }

  @Test
  void updateDeveAlterarCamposEPreservarSituacao() {
    Viagem existente = new Viagem(destino, "Treinamento", LocalDate.of(2026, 9, 1),
        LocalDate.of(2026, 9, 2), MeioTransporte.RODOVIARIO, SituacaoViagem.APROVADA);
    when(repository.findById(1L)).thenReturn(Optional.of(existente));
    when(destinoRepository.findById(1L)).thenReturn(Optional.of(destino));
    when(repository.save(any(Viagem.class))).thenAnswer(invocation -> invocation.getArgument(0));

    ViagemResponse response = service.update(1L, request());

    assertThat(response.getMotivo()).isEqualTo("Reunião com cliente");
    assertThat(response.getMeioTransporte()).isEqualTo(MeioTransporte.AEREO);
    assertThat(response.getSituacao()).isEqualTo(SituacaoViagem.APROVADA);
  }

  @Test
  void deleteInexistenteDeveLancarViagemNotFound() {
    when(repository.existsById(99L)).thenReturn(false);

    assertThatThrownBy(() -> service.delete(99L))
        .isInstanceOf(ViagemNotFoundException.class);
    verify(repository, never()).deleteById(any());
  }
}
