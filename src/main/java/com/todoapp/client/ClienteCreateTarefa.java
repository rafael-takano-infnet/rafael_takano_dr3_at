package com.todoapp.client;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ClienteCreateTarefa {

    public static void main(String[] args) {
        try {
            criarMultiplasTarefas();
        } catch (Exception e) {
            System.err.println("Erro ao criar tarefas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void criarMultiplasTarefas() throws IOException {
        String[][] tarefas = {
                {"Comprar leite", "Ir ao mercado"},
                {"Exercitar-se", "caminhada"},
                {"Ler livro", "Continuar leitura"},
                {"Ligar para mãe", "Conversar com família"},
                {"Lavar roupa", "Roupa de cama"},
                {"Estudar inglês", "30 minutos de estudo"},
                {"Fazer almoço", "Preparar comida"},
                {"Limpar casa", "Organizar os cômodos"}
        };

        System.out.println("=> CRIANDO MÚLTIPLAS TAREFAS <=");

        for (int i = 0; i < tarefas.length; i++) {
            String titulo = tarefas[i][0];
            String descricao = tarefas[i][1];

            System.out.printf("Criando tarefa %d/%d: %s\n", i + 1, tarefas.length, titulo);

            boolean sucesso = criarTarefa(titulo, descricao);

            if (sucesso) {
                System.out.println("Sucesso!");
            } else {
                System.out.println("Erro!");
            }

            // Ppausa entre as criações
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private static boolean criarTarefa(String titulo, String descricao) throws IOException {
        String urlString = "http://localhost:7000/tarefas";
        String jsonBody = "{\n" +
                "  \"titulo\": \"" + titulo + "\",\n" +
                "  \"descricao\": \"" + descricao + "\"\n" +
                "}";

        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Configura a requisição POST
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        // Envia o JSON no body
        try (var outputStream = connection.getOutputStream()) {
            byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
            outputStream.write(input, 0, input.length);
        }

        // Lê a resposta
        int responseCode = connection.getResponseCode();

        if (responseCode == 201) {
            String response = lerResposta(connection);
            // Só mostra detalhes da primeira tarefa para não poluir o console
            if (titulo.equals("Estudar Java")) {
                System.out.println("Exemplo de resposta: " + response);
            }
        }

        connection.disconnect();
        return responseCode == 201;
    }

    private static String lerResposta(HttpURLConnection connection) throws IOException {
        StringBuilder response = new StringBuilder();
        try (var reader = new java.io.BufferedReader(
                new java.io.InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }
        return response.toString();
    }

    private static String lerErro(HttpURLConnection connection) throws IOException {
        StringBuilder response = new StringBuilder();
        try (var reader = new java.io.BufferedReader(
                new java.io.InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }
        return response.toString();
    }
}