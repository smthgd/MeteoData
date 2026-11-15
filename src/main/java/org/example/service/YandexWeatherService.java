package org.example.service;

import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class YandexWeatherService implements AutoCloseable {
    private final String headerName = "X-Yandex-Weather-Key";
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

    public String getForecast(double latitude, double longitude)
            throws IOException, InterruptedException {
        return getForecast(latitude, longitude, 0);
    }

    public String getForecast(double latitude, double longitude, int limit) throws IOException, InterruptedException {
        if (!Double.isFinite(latitude) || latitude < -90 || latitude > 90
                || !Double.isFinite(longitude) || longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Некорректные координаты.");
        }

        URI uri;

        if (limit == 0) {
            uri = URI.create("https://api.weather.yandex.ru/v2/forecast?lat="
                    + latitude + "&lon=" + longitude);
        }
        else{
            uri = URI.create("https://api.weather.yandex.ru/v2/forecast?lat="
                    + latitude + "&lon=" + longitude + "&limit="+ limit);
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header(headerName, accessKey)
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

    public String getTemperature(String forecast) {
        var fact = JsonParser.parseString(forecast).getAsJsonObject().getAsJsonObject("fact");
        if (fact == null || !fact.has("temp") || fact.get("temp").isJsonNull()) {
            throw new IllegalArgumentException("В прогнозе отсутствует температура fact.temp.");
        }
        return fact.get("temp").getAsString();
    }

    @Override
    public void close() {
        client.close();
    }
}
