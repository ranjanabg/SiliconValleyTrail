package com.siliconvalleytrail;

public class SiliconValleyTrail {
    public static void main(String[] args) {
        orchestrateGame();
    }

    private static void orchestrateGame() {
        final Menu menu = new Menu();
        menu.show();
        menu.executeOption(menu.requestOption());
    }
}
