package com.sgv.api.viagem;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AjusteRequest {

  @NotBlank(message = "motivo do ajuste é obrigatório")
  @Size(max = 500, message = "motivo do ajuste deve ter no máximo 500 caracteres")
  private String motivo;

  public String getMotivo() {
    return motivo;
  }

  public void setMotivo(String motivo) {
    this.motivo = motivo;
  }
}
