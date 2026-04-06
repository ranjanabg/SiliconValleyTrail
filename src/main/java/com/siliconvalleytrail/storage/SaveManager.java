package com.siliconvalleytrail.storage;

import com.google.gson.Gson;
import com.siliconvalleytrail.model.GameState;

import java.io.*;

public class SaveManager {

    private static final String SAVE_DIR = "data/saves/";
    private final Gson gson = new Gson();

    public boolean hasSave(String userId) {
        return saveFile(userId).exists();
    }

    public void save(String userId, GameState state) {
        File file = saveFile(userId);
        file.getParentFile().mkdirs();
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(state, writer);
        } catch (IOException e) {
            System.err.println("Warning: could not save game — " + e.getMessage());
        }
    }

    public GameState load(String userId) {
        try (Reader reader = new FileReader(saveFile(userId))) {
            return gson.fromJson(reader, GameState.class);
        } catch (IOException e) {
            System.err.println("Warning: could not load save — " + e.getMessage());
            return new GameState();
        }
    }

    public void deleteSave(String userId) {
        saveFile(userId).delete();
    }

    private File saveFile(String userId) {
        return new File(SAVE_DIR + userId + ".json");
    }
}
