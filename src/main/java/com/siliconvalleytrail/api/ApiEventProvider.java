package com.siliconvalleytrail.api;

import com.siliconvalleytrail.api.news.NewsApiClient;
import com.siliconvalleytrail.api.weather.WeatherApiClient;

public class ApiEventProvider {

    private static final int NEWS_COOLDOWN_DAYS = 2;

    private final WeatherApiClient weatherClient = new WeatherApiClient();
    private final NewsApiClient newsClient = new NewsApiClient();

    private ApiEffect cachedWeatherEffect = null;
    private int cachedWeatherZone = -1;

    private ApiEffect cachedNewsEffect = null;
    private int cachedNewsDay = -1;
    private int lastNewsTriggerDay = -10;

    public ApiEffect getWeatherEffect(int progress) {
        int currentZone = zoneForProgress(progress);
        if (cachedWeatherEffect == null || currentZone != cachedWeatherZone) {
            cachedWeatherEffect = weatherClient.fetchEffect(progress);
            cachedWeatherZone = currentZone;
        }
        return cachedWeatherEffect;
    }

    public ApiEffect getNewsEffect(int day) {
        if (day - lastNewsTriggerDay <= NEWS_COOLDOWN_DAYS) return null;
        if (cachedNewsEffect == null || day != cachedNewsDay) {
            cachedNewsEffect = newsClient.fetchEffect();
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
