package com.sgv.api.viagem;

import com.sgv.api.area.Area;
import com.sgv.api.colaborador.Cargo;
import com.sgv.api.colaborador.Colaborador;
import com.sgv.api.destino.Destino;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ViagemController.class)
class ViagemControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private ViagemService service;

  private ViagemResponse response() {
    Destino destino = new Destino("Matriz SP", "São Paulo", "Brasil");
    Colaborador colaborador = new Colaborador("1001-2", "Ana Souza", new Area("Comercial"), Cargo.COLABORADOR);
    Viagem viagem = new Viagem(destino, colaborador, "Reunião com cliente", LocalDate.of(2026, 9, 10),
        LocalDate.of(2026, 9, 12), MeioTransporte.AEREO, SituacaoViagem.RASCUNHO);
    return new ViagemResponse(viagem);
  }

  private String payload(String motivo, String dataRetorno) {
    return """
        {
          "destinoId": 1,
          "colaboradorId": 2,
          "motivo": "%s",
          "dataSaida": "2026-09-10",
          "dataRetorno": "%s",
          "meioTransporte": "AEREO"
        }
        """.formatted(motivo, dataRetorno);
  }

  @Test
  void findAllDeveRetornarViagensComDestinoAninhado() throws Exception {
    when(service.findAll()).thenReturn(List.of(response()));

    mockMvc.perform(get("/viagens"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].motivo").value("Reunião com cliente"))
        .andExpect(jsonPath("$[0].situacao").value("RASCUNHO"))
        .andExpect(jsonPath("$[0].dataSaida").value("2026-09-10"))
        .andExpect(jsonPath("$[0].destino.cidade").value("São Paulo"))
        .andExpect(jsonPath("$[0].colaborador.matricula").value("1001-2"));
  }

  @Test
  void createValidoDeveRetornar201() throws Exception {
    when(service.create(any(ViagemRequest.class))).thenReturn(response());

    mockMvc.perform(post("/viagens")
        .contentType(MediaType.APPLICATION_JSON)
        .content(payload("Reunião com cliente", "2026-09-12")))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.situacao").value("RASCUNHO"));
  }

  @Test
  void createSemMotivoDeveRetornar400() throws Exception {
    mockMvc.perform(post("/viagens")
        .contentType(MediaType.APPLICATION_JSON)
        .content(payload("   ", "2026-09-12")))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.campos.motivo").value("motivo é obrigatório"));
  }

  @Test
  void createComRetornoAntesDaSaidaDeveRetornar400() throws Exception {
    mockMvc.perform(post("/viagens")
        .contentType(MediaType.APPLICATION_JSON)
        .content(payload("Reunião com cliente", "2026-09-09")))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.campos.periodoValido").exists());
  }

  @Test
  void findByIdInexistenteDeveRetornar404() throws Exception {
    when(service.findById(eq(99L))).thenThrow(new ViagemNotFoundException(99L));

    mockMvc.perform(get("/viagens/99"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.message").value("Viagem não encontrada: 99"));
  }
}
