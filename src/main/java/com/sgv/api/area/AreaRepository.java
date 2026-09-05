package com.sgv.api.area;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AreaRepository extends JpaRepository<Area, Long> {

  boolean existsByNome(String nome);

  boolean existsByNomeAndIdNot(String nome, Long id);
}
