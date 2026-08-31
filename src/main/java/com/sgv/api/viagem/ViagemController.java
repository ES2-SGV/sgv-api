package com.sgv.api.viagem;

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

  @GetMapping
  public List<ViagemResponse> findAll() {
    return service.findAll();
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
  public ViagemResponse update(@PathVariable Long id, @Valid @RequestBody ViagemRequest request) {
    return service.update(id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    service.delete(id);
  }
}
