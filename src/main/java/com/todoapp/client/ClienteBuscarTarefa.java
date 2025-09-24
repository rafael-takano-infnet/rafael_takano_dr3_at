package com.todoapp.client;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ClienteBuscarTarefa {

    public static void main(String[] args) {
        try {
            buscarTarefa();
        } catch (Exception e) {
            System.err.println("Erro ao buscar tarefa: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void buscarTarefa() throws IOException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=> BUSCAR TAREFA <=");
        System.out.print("Digite o ID da tarefa que deseja buscar: ");
        String tarefaId = scanner.nextLine().trim();

        if (tarefaId.isEmpty()) {
            System.out.println("ID não pode estar vazio!");
            return;
        }

        String urlString = "http://localhost:7000/tarefas/" + tarefaId;

        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        //Configura a requisição GET

        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");

        //Lê a resposta

        int responseCode = connection.getResponseCode();
        System.out.println("Status Code: " + responseCode);
        System.out.println("Mensagem: " + connection.getResponseMessage());

        if (responseCode == 200) {
            String response = lerResposta(connection);
            System.out.println("Tarefa encontrada:");
            System.out.println(formatarJson(response));
        } else if (responseCode == 404) {
            System.out.println("Tarefa não encontrada com o ID: " + tarefaId);
        } else {
            String errorResponse = lerErro(connection);
            System.out.println("Erro na busca: " + errorResponse);
        }

        connection.disconnect();
        scanner.close();
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

    // Formatação do JSON pra ficar mais legível

    private static String formatarJson(String json) {
        return json.replace(",\"", ",\n  \"")
                .replace("{\"", "{\n  \"")
                .replace("\"}", "\"\n}");
    }
}