package com.sgv.api.viagem;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ViagemHistoricoRepository extends JpaRepository<ViagemHistorico, Long> {

  /** Em ordem cronológica. O id desempata registros do mesmo instante. */
  List<ViagemHistorico> findByViagemIdOrderByRegistradoEmAscIdAsc(Long viagemId);
}
