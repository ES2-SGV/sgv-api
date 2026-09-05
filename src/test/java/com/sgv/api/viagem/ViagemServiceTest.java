package com.sgv.api.viagem;

import com.sgv.api.area.Area;
import com.sgv.api.colaborador.AcaoRestritaAGestorException;
import com.sgv.api.colaborador.AtorService;
import com.sgv.api.colaborador.Cargo;
import com.sgv.api.colaborador.Colaborador;
import com.sgv.api.colaborador.ColaboradorNotFoundException;
import com.sgv.api.colaborador.ColaboradorRepository;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
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
class ViagemServiceTest {

  private static final long SOLICITANTE_ID = 2L;
  private static final long GESTOR_ID = 3L;
  private static final long OUTRO_ID = 4L;

  @Mock
  private ViagemRepository repository;

  @Mock
  private ViagemHistoricoRepository historicoRepository;

  @Mock
  private DestinoRepository destinoRepository;

  @Mock
  private ColaboradorRepository colaboradorRepository;

  @Mock
  private AtorService atorService;

  @InjectMocks
  private ViagemService service;

  private Destino destino;
  private Colaborador solicitante;
  private Colaborador gestor;
  private Colaborador outro;

  @BeforeEach
  void setUp() {
    destino = new Destino("Matriz SP", "São Paulo", "Brasil");
    solicitante = colaborador(SOLICITANTE_ID, "1001-2", Cargo.COLABORADOR);
    gestor = colaborador(GESTOR_ID, "3003-4", Cargo.GESTOR);
    outro = colaborador(OUTRO_ID, "4004-5", Cargo.COLABORADOR);
  }

  private Colaborador colaborador(long id, String matricula, Cargo cargo) {
    Colaborador c = new Colaborador(matricula, "Fulano", new Area("Comercial"), cargo);
    ReflectionTestUtils.setField(c, "id", id);
    return c;
  }

  private Viagem viagem(SituacaoViagem situacao) {
    Viagem viagem = new Viagem(destino, solicitante, "Reunião com cliente",
        LocalDate.of(2026, 9, 10), LocalDate.of(2026, 9, 12), MeioTransporte.AEREO, situacao);
    ReflectionTestUtils.setField(viagem, "id", 1L);
    return viagem;
  }

  /** Devolve a viagem em `situacao` já registrada no repositório, e ecoa o save. */
  private Viagem dadaViagem(SituacaoViagem situacao) {
    Viagem viagem = viagem(situacao);
    when(repository.findById(1L)).thenReturn(Optional.of(viagem));
    return viagem;
  }

  private void ecoarSave() {
    when(repository.save(any(Viagem.class))).thenAnswer(invocation -> invocation.getArgument(0));
  }

  /** O último registro de histórico que o serviço mandou salvar. */
  private ViagemHistorico historicoSalvo() {
    ArgumentCaptor<ViagemHistorico> captor = ArgumentCaptor.forClass(ViagemHistorico.class);
    verify(historicoRepository, org.mockito.Mockito.atLeastOnce()).save(captor.capture());
    return captor.getValue();
  }

  private ViagemRequest request() {
    ViagemRequest request = new ViagemRequest();
    request.setDestinoId(1L);
    request.setColaboradorId(SOLICITANTE_ID);
    request.setMotivo("Reunião com cliente");
    request.setDataSaida(LocalDate.of(2026, 9, 10));
    request.setDataRetorno(LocalDate.of(2026, 9, 12));
    request.setMeioTransporte(MeioTransporte.AEREO);
    return request;
  }

  private AjusteRequest ajuste(String motivo) {
    AjusteRequest request = new AjusteRequest();
    request.setMotivo(motivo);
    return request;
  }

  // --- CRUD ---

  @Test
  void createDeveSalvarViagemEmRascunho() {
    when(destinoRepository.findById(1L)).thenReturn(Optional.of(destino));
    when(colaboradorRepository.findById(SOLICITANTE_ID)).thenReturn(Optional.of(solicitante));
    ecoarSave();

    ViagemResponse response = service.create(request());

    ArgumentCaptor<Viagem> captor = ArgumentCaptor.forClass(Viagem.class);
    verify(repository).save(captor.capture());
    assertThat(captor.getValue().getSituacao()).isEqualTo(SituacaoViagem.RASCUNHO);
    assertThat(response.getSituacao()).isEqualTo(SituacaoViagem.RASCUNHO);
    assertThat(response.getDestino().getNome()).isEqualTo("Matriz SP");
    assertThat(historicoSalvo().getSituacao()).isEqualTo(SituacaoViagem.RASCUNHO);
    assertThat(historicoSalvo().getResponsavel()).isSameAs(solicitante);
  }

  @Test
  void createComDestinoInexistenteDeveLancarDestinoNotFound() {
    when(destinoRepository.findById(1L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.create(request()))
        .isInstanceOf(DestinoNotFoundException.class);
    verify(repository, never()).save(any());
  }

  @Test
  void createComColaboradorInexistenteDeveLancarColaboradorNotFound() {
    when(destinoRepository.findById(1L)).thenReturn(Optional.of(destino));
    when(colaboradorRepository.findById(SOLICITANTE_ID)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.create(request()))
        .isInstanceOf(ColaboradorNotFoundException.class);
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
  void findAllSemFiltroDeveUsarFindAll() {
    when(repository.findAll()).thenReturn(List.of(viagem(SituacaoViagem.RASCUNHO)));

    assertThat(service.findAll(null)).hasSize(1);
    verify(repository, never()).findBySituacao(any());
  }

  @Test
  void findAllComSituacaoDeveFiltrarAFilaDoGestor() {
    when(repository.findBySituacao(SituacaoViagem.SOLICITADA))
        .thenReturn(List.of(viagem(SituacaoViagem.SOLICITADA)));

    assertThat(service.findAll(SituacaoViagem.SOLICITADA)).hasSize(1);
    verify(repository, never()).findAll();
  }

  // --- Edição ---

  @Test
  void updateEmRascunhoDeveAlterarCamposEPreservarSituacao() {
    dadaViagem(SituacaoViagem.RASCUNHO);
    when(atorService.resolver(SOLICITANTE_ID)).thenReturn(solicitante);
    when(destinoRepository.findById(1L)).thenReturn(Optional.of(destino));
    when(colaboradorRepository.findById(SOLICITANTE_ID)).thenReturn(Optional.of(solicitante));
    ecoarSave();

    ViagemResponse response = service.update(1L, request(), SOLICITANTE_ID);

    assertThat(response.getMotivo()).isEqualTo("Reunião com cliente");
    assertThat(response.getSituacao()).isEqualTo(SituacaoViagem.RASCUNHO);
  }

  @Test
  void updateEmAjusteDeveSerPermitido() {
    dadaViagem(SituacaoViagem.EM_AJUSTE);
    when(atorService.resolver(SOLICITANTE_ID)).thenReturn(solicitante);
    when(destinoRepository.findById(1L)).thenReturn(Optional.of(destino));
    when(colaboradorRepository.findById(SOLICITANTE_ID)).thenReturn(Optional.of(solicitante));
    ecoarSave();

    assertThat(service.update(1L, request(), SOLICITANTE_ID).getSituacao())
        .isEqualTo(SituacaoViagem.EM_AJUSTE);
  }

  @Test
  void updateDepoisDeSolicitadaDeveLancarTransicaoInvalida() {
    dadaViagem(SituacaoViagem.SOLICITADA);
    when(atorService.resolver(SOLICITANTE_ID)).thenReturn(solicitante);

    assertThatThrownBy(() -> service.update(1L, request(), SOLICITANTE_ID))
        .isInstanceOf(TransicaoInvalidaException.class)
        .hasMessageContaining("editar");
    verify(repository, never()).save(any());
  }

  @Test
  void updatePorQuemNaoEhOSolicitanteDeveLancar403() {
    dadaViagem(SituacaoViagem.RASCUNHO);
    when(atorService.resolver(OUTRO_ID)).thenReturn(outro);

    assertThatThrownBy(() -> service.update(1L, request(), OUTRO_ID))
        .isInstanceOf(AcaoRestritaAoSolicitanteException.class);
    verify(repository, never()).save(any());
  }

  @Test
  void deleteEmRascunhoDeveApagar() {
    Viagem viagem = dadaViagem(SituacaoViagem.RASCUNHO);
    when(atorService.resolver(SOLICITANTE_ID)).thenReturn(solicitante);

    service.delete(1L, SOLICITANTE_ID);

    verify(repository).delete(viagem);
  }

  @Test
  void deleteDepoisDeSolicitadaDeveLancarTransicaoInvalida() {
    dadaViagem(SituacaoViagem.SOLICITADA);
    when(atorService.resolver(SOLICITANTE_ID)).thenReturn(solicitante);

    assertThatThrownBy(() -> service.delete(1L, SOLICITANTE_ID))
        .isInstanceOf(TransicaoInvalidaException.class);
    verify(repository, never()).delete(any());
  }

  @Test
  void deleteInexistenteDeveLancarViagemNotFound() {
    when(repository.findById(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.delete(99L, SOLICITANTE_ID))
        .isInstanceOf(ViagemNotFoundException.class);
    verify(repository, never()).delete(any());
  }

  // --- Ações do solicitante ---

  @Test
  void solicitarEmRascunhoDeveIrParaSolicitada() {
    dadaViagem(SituacaoViagem.RASCUNHO);
    when(atorService.resolver(SOLICITANTE_ID)).thenReturn(solicitante);
    ecoarSave();

    assertThat(service.solicitar(1L, SOLICITANTE_ID).getSituacao())
        .isEqualTo(SituacaoViagem.SOLICITADA);
  }

  @Test
  void solicitarEmAjusteDeveReenviarELimparOMotivo() {
    Viagem viagem = dadaViagem(SituacaoViagem.EM_AJUSTE);
    viagem.setMotivoAjuste("faltou o orçamento");
    when(atorService.resolver(SOLICITANTE_ID)).thenReturn(solicitante);
    ecoarSave();

    ViagemResponse response = service.solicitar(1L, SOLICITANTE_ID);

    assertThat(response.getSituacao()).isEqualTo(SituacaoViagem.SOLICITADA);
    assertThat(response.getMotivoAjuste()).isNull();
  }

  @Test
  void solicitarViagemJaAprovadaDeveLancarTransicaoInvalida() {
    dadaViagem(SituacaoViagem.APROVADA);
    when(atorService.resolver(SOLICITANTE_ID)).thenReturn(solicitante);

    assertThatThrownBy(() -> service.solicitar(1L, SOLICITANTE_ID))
        .isInstanceOf(TransicaoInvalidaException.class)
        .hasMessageContaining("APROVADA");
  }

  @Test
  void solicitarPorOutroColaboradorDeveLancar403() {
    dadaViagem(SituacaoViagem.RASCUNHO);
    when(atorService.resolver(OUTRO_ID)).thenReturn(outro);

    assertThatThrownBy(() -> service.solicitar(1L, OUTRO_ID))
        .isInstanceOf(AcaoRestritaAoSolicitanteException.class);
  }

  @Test
  void cancelarEmRascunhoDeveEncerrarOFluxo() {
    dadaViagem(SituacaoViagem.RASCUNHO);
    when(atorService.resolver(SOLICITANTE_ID)).thenReturn(solicitante);
    ecoarSave();

    assertThat(service.cancelar(1L, SOLICITANTE_ID).getSituacao())
        .isEqualTo(SituacaoViagem.CANCELADA);
  }

  @Test
  void cancelarEmAjusteDeveSerPermitido() {
    dadaViagem(SituacaoViagem.EM_AJUSTE);
    when(atorService.resolver(SOLICITANTE_ID)).thenReturn(solicitante);
    ecoarSave();

    assertThat(service.cancelar(1L, SOLICITANTE_ID).getSituacao())
        .isEqualTo(SituacaoViagem.CANCELADA);
  }

  @Test
  void cancelarDepoisDeSolicitadaDeveLancarTransicaoInvalida() {
    dadaViagem(SituacaoViagem.SOLICITADA);
    when(atorService.resolver(SOLICITANTE_ID)).thenReturn(solicitante);

    assertThatThrownBy(() -> service.cancelar(1L, SOLICITANTE_ID))
        .isInstanceOf(TransicaoInvalidaException.class);
  }

  // --- Ações do gestor ---

  @Test
  void aprovarDeveExigirGestorEEncerrarOFluxo() {
    dadaViagem(SituacaoViagem.SOLICITADA);
    when(atorService.exigirGestor(GESTOR_ID)).thenReturn(gestor);
    ecoarSave();

    assertThat(service.aprovar(1L, GESTOR_ID).getSituacao()).isEqualTo(SituacaoViagem.APROVADA);
  }

  @Test
  void rejeitarDeveEncerrarOFluxo() {
    dadaViagem(SituacaoViagem.SOLICITADA);
    when(atorService.exigirGestor(GESTOR_ID)).thenReturn(gestor);
    ecoarSave();

    assertThat(service.rejeitar(1L, GESTOR_ID).getSituacao()).isEqualTo(SituacaoViagem.REJEITADA);
  }

  @Test
  void aprovarPorNaoGestorDeveLancar403() {
    dadaViagem(SituacaoViagem.SOLICITADA);
    when(atorService.exigirGestor(SOLICITANTE_ID))
        .thenThrow(new AcaoRestritaAGestorException(solicitante));

    assertThatThrownBy(() -> service.aprovar(1L, SOLICITANTE_ID))
        .isInstanceOf(AcaoRestritaAGestorException.class);
    verify(repository, never()).save(any());
  }

  @Test
  void aprovarViagemAindaEmRascunhoDeveLancarTransicaoInvalida() {
    dadaViagem(SituacaoViagem.RASCUNHO);
    when(atorService.exigirGestor(GESTOR_ID)).thenReturn(gestor);

    assertThatThrownBy(() -> service.aprovar(1L, GESTOR_ID))
        .isInstanceOf(TransicaoInvalidaException.class)
        .hasMessageContaining("SOLICITADA");
    verify(repository, never()).save(any());
  }

  @Test
  void solicitarAjustesDeveDevolverComOMotivo() {
    dadaViagem(SituacaoViagem.SOLICITADA);
    when(atorService.exigirGestor(GESTOR_ID)).thenReturn(gestor);
    ecoarSave();

    ViagemResponse response = service.solicitarAjustes(1L, ajuste("faltou o orçamento"), GESTOR_ID);

    assertThat(response.getSituacao()).isEqualTo(SituacaoViagem.EM_AJUSTE);
    assertThat(response.getMotivoAjuste()).isEqualTo("faltou o orçamento");
  }

  @Test
  void solicitarAjustesPorNaoGestorDeveLancar403() {
    dadaViagem(SituacaoViagem.SOLICITADA);
    when(atorService.exigirGestor(SOLICITANTE_ID))
        .thenThrow(new AcaoRestritaAGestorException(solicitante));

    assertThatThrownBy(() -> service.solicitarAjustes(1L, ajuste("faltou"), SOLICITANTE_ID))
        .isInstanceOf(AcaoRestritaAGestorException.class);
    verify(repository, never()).save(any());
  }

  // --- Fluxo completo ---

  @Test
  void fluxoCompletoRascunhoSolicitadaAjusteSolicitadaAprovada() {
    Viagem viagem = dadaViagem(SituacaoViagem.RASCUNHO);
    when(atorService.resolver(SOLICITANTE_ID)).thenReturn(solicitante);
    when(atorService.exigirGestor(GESTOR_ID)).thenReturn(gestor);
    ecoarSave();

    assertThat(service.solicitar(1L, SOLICITANTE_ID).getSituacao())
        .isEqualTo(SituacaoViagem.SOLICITADA);
    assertThat(service.solicitarAjustes(1L, ajuste("faltou o orçamento"), GESTOR_ID).getSituacao())
        .isEqualTo(SituacaoViagem.EM_AJUSTE);
    assertThat(viagem.getMotivoAjuste()).isEqualTo("faltou o orçamento");
    assertThat(service.solicitar(1L, SOLICITANTE_ID).getSituacao())
        .isEqualTo(SituacaoViagem.SOLICITADA);
    assertThat(viagem.getMotivoAjuste()).isNull();
    assertThat(service.aprovar(1L, GESTOR_ID).getSituacao()).isEqualTo(SituacaoViagem.APROVADA);
  }

  // --- Histórico ---

  @Test
  void solicitarDeveRegistrarQuemESituacao() {
    dadaViagem(SituacaoViagem.RASCUNHO);
    when(atorService.resolver(SOLICITANTE_ID)).thenReturn(solicitante);
    ecoarSave();

    service.solicitar(1L, SOLICITANTE_ID);

    ViagemHistorico registro = historicoSalvo();
    assertThat(registro.getSituacao()).isEqualTo(SituacaoViagem.SOLICITADA);
    assertThat(registro.getResponsavel()).isSameAs(solicitante);
    assertThat(registro.getObservacao()).isNull();
    assertThat(registro.getRegistradoEm()).isNotNull();
  }

  @Test
  void solicitarAjustesDeveGuardarOMotivoNoHistorico() {
    dadaViagem(SituacaoViagem.SOLICITADA);
    when(atorService.exigirGestor(GESTOR_ID)).thenReturn(gestor);
    ecoarSave();

    service.solicitarAjustes(1L, ajuste("faltou o orçamento"), GESTOR_ID);

    ViagemHistorico registro = historicoSalvo();
    assertThat(registro.getSituacao()).isEqualTo(SituacaoViagem.EM_AJUSTE);
    assertThat(registro.getResponsavel()).isSameAs(gestor);
    assertThat(registro.getObservacao()).isEqualTo("faltou o orçamento");
  }

  @Test
  void aprovarDeveRegistrarOGestorComoResponsavel() {
    dadaViagem(SituacaoViagem.SOLICITADA);
    when(atorService.exigirGestor(GESTOR_ID)).thenReturn(gestor);
    ecoarSave();

    service.aprovar(1L, GESTOR_ID);

    assertThat(historicoSalvo().getResponsavel()).isSameAs(gestor);
    assertThat(historicoSalvo().getSituacao()).isEqualTo(SituacaoViagem.APROVADA);
  }

  @Test
  void transicaoRecusadaNaoDeveRegistrarNada() {
    dadaViagem(SituacaoViagem.APROVADA);
    when(atorService.resolver(SOLICITANTE_ID)).thenReturn(solicitante);

    assertThatThrownBy(() -> service.solicitar(1L, SOLICITANTE_ID))
        .isInstanceOf(TransicaoInvalidaException.class);
    verify(historicoRepository, never()).save(any());
  }

  @Test
  void historicoDeveVirEmOrdemCronologica() {
    dadaViagem(SituacaoViagem.APROVADA);
    Viagem viagem = viagem(SituacaoViagem.APROVADA);
    when(historicoRepository.findByViagemIdOrderByRegistradoEmAscIdAsc(1L)).thenReturn(List.of(
        new ViagemHistorico(viagem(SituacaoViagem.RASCUNHO), solicitante,
            LocalDateTime.of(2026, 9, 1, 9, 0), null),
        new ViagemHistorico(viagem, gestor, LocalDateTime.of(2026, 9, 2, 10, 0), null)));

    List<ViagemHistoricoResponse> historico = service.historico(1L);

    assertThat(historico).hasSize(2);
    assertThat(historico.get(0).getSituacao()).isEqualTo(SituacaoViagem.RASCUNHO);
    assertThat(historico.get(0).getResponsavel().getMatricula()).isEqualTo("1001-2");
    assertThat(historico.get(1).getSituacao()).isEqualTo(SituacaoViagem.APROVADA);
    assertThat(historico.get(1).getRegistradoEm()).isEqualTo(LocalDateTime.of(2026, 9, 2, 10, 0));
  }

  @Test
  void historicoDeViagemInexistenteDeveLancarViagemNotFound() {
    when(repository.findById(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.historico(99L))
        .isInstanceOf(ViagemNotFoundException.class);
  }
}
