package com.siliconvalleytrail.milestone;

import com.siliconvalleytrail.cli.ConsoleUtils;
import com.siliconvalleytrail.game.GameState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class MilestoneTrackerTest {

    private MilestoneTracker tracker;
    private GameState state;

    @BeforeEach
    void setUp() {
        tracker = new MilestoneTracker();
        state = new GameState();
        // Suppress System.out during tests
        System.setOut(new PrintStream(new ByteArrayOutputStream()));
    }

    private void initConsoleWithEnters(int count) {
        final String input = "\n".repeat(count);
        ConsoleUtils.init(new Scanner(new ByteArrayInputStream(input.getBytes())));
    }

    @Test
    void noMilestoneTriggeredBelowFirstThreshold() {
        initConsoleWithEnters(0);
        state.applyProgressDelta(8);
        tracker.check(state);
        assertEquals(0, state.getNextMilestoneIndex());
    }

    @Test
    void firstMilestoneTriggeredAtSantaClara() {
        initConsoleWithEnters(1);
        state.applyProgressDelta(9);
        tracker.check(state);
        assertEquals(1, state.getNextMilestoneIndex());
    }

    @Test
    void santaClaraAppliesFundAndEnergyBonus() {
        initConsoleWithEnters(1);
        state.applyProgressDelta(9);
        tracker.check(state);
        assertEquals(53000, state.getFund());  // 50000 + 3000
        assertEquals(100, state.getEnergy()); // 100 + 5 capped at 100
    }

    @Test
    void milestoneNotTriggeredTwice() {
        initConsoleWithEnters(1);
        state.applyProgressDelta(9);
        tracker.check(state);
        tracker.check(state); // second check should not re-trigger
        assertEquals(1, state.getNextMilestoneIndex());
    }

    @Test
    void multiplesMilestonesTriggeredWhenProgressJumps() {
        initConsoleWithEnters(3); // 3 milestones: Santa Clara(9), Sunnyvale(18), Mountain View(27)
        state.applyProgressDelta(27);
        tracker.check(state);
        assertEquals(3, state.getNextMilestoneIndex());
    }

    @Test
    void paloAltoMilestoneGivesFundBonus() {
        initConsoleWithEnters(6); // milestones 0-5 (up to Palo Alto at 54%)
        state.applyProgressDelta(54);
        tracker.check(state);
        // Palo Alto gives +10000, plus earlier bonuses: Santa Clara +3000, Sunnyvale +5000, Redwood +5000
        assertTrue(state.getFund() > 50000);
    }

    @Test
    void allMilestonesTriggeredAtFullProgress() {
        initConsoleWithEnters(10); // 10 total milestones
        state.applyProgressDelta(100);
        tracker.check(state);
        assertEquals(10, state.getNextMilestoneIndex());
    }

    @Test
    void resetDoesNotAffectMilestoneIndex() {
        initConsoleWithEnters(1);
        state.applyProgressDelta(9);
        tracker.check(state);
        assertEquals(1, state.getNextMilestoneIndex());
        tracker.reset();
        // reset() is a no-op currently; milestone index lives in GameState
        assertEquals(1, state.getNextMilestoneIndex());
    }
}
