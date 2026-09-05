package com.sgv.api.colaborador;

import com.sgv.api.area.Area;
import com.sgv.api.area.AreaNotFoundException;
import com.sgv.api.area.AreaRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ColaboradorService {

  private final ColaboradorRepository repository;
  private final AreaRepository areaRepository;

  public ColaboradorService(ColaboradorRepository repository, AreaRepository areaRepository) {
    this.repository = repository;
    this.areaRepository = areaRepository;
  }

  public List<ColaboradorResponse> findAll() {
    return repository.findAll().stream()
        .map(ColaboradorResponse::new)
        .toList();
  }

  public ColaboradorResponse findById(Long id) {
    Colaborador colaborador = repository.findById(id)
        .orElseThrow(() -> new ColaboradorNotFoundException(id));
    return new ColaboradorResponse(colaborador);
  }

  public ColaboradorResponse create(ColaboradorRequest request) {
    if (repository.existsByMatricula(request.getMatricula())) {
      throw new MatriculaJaCadastradaException(request.getMatricula());
    }
    Colaborador colaborador = new Colaborador(request.getMatricula(), request.getNome(),
        buscarArea(request.getAreaId()), request.getCargo());
    return new ColaboradorResponse(repository.save(colaborador));
  }

  public ColaboradorResponse update(Long id, ColaboradorRequest request) {
    Colaborador colaborador = repository.findById(id)
        .orElseThrow(() -> new ColaboradorNotFoundException(id));
    if (repository.existsByMatriculaAndIdNot(request.getMatricula(), id)) {
      throw new MatriculaJaCadastradaException(request.getMatricula());
    }
    colaborador.setMatricula(request.getMatricula());
    colaborador.setNome(request.getNome());
    colaborador.setArea(buscarArea(request.getAreaId()));
    colaborador.setCargo(request.getCargo());
    return new ColaboradorResponse(repository.save(colaborador));
  }

  public void delete(Long id) {
    if (!repository.existsById(id)) {
      throw new ColaboradorNotFoundException(id);
    }
    repository.deleteById(id);
  }

  private Area buscarArea(Long areaId) {
    return areaRepository.findById(areaId)
        .orElseThrow(() -> new AreaNotFoundException(areaId));
  }
}
