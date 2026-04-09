package com.siliconvalleytrail.events;

import com.siliconvalleytrail.game.GameState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class EventEngineTest {

    private EventEngine engine;

    @BeforeEach
    void setUp() {
        engine = new EventEngine(new Scanner(new ByteArrayInputStream(new byte[0])));
    }

    // --- selectEvent ---

    @Test
    void selectEvent_lowTechDebt_alwaysReturnsNormalEvent() {
        // techDebt = 0, crisis branch condition (0 > 60) is never true
        GameState state = new GameState();
        for (int i = 0; i < 50; i++) {
            RandomEvent event = engine.selectEvent(state);
            assertTrue(EventEngine.EVENT_POOL.contains(event),
                "Expected normal event when tech debt is low");
        }
    }

    @Test
    void selectEvent_highTechDebt_canReturnCrisisEvent() {
        // techDebt = 70 > 60, crisis fires with 40% chance per call
        // P(no crisis in 100 calls) = 0.6^100 ≈ 6e-23 — effectively impossible
        GameState state = new GameState();
        state.applyTechDebtDelta(70);
        boolean gotCrisis = false;
        for (int i = 0; i < 100; i++) {
            if (EventEngine.CRISIS_POOL.contains(engine.selectEvent(state))) {
                gotCrisis = true;
                break;
            }
        }
        assertTrue(gotCrisis, "Expected at least one crisis event when tech debt > 60");
    }

    @Test
    void selectEvent_highTechDebt_canAlsoReturnNormalEvent() {
        // Normal events still possible (60% chance) when tech debt is high
        GameState state = new GameState();
        state.applyTechDebtDelta(70);
        boolean gotNormal = false;
        for (int i = 0; i < 100; i++) {
            if (EventEngine.EVENT_POOL.contains(engine.selectEvent(state))) {
                gotNormal = true;
                break;
            }
        }
        assertTrue(gotNormal, "Expected at least one normal event even when tech debt > 60");
    }

    @Test
    void selectEvent_returnsEventFromKnownPool() {
        GameState state = new GameState();
        for (int i = 0; i < 30; i++) {
            RandomEvent event = engine.selectEvent(state);
            assertTrue(
                EventEngine.EVENT_POOL.contains(event) || EventEngine.CRISIS_POOL.contains(event),
                "Event must come from EVENT_POOL or CRISIS_POOL"
            );
        }
    }

    // --- pickDifferentIndex ---

    @Test
    void pickDifferentIndex_neverReturnsLastIndex() {
        // The result is always != lastIndex when poolSize > 1 — deterministic guarantee
        for (int lastIndex = 0; lastIndex < 5; lastIndex++) {
            for (int i = 0; i < 30; i++) {
                int result = engine.pickDifferentIndex(5, lastIndex);
                assertNotEquals(lastIndex, result,
                    "pickDifferentIndex must not return the same index as lastIndex");
            }
        }
    }

    @Test
    void pickDifferentIndex_returnsValidIndex() {
        for (int i = 0; i < 50; i++) {
            int result = engine.pickDifferentIndex(10, 3);
            assertTrue(result >= 0 && result < 10,
                "Result must be a valid index in [0, poolSize)");
        }
    }

    @Test
    void pickDifferentIndex_poolSizeOne_returnsZero() {
        // With poolSize = 1, can't avoid lastIndex — always returns 0
        int result = engine.pickDifferentIndex(1, 0);
        assertEquals(0, result);
    }

    @Test
    void pickDifferentIndex_poolSizeTwo_alwaysReturnsDifferent() {
        // With pool [0,1]: if random picks 0 and lastIndex=0, bumps to 1, and vice versa
        for (int i = 0; i < 30; i++) {
            assertNotEquals(0, engine.pickDifferentIndex(2, 0));
            assertNotEquals(1, engine.pickDifferentIndex(2, 1));
        }
    }

    // --- getWeatherEmoji ---

    @Test
    void getWeatherEmoji_returnsNonEmptyString() {
        assertFalse(engine.getWeatherEmoji(0).isBlank());
    }

    @Test
    void getWeatherEmoji_returnsConsistentEmojiForSameProgress() {
        // Same zone should return the same cached emoji
        String first = engine.getWeatherEmoji(5);
        String second = engine.getWeatherEmoji(10); // still zone 0
        assertEquals(first, second);
    }
}
