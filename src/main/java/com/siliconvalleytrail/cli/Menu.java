package com.siliconvalleytrail.cli;

import com.siliconvalleytrail.cli.commands.Command;
import com.siliconvalleytrail.cli.commands.LoadGameCommand;
import com.siliconvalleytrail.cli.commands.NewGameCommand;
import com.siliconvalleytrail.cli.commands.QuitCommand;
import com.siliconvalleytrail.storage.PlayerDataStore;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class Menu {

    private final Map<String, Command> options = new LinkedHashMap<>();
    private final Scanner scanner;

    public Menu(final Scanner scanner, final PlayerDataStore saveManager, final String userId) {
        this.scanner = scanner;
        if (saveManager.hasSave(userId)) {
            options.put("1", new LoadGameCommand(scanner, saveManager, userId));
            options.put("2", new NewGameCommand(scanner, saveManager, userId));
            options.put("3", new QuitCommand(userId));
        } else {
            options.put("1", new NewGameCommand(scanner, saveManager, userId));
            options.put("2", new QuitCommand(userId));
        }
    }

    public void show() {
        for (Map.Entry<String, Command> entry : options.entrySet()) {
            System.out.println("  " + entry.getKey() + ". " + entry.getValue().getOptionLabel());
        }
        System.out.println();
    }

    public String requestOption() {
        System.out.print("Enter your choice: ");
        return scanner.nextLine().trim();
    }

    public void executeOption(final String playerInput) {
        final Command selected = options.get(playerInput);

        if (selected == null) {
            System.out.println("Invalid option. Please enter a number between 1 and " + options.size() + ".");
            return;
        }

        selected.execute();
    }

}
