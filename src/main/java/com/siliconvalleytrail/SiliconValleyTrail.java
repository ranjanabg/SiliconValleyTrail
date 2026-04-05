package com.siliconvalleytrail;

import com.siliconvalleytrail.model.User;

import java.util.Scanner;

public class SiliconValleyTrail {
    public static void main(String[] args) {
        orchestrateGame();
    }

    private static void orchestrateGame() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter your user ID: ");
        String userId = scanner.nextLine().trim();

        User user = User.createNew(userId);
        System.out.println("Welcome, " + user.getUserId() + "! Role: " + user.getRole());
        System.out.println();

        Menu menu = new Menu(scanner);
        menu.show();
        menu.executeOption(menu.requestOption());
    }
}
