package com.sgv.api.destino;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DestinoController.class)
class DestinoControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private DestinoService service;

  private String payload(String nome) {
    return """
        {
          "nome": "%s",
          "cidade": "São Paulo",
          "pais": "Brasil"
        }
        """.formatted(nome);
  }

  private DestinoResponse response() {
    return new DestinoResponse(new Destino("Matriz SP", "São Paulo", "Brasil"));
  }

  @Test
  void findAllDeveRetornarDestinos() throws Exception {
    when(service.findAll()).thenReturn(List.of(response()));

    mockMvc.perform(get("/destinos"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].nome").value("Matriz SP"));
  }

  @Test
  void createValidoDeveRetornar201() throws Exception {
    when(service.create(any(DestinoRequest.class))).thenReturn(response());

    mockMvc.perform(post("/destinos")
        .contentType(MediaType.APPLICATION_JSON)
        .content(payload("Matriz SP")))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.cidade").value("São Paulo"));
  }

  @Test
  void createSemNomeDeveRetornar400() throws Exception {
    mockMvc.perform(post("/destinos")
        .contentType(MediaType.APPLICATION_JSON)
        .content(payload("  ")))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.campos.nome").value("nome é obrigatório"));
  }

  @Test
  void findByIdInexistenteDeveRetornar404() throws Exception {
    when(service.findById(eq(99L))).thenThrow(new DestinoNotFoundException(99L));

    mockMvc.perform(get("/destinos/99"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Destino não encontrado: 99"));
  }

  @Test
  void deleteDeveRetornar204() throws Exception {
    mockMvc.perform(delete("/destinos/1"))
        .andExpect(status().isNoContent());
  }
}
