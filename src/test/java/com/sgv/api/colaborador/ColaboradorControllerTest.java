package com.sgv.api.colaborador;

import com.sgv.api.area.Area;
import com.sgv.api.area.AreaNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

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
    return new ColaboradorResponse(new Colaborador("M-1001", "Ana Souza", new Area("Comercial")));
  }

  private String payload(String matricula) {
    return """
        {
          "matricula": "%s",
          "nome": "Ana Souza",
          "areaId": 1
        }
        """.formatted(matricula);
  }

  @Test
  void findAllDeveRetornarColaboradores() throws Exception {
    when(service.findAll()).thenReturn(List.of(response()));

    mockMvc.perform(get("/colaboradores"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].matricula").value("M-1001"))
        .andExpect(jsonPath("$[0].area.nome").value("Comercial"));
  }

  @Test
  void createValidoDeveRetornar201() throws Exception {
    when(service.create(any(ColaboradorRequest.class))).thenReturn(response());

    mockMvc.perform(post("/colaboradores")
        .contentType(MediaType.APPLICATION_JSON)
        .content(payload("M-1001")))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.nome").value("Ana Souza"));
  }

  @Test
  void createSemMatriculaDeveRetornar400() throws Exception {
    mockMvc.perform(post("/colaboradores")
        .contentType(MediaType.APPLICATION_JSON)
        .content(payload("  ")))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.campos.matricula").value("matrícula é obrigatória"));
  }

  @Test
  void createSemAreaDeveRetornar400() throws Exception {
    mockMvc.perform(post("/colaboradores")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "matricula": "M-1001",
              "nome": "Ana Souza"
            }
            """))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.campos.areaId").value("área é obrigatória"));
  }

  @Test
  void createComAreaInexistenteDeveRetornar404() throws Exception {
    when(service.create(any(ColaboradorRequest.class))).thenThrow(new AreaNotFoundException(1L));

    mockMvc.perform(post("/colaboradores")
        .contentType(MediaType.APPLICATION_JSON)
        .content(payload("M-1001")))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Área não encontrada: 1"));
  }

  @Test
  void createComMatriculaDuplicadaDeveRetornar409() throws Exception {
    when(service.create(any(ColaboradorRequest.class)))
        .thenThrow(new MatriculaJaCadastradaException("M-1001"));

    mockMvc.perform(post("/colaboradores")
        .contentType(MediaType.APPLICATION_JSON)
        .content(payload("M-1001")))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.status").value(409))
        .andExpect(jsonPath("$.message").value("Matrícula já cadastrada: M-1001"));
  }

  @Test
  void findByIdInexistenteDeveRetornar404() throws Exception {
    when(service.findById(eq(99L))).thenThrow(new ColaboradorNotFoundException(99L));

    mockMvc.perform(get("/colaboradores/99"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Colaborador não encontrado: 99"));
  }
}
