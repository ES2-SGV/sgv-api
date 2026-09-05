package com.sgv.api.colaborador;

import com.sgv.api.area.Area;
import com.sgv.api.area.AreaNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ColaboradorController.class)
class ColaboradorControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private ColaboradorService service;

  private ColaboradorResponse response() {
    return new ColaboradorResponse(
        new Colaborador("1001-2", "Ana Souza", new Area("Comercial"), Cargo.GESTOR));
  }

  private String payload(String matricula) {
    return """
        {
          "matricula": "%s",
          "nome": "Ana Souza",
          "areaId": 1,
          "cargo": "GESTOR"
        }
        """.formatted(matricula);
  }

  @Test
  void findAllDeveRetornarColaboradores() throws Exception {
    when(service.findAll()).thenReturn(List.of(response()));

    mockMvc.perform(get("/colaboradores"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].matricula").value("1001-2"))
        .andExpect(jsonPath("$[0].area.nome").value("Comercial"))
        .andExpect(jsonPath("$[0].cargo").value("GESTOR"));
  }

  @Test
  void createValidoDeveRetornar201() throws Exception {
    when(service.create(any(ColaboradorRequest.class))).thenReturn(response());

    mockMvc.perform(post("/colaboradores")
        .contentType(MediaType.APPLICATION_JSON)
        .content(payload("1001-2")))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.nome").value("Ana Souza"))
        .andExpect(jsonPath("$.cargo").value("GESTOR"));
  }

  @Test
  void createSemMatriculaDeveRetornar400() throws Exception {
    mockMvc.perform(post("/colaboradores")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "nome": "Ana Souza",
              "areaId": 1,
              "cargo": "GESTOR"
            }
            """))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.campos.matricula").value("matrícula é obrigatória"));
  }

  @Test
  void createComMatriculaForaDoFormatoDeveRetornar400() throws Exception {
    for (String invalida : List.of("M-1001", "1001", "10012", "1001-", "12345-6", "abcd-1", "1001-23", "  ", "")) {
      mockMvc.perform(post("/colaboradores")
          .contentType(MediaType.APPLICATION_JSON)
          .content(payload(invalida)))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.campos.matricula")
              .value("matrícula deve estar no formato XXXX-X (somente dígitos)"));
    }
  }

  @Test
  void createSemAreaDeveRetornar400() throws Exception {
    mockMvc.perform(post("/colaboradores")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "matricula": "1001-2",
              "nome": "Ana Souza",
              "cargo": "GESTOR"
            }
            """))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.campos.areaId").value("área é obrigatória"));
  }

  @Test
  void createSemCargoDeveRetornar400() throws Exception {
    mockMvc.perform(post("/colaboradores")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "matricula": "1001-2",
              "nome": "Ana Souza",
              "areaId": 1
            }
            """))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.campos.cargo").value("cargo é obrigatório"));
  }

  @Test
  void createComAreaInexistenteDeveRetornar404() throws Exception {
    when(service.create(any(ColaboradorRequest.class))).thenThrow(new AreaNotFoundException(1L));

    mockMvc.perform(post("/colaboradores")
        .contentType(MediaType.APPLICATION_JSON)
        .content(payload("1001-2")))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Área não encontrada: 1"));
  }

  @Test
  void createComMatriculaDuplicadaDeveRetornar409() throws Exception {
    when(service.create(any(ColaboradorRequest.class)))
        .thenThrow(new MatriculaJaCadastradaException("1001-2"));

    mockMvc.perform(post("/colaboradores")
        .contentType(MediaType.APPLICATION_JSON)
        .content(payload("1001-2")))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.status").value(409))
        .andExpect(jsonPath("$.message").value("Matrícula já cadastrada: 1001-2"));
  }

  @Test
  void idNaoNumericoDeveRetornar400ComApiError() throws Exception {
    mockMvc.perform(get("/colaboradores/abc"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.message").value("valor inválido para id: abc"));
  }

  @Test
  void findByIdInexistenteDeveRetornar404() throws Exception {
    when(service.findById(eq(99L))).thenThrow(new ColaboradorNotFoundException(99L));

    mockMvc.perform(get("/colaboradores/99"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Colaborador não encontrado: 99"));
  }

  @Test
  void lotacoesDeveRetornarOHistoricoDeAreaECargo() throws Exception {
    Colaborador ana = new Colaborador("1001-2", "Ana", new Area("INFORMATICA"),
        Cargo.COLABORADOR, LocalDateTime.of(2026, 9, 1, 8, 0));
    ana.lotar(new Area("RH"), Cargo.GESTOR, LocalDateTime.of(2026, 9, 10, 8, 0));
    when(service.lotacoes(eq(1L)))
        .thenReturn(ana.getLotacoes().stream().map(LotacaoResponse::new).toList());

    mockMvc.perform(get("/colaboradores/1/lotacoes"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].area.nome").value("INFORMATICA"))
        .andExpect(jsonPath("$[0].cargo").value("COLABORADOR"))
        .andExpect(jsonPath("$[0].fim").value("2026-09-10T08:00:00"))
        .andExpect(jsonPath("$[1].area.nome").value("RH"))
        .andExpect(jsonPath("$[1].cargo").value("GESTOR"))
        .andExpect(jsonPath("$[1].fim").doesNotExist());
  }
}
