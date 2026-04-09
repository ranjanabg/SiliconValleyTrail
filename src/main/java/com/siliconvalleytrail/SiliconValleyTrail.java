package com.siliconvalleytrail;

import com.siliconvalleytrail.cli.Menu;
import com.siliconvalleytrail.cli.commands.LoadGameCommand;
import com.siliconvalleytrail.cli.commands.NewGameCommand;
import com.siliconvalleytrail.cli.commands.QuitCommand;
import com.siliconvalleytrail.model.User;
import com.siliconvalleytrail.storage.PlayerDataStore;

import java.util.Scanner;

public class SiliconValleyTrail {

    public static void main(String[] args) {
        orchestrateGame();
    }

    private static void orchestrateGame() {
        final Scanner scanner = new Scanner(System.in);
        final PlayerDataStore saveManager = new PlayerDataStore();

        printWelcomeIntro(scanner);

        final String userId = promptFounderName(scanner);

        final Menu menu = buildMenu(scanner, saveManager, userId);
        menu.show();
        menu.executeOption(menu.requestOption());
    }

    private static void printWelcomeIntro(Scanner scanner) {
        System.out.print("""
                ========================================
                       Welcome to Silicon Valley Trail
                ========================================

                """);

        pause(scanner, "Press Enter to begin your journey...");

        System.out.print("""

                It's the early days of your startup. The idea is bold, the team is hungry,
                and the valley is full of promise — and peril.

                Your journey begins in San Jose and ends in San Francisco, 50 miles of
                ambition, decisions, and unpredictable twists. Along the way, real weather
                will batter your team, tech headlines will shake your confidence (or fuel it),
                and every choice you make will cost you — money, energy, or morale.

                Manage your team wisely. Keep the lights on. Make it to San Francisco.

                The valley doesn't wait for anyone.

                """);

        pause(scanner, "Press Enter to continue...");
        System.out.println();
    }

    private static String promptFounderName(Scanner scanner) {
        System.out.print("There is always a brilliant mind behind every great startup.What's yours, Founder? ");
        final String userId = scanner.nextLine().trim();

        final User user = User.createNew(userId);
        System.out.println("Welcome, " + user.getUserId() + "! Role: " + user.getRole());
        System.out.println();

        return userId;
    }

    private static Menu buildMenu(Scanner scanner, PlayerDataStore saveManager, String userId) {
        final Menu menu = new Menu(scanner);

        if (saveManager.hasSave(userId)) {
            menu.addOption("1", new LoadGameCommand(scanner, saveManager, userId));
            menu.addOption("2", new NewGameCommand(scanner, saveManager, userId));
            menu.addOption("3", new QuitCommand());
        } else {
            menu.addOption("1", new NewGameCommand(scanner, saveManager, userId));
            menu.addOption("2", new QuitCommand());
        }

        return menu;
    }

    private static void pause(Scanner scanner, String prompt) {
        System.out.print(prompt);
        scanner.nextLine();
    }
}
