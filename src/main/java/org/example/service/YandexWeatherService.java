package org.example.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class YandexWeatherService implements AutoCloseable {
    private final String accessKey;
    private final HttpClient client;

    public YandexWeatherService(String accessKey) {
        if (accessKey == null || accessKey.isBlank()) {
            throw new IllegalArgumentException("Ключ API не должен быть пустым.");
        }
        this.accessKey = accessKey;
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public String getForecast(double latitude, double longitude) throws IOException, InterruptedException {
        if (!Double.isFinite(latitude) || latitude < -90 || latitude > 90
                || !Double.isFinite(longitude) || longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Некорректные координаты.");
        }
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.weather.yandex.ru/v2/forecast?lat="
                        + latitude + "&lon=" + longitude))
                .header("X-Yandex-Weather-Key", accessKey)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        HttpResponse<String> response = client.send(
                request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException("Ошибка API: HTTP " + response.statusCode() + "\n" + response.body());
        }
        return response.body();
    }

    @Override
    public void close() {
        client.close();
    }
}
