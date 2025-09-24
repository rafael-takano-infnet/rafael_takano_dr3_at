package com.todoapp.client;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ClienteEcho {

    public static void main(String[] args) {
        try {
            testarEcho();
        } catch (Exception e) {
            System.err.println("Erro ao testar echo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void testarEcho() throws IOException {
        String urlString = "http://localhost:7000/echo";

        // Timestamp
        String timestampAtual = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        String jsonBody = "{\n" +
                "  \"mensagem\": \"Testando o endpoint echo!\",\n" +
                "  \"autor\": \"Sistema\",\n" +
                "  \"timestamp\": \"" + timestampAtual + "\"\n" +
                "}";

        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        //Configura a requisição POST

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        // Envia o JSON no body
        try (var outputStream = connection.getOutputStream()) {
            byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
            outputStream.write(input, 0, input.length);
        }

        //Lê a resposta
        int responseCode = connection.getResponseCode();
        System.out.println("=> TESTE ENDPOINT ECHO <=");
        System.out.println("Status Code: " + responseCode);
        System.out.println("Mensagem: " + connection.getResponseMessage());

        if (responseCode == 200) {
            String response = lerResposta(connection);
            System.out.println("Echo funcionou!");
            System.out.println("JSON enviado:");
            System.out.println(jsonBody);
            System.out.println("\nJSON recebido:");
            System.out.println(response);
        } else {
            String errorResponse = lerErro(connection);
            System.out.println("Erro no echo: " + errorResponse);
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
}