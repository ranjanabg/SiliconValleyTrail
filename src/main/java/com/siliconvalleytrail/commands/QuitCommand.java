package com.siliconvalleytrail.commands;

public class QuitCommand implements Command {

    @Override
    public String getOptionLabel() {
        return "Quit";
    }

    @Override
    public void execute() {
        System.out.println("Thanks for playing! Goodbye.");
    }
}
