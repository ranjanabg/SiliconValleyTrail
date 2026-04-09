package com.siliconvalleytrail.api.weather;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.siliconvalleytrail.api.ApiEffect;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class WeatherApiClient {

    private static final String API_BASE =
        "https://api.openweathermap.org/data/2.5/weather?units=metric&appid=";
    private static final double HEAT_WAVE_THRESHOLD = 35.0;

    private final Gson gson = new Gson();
    private final Random random = new Random();
    private final HttpClient httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(5))
        .build();

    private List<ApiEffect> mockConditions(String city) {
        return Arrays.asList(
            new ApiEffect("☀️",  "Beautiful clear day in " + city + ". The team feels alive.",        +3,  +5,  0, 0, 0, 0),
            new ApiEffect("⛅",  "Overcast skies over " + city + ". The team stays focused.",          0,   -2,  0, 0, 0, 0),
            new ApiEffect("🌧️", "Rain in " + city + " slows the commute and dampens spirits.",       -5,   -8, -2, 0, 0, 0),
            new ApiEffect("🌫️", "Morning fog settles over " + city + ". Visibility is poor, pace slows.", 0, -3, -1, 0, 0, 0)
        );
    }

    private static String cityForProgress(int progress) {
        if (progress < 18)  return "San+Jose,CA,US";
        if (progress < 45)  return "Mountain+View,CA,US";
        if (progress < 72)  return "Palo+Alto,CA,US";
        return "San+Francisco,CA,US";
    }

    private static String displayCityForProgress(int progress) {
        if (progress < 18)  return "San Jose";
        if (progress < 45)  return "Mountain View";
        if (progress < 72)  return "Palo Alto";
        return "San Francisco";
    }

    public ApiEffect fetchEffect(int progress) {
        final String apiKey = System.getenv("OPENWEATHER_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            return randomMock(progress);
        }
        try {
            final String url = API_BASE + apiKey + "&q=" + cityForProgress(progress);
            final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build();

            final HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) return randomMock(progress);

            final WeatherResponse data = gson.fromJson(response.body(), WeatherResponse.class);
            return mapToEffect(data, displayCityForProgress(progress));

        } catch (Exception e) {
            return randomMock(progress);
        }
    }

    private ApiEffect randomMock(int progress) {
        final List<ApiEffect> mocks = mockConditions(displayCityForProgress(progress));
        return mocks.get(random.nextInt(mocks.size()));
    }

    private ApiEffect mapToEffect(WeatherResponse data, String city) {
        final double temp = data.main != null ? data.main.temp : 20.0;
        final int conditionCode = (data.weather != null && !data.weather.isEmpty())
            ? data.weather.get(0).id : 800;

        if (temp > HEAT_WAVE_THRESHOLD) {
            return new ApiEffect(
                "🥵", "Brutal heat in " + city + ". Someone runs out for cold drinks.",
                0, -15, -3, -200, 0, 0
            );
        }
        if (conditionCode >= 200 && conditionCode < 300) {
            return new ApiEffect(
                "⛈️", "Thunderstorm in " + city + " forces the team indoors. Progress stalls.",
                -10, -15, -5, 0, 0, 0
            );
        }
        if ((conditionCode >= 300 && conditionCode < 400) || (conditionCode >= 500 && conditionCode < 600)) {
            return new ApiEffect(
                "🌧️", "Rain in " + city + " slows the commute and dampens spirits.",
                -5, -8, -2, 0, 0, 0
            );
        }
        if (conditionCode >= 700 && conditionCode < 800) {
            return new ApiEffect(
                "🌫️", "Morning fog settles over " + city + ". Visibility is poor, pace slows.",
                0, -3, -1, 0, 0, 0
            );
        }
        if (conditionCode == 800) {
            return new ApiEffect(
                "☀️", "Beautiful clear day in " + city + ". The team feels alive.",
                +5, +5, +1, 0, 0, 0
            );
        }
        // 801-804: Cloudy — no effect
        return new ApiEffect("⛅", "Overcast skies over " + city + ". The team stays focused.", 0, 0, 0, 0, 0, 0);
    }

    // Internal Gson mapping classes for OpenWeatherMap response
    private static class WeatherResponse {
        List<WeatherCondition> weather;
        Main main;
    }

    private static class WeatherCondition {
        int id;
    }

    private static class Main {
        double temp;
        @SerializedName("feels_like") double feelsLike;
    }
}
