package com.siliconvalleytrail.cli.commands;

public class QuitCommand implements Command {

    private final String userId;

    public QuitCommand(String userId) {
        this.userId = userId;
    }

    @Override
    public String getOptionLabel() {
        return "Quit";
    }

    @Override
    public void execute() {
        System.out.println("The valley will wait... but not forever. Your team believes in you, " + userId + ". Come back soon!");
        System.exit(0);
    }
}
