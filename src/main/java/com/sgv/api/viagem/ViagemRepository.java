package com.sgv.api.viagem;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ViagemRepository extends JpaRepository<Viagem, Long> {

  List<Viagem> findBySituacao(SituacaoViagem situacao);
}
