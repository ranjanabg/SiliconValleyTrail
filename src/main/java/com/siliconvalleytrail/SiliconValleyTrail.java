package com.siliconvalleytrail;

import com.siliconvalleytrail.cli.ConsoleUtils;
import com.siliconvalleytrail.cli.GameIntro;
import com.siliconvalleytrail.cli.Menu;
import com.siliconvalleytrail.storage.PlayerDataStore;

import java.util.Scanner;

public class SiliconValleyTrail {

    public static void main(String[] args) {
        orchestrateGame();
    }

    private static void orchestrateGame() {
        final Scanner scanner = new Scanner(System.in);
        final PlayerDataStore saveManager = new PlayerDataStore();

        ConsoleUtils.init(scanner);

        GameIntro.printWelcomeIntro();

        ConsoleUtils.clearScreen();
        final String userId = GameIntro.promptFounderName(scanner);

        while (true) {
            ConsoleUtils.clearScreen();
            final Menu menu = new Menu(scanner, saveManager, userId);
            menu.show();
            menu.executeOption(menu.requestOption());
        }
    }
}
