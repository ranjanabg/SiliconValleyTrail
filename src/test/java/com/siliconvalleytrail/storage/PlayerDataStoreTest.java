package com.siliconvalleytrail.storage;

import com.siliconvalleytrail.game.GameState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class PlayerDataStoreTest {

    @TempDir
    Path tempDir;

    private PlayerDataStore store;

    @BeforeEach
    void setUp() {
        store = new PlayerDataStore(tempDir.toString() + "/");
    }

    @Test
    void hasSaveReturnsFalseWhenNoSaveExists() {
        assertFalse(store.hasSave("alice"));
    }

    @Test
    void hasSaveReturnsTrueAfterSaving() {
        store.savePlayerData("alice", new GameState());
        assertTrue(store.hasSave("alice"));
    }

    @Test
    void saveAndLoadRoundTrip() {
        final GameState original = new GameState();
        original.applyFundDelta(-10000);
        original.applyMoraleDelta(-20);
        original.applyProgressDelta(35);

        store.savePlayerData("alice", original);
        final GameState loaded = store.loadPlayerData("alice");

        assertEquals(original.getFund(), loaded.getFund());
        assertEquals(original.getMorale(), loaded.getMorale());
        assertEquals(original.getProgress(), loaded.getProgress());
    }

    @Test
    void differentUsersHaveSeparateSaves() {
        final GameState stateA = new GameState();
        stateA.applyProgressDelta(20);

        final GameState stateB = new GameState();
        stateB.applyProgressDelta(50);

        store.savePlayerData("alice", stateA);
        store.savePlayerData("bob", stateB);

        assertEquals(20, store.loadPlayerData("alice").getProgress());
        assertEquals(50, store.loadPlayerData("bob").getProgress());
    }

    @Test
    void deletePlayerDataRemovesSave() {
        store.savePlayerData("alice", new GameState());
        assertTrue(store.hasSave("alice"));

        store.deletePlayerData("alice");
        assertFalse(store.hasSave("alice"));
    }

    @Test
    void loadReturnsDefaultStateWhenNoSaveExists() {
        final GameState loaded = store.loadPlayerData("nonexistent");
        assertEquals(new GameState().getFund(), loaded.getFund());
        assertEquals(new GameState().getMorale(), loaded.getMorale());
        assertEquals(new GameState().getEnergy(), loaded.getEnergy());
    }

    @Test
    void overwritingSavePreservesLatestState() {
        final GameState first = new GameState();
        first.applyProgressDelta(10);
        store.savePlayerData("alice", first);

        final GameState second = new GameState();
        second.applyProgressDelta(50);
        store.savePlayerData("alice", second);

        assertEquals(50, store.loadPlayerData("alice").getProgress());
    }
}
