package com.sgv.api.area;

import com.sgv.api.colaborador.ColaboradorRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AreaService {

  private final AreaRepository repository;
  private final ColaboradorRepository colaboradorRepository;

  public AreaService(AreaRepository repository, ColaboradorRepository colaboradorRepository) {
    this.repository = repository;
    this.colaboradorRepository = colaboradorRepository;
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
    // A FK de colaborador já barraria no banco, mas aí o erro chegaria como 500.
    if (colaboradorRepository.existsByAreaId(id)) {
      throw new AreaEmUsoException(id);
    }
    repository.deleteById(id);
  }
}
