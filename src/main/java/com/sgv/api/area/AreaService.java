package com.sgv.api.area;

import com.sgv.api.colaborador.LotacaoRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AreaService {

  private final AreaRepository repository;
  private final LotacaoRepository lotacaoRepository;

  public AreaService(AreaRepository repository, LotacaoRepository lotacaoRepository) {
    this.repository = repository;
    this.lotacaoRepository = lotacaoRepository;
  }

  public List<AreaResponse> findAll() {
    return repository.findAll().stream()
        .map(AreaResponse::new)
        .toList();
  }

  public AreaResponse findById(Long id) {
    Area area = repository.findById(id)
        .orElseThrow(() -> new AreaNotFoundException(id));
    return new AreaResponse(area);
  }

  public AreaResponse create(AreaRequest request) {
    if (repository.existsByNome(request.getNome())) {
      throw new NomeAreaJaCadastradoException(request.getNome());
    }
    return new AreaResponse(repository.save(new Area(request.getNome())));
  }

  public AreaResponse update(Long id, AreaRequest request) {
    Area area = repository.findById(id)
        .orElseThrow(() -> new AreaNotFoundException(id));
    if (repository.existsByNomeAndIdNot(request.getNome(), id)) {
      throw new NomeAreaJaCadastradoException(request.getNome());
    }
    area.setNome(request.getNome());
    return new AreaResponse(repository.save(area));
  }

  public void delete(Long id) {
    if (!repository.existsById(id)) {
      throw new AreaNotFoundException(id);
    }
    // Vale para lotação encerrada também: apagar a área apagaria o passado de
    // quem já trabalhou nela. A FK barraria no banco, mas como 500.
    if (lotacaoRepository.existsByAreaId(id)) {
      throw new AreaEmUsoException(id);
    }
    repository.deleteById(id);
  }
}
