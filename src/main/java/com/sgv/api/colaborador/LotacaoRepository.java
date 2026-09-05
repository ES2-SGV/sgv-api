package com.sgv.api.colaborador;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LotacaoRepository extends JpaRepository<Lotacao, Long> {

  /** Uma área com lotação — vigente ou encerrada — não pode ser apagada. */
  boolean existsByAreaId(Long areaId);
}
