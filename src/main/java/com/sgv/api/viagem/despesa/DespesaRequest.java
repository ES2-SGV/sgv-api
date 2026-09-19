package com.sgv.api.viagem.despesa;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

public record DespesaRequest(
    @NotNull LocalDate dataDespesa,
    @NotNull TipoDespesa tipoDespesa,
    @NotBlank String descricao,
    @NotNull @Positive BigDecimal valor
) {
}
