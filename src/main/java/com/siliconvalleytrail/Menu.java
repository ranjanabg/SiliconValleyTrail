package com.siliconvalleytrail;

import com.siliconvalleytrail.commands.Command;
import com.siliconvalleytrail.commands.LoadGameCommand;
import com.siliconvalleytrail.commands.NewGameCommand;
import com.siliconvalleytrail.commands.QuitCommand;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class Menu {

    private final Map<String, Command> options = new LinkedHashMap<>();
    private final Scanner scanner;

    public Menu(Scanner scanner) {
        this.scanner = scanner;
        addOption("1", new NewGameCommand());
        addOption("2", new LoadGameCommand());
        addOption("3", new QuitCommand());
    }

    public void addOption(String key, Command command) {
        options.put(key, command);
    }

    public void show() {
        welcomeMessage();
        for (Map.Entry<String, Command> entry : options.entrySet()) {
            System.out.println("  " + entry.getKey() + ". " + entry.getValue().getOptionLabel());
        }
        System.out.println();
    }

    public String requestOption() {
        System.out.print("Enter your choice: ");
        return scanner.nextLine().trim();
    }

    public void executeOption(String playerInput) {
        Command selected = options.get(playerInput);

        if (selected == null) {
            System.out.println("Invalid option. Please enter a number between 1 and " + options.size() + ".");
            return;
        }

        selected.execute();
    }

    private void welcomeMessage() {
        System.out.println("========================================");
        System.out.println("       Welcome to Silicon Valley Trail  ");
        System.out.println("========================================");
        System.out.println();
    }
}
