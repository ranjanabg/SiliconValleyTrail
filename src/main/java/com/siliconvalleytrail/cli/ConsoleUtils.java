package com.siliconvalleytrail.cli;

import java.util.Scanner;

public class ConsoleUtils {

    private static Scanner scanner;

    public static void init(Scanner s) {
        scanner = s;
    }

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static void waitForEnter(String prompt) {
        System.out.println();
        System.out.print(prompt);
        scanner.nextLine();
        clearScreen();
    }

    public static void waitForEnter() {
        waitForEnter("Press Enter to continue...");
    }
}
