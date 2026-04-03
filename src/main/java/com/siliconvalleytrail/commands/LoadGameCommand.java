package com.siliconvalleytrail.commands;

public class LoadGameCommand implements Command {

    @Override
    public String getOptionLabel() {
        return "Load Game";
    }

    @Override
    public void execute() {
        System.out.println("Loading saved game...");
    }
}
