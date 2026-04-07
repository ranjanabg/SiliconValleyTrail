package com.siliconvalleytrail.core;

import com.siliconvalleytrail.model.Choice;
import com.siliconvalleytrail.model.GameState;
import com.siliconvalleytrail.storage.SaveManager;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class GameEngine {

    public static final String SOURCE = "San Jose";
    public static final String DESTINATION = "San Francisco";

    private final GameState state;
    private final Scanner scanner;
    private final SaveManager saveManager;
    private final String userId;
    private final MilestoneTracker milestoneTracker = new MilestoneTracker();
    private final EventEngine eventEngine;
    private boolean playerExited = false;

    private static final int TEAM_EVENT_INDEX = 4;
    private static final int TEAM_EVENT_MIN_FUND = 10000;
    private static final int TEAM_EVENT_MAX_MORALE = 70;

    private static final int HACKATHON_INDEX = 5;
    private static final int HACKATHON_MIN_ENERGY = 40;
    private static final int HACKATHON_MIN_CONNECTIONS = 25;

    private static final int INVESTOR_MEETING_INDEX = 6;
    private static final int INVESTOR_MEETING_MIN_HYPE = 25;
    private static final int INVESTOR_MEETING_MIN_PROGRESS = 30;
    private static final int INVESTOR_MEETING_COOLDOWN_DAYS = 5;

    private static final List<Choice> DAILY_CHOICES = Arrays.asList(
        new Choice("🏃 Sprint           - Push the team hard to move faster",    -3000, -10, -20, +8,   0,  0, +15),
        new Choice("🚶 Steady Pace      - Move at a sustainable speed",          -2000,  -5, -10, +3,   0,  0,  +5),
        new Choice("😴 Rest Day         - Full day off, recover and clean up",   -1000, +30, +35,  0,   0,  0, -20),
        new Choice("🍕 Food Break       - Fuel the team with a proper meal",      -800, +10, +12, +1,   0,  0,   0),
        new Choice("🎉 Team Event       - Boost morale with a team outing",      -5000, +25, +15,  0,  +5, +5,   0),
        new Choice("💻 Hackathon        - Public build session, high visibility",-2000,  +8, -20, +3, +10,+15, +10),
        new Choice("🤝 Investor Meeting - Pitch for funding, costs a day",        +8000,  -5, -15,  0, +10,+10,   0)
    );

    public GameEngine(Scanner scanner, SaveManager saveManager, String userId) {
        this.state = new GameState();
        this.scanner = scanner;
        this.saveManager = saveManager;
        this.userId = userId;
        this.eventEngine = new EventEngine(scanner);
    }

    public GameEngine(GameState state, Scanner scanner, SaveManager saveManager, String userId) {
        this.state = state;
        this.scanner = scanner;
        this.saveManager = saveManager;
        this.userId = userId;
        this.eventEngine = new EventEngine(scanner);
    }

    public void start() {
        while (true) {
            while (!state.isGameOver()) {
                runDay();
            }
            if (playerExited || !promptRestart()) {
                break;
            }
            state.reset();
            milestoneTracker.reset();
            playerExited = false;
        }
    }

    public void runDay() {
        printDayHeader();
        printChoices();

        Choice choice = getPlayerChoice();
        if (choice == null) return;

        applyChoice(choice);
        milestoneTracker.check(state);
        eventEngine.triggerDailyEvent(state);
        milestoneTracker.check(state);

        checkLoseConditions();
        if (state.isGameOver()) {
            saveManager.deleteSave(userId);
            return;
        }

        checkWinCondition();
        if (state.isGameOver()) {
            saveManager.deleteSave(userId);
            return;
        }

        state.advanceDay();
        saveManager.save(userId, state);
        pause();
    }

    private void printDayHeader() {
        System.out.println();
        System.out.println("==========================================");
        System.out.println("  ☀️  Day " + state.getDay());
        System.out.println("==========================================");
        String fundEmoji = state.getFund() < 10000 ? "💸" : "💰";
        System.out.printf("  " + fundEmoji + " Fund         : $%,d%n", state.getFund());
        String moraleEmoji = state.getMorale() < 20 ? "😞" : "😊";
        String energyEmoji = state.getEnergy() < 20 ? "🪫" : "🔋";
        System.out.println("  " + moraleEmoji + " Team Morale  : " + state.getMorale() + "/100");
        System.out.println("  " + energyEmoji + " Team Energy  : " + state.getEnergy() + "/100");
        System.out.println("  🤝 Connections  : " + state.getConnections() + "/100");
        System.out.println("  🔥 Hype         : " + state.getHype() + "/100");
        System.out.println("  🗺️  Journey      : " + SOURCE + " " + buildProgressBar(state.getProgress()) + " " + DESTINATION);
        System.out.println("==========================================");
        System.out.println();
    }

    private void printChoices() {
        System.out.println("What does your team do today?");
        System.out.println();
        for (int i = 0; i < DAILY_CHOICES.size(); i++) {
            Choice choice = DAILY_CHOICES.get(i);
            System.out.println("  " + (i + 1) + ". " + choice.getDescription());
            String lockReason = getLockReason(i);
            if (lockReason != null) {
                System.out.println("       🔒 Locked: " + lockReason);
                System.out.println();
                continue;
            }
            System.out.printf("       Fund: $%,d  |  Morale: %+d  |  Energy: %+d  |  Progress: %+d%%%n",
                choice.getFundDelta(), choice.getMoraleDelta(), choice.getEnergyDelta(), choice.getProgressDelta());
            System.out.println();
        }
        System.out.println("  " + (DAILY_CHOICES.size() + 1) + ". 👋 Exit Game");
        System.out.println();
    }

    private Choice getPlayerChoice() {
        int exitOption = DAILY_CHOICES.size() + 1;
        while (true) {
            System.out.print("Enter your choice (1-" + exitOption + "): ");
            String input = scanner.nextLine().trim();
            try {
                int chosen = Integer.parseInt(input);
                if (chosen >= 1 && chosen <= DAILY_CHOICES.size()) {
                    String lockReason = getLockReason(chosen - 1);
                    if (lockReason != null) {
                        System.out.println("  🔒 " + lockReason);
                        continue;
                    }
                    return DAILY_CHOICES.get(chosen - 1);
                } else if (chosen == exitOption) {
                    System.out.println();
                    System.out.println("Exiting game. See you on the trail!");
                    playerExited = true;
                    state.endGame();
                    return null;
                }
            } catch (NumberFormatException ignored) {}
            System.out.println("Invalid choice. Please enter a number between 1 and " + exitOption + ".");
        }
    }

    private void applyChoice(Choice choice) {
        state.applyFundDelta(choice.getFundDelta());
        state.applyMoraleDelta(choice.getMoraleDelta());
        state.applyEnergyDelta(choice.getEnergyDelta());
        state.applyProgressDelta(choice.getProgressDelta());
        state.applyConnectionsDelta(choice.getConnectionsDelta());
        state.applyHypeDelta(choice.getHypeDelta());
        state.applyTechDebtDelta(choice.getTechDebtDelta());
        if (DAILY_CHOICES.indexOf(choice) == INVESTOR_MEETING_INDEX) {
            state.recordInvestorMeeting();
        }


        System.out.println();
        System.out.println("Your team chose: " + choice.getDescription().split("-")[0].trim());
        System.out.println("  " + getChoiceNarrative(DAILY_CHOICES.indexOf(choice)));
    }

    private String getChoiceNarrative(int index) {
        switch (index) {
            case 0: return "The team pushes hard. Every mile counts — but so does every drop of energy.";
            case 1: return "Measured and deliberate. Not the fastest, but the team is still standing.";
            case 2: return "The laptops close. The team breathes. Tomorrow will be better.";
            case 3: return "Full stomachs, clearer minds. Sometimes the best investment is a good meal.";
            case 4: return "Laughter fills the room. The team remembers why they started this together.";
            case 5: return "Heads down, keyboards loud. The team is in the zone and the valley is watching.";
            case 6: return "Suits, slides, and handshakes. The pitch is done — now you wait.";
            default: return "The team makes their move.";
        }
    }

    private void checkLoseConditions() {
        if (state.getFund() <= 0) {
            printLoseScreen("You ran out of funding. The journey ends here.");
        } else if (state.getMorale() <= 0) {
            printLoseScreen("Team morale hit zero. Everyone quit.");
        } else if (state.getEnergy() <= 0) {
            printLoseScreen("Your team burned out completely.");
        }
    }

    private void checkWinCondition() {
        if (state.getProgress() >= 100) {
            printWinScreen();
        }
    }

    private void printWinScreen() {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║  🎉  YOU MADE IT TO SAN FRANCISCO!  🎉  ║");
        System.out.println("║     Your startup journey is complete!    ║");
        System.out.println("╚══════════════════════════════════════════╝");
        printStatsSummary();
        state.endGame();
    }

    private void printLoseScreen(String reason) {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║           💀  GAME OVER  💀              ║");
        System.out.println("╚══════════════════════════════════════════╝");
        System.out.println("  Reason  : " + reason);
        printStatsSummary();
        state.endGame();
    }

    private void printStatsSummary() {
        System.out.println();
        System.out.println("  --- Final Stats ---");
        System.out.println("  Days Survived  : " + state.getDay());
        System.out.printf("  Fund Remaining : $%,d%n", state.getFund());
        System.out.println("  Team Morale    : " + state.getMorale() + "/100");
        System.out.println("  Team Energy    : " + state.getEnergy() + "/100");
        System.out.println("  Connections    : " + state.getConnections() + "/100");
        System.out.println("  Hype           : " + state.getHype() + "/100");
        System.out.println("  Progress       : " + buildProgressBar(state.getProgress()));
        System.out.println("  --------------------");
    }

    private String buildProgressBar(int progress) {
        int filled = progress / 10;
        int empty = 10 - filled;
        return "[" + "█".repeat(filled) + "░".repeat(empty) + "] " + progress + "%";
    }

    private String getLockReason(int index) {
        if (index == TEAM_EVENT_INDEX) {
            if (state.getFund() <= TEAM_EVENT_MIN_FUND)
                return "Not enough funds — need more than $" + String.format("%,d", TEAM_EVENT_MIN_FUND) + " to run a team event";
            if (state.getMorale() >= TEAM_EVENT_MAX_MORALE)
                return "Team morale is already high — save the budget (need Morale < " + TEAM_EVENT_MAX_MORALE + ")";
        }
        if (index == HACKATHON_INDEX) {
            if (state.getEnergy() <= HACKATHON_MIN_ENERGY)
                return "Team is too tired — rest up first (need Energy > " + HACKATHON_MIN_ENERGY + ")";
            if (state.getConnections() <= HACKATHON_MIN_CONNECTIONS)
                return "Not enough connections — build your network first (need Connections > " + HACKATHON_MIN_CONNECTIONS + ")";
        }
        if (index == INVESTOR_MEETING_INDEX) {
            if (state.getHype() <= INVESTOR_MEETING_MIN_HYPE)
                return "Hype too low — build your reputation first (need Hype > " + INVESTOR_MEETING_MIN_HYPE + ")";
            if (state.getProgress() < INVESTOR_MEETING_MIN_PROGRESS)
                return "Too early — reach Mountain View first (need Progress > " + INVESTOR_MEETING_MIN_PROGRESS + "%)";
            int daysSinceLastMeeting = state.getDay() - state.getLastInvestorMeetingDay();
            if (daysSinceLastMeeting < INVESTOR_MEETING_COOLDOWN_DAYS) {
                int daysLeft = INVESTOR_MEETING_COOLDOWN_DAYS - daysSinceLastMeeting;
                return "On cooldown — investors need time between meetings (available in " + daysLeft + " day(s))";
            }
        }
        return null;
    }

    private boolean promptRestart() {
        System.out.println();
        System.out.print("🔄 Would you like to play again? (y/n): ");
        String input = scanner.nextLine().trim().toLowerCase();
        return input.equals("y") || input.equals("yes");
    }

    private void pause() {
        System.out.println();
        System.out.print("Press Enter to continue to Day " + state.getDay() + "...");
        scanner.nextLine();
    }
}
