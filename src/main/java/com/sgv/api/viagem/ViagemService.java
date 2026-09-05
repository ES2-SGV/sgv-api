package com.sgv.api.viagem;

import com.sgv.api.colaborador.AtorService;
import com.sgv.api.colaborador.Colaborador;
import com.sgv.api.colaborador.ColaboradorNotFoundException;
import com.sgv.api.colaborador.ColaboradorRepository;
import com.sgv.api.destino.Destino;
import com.sgv.api.destino.DestinoNotFoundException;
import com.sgv.api.destino.DestinoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.List;

@Service
public class ViagemService {

  private final ViagemRepository repository;
  private final ViagemHistoricoRepository historicoRepository;
  private final DestinoRepository destinoRepository;
  private final ColaboradorRepository colaboradorRepository;
  private final AtorService atorService;

  public ViagemService(ViagemRepository repository,
      ViagemHistoricoRepository historicoRepository, DestinoRepository destinoRepository,
      ColaboradorRepository colaboradorRepository, AtorService atorService) {
    this.repository = repository;
    this.historicoRepository = historicoRepository;
    this.destinoRepository = destinoRepository;
    this.colaboradorRepository = colaboradorRepository;
    this.atorService = atorService;
  }

  public List<ViagemResponse> findAll(SituacaoViagem situacao) {
    List<Viagem> viagens = situacao == null
        ? repository.findAll()
        : repository.findBySituacao(situacao);
    return viagens.stream().map(ViagemResponse::new).toList();
  }

  public ViagemResponse findById(Long id) {
    return new ViagemResponse(buscar(id));
  }

  public ViagemResponse create(ViagemRequest request) {
    Viagem viagem = new Viagem(
        buscarDestino(request.getDestinoId()),
        buscarColaborador(request.getColaboradorId()),
        request.getMotivo(),
        request.getDataSaida(),
        request.getDataRetorno(),
        request.getMeioTransporte(),
        SituacaoViagem.RASCUNHO);
    Viagem salva = repository.save(viagem);
    registrar(salva, salva.getColaborador(), null);
    return new ViagemResponse(salva);
  }

  public ViagemResponse update(Long id, ViagemRequest request, Long atorId) {
    Viagem viagem = buscar(id);
    exigirSolicitante(viagem, atorId);
    exigirSituacao(viagem, "editar", SituacaoViagem.RASCUNHO, SituacaoViagem.EM_AJUSTE);
    viagem.setDestino(buscarDestino(request.getDestinoId()));
    viagem.setColaborador(buscarColaborador(request.getColaboradorId()));
    viagem.setMotivo(request.getMotivo());
    viagem.setDataSaida(request.getDataSaida());
    viagem.setDataRetorno(request.getDataRetorno());
    viagem.setMeioTransporte(request.getMeioTransporte());
    return new ViagemResponse(repository.save(viagem));
  }

  public void delete(Long id, Long atorId) {
    Viagem viagem = buscar(id);
    exigirSolicitante(viagem, atorId);
    // Depois de submetida, a saída é cancelar — apagar apagaria o rastro.
    exigirSituacao(viagem, "excluir", SituacaoViagem.RASCUNHO);
    repository.delete(viagem);
  }

  // --- Ações do solicitante ---

  public ViagemResponse solicitar(Long id, Long atorId) {
    Viagem viagem = buscar(id);
    Colaborador ator = exigirSolicitante(viagem, atorId);
    exigirSituacao(viagem, "solicitar", SituacaoViagem.RASCUNHO, SituacaoViagem.EM_AJUSTE);
    viagem.setSituacao(SituacaoViagem.SOLICITADA);
    // Congela área e cargo de agora. No reenvio depois de um ajuste, vale a
    // lotação do reenvio: é essa a solicitação que o gestor vai avaliar.
    viagem.setLotacaoSolicitante(ator.getLotacaoVigente());
    // O pedido de ajuste morre aqui: ou foi atendido, ou o solicitante decidiu
    // reenviar assim mesmo. Manter o texto daria a entender que ainda vale — e
    // o que foi pedido continua no histórico.
    viagem.setMotivoAjuste(null);
    return salvarERegistrar(viagem, ator, null);
  }

  public ViagemResponse cancelar(Long id, Long atorId) {
    Viagem viagem = buscar(id);
    Colaborador ator = exigirSolicitante(viagem, atorId);
    exigirSituacao(viagem, "cancelar", SituacaoViagem.RASCUNHO, SituacaoViagem.EM_AJUSTE);
    viagem.setSituacao(SituacaoViagem.CANCELADA);
    return salvarERegistrar(viagem, ator, null);
  }

  // --- Ações do gestor ---

  public ViagemResponse aprovar(Long id, Long atorId) {
    return decidir(id, atorId, "aprovar", SituacaoViagem.APROVADA, null);
  }

  public ViagemResponse rejeitar(Long id, Long atorId) {
    return decidir(id, atorId, "rejeitar", SituacaoViagem.REJEITADA, null);
  }

  public ViagemResponse solicitarAjustes(Long id, AjusteRequest request, Long atorId) {
    return decidir(id, atorId, "solicitar ajustes em", SituacaoViagem.EM_AJUSTE,
        request.getMotivo());
  }

  private ViagemResponse decidir(Long id, Long atorId, String acao, SituacaoViagem destino,
      String observacao) {
    Viagem viagem = buscar(id);
    Colaborador gestor = atorService.exigirGestor(atorId);
    exigirSituacao(viagem, acao, SituacaoViagem.SOLICITADA);
    viagem.setSituacao(destino);
    // Só "solicitar ajustes" traz texto; aprovar e rejeitar passam null, o que
    // de quebra limpa qualquer pedido antigo que tenha sobrado.
    viagem.setMotivoAjuste(observacao);
    return salvarERegistrar(viagem, gestor, observacao);
  }

  private ViagemResponse salvarERegistrar(Viagem viagem, Colaborador responsavel,
      String observacao) {
    Viagem salva = repository.save(viagem);
    registrar(salva, responsavel, observacao);
    return new ViagemResponse(salva);
  }

  public List<ViagemHistoricoResponse> historico(Long id) {
    buscar(id);
    return historicoRepository.findByViagemIdOrderByRegistradoEmAscIdAsc(id).stream()
        .map(ViagemHistoricoResponse::new)
        .toList();
  }

  // --- Regras comuns ---

  private Colaborador exigirSolicitante(Viagem viagem, Long atorId) {
    Colaborador ator = atorService.resolver(atorId);
    if (!Objects.equals(ator.getId(), viagem.getColaborador().getId())) {
      throw new AcaoRestritaAoSolicitanteException(viagem.getId(), atorId);
    }
    return ator;
  }

  private void registrar(Viagem viagem, Colaborador responsavel, String observacao) {
    historicoRepository.save(
        new ViagemHistorico(viagem, responsavel, LocalDateTime.now(), observacao));
  }

  private void exigirSituacao(Viagem viagem, String acao, SituacaoViagem... permitidas) {
    if (Arrays.stream(permitidas).noneMatch(s -> s == viagem.getSituacao())) {
      throw new TransicaoInvalidaException(acao, viagem.getSituacao(), permitidas);
    }
  }

  private Viagem buscar(Long id) {
    return repository.findById(id)
        .orElseThrow(() -> new ViagemNotFoundException(id));
  }

  private Destino buscarDestino(Long destinoId) {
    return destinoRepository.findById(destinoId)
        .orElseThrow(() -> new DestinoNotFoundException(destinoId));
  }

  private Colaborador buscarColaborador(Long colaboradorId) {
    return colaboradorRepository.findById(colaboradorId)
        .orElseThrow(() -> new ColaboradorNotFoundException(colaboradorId));
  }
}
