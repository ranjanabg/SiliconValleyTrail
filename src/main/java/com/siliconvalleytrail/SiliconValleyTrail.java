package com.siliconvalleytrail;

import com.siliconvalleytrail.cli.Menu;
import com.siliconvalleytrail.cli.commands.LoadGameCommand;
import com.siliconvalleytrail.cli.commands.NewGameCommand;
import com.siliconvalleytrail.cli.commands.QuitCommand;
import com.siliconvalleytrail.model.User;
import com.siliconvalleytrail.storage.PlayerDataStore;

import com.siliconvalleytrail.model.TeamMember;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SiliconValleyTrail {

    public static void main(String[] args) {
        orchestrateGame();
    }

    private static void orchestrateGame() {
        final Scanner scanner = new Scanner(System.in);
        final PlayerDataStore saveManager = new PlayerDataStore();

        System.out.print("""
                ========================================
                       Welcome to Silicon Valley Trail
                ========================================

                """);
        System.out.print("Every great startup begins with a name. What's yours, Founder? ");
        String userId = scanner.nextLine().trim();

        User user = User.createNew(userId);
        System.out.println("Welcome, " + user.getUserId() + "! Role: " + user.getRole());
        System.out.println();

        List<TeamMember> teamMembers = collectTeamMembers(scanner, userId);
        System.out.println();
        System.out.println("Your crew is assembled, " + userId + "! " + teamMembers.size() + " brilliant minds ready to take on Silicon Valley:");
        for (TeamMember member : teamMembers) {
            System.out.println("  - " + member.getName() + " (" + member.getJobTitle() + ")");
        }
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

    private static List<TeamMember> collectTeamMembers(Scanner scanner, String founderId) {
        System.out.println("A great founder never travels alone. Who's on your dream team?");
        System.out.println("(Enter at least 4 team members. Press Enter after the 4th to stop, or keep adding.)");
        System.out.println();

        List<TeamMember> members = new ArrayList<>();
        int memberNumber = 1;

        while (true) {
            System.out.print("Team member " + memberNumber + " name: ");
            String name = scanner.nextLine().trim();

            if (name.isEmpty()) {
                if (members.size() < 4) {
                    System.out.println("  Your startup needs at least 4 team members. Keep going!");
                    continue;
                }
                break;
            }

            if (name.equalsIgnoreCase(founderId)) {
                System.out.println("  That's you, Founder! Add someone else to your team.");
                continue;
            }

            System.out.print("  " + name + "'s job title: ");
            String jobTitle = scanner.nextLine().trim();
            if (jobTitle.isEmpty()) jobTitle = "Team Member";

            members.add(new TeamMember("member-" + memberNumber, name, jobTitle));
            memberNumber++;

            if (members.size() >= 4) {
                System.out.print("  Add another team member? (press Enter to stop, or type a name): ");
                String next = scanner.nextLine().trim();
                if (next.isEmpty()) break;
                System.out.print("  " + next + "'s job title: ");
                String nextTitle = scanner.nextLine().trim();
                if (nextTitle.isEmpty()) nextTitle = "Team Member";
                members.add(new TeamMember("member-" + memberNumber, next, nextTitle));
                memberNumber++;
            }
        }

        return members;
    }
}
