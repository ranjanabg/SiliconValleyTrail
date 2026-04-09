package com.siliconvalleytrail.api.weather;

import com.siliconvalleytrail.api.ExternalEvent;
import com.siliconvalleytrail.game.GameState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WeatherEventHandlerTest {

    private WeatherEventHandler handler;
    private GameState state;

    @BeforeEach
    void setUp() {
        handler = new WeatherEventHandler();
        state = new GameState();
    }

    @Test
    void appliesPositiveMoraleDelta() {
        handler.execute(new ExternalEvent("☀️", "Sunny", 5, 0, 0, 0, 0, 0), state);
        assertEquals(100, state.getMorale()); // 100 + 5 clamped to 100
    }

    @Test
    void appliesNegativeMoraleDelta() {
        handler.execute(new ExternalEvent("🌧️", "Rain", -5, 0, 0, 0, 0, 0), state);
        assertEquals(95, state.getMorale());
    }

    @Test
    void appliesNegativeEnergyDelta() {
        handler.execute(new ExternalEvent("🌧️", "Rain", 0, -8, 0, 0, 0, 0), state);
        assertEquals(92, state.getEnergy());
    }

    @Test
    void appliesNegativeProgressDelta() {
        state.applyProgressDelta(10);
        handler.execute(new ExternalEvent("⛈️", "Storm", 0, 0, -5, 0, 0, 0), state);
        assertEquals(5, state.getProgress());
    }

    @Test
    void appliesFundDelta() {
        handler.execute(new ExternalEvent("🥵", "Heat wave", 0, 0, 0, -200, 0, 0), state);
        assertEquals(49800, state.getFund());
    }

    @Test
    void appliesHypeDelta() {
        handler.execute(new ExternalEvent("☀️", "Sunny", 0, 0, 0, 0, 5, 0), state);
        assertEquals(15, state.getHype()); // starts at 10
    }

    @Test
    void appliesConnectionsDelta() {
        handler.execute(new ExternalEvent("☀️", "Sunny", 0, 0, 0, 0, 0, 5), state);
        assertEquals(15, state.getConnections()); // starts at 10
    }

    @Test
    void allZeroDeltas_stateUnchanged() {
        handler.execute(new ExternalEvent("⛅", "Cloudy", 0, 0, 0, 0, 0, 0), state);
        assertEquals(100, state.getMorale());
        assertEquals(100, state.getEnergy());
        assertEquals(50000, state.getFund());
        assertEquals(0, state.getProgress());
    }

    @Test
    void appliesAllDeltas_thunderstorm() {
        handler.execute(new ExternalEvent("⛈️", "Thunderstorm", -10, -15, -5, 0, 0, 0), state);
        assertEquals(90, state.getMorale());
        assertEquals(85, state.getEnergy());
        assertEquals(0, state.getProgress()); // 0 - 5 clamped to 0
    }

    @Test
    void deltasClamped_moraleDoesNotGoBelowZero() {
        handler.execute(new ExternalEvent("⛈️", "Extreme", -200, 0, 0, 0, 0, 0), state);
        assertEquals(0, state.getMorale());
    }

    @Test
    void deltasClamped_energyDoesNotGoBelowZero() {
        handler.execute(new ExternalEvent("⛈️", "Extreme", 0, -200, 0, 0, 0, 0), state);
        assertEquals(0, state.getEnergy());
    }
}
