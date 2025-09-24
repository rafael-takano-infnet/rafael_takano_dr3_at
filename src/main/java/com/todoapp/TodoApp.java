package com.todoapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.todoapp.controller.TodoController;
import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;

public class TodoApp {

    public static void main(String[] args) {
        // Configura Jackson pra lidar com datas
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        TodoController controller = new TodoController();

        Javalin app = Javalin.create(config -> {
            config.jsonMapper(new JavalinJackson(mapper, false));
        }).start(7000);

        System.out.println(" Servidor rodando na porta 7000!");
        System.out.println(" API de Gerenciamento de Tarefas - Javalin 6.6.0");

        // Endpoints básicos
        app.get("/hello", controller::hello);
        app.get("/status", controller::status);
        app.post("/echo", controller::echo);
        app.get("/saudacao/{nome}", controller::saudacao);

        // Endpoints das tarefas
        app.post("/tarefas", controller::criarTarefa);
        app.get("/tarefas", controller::listarTarefas);
        app.get("/tarefas/{id}", controller::buscarTarefaPorId);
        app.put("/tarefas/{id}/status", controller::atualizarStatus); // Bonus

        //Tratamento de erro global
        app.exception(Exception.class, (e, ctx) -> {
            System.err.println("Erro não tratado: " + e.getMessage());
            e.printStackTrace();
            ctx.status(500).json("Erro interno do servidor");
        });
    }

    // Método pra criar app sem iniciar
    public static Javalin criarApp() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        TodoController controller = new TodoController();

        Javalin app = Javalin.create(config -> {
            config.jsonMapper(new JavalinJackson(mapper, false));
        });

        //Registra as rotas
        app.get("/hello", controller::hello);
        app.get("/status", controller::status);
        app.post("/echo", controller::echo);
        app.get("/saudacao/{nome}", controller::saudacao);

        // Endpoints das tarefas
        app.post("/tarefas", controller::criarTarefa);
        app.get("/tarefas", controller::listarTarefas);
        app.get("/tarefas/{id}", controller::buscarTarefaPorId);
        app.put("/tarefas/{id}/status", controller::atualizarStatus);

        // Tratamento de erro global
        app.exception(Exception.class, (e, ctx) -> {
            System.err.println("Erro não tratado: " + e.getMessage());
            e.printStackTrace();
            ctx.status(500).json("Erro interno do servidor");
        });

        return app;
    }
}