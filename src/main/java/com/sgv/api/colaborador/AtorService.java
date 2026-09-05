package com.sgv.api.colaborador;

import org.springframework.stereotype.Service;

/**
 * Resolve quem está executando a ação, a partir do header {@value #HEADER}.
 *
 * <p>Isto <strong>não é autenticação</strong>: o header é informado pelo
 * cliente e ninguém verifica se ele é quem diz ser. O que está aqui é a regra
 * de negócio — quem pode fazer o quê — isolada num ponto só, para que trocar
 * pela identidade real (Spring Security) não encoste nos serviços de domínio.
 */
@Service
public class AtorService {

  public static final String HEADER = "X-Colaborador-Id";

  private final ColaboradorRepository repository;

  public AtorService(ColaboradorRepository repository) {
    this.repository = repository;
  }

  /** O colaborador que está agindo. 404 se o header apontar para quem não existe. */
  public Colaborador resolver(Long atorId) {
    return repository.findById(atorId)
        .orElseThrow(() -> new ColaboradorNotFoundException(atorId));
  }

  /** Idem, mas exige o cargo de gestor. 403 caso contrário. */
  public Colaborador exigirGestor(Long atorId) {
    Colaborador ator = resolver(atorId);
    if (ator.getCargo() != Cargo.GESTOR) {
      throw new AcaoRestritaAGestorException(ator);
    }
    return ator;
  }
}
