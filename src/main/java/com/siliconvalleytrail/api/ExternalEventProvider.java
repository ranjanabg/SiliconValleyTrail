package com.siliconvalleytrail.api;

import com.siliconvalleytrail.api.news.NewsApiClient;
import com.siliconvalleytrail.api.weather.WeatherApiClient;

public class ExternalEventProvider {

    private static final int NEWS_COOLDOWN_DAYS = 2;

    private final WeatherApiClient weatherClient;
    private final NewsApiClient newsClient;

    public ExternalEventProvider() {
        this(new WeatherApiClient(), new NewsApiClient());
    }

    ExternalEventProvider(final WeatherApiClient weatherClient, final NewsApiClient newsClient) {
        this.weatherClient = weatherClient;
        this.newsClient = newsClient;
    }

    private ExternalEvent cachedWeatherEvent = null;
    private int cachedWeatherZone = -1;

    private ExternalEvent cachedNewsEvent = null;
    private int cachedNewsDay = -1;
    private int lastNewsTriggerDay = -10;

    public ExternalEvent getWeather(int progress) {
        int currentZone = zoneForProgress(progress);
        if (cachedWeatherEvent == null || currentZone != cachedWeatherZone) {
            cachedWeatherEvent = weatherClient.fetch(progress);
            cachedWeatherZone = currentZone;
        }
        return cachedWeatherEvent;
    }

    public ExternalEvent getNews(int day) {
        if (day - lastNewsTriggerDay <= NEWS_COOLDOWN_DAYS) return null;
        if (cachedNewsEvent == null || day != cachedNewsDay) {
            cachedNewsEvent = newsClient.fetch();
            cachedNewsDay = day;
            if (cachedNewsEvent != null) lastNewsTriggerDay = day;
        }
        return cachedNewsEvent;
    }

    private int zoneForProgress(int progress) {
        if (progress < 18)  return 0; // San Jose
        if (progress < 45)  return 1; // Mountain View
        if (progress < 72)  return 2; // Palo Alto
        return 3;                      // San Francisco
    }

    // Called on game restart to refresh API data for the new session
    public void reset() {
        cachedWeatherEvent = null;
        cachedWeatherZone = -1;
        cachedNewsEvent = null;
        cachedNewsDay = -1;
        lastNewsTriggerDay = -10;
    }
}
