package com.sgv.api.destino;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/destinos")
public class DestinoController {

  private final DestinoService service;

  public DestinoController(DestinoService service) {
    this.service = service;
  }

  @GetMapping
  public List<DestinoResponse> findAll() {
    return service.findAll();
  }

  @GetMapping("/{id}")
  public DestinoResponse findById(@PathVariable Long id) {
    return service.findById(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public DestinoResponse create(@Valid @RequestBody DestinoRequest request) {
    return service.create(request);
  }

  @PutMapping("/{id}")
  public DestinoResponse update(@PathVariable Long id, @Valid @RequestBody DestinoRequest request) {
    return service.update(id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    service.delete(id);
  }
}
