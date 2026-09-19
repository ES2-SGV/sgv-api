package com.sgv.api.viagem.despesa;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/viagens/{viagemId}/despesas")
public class DespesaController {

  private final DespesaService service;

  public DespesaController(DespesaService service) {
    this.service = service;
  }

  @GetMapping
  public List<DespesaResponse> findAll(@PathVariable Long viagemId) {
    return service.findAll(viagemId);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public DespesaResponse create(@PathVariable Long viagemId, @Valid @RequestBody DespesaRequest request) {
    return service.create(viagemId, request);
  }

  @DeleteMapping("/{despesaId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long viagemId, @PathVariable Long despesaId) {
    service.delete(viagemId, despesaId);
  }
}
