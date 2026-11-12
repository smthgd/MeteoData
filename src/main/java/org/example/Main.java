package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import org.example.service.CalculationService;
import org.example.service.YandexWeatherService;

public class Main {
    public static void main(String[] args) {
        String key = GetKey();

        if (key.isBlank()) {
            return;
        }

        CalculationService calculationService = new CalculationService();

        try (YandexWeatherService service = new YandexWeatherService(key)) {
            String forecast = service.getForecast(52.37125, 4.89388);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String prettyJson = gson.toJson(JsonParser.parseString(forecast));
            System.out.println(prettyJson);

            var averageTemperature = calculationService.CalculateAverageTemperature();
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

        return key;
    }
}
