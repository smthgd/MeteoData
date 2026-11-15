package org.example.service;

import com.google.gson.JsonParser;

public class CalculationService {
    public CalculationService(){
    }

    public double CalculateAverageTemperature(String forecast){
        var forecasts = JsonParser.parseString(forecast).getAsJsonObject().getAsJsonArray("forecasts");
        if (forecasts == null || forecasts.isEmpty()) {
            throw new IllegalArgumentException("В ответе отсутствуют сутки прогноза forecasts.");
        }

        double sum = 0;
        for (var dailyForecast : forecasts) {
            var parts = dailyForecast.getAsJsonObject().getAsJsonObject("parts");
            var day = parts == null ? null : parts.getAsJsonObject("day");

            if (day == null || !day.has("temp_avg") || day.get("temp_avg").isJsonNull()) {
                throw new IllegalArgumentException("В прогнозе отсутствует forecasts[*].parts.day.temp_avg.");
            }
            sum += day.get("temp_avg").getAsDouble();
        }

        return sum / forecasts.size();
    }
}
