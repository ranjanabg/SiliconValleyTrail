package com.siliconvalleytrail.commands;

import com.siliconvalleytrail.engine.GameEngine;
import com.siliconvalleytrail.engine.GameState;


import java.util.Scanner;

public class NewGameCommand implements Command {

    private final Scanner scanner;

    public NewGameCommand(Scanner scanner) {
        this.scanner = scanner;
    }

    @Override
    public String getOptionLabel() {
        return "New Game";
    }

    @Override
    public void execute() {
        System.out.println("Starting a new game...");
        System.out.println();
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║   Welcome to the Silicon Valley Trail!   ║");
        System.out.println("╚══════════════════════════════════════════╝");
        System.out.println();
        System.out.println("Your team is embarking on a journey from " + GameEngine.SOURCE);
        System.out.println("all the way to " + GameEngine.DESTINATION + " to build the next Big thing in Silicon Valley. Good luck!");
        System.out.println();
        System.out.println("--- Watch Out For ---");
        System.out.println("  Fund         : Don't run out — every decision costs money");
        System.out.println("  Team Morale  : Keep trust alive — tension and doubt will break the team");
        System.out.println("  Team Energy  : Watch stamina — sleep deprivation and burnout end journeys");
        System.out.println("  Progress     : Reach 100% to make it to Silicon Valley");
        System.out.println("---------------------");

        GameEngine engine = new GameEngine(scanner);
        engine.start();
    }
}
