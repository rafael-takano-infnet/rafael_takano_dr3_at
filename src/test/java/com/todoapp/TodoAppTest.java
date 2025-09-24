package com.todoapp;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.todoapp.model.Tarefa;
import io.javalin.Javalin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TodoAppTest {

    private Javalin app;
    private HttpClient client;
    private ObjectMapper mapper;
    private final String BASE_URL = "http://localhost:7001";

    @BeforeEach
    void setUp() {
        //Configura mapper
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        // Inicia app na porta 7001 pra não conflitar
        app = TodoApp.criarApp().start(7001);

        //Configura cliente HTTP com timeout
        client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        // Aguardar um pouco pra garantir que o servidor subiu
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @AfterEach
    void tearDown() {
        if (app != null) {
            app.stop();
        }
    }

    @Test
    void testHelloEndpoint() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/hello"))
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("Hello, Javalin 6.6.0!", response.body());
    }

    @Test
    void testCriarTarefa() throws IOException, InterruptedException {
        String json = "{\"titulo\":\"Teste Javalin 6.6.0\",\"descricao\":\"Testando versão atualizada\"}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tarefas"))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(5))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        Tarefa tarefa = mapper.readValue(response.body(), Tarefa.class);
        assertNotNull(tarefa.getId());
        assertEquals("Teste Javalin 6.6.0", tarefa.getTitulo());
        assertEquals("Testando versão atualizada", tarefa.getDescricao());
        assertFalse(tarefa.isConcluida());
        assertNotNull(tarefa.getDataCriacao());
    }

    @Test
    void testBuscarTarefaPorId() throws IOException, InterruptedException {
        // Primeiro cria uma tarefa
        String json = "{\"titulo\":\"Buscar Teste\",\"descricao\":\"Para testar busca\"}";

        HttpRequest createRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tarefas"))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(5))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> createResponse = client.send(createRequest, HttpResponse.BodyHandlers.ofString());
        Tarefa tarefaCriada = mapper.readValue(createResponse.body(), Tarefa.class);

        //Depois busca pela tarefa criada
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tarefas/" + tarefaCriada.getId()))
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build();

        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, getResponse.statusCode());

        Tarefa tarefaBuscada = mapper.readValue(getResponse.body(), Tarefa.class);
        assertEquals(tarefaCriada.getId(), tarefaBuscada.getId());
        assertEquals("Buscar Teste", tarefaBuscada.getTitulo());
    }

    @Test
    void testListarTarefas() throws IOException, InterruptedException {
        //Cria algumas tarefas primeiro
        String json1 = "{\"titulo\":\"Tarefa 1\",\"descricao\":\"Primeira tarefa\"}";
        String json2 = "{\"titulo\":\"Tarefa 2\",\"descricao\":\"Segunda tarefa\"}";

        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tarefas"))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(5))
                .POST(HttpRequest.BodyPublishers.ofString(json1))
                .build();

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tarefas"))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(5))
                .POST(HttpRequest.BodyPublishers.ofString(json2))
                .build();

        client.send(request1, HttpResponse.BodyHandlers.ofString());
        client.send(request2, HttpResponse.BodyHandlers.ofString());

        // Agora lista todas
        HttpRequest listRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tarefas"))
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build();

        HttpResponse<String> listResponse = client.send(listRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, listResponse.statusCode());

        List<Tarefa> tarefas = mapper.readValue(listResponse.body(), new TypeReference<List<Tarefa>>() {});
        assertTrue(tarefas.size() >= 2);
    }

    @Test
    void testStatusEndpoint() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/status"))
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Map<String, Object> status = mapper.readValue(response.body(), new TypeReference<Map<String, Object>>() {});
        assertEquals("ok", status.get("status"));
        assertEquals("6.6.0", status.get("javalin_version"));
        assertNotNull(status.get("timestamp"));
    }

    @Test
    void testTarefaNaoEncontrada() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tarefas/id-inexistente-123"))
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    void testEchoEndpoint() throws IOException, InterruptedException {
        String json = "{\"mensagem\":\"teste echo com Javalin 6.6.0\"}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/echo"))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(5))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Map<String, Object> responseBody = mapper.readValue(response.body(), new TypeReference<Map<String, Object>>() {});
        assertEquals("teste echo com Javalin 6.6.0", responseBody.get("mensagem"));
    }
}