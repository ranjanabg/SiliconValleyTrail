package com.siliconvalleytrail.api.news;

import com.siliconvalleytrail.api.ExternalEvent;
import com.siliconvalleytrail.game.GameState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NewsEventHandlerTest {

    private NewsEventHandler handler;
    private GameState state;

    @BeforeEach
    void setUp() {
        handler = new NewsEventHandler();
        state = new GameState();
    }

    @Test
    void allZeroDeltas_stateUnchanged() {
        handler.execute(new ExternalEvent("😶", "No impact", 0, 0, 0, 0, 0, 0), state);
        assertEquals(100, state.getMorale());
        assertEquals(100, state.getEnergy());
        assertEquals(50000, state.getFund());
        assertEquals(10, state.getHype());
        assertEquals(10, state.getConnections());
    }

    @Test
    void zeroDeltas_onlyMoraleNonZero_stillApplied() {
        // hasNoImpact is false when any delta is non-zero
        handler.execute(new ExternalEvent("😮‍💨", "Burnout", -5, 0, 0, 0, 0, 0), state);
        assertEquals(95, state.getMorale());
    }

    @Test
    void negativeLayoffEvent_appliesDeltas() {
        handler.execute(new ExternalEvent("😨", "Layoffs", -10, 0, 0, 0, -5, 0), state);
        assertEquals(90, state.getMorale());
        assertEquals(5, state.getHype()); // 10 - 5
    }

    @Test
    void positiveAiEvent_appliesDeltas() {
        handler.execute(new ExternalEvent("🤖", "AI buzz", 5, 10, 3, 0, 10, 5), state);
        assertEquals(100, state.getMorale()); // 100 + 5 clamped
        assertEquals(100, state.getEnergy()); // 100 + 10 clamped
        assertEquals(3, state.getProgress());
        assertEquals(20, state.getHype());    // 10 + 10
        assertEquals(15, state.getConnections()); // 10 + 5
    }

    @Test
    void fundDelta_applied() {
        handler.execute(new ExternalEvent("🤑", "Funding", 8, 5, 0, 5000, 10, 5), state);
        assertEquals(55000, state.getFund());
    }

    @Test
    void negativeFundDelta_applied() {
        handler.execute(new ExternalEvent("📉", "Recession", -8, -5, 0, -500, -5, 0), state);
        assertEquals(49500, state.getFund());
    }

    @Test
    void deltasClamped_moraleDoesNotGoBelowZero() {
        handler.execute(new ExternalEvent("😨", "Extreme", -200, 0, 0, 0, 0, 0), state);
        assertEquals(0, state.getMorale());
    }

    @Test
    void allZeroDeltas_doesNotChangeHype() {
        handler.execute(new ExternalEvent("📰", "Noise", 0, 0, 0, 0, 0, 0), state);
        assertEquals(10, state.getHype());
        assertEquals(10, state.getConnections());
    }
}
