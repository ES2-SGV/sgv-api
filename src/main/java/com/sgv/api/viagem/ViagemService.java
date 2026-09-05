package com.sgv.api.viagem;

import com.sgv.api.colaborador.AtorService;
import com.sgv.api.colaborador.Colaborador;
import com.sgv.api.colaborador.ColaboradorNotFoundException;
import com.sgv.api.colaborador.ColaboradorRepository;
import com.sgv.api.destino.Destino;
import com.sgv.api.destino.DestinoNotFoundException;
import com.sgv.api.destino.DestinoRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Objects;
import java.util.List;

@Service
public class ViagemService {

  private final ViagemRepository repository;
  private final DestinoRepository destinoRepository;
  private final ColaboradorRepository colaboradorRepository;
  private final AtorService atorService;

  public ViagemService(ViagemRepository repository, DestinoRepository destinoRepository,
      ColaboradorRepository colaboradorRepository, AtorService atorService) {
    this.repository = repository;
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
    return new ViagemResponse(repository.save(viagem));
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
    exigirSolicitante(viagem, atorId);
    exigirSituacao(viagem, "solicitar", SituacaoViagem.RASCUNHO, SituacaoViagem.EM_AJUSTE);
    viagem.setSituacao(SituacaoViagem.SOLICITADA);
    // O pedido de ajuste morre aqui: ou foi atendido, ou o solicitante decidiu
    // reenviar assim mesmo. Manter o texto daria a entender que ainda vale.
    viagem.setMotivoAjuste(null);
    return new ViagemResponse(repository.save(viagem));
  }

  public ViagemResponse cancelar(Long id, Long atorId) {
    Viagem viagem = buscar(id);
    exigirSolicitante(viagem, atorId);
    exigirSituacao(viagem, "cancelar", SituacaoViagem.RASCUNHO, SituacaoViagem.EM_AJUSTE);
    viagem.setSituacao(SituacaoViagem.CANCELADA);
    return new ViagemResponse(repository.save(viagem));
  }

  // --- Ações do gestor ---

  public ViagemResponse aprovar(Long id, Long atorId) {
    return decidir(id, atorId, "aprovar", SituacaoViagem.APROVADA);
  }

  public ViagemResponse rejeitar(Long id, Long atorId) {
    return decidir(id, atorId, "rejeitar", SituacaoViagem.REJEITADA);
  }

  public ViagemResponse solicitarAjustes(Long id, AjusteRequest request, Long atorId) {
    Viagem viagem = decidirEntidade(id, atorId, "solicitar ajustes em", SituacaoViagem.EM_AJUSTE);
    viagem.setMotivoAjuste(request.getMotivo());
    return new ViagemResponse(repository.save(viagem));
  }

  private ViagemResponse decidir(Long id, Long atorId, String acao, SituacaoViagem destino) {
    return new ViagemResponse(repository.save(decidirEntidade(id, atorId, acao, destino)));
  }

  private Viagem decidirEntidade(Long id, Long atorId, String acao, SituacaoViagem destino) {
    Viagem viagem = buscar(id);
    atorService.exigirGestor(atorId);
    exigirSituacao(viagem, acao, SituacaoViagem.SOLICITADA);
    viagem.setSituacao(destino);
    return viagem;
  }

  // --- Regras comuns ---

  private void exigirSolicitante(Viagem viagem, Long atorId) {
    Colaborador ator = atorService.resolver(atorId);
    if (!Objects.equals(ator.getId(), viagem.getColaborador().getId())) {
      throw new AcaoRestritaAoSolicitanteException(viagem.getId(), atorId);
    }
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
