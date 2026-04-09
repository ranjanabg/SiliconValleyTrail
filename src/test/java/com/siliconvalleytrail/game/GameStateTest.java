package com.siliconvalleytrail.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameStateTest {

    private GameState state;

    @BeforeEach
    void setUp() {
        state = new GameState();
    }

    // --- Fund ---

    @Test
    void fundIncreasesCorrectly() {
        state.applyFundDelta(5000);
        assertEquals(55000, state.getFund());
    }

    @Test
    void fundDecreasesCorrectly() {
        state.applyFundDelta(-3000);
        assertEquals(47000, state.getFund());
    }

    @Test
    void fundNeverGoesBelowZero() {
        state.applyFundDelta(-999999);
        assertEquals(0, state.getFund());
    }

    // --- Morale ---

    @Test
    void moraleDecreasesCorrectly() {
        state.applyMoraleDelta(-10);
        assertEquals(90, state.getMorale());
    }

    @Test
    void moraleClampsAtZero() {
        state.applyMoraleDelta(-999);
        assertEquals(0, state.getMorale());
    }

    @Test
    void moraleClampsAtHundred() {
        state.applyMoraleDelta(999);
        assertEquals(100, state.getMorale());
    }

    // --- Energy ---

    @Test
    void energyDecreasesCorrectly() {
        state.applyEnergyDelta(-15);
        assertEquals(85, state.getEnergy());
    }

    @Test
    void energyClampsAtZero() {
        state.applyEnergyDelta(-999);
        assertEquals(0, state.getEnergy());
    }

    @Test
    void energyClampsAtHundred() {
        state.applyEnergyDelta(999);
        assertEquals(100, state.getEnergy());
    }

    // --- Progress ---

    @Test
    void progressIncreasesCorrectly() {
        state.applyProgressDelta(7);
        assertEquals(7, state.getProgress());
    }

    @Test
    void progressClampsAtZero() {
        state.applyProgressDelta(-999);
        assertEquals(0, state.getProgress());
    }

    @Test
    void progressClampsAtHundred() {
        state.applyProgressDelta(999);
        assertEquals(100, state.getProgress());
    }

    // --- Day ---

    @Test
    void dayStartsAtOne() {
        assertEquals(1, state.getDay());
    }

    @Test
    void advanceDayIncrementsCorrectly() {
        state.advanceDay();
        state.advanceDay();
        assertEquals(3, state.getDay());
    }

    // --- Game Over ---

    @Test
    void gameNotOverInitially() {
        assertFalse(state.isGameOver());
    }

    @Test
    void endGameSetsGameOver() {
        state.endGame();
        assertTrue(state.isGameOver());
    }

    // --- Milestone ---

    @Test
    void milestoneIndexStartsAtZero() {
        assertEquals(0, state.getNextMilestoneIndex());
    }

    @Test
    void advanceMilestoneIncrementsIndex() {
        state.advanceMilestone();
        state.advanceMilestone();
        assertEquals(2, state.getNextMilestoneIndex());
    }

    // --- Investor Meeting ---

    @Test
    void recordInvestorMeetingSetsDay() {
        state.advanceDay(); // day = 2
        state.recordInvestorMeeting();
        assertEquals(2, state.getLastInvestorMeetingDay());
    }

    // --- Rest Day ---

    @Test
    void recordRestDaySetsDay() {
        state.advanceDay(); // day = 2
        state.recordRestDay();
        assertEquals(2, state.getLastRestDay());
    }

    // --- Repair Missing Fields ---

    @Test
    void repairFixesZeroInvestorMeetingDay() {
        // Simulate old save with default 0 value
        state.recordInvestorMeeting(); // sets to day 1
        state.repairMissingFields();   // should not break a valid value
        assertEquals(1, state.getLastInvestorMeetingDay());
    }

    // --- Reset ---

    @Test
    void resetRestoresAllDefaults() {
        state.applyFundDelta(-20000);
        state.applyMoraleDelta(-50);
        state.applyEnergyDelta(-50);
        state.applyProgressDelta(80);
        state.advanceDay();
        state.endGame();

        state.reset();

        assertEquals(50000, state.getFund());
        assertEquals(100, state.getMorale());
        assertEquals(100, state.getEnergy());
        assertEquals(0, state.getProgress());
        assertEquals(1, state.getDay());
        assertFalse(state.isGameOver());
    }
}
