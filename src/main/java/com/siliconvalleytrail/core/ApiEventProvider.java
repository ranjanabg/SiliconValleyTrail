package com.siliconvalleytrail.core;

import com.siliconvalleytrail.model.ApiEffect;

public class ApiEventProvider {

    private final WeatherApiClient weatherClient = new WeatherApiClient();

    private ApiEffect cachedWeatherEffect = null;
    private int cachedWeatherZone = -1;

    public ApiEffect getWeatherEffect(int progress) {
        int currentZone = zoneForProgress(progress);
        if (cachedWeatherEffect == null || currentZone != cachedWeatherZone) {
            cachedWeatherEffect = weatherClient.fetchEffect(progress);
            cachedWeatherZone = currentZone;
        }
        return cachedWeatherEffect;
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
    }
}
