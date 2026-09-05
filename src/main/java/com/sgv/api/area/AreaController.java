package com.sgv.api.area;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/areas")
public class AreaController {

  private final AreaService service;

  public AreaController(AreaService service) {
    this.service = service;
  }

  @GetMapping
  public List<AreaResponse> findAll() {
    return service.findAll();
  }

  @GetMapping("/{id}")
  public AreaResponse findById(@PathVariable Long id) {
    return service.findById(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public AreaResponse create(@Valid @RequestBody AreaRequest request) {
    return service.create(request);
  }

  @PutMapping("/{id}")
  public AreaResponse update(@PathVariable Long id, @Valid @RequestBody AreaRequest request) {
    return service.update(id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    service.delete(id);
  }
}
