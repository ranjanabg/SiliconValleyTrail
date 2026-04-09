package com.siliconvalleytrail.storage;

import com.google.gson.Gson;
import com.siliconvalleytrail.game.GameState;

import java.io.*;

public class PlayerDataStore {

    private static final String SAVE_DIR = "data/saves/";
    private final Gson gson = new Gson();

    public boolean hasSave(String userId) {
        return saveFile(userId).exists();
    }

    public void savePlayerData(String userId, GameState state) {
        final File file = saveFile(userId);
        file.getParentFile().mkdirs();
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(state, writer);
        } catch (IOException e) {
            System.err.println("Warning: could not save game — " + e.getMessage());
        }
    }

    public GameState loadPlayerData(String userId) {
        try (Reader reader = new FileReader(saveFile(userId))) {
            GameState state = gson.fromJson(reader, GameState.class);
            state.repairMissingFields();
            return state;
        } catch (IOException e) {
            System.err.println("Warning: could not load save — " + e.getMessage());
            return new GameState();
        }
    }

    // Removes the player's save file when the game ends (win or lose)
    public void deletePlayerData(String userId) {
        saveFile(userId).delete();
    }

    // Resolves the save file path for a given player ID: data/saves/{userId}.json
    private File saveFile(String userId) {
        return new File(SAVE_DIR + userId + ".json");
    }
}
