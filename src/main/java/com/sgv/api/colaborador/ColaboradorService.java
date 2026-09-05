package com.sgv.api.colaborador;

import com.sgv.api.area.Area;
import com.sgv.api.area.AreaNotFoundException;
import com.sgv.api.area.AreaRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

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
        buscarArea(request.getAreaId()), request.getCargo(), LocalDateTime.now());
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
    relotarSeMudou(colaborador, buscarArea(request.getAreaId()), request.getCargo());
    return new ColaboradorResponse(repository.save(colaborador));
  }

  public void delete(Long id) {
    if (!repository.existsById(id)) {
      throw new ColaboradorNotFoundException(id);
    }
    repository.deleteById(id);
  }

  public List<LotacaoResponse> lotacoes(Long id) {
    Colaborador colaborador = repository.findById(id)
        .orElseThrow(() -> new ColaboradorNotFoundException(id));
    return colaborador.getLotacoes().stream().map(LotacaoResponse::new).toList();
  }

  /**
   * Mudar de área ou de cargo fecha a lotação atual e abre outra. Reenviar os
   * mesmos valores não gera linha nova — senão o histórico encheria de ruído a
   * cada edição de nome.
   */
  private void relotarSeMudou(Colaborador colaborador, Area area, Cargo cargo) {
    Lotacao vigente = colaborador.getLotacaoVigente();
    if (!Objects.equals(vigente.getArea().getId(), area.getId()) || vigente.getCargo() != cargo) {
      colaborador.lotar(area, cargo, LocalDateTime.now());
    }
  }

  private Area buscarArea(Long areaId) {
    return areaRepository.findById(areaId)
        .orElseThrow(() -> new AreaNotFoundException(areaId));
  }
}
