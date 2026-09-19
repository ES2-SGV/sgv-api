package com.sgv.api.viagem.despesa;

import com.sgv.api.shared.NotFoundException;
import com.sgv.api.viagem.SituacaoViagem;
import com.sgv.api.viagem.Viagem;
import com.sgv.api.viagem.ViagemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DespesaService {

  private final DespesaRepository despesaRepository;
  private final ViagemRepository viagemRepository;

  public DespesaService(DespesaRepository despesaRepository, ViagemRepository viagemRepository) {
    this.despesaRepository = despesaRepository;
    this.viagemRepository = viagemRepository;
  }

  @Transactional(readOnly = true)
  public List<DespesaResponse> findAll(Long viagemId) {
    Viagem viagem = viagemRepository.findById(viagemId)
        .orElseThrow(() -> new NotFoundException("Viagem não encontrada"));
    return despesaRepository.findByViagemId(viagem.getId())
        .stream()
        .map(DespesaResponse::new)
        .collect(Collectors.toList());
  }

  @Transactional
  public DespesaResponse create(Long viagemId, DespesaRequest request) {
    Viagem viagem = viagemRepository.findById(viagemId)
        .orElseThrow(() -> new NotFoundException("Viagem não encontrada"));

    if (viagem.getSituacao() != SituacaoViagem.APROVADA) {
      throw new IllegalArgumentException("A viagem não está aprovada");
    }

    if (request.dataDespesa().isAfter(LocalDate.now())) {
      throw new IllegalArgumentException("A data da despesa não pode ser futura");
    }

    Despesa despesa = new Despesa(
        viagem,
        request.dataDespesa(),
        request.tipoDespesa(),
        request.descricao(),
        request.valor()
    );

    despesa = despesaRepository.save(despesa);
    return new DespesaResponse(despesa);
  }

  @Transactional
  public void delete(Long viagemId, Long despesaId) {
    Despesa despesa = despesaRepository.findById(despesaId)
        .orElseThrow(() -> new NotFoundException("Despesa não encontrada"));

    if (!despesa.getViagem().getId().equals(viagemId)) {
      throw new IllegalArgumentException("Despesa não pertence a esta viagem");
    }

    despesaRepository.delete(despesa);
  }
}
