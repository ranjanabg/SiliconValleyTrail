package com.siliconvalleytrail.cli.commands;

import com.siliconvalleytrail.core.GameEngine;
import com.siliconvalleytrail.model.GameState;
import com.siliconvalleytrail.storage.SaveManager;

import java.util.Scanner;

public class LoadGameCommand implements Command {

    private final Scanner scanner;
    private final SaveManager saveManager;
    private final String userId;

    public LoadGameCommand(Scanner scanner, SaveManager saveManager, String userId) {
        this.scanner = scanner;
        this.saveManager = saveManager;
        this.userId = userId;
    }

    @Override
    public String getOptionLabel() {
        return "Resume Game";
    }

    @Override
    public void execute() {
        System.out.println("Resuming your saved game...");
        System.out.println();

        GameState savedState = saveManager.load(userId);
        new GameEngine(savedState, scanner, saveManager, userId).start();
    }
}
