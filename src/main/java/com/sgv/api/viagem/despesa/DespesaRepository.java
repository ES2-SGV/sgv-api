package com.sgv.api.viagem.despesa;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DespesaRepository extends JpaRepository<Despesa, Long> {
  List<Despesa> findByViagemId(Long viagemId);
}
