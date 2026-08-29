package com.sgv.api.viagem;

import com.sgv.api.destino.Destino;
import com.sgv.api.destino.DestinoNotFoundException;
import com.sgv.api.destino.DestinoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ViagemService {

  private final ViagemRepository repository;
  private final DestinoRepository destinoRepository;

  public ViagemService(ViagemRepository repository, DestinoRepository destinoRepository) {
    this.repository = repository;
    this.destinoRepository = destinoRepository;
  }

  public List<ViagemResponse> findAll() {
    return repository.findAll().stream()
        .map(ViagemResponse::new)
        .toList();
  }

  public ViagemResponse findById(Long id) {
    Viagem viagem = repository.findById(id)
        .orElseThrow(() -> new ViagemNotFoundException(id));
    return new ViagemResponse(viagem);
  }

  public ViagemResponse create(ViagemRequest request) {
    Viagem viagem = new Viagem(
        buscarDestino(request.getDestinoId()),
        request.getMotivo(),
        request.getDataSaida(),
        request.getDataRetorno(),
        request.getMeioTransporte(),
        SituacaoViagem.RASCUNHO);
    return new ViagemResponse(repository.save(viagem));
  }

  public ViagemResponse update(Long id, ViagemRequest request) {
    Viagem viagem = repository.findById(id)
        .orElseThrow(() -> new ViagemNotFoundException(id));
    viagem.setDestino(buscarDestino(request.getDestinoId()));
    viagem.setMotivo(request.getMotivo());
    viagem.setDataSaida(request.getDataSaida());
    viagem.setDataRetorno(request.getDataRetorno());
    viagem.setMeioTransporte(request.getMeioTransporte());
    return new ViagemResponse(repository.save(viagem));
  }

  public void delete(Long id) {
    if (!repository.existsById(id)) {
      throw new ViagemNotFoundException(id);
    }
    repository.deleteById(id);
  }

  private Destino buscarDestino(Long destinoId) {
    return destinoRepository.findById(destinoId)
        .orElseThrow(() -> new DestinoNotFoundException(destinoId));
  }
}
