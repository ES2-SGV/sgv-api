package com.sgv.api.colaborador;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ColaboradorRepository extends JpaRepository<Colaborador, Long> {

  boolean existsByMatricula(String matricula);

  boolean existsByMatriculaAndIdNot(String matricula, Long id);
}
