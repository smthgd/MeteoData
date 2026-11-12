package org.example;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {
        String key = GetKey();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.weather.yandex.ru/v2/forecast?lat=52.37125&lon=4.89388"))
                .header("X-Yandex-Weather-Key", key)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        try (HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build()) {
            HttpResponse<String> response = client.send(
                    request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                System.out.println(response.body());
            } else {
                System.err.println("Ошибка API: HTTP " + response.statusCode());
                System.err.println(response.body());
            }
        } catch (IOException e) {
            System.err.println("Не удалось выполнить запрос: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Запрос прерван.");
        }
    }

    public static String GetKey() {
        String key = "";
        Properties config = new Properties();

        try (var input = Files.newInputStream(Path.of("config.properties"))) {
            config.load(input);
            key = config.getProperty("yandex.weather.key", "").trim();
        } catch (IOException e) {
            System.err.println("Не удалось прочитать config.properties. Создайте его по образцу config.properties.example.");
        }
        if (key.isBlank()) {
            System.err.println("Укажите yandex.weather.key в config.properties.");
        }

        return key;
    }
}
