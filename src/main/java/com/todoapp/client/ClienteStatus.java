package com.todoapp.client;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ClienteStatus {

    public static void main(String[] args) {
        try {
            verificarStatus();
        } catch (Exception e) {
            System.err.println("Erro ao verificar status: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void verificarStatus() throws IOException {
        String urlString = "http://localhost:7000/status";

        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Configura a requisição GET
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");

        //Lê resposta
        int responseCode = connection.getResponseCode();
        System.out.println("=> STATUS DO SISTEMA <=");
        System.out.println("Status Code: " + responseCode);
        System.out.println("Mensagem: " + connection.getResponseMessage());

        if (responseCode == 200) {
            String response = lerResposta(connection);
            System.out.println("Status do servidor:");
            System.out.println(formatarJson(response));
        } else {
            String errorResponse = lerErro(connection);
            System.out.println("Erro ao verificar status: " + errorResponse);
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

    // Formatação simples do JSON pra ficar mais legível
    private static String formatarJson(String json) {
        return json.replace(",\"", ",\n  \"")
                .replace("{\"", "{\n  \"")
                .replace("\"}", "\"\n}");
    }
}