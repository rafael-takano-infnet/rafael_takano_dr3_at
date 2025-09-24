package com.todoapp.client;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ClienteListTarefas {

    public static void main(String[] args) {
        try {
            listarTarefas();
        } catch (Exception e) {
            System.err.println("Erro ao listar tarefas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void listarTarefas() throws IOException {
        String urlString = "http://localhost:7000/tarefas";

        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Configura a requisição GET
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");

        // Lê a resposta
        int responseCode = connection.getResponseCode();
        System.out.println("=> LISTAR TAREFAS <=");
        System.out.println("Status Code: " + responseCode);
        System.out.println("Mensagem: " + connection.getResponseMessage());

        if (responseCode == 200) {
            String response = lerResposta(connection);
            System.out.println("Lista de tarefas:");

            // Tenta formatar o JSON de forma mais legível
            String jsonFormatado = formatarJson(response);
            System.out.println(jsonFormatado);
        } else {
            String errorResponse = lerErro(connection);
            System.out.println("Erro ao listar: " + errorResponse);
        }

        connection.disconnect();
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

    // Formataçãodo JSON pra ficar mais legível
    private static String formatarJson(String json) {
        if (json.equals("[]")) {
            return "Nenhuma tarefa encontrada.";
        }

        // Substitui algumas vírgulas por quebras de linha pra ficar mais legível
        return json.replace("},{", "},\n{")
                .replace("[{", "[\n  {")
                .replace("}]", "}\n]")
                .replace(",\"", ",\n    \"");
    }
}