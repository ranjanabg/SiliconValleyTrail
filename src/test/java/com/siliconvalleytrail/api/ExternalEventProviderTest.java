package com.siliconvalleytrail.api;

import com.siliconvalleytrail.api.news.NewsApiClient;
import com.siliconvalleytrail.api.weather.WeatherApiClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExternalEventProviderTest {

    private static final ExternalEvent WEATHER_EVENT =
        new ExternalEvent("☀️", "Test weather", 5, 5, 0, 0, 0, 0);
    private static final ExternalEvent NEWS_EVENT =
        new ExternalEvent("📰", "Test news", -5, 0, 0, 0, 0, 0);

    private int weatherFetchCount;
    private int newsFetchCount;
    private ExternalEventProvider provider;

    @BeforeEach
    void setUp() {
        weatherFetchCount = 0;
        newsFetchCount = 0;

        final WeatherApiClient weatherClient = new WeatherApiClient() {
            @Override public ExternalEvent fetch(int progress) {
                weatherFetchCount++;
                return WEATHER_EVENT;
            }
        };

        final NewsApiClient newsClient = new NewsApiClient() {
            @Override public ExternalEvent fetch() {
                newsFetchCount++;
                return NEWS_EVENT;
            }
        };

        provider = new ExternalEventProvider(weatherClient, newsClient);
    }

    // --- Weather caching ---

    @Test
    void weather_cachedForSameZone() {
        provider.getWeather(5);
        provider.getWeather(10); // still zone 0 (San Jose, progress < 18)
        assertEquals(1, weatherFetchCount, "Weather client should be called once for the same zone");
    }

    @Test
    void weather_fetchesAgainOnZoneChange() {
        provider.getWeather(5);  // zone 0
        provider.getWeather(20); // zone 1 (Mountain View)
        assertEquals(2, weatherFetchCount, "Weather client should be called again when zone changes");
    }

    @Test
    void weather_cachedAfterZoneChange() {
        provider.getWeather(5);  // zone 0
        provider.getWeather(20); // zone 1
        provider.getWeather(25); // still zone 1 — cached
        assertEquals(2, weatherFetchCount);
    }

    @Test
    void weather_returnsExpectedEvent() {
        ExternalEvent event = provider.getWeather(5);
        assertEquals(WEATHER_EVENT, event);
    }

    @Test
    void weather_allFourZonesFetch() {
        provider.getWeather(5);  // zone 0
        provider.getWeather(20); // zone 1
        provider.getWeather(50); // zone 2 (Palo Alto)
        provider.getWeather(80); // zone 3 (San Francisco)
        assertEquals(4, weatherFetchCount);
    }

    // --- News cooldown ---

    @Test
    void news_returnedWhenCooldownNotActive() {
        // lastNewsTriggerDay = -10, day = 1, diff = 11 > 2 (NEWS_COOLDOWN_DAYS)
        ExternalEvent event = provider.getNews(1);
        assertEquals(NEWS_EVENT, event);
        assertEquals(1, newsFetchCount);
    }

    @Test
    void news_returnsNullWithinCooldown() {
        provider.getNews(1); // triggers, lastNewsTriggerDay = 1
        ExternalEvent event = provider.getNews(2); // 2 - 1 = 1 <= 2 — within cooldown
        assertNull(event);
    }

    @Test
    void news_returnsNullOnCooldownBoundary() {
        provider.getNews(1); // triggers, lastNewsTriggerDay = 1
        ExternalEvent event = provider.getNews(3); // 3 - 1 = 2 <= 2 — still within cooldown
        assertNull(event);
    }

    @Test
    void news_availableAfterCooldownExpires() {
        provider.getNews(1); // triggers, lastNewsTriggerDay = 1
        provider.getNews(3); // within cooldown
        ExternalEvent event = provider.getNews(4); // 4 - 1 = 3 > 2 — cooldown expired
        assertEquals(NEWS_EVENT, event);
    }

    @Test
    void news_cachedForSameDay() {
        provider.getNews(1);
        provider.getNews(1); // same day — cached
        assertEquals(1, newsFetchCount);
    }

    // --- Reset ---

    @Test
    void reset_clearsWeatherCache() {
        provider.getWeather(5);
        provider.reset();
        provider.getWeather(5); // should fetch again
        assertEquals(2, weatherFetchCount);
    }

    @Test
    void reset_clearsNewsCooldown() {
        provider.getNews(1); // triggers, lastNewsTriggerDay = 1
        provider.reset();
        provider.getNews(2); // after reset, cooldown cleared — should fetch
        assertEquals(2, newsFetchCount);
    }
}
