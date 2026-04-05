package com.siliconvalleytrail.commands;

public class NewGameCommand implements Command {

    @Override
    public String getOptionLabel() {
        return "New Game";
    }

    @Override
    public void execute() {
        System.out.println("Starting a new game...");
        System.out.println();
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║   Welcome to the Silicon Valley Trail!   ║");
        System.out.println("╚══════════════════════════════════════════╝");
        System.out.println();
        System.out.println("Your team is embarking on a journey to build");
        System.out.println("the next big thing in Silicon Valley. Good luck!");
        System.out.println();
        System.out.println("--- Starting Resources ---");
        System.out.println("  Fund         : $80,000");
        System.out.println("  Team Morale  : 100/100");
        System.out.println("  Team Energy  : 100/100");
        System.out.println("  Progress     : 0% to destination");
        System.out.println("--------------------------");
    }
}
