package com.siliconvalleytrail.api.news;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.siliconvalleytrail.api.ExternalEvent;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class NewsApiClient {

    private static final String API_URL =
        "https://newsapi.org/v2/top-headlines?category=technology&pageSize=10&apiKey=";
    private static final double MOCK_TRIGGER_CHANCE = 0.25;

    private static final List<ExternalEvent> MOCK_NEWS = Arrays.asList(
        new ExternalEvent("😨", "Tech layoff headlines shake confidence across the valley.",           -10,   0, 0, 0,  -5,  0),
        new ExternalEvent("😮‍💨", "Burnout stories in the news hit close to home.",                   -5, -10, 0, 0,   0,  0),
        new ExternalEvent("📉", "Economic uncertainty spreads across the valley.",                     -8,  -5, 0, 0,  -5,  0),
        new ExternalEvent("🚀", "An IPO story fires up the team's ambition.",                          +5,  +5, +2, 0, +8,  0),
        new ExternalEvent("🤑", "A big funding round inspires the team to keep pushing.",              +8,  +5, 0, 0, +10, +5),
        new ExternalEvent("🤖", "An AI breakthrough has everyone buzzing. The team stays late to experiment.", +5, +10, +3, 0, +10, +5)
    );

    private final Gson gson = new Gson();
    private final Random random = new Random();
    private final HttpClient httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(5))
        .build();
    private int lastMockIndex = -1;

    public ExternalEvent fetch() {
        final String apiKey = System.getenv("NEWS_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            return randomMock();
        }
        try {
            final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + apiKey))
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build();

            final HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) return randomMock();

            final NewsResponse data = gson.fromJson(response.body(), NewsResponse.class);
            if (data.articles == null || data.articles.isEmpty()) return randomMock();

            return mapToEvent(data.articles);

        } catch (Exception e) {
            return randomMock();
        }
    }

    private ExternalEvent randomMock() {
        if (random.nextDouble() >= MOCK_TRIGGER_CHANCE) return null;
        int index = random.nextInt(MOCK_NEWS.size());
        if (index == lastMockIndex && MOCK_NEWS.size() > 1) {
            index = (index + 1) % MOCK_NEWS.size();
        }
        lastMockIndex = index;
        return MOCK_NEWS.get(index);
    }

    private ExternalEvent mapToEvent(List<Article> articles) {
        final String combinedHeadlines = articles.stream()
            .map(a -> a.title != null ? a.title.toLowerCase() : "")
            .reduce("", (a, b) -> a + " " + b);

        if (containsAny(combinedHeadlines, "layoff", "layoffs", "fired", "cut jobs")) {
            return new ExternalEvent("😨",
                "Tech layoff headlines shake confidence across the valley.",
                -10, 0, 0, 0, -5, 0);
        }
        if (containsAny(combinedHeadlines, "burnout", "mental health", "overwork")) {
            return new ExternalEvent("😮‍💨",
                "Burnout stories in the news hit close to home.",
                -5, -10, 0, 0, 0, 0);
        }
        if (containsAny(combinedHeadlines, "recession", "crash", "downturn", "market down")) {
            return new ExternalEvent("📉",
                "Economic uncertainty spreads across the valley.",
                -8, -5, 0, 0, -5, 0);
        }
        if (containsAny(combinedHeadlines, "ipo", "acquisition", "acquired")) {
            return new ExternalEvent("🚀",
                "An IPO story fires up the team's ambition.",
                +5, +5, +2, 0, +8, 0);
        }
        if (containsAny(combinedHeadlines, "funding", "raised", "series", "venture")) {
            return new ExternalEvent("🤑",
                "A big funding round inspires the team to keep pushing.",
                +8, +5, 0, 0, +10, +5);
        }
        if (containsAny(combinedHeadlines, "ai", "artificial intelligence", "breakthrough")) {
            return new ExternalEvent("🤖",
                "An AI breakthrough has everyone buzzing. The team stays late to experiment.",
                +5, +10, +3, 0, +10, +5);
        }

        return null;
    }

    private boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) return true;
        }
        return false;
    }

    // Internal Gson mapping classes for NewsAPI response
    private static class NewsResponse {
        List<Article> articles;
    }

    private static class Article {
        String title;
        @SerializedName("description") String description;
    }
}
