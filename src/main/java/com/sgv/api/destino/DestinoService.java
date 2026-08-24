package com.sgv.api.destino;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DestinoService {

  private final DestinoRepository repository;

  public DestinoService(DestinoRepository repository) {
    this.repository = repository;
  }

  public List<DestinoResponse> findAll() {
    return repository.findAll().stream()
        .map(DestinoResponse::new)
        .toList();
  }

  public DestinoResponse findById(Long id) {
    Destino destino = repository.findById(id)
        .orElseThrow(() -> new DestinoNotFoundException(id));
    return new DestinoResponse(destino);
  }

  public DestinoResponse create(DestinoRequest request) {
    Destino destino = new Destino(request.getNome(), request.getCidade(), request.getPais());
    return new DestinoResponse(repository.save(destino));
  }

  public DestinoResponse update(Long id, DestinoRequest request) {
    Destino destino = repository.findById(id)
        .orElseThrow(() -> new DestinoNotFoundException(id));
    destino.setNome(request.getNome());
    destino.setCidade(request.getCidade());
    destino.setPais(request.getPais());
    return new DestinoResponse(repository.save(destino));
  }

  public void delete(Long id) {
    if (!repository.existsById(id)) {
      throw new DestinoNotFoundException(id);
    }
    repository.deleteById(id);
  }
}
