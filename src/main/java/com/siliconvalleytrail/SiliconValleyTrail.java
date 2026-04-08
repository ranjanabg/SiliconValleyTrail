package com.siliconvalleytrail;

import com.siliconvalleytrail.cli.Menu;
import com.siliconvalleytrail.cli.commands.LoadGameCommand;
import com.siliconvalleytrail.cli.commands.NewGameCommand;
import com.siliconvalleytrail.cli.commands.QuitCommand;
import com.siliconvalleytrail.model.User;
import com.siliconvalleytrail.storage.SaveManager;

import java.util.Scanner;

public class SiliconValleyTrail {

    public static void main(String[] args) {
        orchestrateGame();
    }

    private static void orchestrateGame() {
        final Scanner scanner = new Scanner(System.in);
        final SaveManager saveManager = new SaveManager();

        System.out.print("Enter your user ID: ");
        String userId = scanner.nextLine().trim();

        User user = User.createNew(userId);
        System.out.println("Welcome, " + user.getUserId() + "! Role: " + user.getRole());
        System.out.println();

        final Menu menu = new Menu(scanner);

        if (saveManager.hasSave(userId)) {
            menu.addOption("1", new LoadGameCommand(scanner, saveManager, userId));
            menu.addOption("2", new NewGameCommand(scanner, saveManager, userId));
            menu.addOption("3", new QuitCommand());
        } else {
            menu.addOption("1", new NewGameCommand(scanner, saveManager, userId));
            menu.addOption("2", new QuitCommand());
        }

        menu.show();
        menu.executeOption(menu.requestOption());
    }
}
