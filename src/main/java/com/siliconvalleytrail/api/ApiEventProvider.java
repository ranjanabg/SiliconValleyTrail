package com.siliconvalleytrail.api;

import com.siliconvalleytrail.api.news.NewsApiClient;
import com.siliconvalleytrail.api.weather.WeatherApiClient;

public class ApiEventProvider {

    private static final int NEWS_COOLDOWN_DAYS = 2;

    private final WeatherApiClient weatherClient;
    private final NewsApiClient newsClient;

    public ApiEventProvider() {
        this(new WeatherApiClient(), new NewsApiClient());
    }

    ApiEventProvider(WeatherApiClient weatherClient, NewsApiClient newsClient) {
        this.weatherClient = weatherClient;
        this.newsClient = newsClient;
    }

    private GameImpact cachedWeatherEffect = null;
    private int cachedWeatherZone = -1;

    private GameImpact cachedNewsEffect = null;
    private int cachedNewsDay = -1;
    private int lastNewsTriggerDay = -10;

    public GameImpact getWeather(int progress) {
        int currentZone = zoneForProgress(progress);
        if (cachedWeatherEffect == null || currentZone != cachedWeatherZone) {
            cachedWeatherEffect = weatherClient.fetch(progress);
            cachedWeatherZone = currentZone;
        }
        return cachedWeatherEffect;
    }

    public GameImpact getNews(int day) {
        if (day - lastNewsTriggerDay <= NEWS_COOLDOWN_DAYS) return null;
        if (cachedNewsEffect == null || day != cachedNewsDay) {
            cachedNewsEffect = newsClient.fetch();
            cachedNewsDay = day;
            if (cachedNewsEffect != null) lastNewsTriggerDay = day;
        }
        return cachedNewsEffect;
    }

    private int zoneForProgress(int progress) {
        if (progress < 18)  return 0; // San Jose
        if (progress < 45)  return 1; // Mountain View
        if (progress < 72)  return 2; // Palo Alto
        return 3;                      // San Francisco
    }

    // Called on game restart to refresh API data for the new session
    public void reset() {
        cachedWeatherEffect = null;
        cachedWeatherZone = -1;
        cachedNewsEffect = null;
        cachedNewsDay = -1;
        lastNewsTriggerDay = -10;
    }
}
