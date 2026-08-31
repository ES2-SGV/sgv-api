package com.sgv.api.colaborador;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ColaboradorService {

  private final ColaboradorRepository repository;

  public ColaboradorService(ColaboradorRepository repository) {
    this.repository = repository;
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
    Colaborador colaborador = new Colaborador(request.getMatricula(), request.getNome(), request.getArea());
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
    colaborador.setArea(request.getArea());
    return new ColaboradorResponse(repository.save(colaborador));
  }

  public void delete(Long id) {
    if (!repository.existsById(id)) {
      throw new ColaboradorNotFoundException(id);
    }
    repository.deleteById(id);
  }
}
