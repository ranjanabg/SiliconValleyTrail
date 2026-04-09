package com.siliconvalleytrail.api.weather;

import com.siliconvalleytrail.api.ExternalEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the mock path (no OPENWEATHER_API_KEY set) — verifies city routing
 * and that returned events are non-null with valid emoji and narrative.
 */
class WeatherApiClientTest {

    private WeatherApiClient client;

    @BeforeEach
    void setUp() {
        client = new WeatherApiClient();
    }

    @Test
    void fetch_sanJoseZone_narrativeContainsSanJose() {
        ExternalEvent event = client.fetch(0);
        assertNotNull(event);
        assertTrue(event.getNarrative().contains("San Jose"));
    }

    @Test
    void fetch_sanJoseZoneBoundary_narrativeContainsSanJose() {
        ExternalEvent event = client.fetch(17); // progress 17 < 18
        assertTrue(event.getNarrative().contains("San Jose"));
    }

    @Test
    void fetch_mountainViewZone_narrativeContainsMountainView() {
        ExternalEvent event = client.fetch(18);
        assertNotNull(event);
        assertTrue(event.getNarrative().contains("Mountain View"));
    }

    @Test
    void fetch_mountainViewZoneBoundary_narrativeContainsMountainView() {
        ExternalEvent event = client.fetch(44); // progress 44 < 45
        assertTrue(event.getNarrative().contains("Mountain View"));
    }

    @Test
    void fetch_paloAltoZone_narrativeContainsPaloAlto() {
        ExternalEvent event = client.fetch(45);
        assertNotNull(event);
        assertTrue(event.getNarrative().contains("Palo Alto"));
    }

    @Test
    void fetch_paloAltoZoneBoundary_narrativeContainsPaloAlto() {
        ExternalEvent event = client.fetch(71); // progress 71 < 72
        assertTrue(event.getNarrative().contains("Palo Alto"));
    }

    @Test
    void fetch_sanFranciscoZone_narrativeContainsSanFrancisco() {
        ExternalEvent event = client.fetch(72);
        assertNotNull(event);
        assertTrue(event.getNarrative().contains("San Francisco"));
    }

    @Test
    void fetch_maxProgress_narrativeContainsSanFrancisco() {
        ExternalEvent event = client.fetch(100);
        assertTrue(event.getNarrative().contains("San Francisco"));
    }

    @Test
    void fetch_returnsNonEmptyEmoji() {
        ExternalEvent event = client.fetch(0);
        assertNotNull(event);
        assertFalse(event.getEmoji().isBlank());
    }

    @Test
    void fetch_returnsNonEmptyNarrative() {
        ExternalEvent event = client.fetch(0);
        assertNotNull(event);
        assertFalse(event.getNarrative().isBlank());
    }
}
