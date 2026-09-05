package com.sgv.api.area;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AreaController.class)
class AreaControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private AreaService service;

  private AreaResponse response() {
    return new AreaResponse(new Area("Comercial"));
  }

  private String payload(String nome) {
    return """
        {
          "nome": "%s"
        }
        """.formatted(nome);
  }

  @Test
  void findAllDeveRetornarAreas() throws Exception {
    when(service.findAll()).thenReturn(List.of(response()));

    mockMvc.perform(get("/areas"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].nome").value("Comercial"));
  }

  @Test
  void createValidoDeveRetornar201() throws Exception {
    when(service.create(any(AreaRequest.class))).thenReturn(response());

    mockMvc.perform(post("/areas")
        .contentType(MediaType.APPLICATION_JSON)
        .content(payload("Comercial")))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.nome").value("Comercial"));
  }

  @Test
  void createSemNomeDeveRetornar400() throws Exception {
    mockMvc.perform(post("/areas")
        .contentType(MediaType.APPLICATION_JSON)
        .content(payload("  ")))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.campos.nome").value("nome é obrigatório"));
  }

  @Test
  void createComNomeDuplicadoDeveRetornar409() throws Exception {
    when(service.create(any(AreaRequest.class)))
        .thenThrow(new NomeAreaJaCadastradoException("Comercial"));

    mockMvc.perform(post("/areas")
        .contentType(MediaType.APPLICATION_JSON)
        .content(payload("Comercial")))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.message").value("Área já cadastrada: Comercial"));
  }

  @Test
  void findByIdInexistenteDeveRetornar404() throws Exception {
    when(service.findById(eq(99L))).thenThrow(new AreaNotFoundException(99L));

    mockMvc.perform(get("/areas/99"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Área não encontrada: 99"));
  }

  @Test
  void deleteDeveRetornar204() throws Exception {
    mockMvc.perform(delete("/areas/1"))
        .andExpect(status().isNoContent());
  }

  @Test
  void deleteDeAreaEmUsoDeveRetornar409() throws Exception {
    doThrow(new AreaEmUsoException(1L)).when(service).delete(1L);

    mockMvc.perform(delete("/areas/1"))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.status").value(409));
  }
}
