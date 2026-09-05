package com.sgv.api.viagem;

import com.sgv.api.area.Area;
import com.sgv.api.colaborador.AcaoRestritaAGestorException;
import com.sgv.api.colaborador.AtorService;
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
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ViagemController.class)
class ViagemControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private ViagemService service;

  private ViagemResponse response() {
    return response(SituacaoViagem.RASCUNHO);
  }

  private ViagemResponse response(SituacaoViagem situacao) {
    Destino destino = new Destino("Matriz SP", "São Paulo", "Brasil");
    Colaborador colaborador = new Colaborador("1001-2", "Ana Souza", new Area("Comercial"), Cargo.COLABORADOR);
    Viagem viagem = new Viagem(destino, colaborador, "Reunião com cliente", LocalDate.of(2026, 9, 10),
        LocalDate.of(2026, 9, 12), MeioTransporte.AEREO, situacao);
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
    when(service.findAll(isNull())).thenReturn(List.of(response()));

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

  @Test
  void findAllComSituacaoDeveRepassarOFiltro() throws Exception {
    when(service.findAll(eq(SituacaoViagem.SOLICITADA)))
        .thenReturn(List.of(response(SituacaoViagem.SOLICITADA)));

    mockMvc.perform(get("/viagens").param("situacao", "SOLICITADA"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].situacao").value("SOLICITADA"));
  }

  @Test
  void findAllComSituacaoDesconhecidaDeveRetornar400() throws Exception {
    mockMvc.perform(get("/viagens").param("situacao", "INEXISTENTE"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400));
  }

  @Test
  void solicitarDeveRetornarAViagemSolicitada() throws Exception {
    when(service.solicitar(eq(1L), eq(2L))).thenReturn(response(SituacaoViagem.SOLICITADA));

    mockMvc.perform(post("/viagens/1/solicitar").header(AtorService.HEADER, 2))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.situacao").value("SOLICITADA"));
  }

  @Test
  void cancelarDeveRetornarAViagemCancelada() throws Exception {
    when(service.cancelar(eq(1L), eq(2L))).thenReturn(response(SituacaoViagem.CANCELADA));

    mockMvc.perform(post("/viagens/1/cancelar").header(AtorService.HEADER, 2))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.situacao").value("CANCELADA"));
  }

  @Test
  void aprovarDeveRetornarAViagemAprovada() throws Exception {
    when(service.aprovar(eq(1L), eq(3L))).thenReturn(response(SituacaoViagem.APROVADA));

    mockMvc.perform(post("/viagens/1/aprovar").header(AtorService.HEADER, 3))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.situacao").value("APROVADA"));
  }

  @Test
  void aprovarPorNaoGestorDeveRetornar403() throws Exception {
    when(service.aprovar(eq(1L), eq(2L))).thenThrow(new AcaoRestritaAGestorException(
        new com.sgv.api.colaborador.Colaborador("1001-2", "Ana", new Area("Comercial"),
            com.sgv.api.colaborador.Cargo.COLABORADOR)));

    mockMvc.perform(post("/viagens/1/aprovar").header(AtorService.HEADER, 2))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.status").value(403));
  }

  @Test
  void acaoSemOHeaderDoAtorDeveRetornar400() throws Exception {
    mockMvc.perform(post("/viagens/1/solicitar"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.message").value("header obrigatório ausente: X-Colaborador-Id"));
  }

  @Test
  void ajustesDeveExigirMotivo() throws Exception {
    mockMvc.perform(post("/viagens/1/ajustes")
        .header(AtorService.HEADER, 3)
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"motivo\": \"  \"}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.campos.motivo").value("motivo do ajuste é obrigatório"));
  }

  @Test
  void ajustesValidoDeveDevolverEmAjuste() throws Exception {
    when(service.solicitarAjustes(eq(1L), any(AjusteRequest.class), eq(3L)))
        .thenReturn(response(SituacaoViagem.EM_AJUSTE));

    mockMvc.perform(post("/viagens/1/ajustes")
        .header(AtorService.HEADER, 3)
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"motivo\": \"faltou o orçamento\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.situacao").value("EM_AJUSTE"));
  }

  @Test
  void updateDepoisDeSolicitadaDeveRetornar409() throws Exception {
    when(service.update(eq(1L), any(ViagemRequest.class), eq(2L)))
        .thenThrow(new TransicaoInvalidaException("editar", SituacaoViagem.SOLICITADA,
            SituacaoViagem.RASCUNHO, SituacaoViagem.EM_AJUSTE));

    mockMvc.perform(put("/viagens/1")
        .header(AtorService.HEADER, 2)
        .contentType(MediaType.APPLICATION_JSON)
        .content(payload("Reunião com cliente", "2026-09-12")))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.status").value(409))
        .andExpect(jsonPath("$.message").value(
            "não é possível editar uma viagem SOLICITADA: a situação precisa ser RASCUNHO ou EM_AJUSTE"));
  }

  @Test
  void meioTransporteInvalidoDeveRetornar400EmVezDe500() throws Exception {
    mockMvc.perform(post("/viagens")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "destinoId": 1,
              "colaboradorId": 2,
              "motivo": "Reunião",
              "dataSaida": "2026-09-10",
              "dataRetorno": "2026-09-12",
              "meioTransporte": "FOGUETE"
            }
            """))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("corpo da requisição inválido"));
  }
}
