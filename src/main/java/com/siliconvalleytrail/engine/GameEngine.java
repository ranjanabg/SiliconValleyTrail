package com.siliconvalleytrail.engine;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class GameEngine {

    private final GameState state;
    private final Scanner scanner;

    private static final List<Choice> DAILY_CHOICES = Arrays.asList(
        new Choice("Sprint       - Push the team hard to move faster",  -3000, -10, -20, +15),
        new Choice("Steady Pace  - Move at a sustainable speed",        -2000,  -5, -10,  +8),
        new Choice("Rest Day     - Let the team recover",               -1000, +20, +25,   0)
    );

    public GameEngine(GameState state, Scanner scanner) {
        this.state = state;
        this.scanner = scanner;
    }

    public void runDay() {
        printDayHeader();
        printChoices();

        Choice choice = getPlayerChoice();
        applyChoice(choice);

        checkLoseConditions();
        if (state.isGameOver()) return;

        checkWinCondition();
        if (state.isGameOver()) return;

        pause();
        state.advanceDay();
    }

    public boolean isGameOver() {
        return state.isGameOver();
    }

    private void printDayHeader() {
        System.out.println();
        System.out.println("==========================================");
        System.out.println("  Day " + state.getDay());
        System.out.println("==========================================");
        System.out.printf("  Fund         : $%,d%n", state.getFund());
        System.out.println("  Team Morale  : " + state.getMorale() + "/100");
        System.out.println("  Team Energy  : " + state.getEnergy() + "/100");
        System.out.println("  Progress     : " + state.getProgress() + "% to destination");
        System.out.println("==========================================");
        System.out.println();
    }

    private void printChoices() {
        System.out.println("What does your team do today?");
        System.out.println();
        for (int i = 0; i < DAILY_CHOICES.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + DAILY_CHOICES.get(i).getDescription());
        }
        System.out.println();
    }

    private Choice getPlayerChoice() {
        while (true) {
            System.out.print("Enter your choice (1-3): ");
            String input = scanner.nextLine().trim();
            if (input.equals("1") || input.equals("2") || input.equals("3")) {
                return DAILY_CHOICES.get(Integer.parseInt(input) - 1);
            }
            System.out.println("Invalid choice. Please enter 1, 2, or 3.");
        }
    }

    private void applyChoice(Choice choice) {
        state.applyFundDelta(choice.getFundDelta());
        state.applyMoraleDelta(choice.getMoraleDelta());
        state.applyEnergyDelta(choice.getEnergyDelta());
        state.applyProgressDelta(choice.getProgressDelta());

        System.out.println();
        System.out.println("Your team chose: " + choice.getDescription().split("-")[0].trim());
        System.out.println("  Fund change    : $" + String.format("%,d", choice.getFundDelta()));
        System.out.println("  Morale change  : " + choice.getMoraleDelta());
        System.out.println("  Energy change  : " + choice.getEnergyDelta());
        System.out.println("  Progress change: " + choice.getProgressDelta() + "%");
    }

    private void checkLoseConditions() {
        if (state.getFund() <= 0) {
            System.out.println();
            System.out.println("GAME OVER: You ran out of funding. The journey ends here.");
            state.endGame();
        } else if (state.getMorale() <= 0) {
            System.out.println();
            System.out.println("GAME OVER: Team morale hit zero. Everyone quit.");
            state.endGame();
        } else if (state.getEnergy() <= 0) {
            System.out.println();
            System.out.println("GAME OVER: Your team burned out completely.");
            state.endGame();
        }
    }

    private void checkWinCondition() {
        if (state.getProgress() >= 100) {
            System.out.println();
            System.out.println("╔══════════════════════════════════════════╗");
            System.out.println("║   YOU MADE IT TO SILICON VALLEY!         ║");
            System.out.println("║   Your startup journey is complete!      ║");
            System.out.println("╚══════════════════════════════════════════╝");
            state.endGame();
        }
    }

    private void pause() {
        System.out.println();
        System.out.print("Press Enter to continue to Day " + (state.getDay() + 1) + "...");
        scanner.nextLine();
    }
}
