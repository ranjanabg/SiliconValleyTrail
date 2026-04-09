package com.siliconvalleytrail.cli;

import com.siliconvalleytrail.player.User;

import java.util.Scanner;

public class GameIntro {

    public static void printWelcomeIntro() {
        ConsoleUtils.clearScreen();
        System.out.print("""
                ========================================
                       Welcome to Silicon Valley Trail
                ========================================

                It's the early days of your startup. The idea is bold, the team is hungry, and the valley is full of promise — and peril.

                Your journey begins in San Jose and ends in San Francisco, 50 miles of ambition, decisions, and unpredictable twists.
                Along the way, real weather will batter your team, tech headlines will shake your confidence (or fuel it), and every
                choice you make will cost you — money, energy, or morale.

                Manage your team wisely. Keep the lights on. Make it to San Francisco. The valley doesn't wait for anyone.

                """);

        ConsoleUtils.waitForEnter();
    }

    public static String promptFounderName(Scanner scanner) {
        String userId;
        while (true) {
            System.out.print("There is always a brilliant mind behind every great startup. What's yours, Founder? ");
            userId = scanner.nextLine().trim();
            if (!userId.isEmpty()) break;
            System.out.println("Every great founder has a name. Don't be shy — what do they call you?");
        }

        final User user = User.createNew(userId);
        System.out.println();
        System.out.println("Welcome, " + user.getUserId() + "! Role: " + user.getRole());

        ConsoleUtils.waitForEnter();

        return userId;
    }
}
