package com.siliconvalleytrail.game;

import com.siliconvalleytrail.storage.PlayerDataStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class GameEngineTest {

    private GameState state;
    private GameEngine engine;

    @BeforeEach
    void setUp() {
        state = new GameState();
        engine = new GameEngine(state, new Scanner(new ByteArrayInputStream(new byte[0])), new PlayerDataStore(), "test");
    }

    // --- Rest Day (index 2) ---

    @Test
    void restDay_lockedWhenEnergyAndMoraleHigh() {
        // initial energy=100, morale=100 — both >= 20
        String reason = engine.getLockReason(2);
        assertNotNull(reason);
        assertTrue(reason.contains("rest when energy or morale drops below 20"));
    }

    @Test
    void restDay_unlockedWhenEnergyLow() {
        state.applyEnergyDelta(-85); // energy = 15 < 20
        assertNull(engine.getLockReason(2));
    }

    @Test
    void restDay_unlockedWhenMoraleLow() {
        state.applyMoraleDelta(-85); // morale = 15 < 20
        assertNull(engine.getLockReason(2));
    }

    @Test
    void restDay_lockedWithinCooldown() {
        state.applyEnergyDelta(-85); // would normally unlock
        state.recordRestDay();       // day=1, so daysSinceLastRest=0 < 5
        String reason = engine.getLockReason(2);
        assertNotNull(reason);
        assertTrue(reason.contains("Just rested"));
    }

    @Test
    void restDay_cooldownCheckedBeforeEnergyMorale() {
        state.recordRestDay(); // cooldown active even with high energy/morale
        String reason = engine.getLockReason(2);
        assertNotNull(reason);
        assertTrue(reason.contains("Just rested"));
    }

    // --- Team Event (index 3) ---

    @Test
    void teamEvent_lockedWhenFundTooLow() {
        state.applyFundDelta(-45000); // fund = 5000 <= 10000
        String reason = engine.getLockReason(3);
        assertNotNull(reason);
        assertTrue(reason.contains("Not enough funds"));
    }

    @Test
    void teamEvent_lockedWhenMoraleHigh() {
        // fund = 50000 > 10000; morale = 100 >= 20
        String reason = engine.getLockReason(3);
        assertNotNull(reason);
        assertTrue(reason.contains("Team morale is still strong"));
    }

    @Test
    void teamEvent_unlockedWhenFundOkAndMoraleLow() {
        state.applyMoraleDelta(-85); // morale = 15 < 20; fund stays at 50000 > 10000
        assertNull(engine.getLockReason(3));
    }

    // --- Hackathon (index 4) ---

    @Test
    void hackathon_lockedWhenEnergyTooLow() {
        state.applyEnergyDelta(-65); // energy = 35 <= 40
        String reason = engine.getLockReason(4);
        assertNotNull(reason);
        assertTrue(reason.contains("Team is too tired"));
    }

    @Test
    void hackathon_lockedWhenConnectionsTooLow() {
        // energy = 100 > 40 (passes); connections = 10 <= 25
        String reason = engine.getLockReason(4);
        assertNotNull(reason);
        assertTrue(reason.contains("Not enough connections"));
    }

    @Test
    void hackathon_unlockedWhenEnergyAndConnectionsOk() {
        state.applyConnectionsDelta(20); // connections = 30 > 25; energy = 100 > 40
        assertNull(engine.getLockReason(4));
    }

    // --- Investor Meeting (index 5) ---

    @Test
    void investorMeeting_lockedWhenHypeTooLow() {
        // hype = 10 <= 25
        String reason = engine.getLockReason(5);
        assertNotNull(reason);
        assertTrue(reason.contains("Hype too low"));
    }

    @Test
    void investorMeeting_lockedWhenProgressTooLow() {
        state.applyHypeDelta(20); // hype = 30 > 25
        // progress = 0 < 30
        String reason = engine.getLockReason(5);
        assertNotNull(reason);
        assertTrue(reason.contains("Too early"));
    }

    @Test
    void investorMeeting_lockedOnCooldown() {
        state.applyHypeDelta(20);     // hype = 30 > 25
        state.applyProgressDelta(35); // progress = 35 >= 30
        state.recordInvestorMeeting(); // day=1, daysSince=0 < 5
        String reason = engine.getLockReason(5);
        assertNotNull(reason);
        assertTrue(reason.contains("cooldown"));
    }

    @Test
    void investorMeeting_unlockedWhenAllConditionsMet() {
        state.applyHypeDelta(20);     // hype = 30 > 25
        state.applyProgressDelta(35); // progress = 35 >= 30
        // lastInvestorMeetingDay = -10, day = 1, daysSince = 11 > 5
        assertNull(engine.getLockReason(5));
    }

    // --- checkLoseConditions ---

    @Test
    void checkLoseConditions_triggerOnZeroFund() {
        state.applyFundDelta(-50000); // fund = 0
        engine.checkLoseConditions();
        assertTrue(state.isGameOver());
    }

    @Test
    void checkLoseConditions_triggerOnZeroMorale() {
        state.applyMoraleDelta(-100); // morale = 0
        engine.checkLoseConditions();
        assertTrue(state.isGameOver());
    }

    @Test
    void checkLoseConditions_triggerOnZeroEnergy() {
        state.applyEnergyDelta(-100); // energy = 0
        engine.checkLoseConditions();
        assertTrue(state.isGameOver());
    }

    @Test
    void checkLoseConditions_noTriggerWhenAllPositive() {
        engine.checkLoseConditions();
        assertFalse(state.isGameOver());
    }

    // --- visualLength ---

    @Test
    void visualLength_asciiString() {
        assertEquals(5, GameEngine.visualLength("Hello"));
    }

    @Test
    void visualLength_surrogateEmoji_countsTwoColumns() {
        assertEquals(2, GameEngine.visualLength("🏃")); // U+1F3C3 — surrogate pair
    }

    @Test
    void visualLength_variationSelectorIgnored() {
        // 🗺️ = U+1F5FA + U+FE0F — variation selector should not add width
        assertEquals(2, GameEngine.visualLength("🗺️"));
    }

    @Test
    void visualLength_mixedString() {
        // "🏃 Sprint" — emoji (2) + space (1) + "Sprint" (6) = 9
        assertEquals(9, GameEngine.visualLength("🏃 Sprint"));
    }
}
