package br.com.joaoteixeira.produtosapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProdutosApiApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void contextLoads() {
	}

	@Test
	void deveNegarAcessoSemToken() throws Exception {
		mockMvc.perform(get("/produtos"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void deveNegarAcessoComTokenInvalido() throws Exception {
		mockMvc.perform(get("/produtos")
						.header("X-API-TOKEN", "token-errado"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void devePermitirAcessoComTokenValido() throws Exception {
		mockMvc.perform(get("/produtos")
						.header("X-API-TOKEN", "produtos-token-123"))
				.andExpect(status().isOk())
				.andExpect(header().string("X-ACESSO", "permitido"));
	}

	@Test
	void deveFiltrarProdutosPorCategoria() throws Exception {
		mockMvc.perform(post("/produtos")
						.header("X-API-TOKEN", "produtos-token-123")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "nome": "Camiseta",
								  "descricao": "Camiseta basica",
								  "preco": 59.90,
								  "categoria": "vestuario"
								}
								"""))
				.andExpect(status().isOk());

		mockMvc.perform(get("/produtos?categoria=VESTUARIO")
						.header("X-API-TOKEN", "produtos-token-123"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].categoria").value("vestuario"));
	}

	@Test
	void deveRetornarStatusDaApi() throws Exception {
		mockMvc.perform(get("/status")
						.header("X-API-TOKEN", "produtos-token-123"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("online"));
	}
}
