package com.sgv.api.colaborador;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/colaboradores")
public class ColaboradorController {

  private final ColaboradorService service;

  public ColaboradorController(ColaboradorService service) {
    this.service = service;
  }

  @GetMapping
  public List<ColaboradorResponse> findAll() {
    return service.findAll();
  }

  @GetMapping("/{id}")
  public ColaboradorResponse findById(@PathVariable Long id) {
    return service.findById(id);
  }

  @GetMapping("/{id}/lotacoes")
  public List<LotacaoResponse> lotacoes(@PathVariable Long id) {
    return service.lotacoes(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ColaboradorResponse create(@Valid @RequestBody ColaboradorRequest request) {
    return service.create(request);
  }

  @PutMapping("/{id}")
  public ColaboradorResponse update(@PathVariable Long id, @Valid @RequestBody ColaboradorRequest request) {
    return service.update(id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    service.delete(id);
  }
}
