package com.siliconvalleytrail.commands;

public class NewGameCommand implements Command {

    @Override
    public String getOptionLabel() {
        return "New Game";
    }

    @Override
    public void execute() {
        System.out.println("Starting a new game...");
    }
}
