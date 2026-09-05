package com.sgv.api.viagem;

import com.sgv.api.colaborador.AtorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/viagens")
public class ViagemController {

  private final ViagemService service;

  public ViagemController(ViagemService service) {
    this.service = service;
  }

  /** Sem filtro, todas. Com {@code ?situacao=SOLICITADA}, a fila do gestor. */
  @GetMapping
  public List<ViagemResponse> findAll(@RequestParam(required = false) SituacaoViagem situacao) {
    return service.findAll(situacao);
  }

  @GetMapping("/{id}")
  public ViagemResponse findById(@PathVariable Long id) {
    return service.findById(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ViagemResponse create(@Valid @RequestBody ViagemRequest request) {
    return service.create(request);
  }

  @PutMapping("/{id}")
  public ViagemResponse update(@PathVariable Long id, @Valid @RequestBody ViagemRequest request,
      @RequestHeader(AtorService.HEADER) Long atorId) {
    return service.update(id, request, atorId);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id, @RequestHeader(AtorService.HEADER) Long atorId) {
    service.delete(id, atorId);
  }

  @PostMapping("/{id}/solicitar")
  public ViagemResponse solicitar(@PathVariable Long id,
      @RequestHeader(AtorService.HEADER) Long atorId) {
    return service.solicitar(id, atorId);
  }

  @PostMapping("/{id}/cancelar")
  public ViagemResponse cancelar(@PathVariable Long id,
      @RequestHeader(AtorService.HEADER) Long atorId) {
    return service.cancelar(id, atorId);
  }

  @PostMapping("/{id}/aprovar")
  public ViagemResponse aprovar(@PathVariable Long id,
      @RequestHeader(AtorService.HEADER) Long atorId) {
    return service.aprovar(id, atorId);
  }

  @PostMapping("/{id}/rejeitar")
  public ViagemResponse rejeitar(@PathVariable Long id,
      @RequestHeader(AtorService.HEADER) Long atorId) {
    return service.rejeitar(id, atorId);
  }

  @PostMapping("/{id}/ajustes")
  public ViagemResponse solicitarAjustes(@PathVariable Long id,
      @Valid @RequestBody AjusteRequest request,
      @RequestHeader(AtorService.HEADER) Long atorId) {
    return service.solicitarAjustes(id, request, atorId);
  }
}
