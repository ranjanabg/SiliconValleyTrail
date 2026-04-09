package com.siliconvalleytrail.cli.commands;

import com.siliconvalleytrail.cli.ConsoleUtils;
import com.siliconvalleytrail.core.GameEngine;
import com.siliconvalleytrail.storage.PlayerDataStore;

import java.util.Scanner;

public class NewGameCommand implements Command {

    private final Scanner scanner;
    private final PlayerDataStore saveManager;
    private final String userId;

    public NewGameCommand(Scanner scanner, PlayerDataStore saveManager, String userId) {
        this.scanner = scanner;
        this.saveManager = saveManager;
        this.userId = userId;
    }

    @Override
    public String getOptionLabel() {
        return "New Game";
    }

    @Override
    public void execute() {
        ConsoleUtils.clearScreen();
        System.out.println("Starting a new game...");
        System.out.print("""

                ╔══════════════════════════════════════════╗
                ║   Welcome to the Silicon Valley Trail!   ║
                ╚══════════════════════════════════════════╝

                """);
        System.out.println("You are leading your team on a journey from " + GameEngine.SOURCE);
        System.out.println("all the way to " + GameEngine.DESTINATION + " to build the next big thing in Silicon Valley. Good luck, Founder!");
        System.out.print("""

                --- Watch Out For ---
                  Fund         : Don't run out — every decision costs money
                  Team Morale  : Keep trust alive — tension and doubt will break the team
                  Team Energy  : Watch stamina — sleep deprivation and burnout end journeys
                  Progress     : Reach 100% to make it to Silicon Valley
                ---------------------

                --- The Valley is Watching ---
                  Each day, real-world weather and tech news headlines may affect your journey.
                  A thunderstorm could slow your progress. A funding wave could energize the team.
                  Stay sharp — the outside world shapes your path.
                ------------------------------
                """);

        ConsoleUtils.waitForEnter("Press Enter to start your journey...");
        new GameEngine(scanner, saveManager, userId).start();
    }
}
