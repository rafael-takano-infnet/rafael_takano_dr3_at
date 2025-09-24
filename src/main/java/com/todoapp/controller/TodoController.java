package com.todoapp.controller;

import com.todoapp.model.Tarefa;
import com.todoapp.service.TarefaService;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

public class TodoController {
    private TarefaService tarefaService;

    public TodoController() {
        this.tarefaService = new TarefaService();
    }

    //Endpoint hello
    public void hello(Context ctx) {
        ctx.result("Hello, Javalin 6.6.0!");
    }

    // Endpoint status do sistema
    public void status(Context ctx) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        Map<String, Object> response = Map.of(
                "status", "ok",
                "timestamp", timestamp,
                "javalin_version", "6.6.0"
        );
        ctx.json(response);
    }

    //Endpoint de echo
    public void echo(Context ctx) {
        try {
            Map<String, Object> body = ctx.bodyAsClass(Map.class);
            ctx.json(body);
        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST)
                    .json(Map.of("erro", "JSON inválido"));
        }
    }

    // Endpoint de saudação com nome
    public void saudacao(Context ctx) {
        String nome = ctx.pathParam("nome");
        Map<String, String> response = Map.of("mensagem", "Olá, " + nome + "!");
        ctx.json(response);
    }

    // Cria nova tarefa
    public void criarTarefa(Context ctx) {
        try {
            Map<String, Object> body = ctx.bodyAsClass(Map.class);
            String titulo = (String) body.get("titulo");
            String descricao = (String) body.get("descricao");

            Tarefa novaTarefa = tarefaService.criarTarefa(titulo, descricao);
            ctx.status(HttpStatus.CREATED).json(novaTarefa);

        } catch (IllegalArgumentException e) {
            ctx.status(HttpStatus.BAD_REQUEST)
                    .json(Map.of("erro", e.getMessage()));
        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST)
                    .json(Map.of("erro", "Dados inválidos"));
        }
    }

    // Lista todas as tarefas
    public void listarTarefas(Context ctx) {
        ctx.json(tarefaService.buscarTodasTarefas());
    }

    //Busca tarefa por ID
    public void buscarTarefaPorId(Context ctx) {
        String id = ctx.pathParam("id");
        Optional<Tarefa> tarefa = tarefaService.buscarTarefaPorId(id);

        if (tarefa.isPresent()) {
            ctx.json(tarefa.get());
        } else {
            ctx.status(HttpStatus.NOT_FOUND)
                    .json(Map.of("erro", "Tarefa não encontrada"));
        }
    }

    //Atualiza status da tarefa
    public void atualizarStatus(Context ctx) {
        try {
            String id = ctx.pathParam("id");
            Map<String, Object> body = ctx.bodyAsClass(Map.class);
            Boolean concluida = (Boolean) body.get("concluida");

            if (concluida == null) {
                ctx.status(HttpStatus.BAD_REQUEST)
                        .json(Map.of("erro", "Campo 'concluida' é obrigatório"));
                return;
            }

            Optional<Tarefa> tarefa = tarefaService.atualizarStatus(id, concluida);
            if (tarefa.isPresent()) {
                ctx.json(tarefa.get());
            } else {
                ctx.status(HttpStatus.NOT_FOUND)
                        .json(Map.of("erro", "Tarefa não encontrada"));
            }

        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST)
                    .json(Map.of("erro", "Dados inválidos"));
        }
    }

    // Getter pro service (útil pra testes_
    public TarefaService getTarefaService() {
        return tarefaService;
    }
}