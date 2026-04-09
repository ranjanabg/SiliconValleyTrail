package com.siliconvalleytrail.core;

import com.siliconvalleytrail.model.ApiEffect;
import com.siliconvalleytrail.model.GameState;
import com.siliconvalleytrail.model.RandomEvent;
import com.siliconvalleytrail.model.RandomEventChoice;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class EventEngine {

    private static final int TECH_DEBT_CRISIS_THRESHOLD = 60;
    private static final double CRISIS_TRIGGER_CHANCE = 0.4;

    private final Scanner scanner;
    private final Random random = new Random();
    private final ApiEventProvider apiEventProvider = new ApiEventProvider();
    private final ApiEffectHandler weatherHandler = new WeatherEffectHandler();
    private final ApiEffectHandler newsHandler = new NewsEffectHandler();
    private int lastNormalEventIndex = -1;
    private int lastCrisisEventIndex = -1;

    private static final List<RandomEvent> EVENT_POOL = Arrays.asList(

        new RandomEvent(
            "The team spots a roadside diner. Everyone's running on fumes.",
            new RandomEventChoice("Stop for a meal — the team deserves it",
                "Everyone feels refreshed and more human.",
                -300, +8, +10, 0, 0, 0, 0),
            new RandomEventChoice("Push on — no time to stop",
                "The team pushes through hunger. Morale quietly dips.",
                0, -5, 0, 0, 0, 0, 0)
        ),

        new RandomEvent(
            "A VC sends a cold email asking to learn more about your startup.",
            new RandomEventChoice("Reply and schedule a meeting — could be big",
                "The VC loved the pitch and wired $15,000. Buzz spreads.",
                +15000, 0, -10, 0, 0, +8, 0),
            new RandomEventChoice("Ignore it — stay focused on the product",
                "The team ships a feature instead. Progress ticks up.",
                0, 0, 0, +5, 0, 0, 0)
        ),

        new RandomEvent(
            "A tech blog wants to write a feature story about your startup journey.",
            new RandomEventChoice("Agree to the interview — get the word out",
                "The article goes live. Your inbox lights up with interest.",
                0, +5, -8, 0, +10, +15, 0),
            new RandomEventChoice("Decline — heads down on the journey",
                "The team stays focused. No distractions.",
                0, 0, +5, 0, 0, 0, 0)
        ),

        new RandomEvent(
            "There's a networking mixer nearby with founders and investors.",
            new RandomEventChoice("Attend the mixer — who knows who you'll meet",
                "You exchange cards with five interesting people. Energy well spent.",
                -200, +8, -10, 0, +15, +5, 0),
            new RandomEventChoice("Skip it — conserve energy for tomorrow",
                "Early night. The team sleeps well.",
                0, 0, +10, 0, 0, 0, 0)
        ),

        new RandomEvent(
            "One of your best engineers just received a competing offer.",
            new RandomEventChoice("Counter-offer — stretch the budget to keep them",
                "She stays. The team breathes a sigh of relief.",
                -8000, +12, 0, 0, 0, 0, 0),
            new RandomEventChoice("Wish them well — you can't match it right now",
                "She leaves. The team feels the gap immediately.",
                0, -15, -10, 0, -5, 0, 0)
        ),

        new RandomEvent(
            "An angel investor at a coffee shop overheard your team talking and wants to invest.",
            new RandomEventChoice("Accept the $10k check — take the money",
                "Cash in hand, but you owe them a favour. Connections take a hit.",
                +10000, 0, 0, 0, -8, 0, 0),
            new RandomEventChoice("Decline — maintain independence",
                "Word gets around that you turned down money. Respect.",
                0, +10, 0, 0, +5, +10, 0)
        ),

        new RandomEvent(
            "An open source project relevant to your stack is looking for contributors.",
            new RandomEventChoice("Contribute — give back to the community",
                "The PR gets 200 stars overnight. Your profile rises.",
                0, +5, -8, 0, +12, +10, 0),
            new RandomEventChoice("Stay focused on your own product",
                "The team keeps their heads down. Progress holds steady.",
                0, 0, 0, 0, 0, 0, 0)
        ),

        new RandomEvent(
            "The team just hit a small internal milestone and wants to celebrate.",
            new RandomEventChoice("Celebrate properly — dinner on the company",
                "The team comes back tomorrow fired up.",
                -500, +15, +12, 0, 0, 0, 0),
            new RandomEventChoice("Acknowledge it briefly and keep moving",
                "A quick cheer. Back to work. Morale nudges up.",
                0, +5, 0, 0, 0, 0, 0)
        ),

        new RandomEvent(
            "The team skipped lunch to hit a deadline. By 3pm everyone's struggling to focus.",
            new RandomEventChoice("Order food for everyone — the team needs fuel",
                "Pizza arrives. The afternoon suddenly looks brighter.",
                -400, +10, +15, 0, 0, 0, 0),
            new RandomEventChoice("Push through to the end of the day",
                "The team finishes exhausted and irritable.",
                0, -10, -12, 0, 0, 0, 0)
        ),

        new RandomEvent(
            "A critical feature isn't working and it's already 11pm. The team is still at their laptops.",
            new RandomEventChoice("Call it — everyone goes home and sleeps",
                "Fresh eyes in the morning. The fix takes 20 minutes.",
                0, +8, +20, -2, 0, 0, 0),
            new RandomEventChoice("Push through until it's done",
                "Fixed at 2am. Everyone drags themselves in late the next day.",
                0, -12, -20, +3, 0, 0, +5)
        ),

        new RandomEvent(
            "Two engineers had a heated argument about the architecture. The tension is palpable.",
            new RandomEventChoice("Sit down with both of them — resolve it today",
                "Cleared the air. The team feels closer for it.",
                0, +12, -8, 0, 0, 0, 0),
            new RandomEventChoice("Let them work it out themselves",
                "The tension lingers. Work slows down.",
                0, -15, -8, 0, 0, 0, 0)
        ),

        new RandomEvent(
            "The team has been running on energy drinks for days. Someone raises a concern about burnout.",
            new RandomEventChoice("Enforce a proper lunch break and early finish today",
                "Real food and rest makes a real difference. The team feels human again.",
                -200, +10, +20, 0, 0, 0, 0),
            new RandomEventChoice("One more day — we're almost through this sprint",
                "The drinks stop working. Everyone's crashing by evening.",
                0, -8, -18, 0, 0, 0, 0)
        ),

        new RandomEvent(
            "A well-funded competitor just launched a product similar to yours. Tech Twitter is buzzing.",
            new RandomEventChoice("Post a public response highlighting what makes you different",
                "The community rallies around the underdog. Hype rises.",
                0, +5, -8, 0, +5, +12, 0),
            new RandomEventChoice("Stay quiet and keep building",
                "The team feels the pressure but keeps their heads down. Morale dips.",
                0, -10, 0, 0, 0, 0, 0)
        ),

        new RandomEvent(
            "Your first real user sent an email saying your product changed their workflow.",
            new RandomEventChoice("Share it with the whole team — this is what it's all about",
                "The team lights up. Everyone remembers why they started.",
                0, +18, +10, 0, 0, +8, 0),
            new RandomEventChoice("Note it and stay focused on the journey",
                "A quiet smile. Back to work.",
                0, +8, 0, 0, 0, 0, 0)
        )
    );

    private static final List<RandomEvent> CRISIS_POOL = Arrays.asList(

        new RandomEvent(
            "Your app crashed during a live demo with a potential client. The technical debt caught up with you.",
            new RandomEventChoice("All hands on deck — fix it properly tonight",
                "Fixed by morning. Barely. The team is exhausted but the debt drops.",
                0, -10, -20, 0, 0, -10, -20),
            new RandomEventChoice("Hire a contractor to patch it fast",
                "Expensive but contained. The patch holds for now.",
                -6000, -5, 0, 0, 0, -5, -10)
        ),

        new RandomEvent(
            "A critical bug slipped into production. Users are complaining publicly.",
            new RandomEventChoice("Stop everything and fix it properly",
                "The fix is clean. Technical debt drops significantly.",
                0, -8, -15, -5, 0, -15, -25),
            new RandomEventChoice("Push a hotfix — good enough for now",
                "Patched for now. The debt quietly grows.",
                0, -12, -5, 0, 0, -10, +10)
        ),

        new RandomEvent(
            "Rumours are spreading that your product is unstable. An investor calls to ask about it.",
            new RandomEventChoice("Be transparent — schedule a call to explain",
                "They appreciate the honesty. Trust holds.",
                0, 0, -5, 0, +8, -10, 0),
            new RandomEventChoice("Stay quiet and hope it blows over",
                "Rumours spread further. Hype and connections take a hit.",
                0, -8, 0, 0, -10, -15, 0)
        ),

        new RandomEvent(
            "The team has spent the week firefighting bugs instead of making progress.",
            new RandomEventChoice("Dedicate time to fixing root causes properly",
                "Painful week. The codebase is noticeably cleaner.",
                -3000, -5, -15, -8, 0, 0, -30),
            new RandomEventChoice("Fix only what's on fire and keep moving",
                "You move forward, but the fire still smoulders.",
                0, -15, -10, 0, 0, 0, +10)
        )
    );

    public EventEngine(Scanner scanner) {
        this.scanner = scanner;
    }

    public void triggerDailyEvent(GameState state) {
        final RandomEvent event = selectEvent(state);
        printEvent(event);
        final RandomEventChoice chosen = getPlayerChoice(event);
        applyChoice(chosen, state);
        System.out.println("  " + chosen.getOutcome());

        weatherHandler.applyAndPrint(apiEventProvider.getWeatherEffect(state.getProgress()), state);

        final ApiEffect newsEffect = apiEventProvider.getNewsEffect(state.getDay());
        if (newsEffect != null) {
            newsHandler.applyAndPrint(newsEffect, state);
        }
    }

    public void resetApiCache() {
        apiEventProvider.reset();
    }

    private RandomEvent selectEvent(GameState state) {
        if (state.getTechDebt() > TECH_DEBT_CRISIS_THRESHOLD && random.nextDouble() < CRISIS_TRIGGER_CHANCE) {
            int index = pickDifferentIndex(CRISIS_POOL.size(), lastCrisisEventIndex);
            lastCrisisEventIndex = index;
            return CRISIS_POOL.get(index);
        }
        int index = pickDifferentIndex(EVENT_POOL.size(), lastNormalEventIndex);
        lastNormalEventIndex = index;
        return EVENT_POOL.get(index);
    }

    private int pickDifferentIndex(int poolSize, int lastIndex) {
        int index = random.nextInt(poolSize);
        if (index == lastIndex && poolSize > 1) {
            index = (index + 1) % poolSize;
        }
        return index;
    }

    private void printEvent(RandomEvent event) {
        System.out.println();
        System.out.println("🎲 Event: " + event.getDescription());
        System.out.println();
        System.out.println("  A. " + event.getChoiceA().getLabel());
        printChoiceDeltas(event.getChoiceA());
        System.out.println();
        System.out.println("  B. " + event.getChoiceB().getLabel());
        printChoiceDeltas(event.getChoiceB());
        System.out.println();
    }

    private void printChoiceDeltas(RandomEventChoice choice) {
        final StringBuilder deltas = new StringBuilder("     ");
        if (choice.getFundDelta() != 0)        deltas.append(String.format("Fund: $%,d  ", choice.getFundDelta()));
        if (choice.getMoraleDelta() != 0)      deltas.append(String.format("Morale: %+d  ", choice.getMoraleDelta()));
        if (choice.getEnergyDelta() != 0)      deltas.append(String.format("Energy: %+d  ", choice.getEnergyDelta()));
        if (choice.getProgressDelta() != 0)    deltas.append(String.format("Progress: %+d%%  ", choice.getProgressDelta()));
        if (choice.getConnectionsDelta() != 0) deltas.append(String.format("Connections: %+d  ", choice.getConnectionsDelta()));
        if (choice.getHypeDelta() != 0)        deltas.append(String.format("Hype: %+d  ", choice.getHypeDelta()));
        System.out.println(deltas.toString().stripTrailing());
    }

    private RandomEventChoice getPlayerChoice(RandomEvent event) {
        while (true) {
            System.out.print("  Your choice (A/B): ");
            final String input = scanner.nextLine().trim().toUpperCase();
            switch (input) {
                case "A": return event.getChoiceA();
                case "B": return event.getChoiceB();
                default: System.out.println("  Invalid input. Please enter A or B.");
            }
        }
    }

    private void applyChoice(RandomEventChoice choice, GameState state) {
        state.applyFundDelta(choice.getFundDelta());
        state.applyMoraleDelta(choice.getMoraleDelta());
        state.applyEnergyDelta(choice.getEnergyDelta());
        state.applyProgressDelta(choice.getProgressDelta());
        state.applyConnectionsDelta(choice.getConnectionsDelta());
        state.applyHypeDelta(choice.getHypeDelta());
        state.applyTechDebtDelta(choice.getTechDebtDelta());
    }

}
