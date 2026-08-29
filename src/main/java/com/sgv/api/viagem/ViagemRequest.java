package com.sgv.api.viagem;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class ViagemRequest {

  @NotNull(message = "destino é obrigatório")
  private Long destinoId;

  @NotNull(message = "colaborador é obrigatório")
  private Long colaboradorId;

  @NotBlank(message = "motivo é obrigatório")
  private String motivo;

  @NotNull(message = "data de saída é obrigatória")
  private LocalDate dataSaida;

  @NotNull(message = "data de retorno é obrigatória")
  private LocalDate dataRetorno;

  @NotNull(message = "meio de transporte é obrigatório")
  private MeioTransporte meioTransporte;

  @AssertTrue(message = "data de retorno deve ser igual ou posterior à data de saída")
  public boolean isPeriodoValido() {
    return dataSaida == null || dataRetorno == null || !dataRetorno.isBefore(dataSaida);
  }

  public Long getDestinoId() {
    return destinoId;
  }

  public void setDestinoId(Long destinoId) {
    this.destinoId = destinoId;
  }

  public Long getColaboradorId() {
    return colaboradorId;
  }

  public void setColaboradorId(Long colaboradorId) {
    this.colaboradorId = colaboradorId;
  }

  public String getMotivo() {
    return motivo;
  }

  public void setMotivo(String motivo) {
    this.motivo = motivo;
  }

  public LocalDate getDataSaida() {
    return dataSaida;
  }

  public void setDataSaida(LocalDate dataSaida) {
    this.dataSaida = dataSaida;
  }

  public LocalDate getDataRetorno() {
    return dataRetorno;
  }

  public void setDataRetorno(LocalDate dataRetorno) {
    this.dataRetorno = dataRetorno;
  }

  public MeioTransporte getMeioTransporte() {
    return meioTransporte;
  }

  public void setMeioTransporte(MeioTransporte meioTransporte) {
    this.meioTransporte = meioTransporte;
  }
}
