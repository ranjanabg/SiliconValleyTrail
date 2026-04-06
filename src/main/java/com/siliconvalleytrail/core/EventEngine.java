package com.siliconvalleytrail.core;

import com.siliconvalleytrail.model.GameState;
import com.siliconvalleytrail.model.RandomEvent;

import java.util.Scanner;

public class EventEngine {

    private final Scanner scanner;

    // Single event for now. Will expand to a pool with random triggering in the future.
    private static final RandomEvent DAILY_EVENT = new RandomEvent(
        "The team stopped at a roadside diner for a meal. Everyone feels a little more human.",
        -300, +3, +5, 0
    );

    public EventEngine(Scanner scanner) {
        this.scanner = scanner;
    }

    public void triggerDailyEvent(GameState state) {
        RandomEvent event = DAILY_EVENT;

        System.out.println();
        System.out.println("[ Event ] " + event.getDescription());
        System.out.println("  Fund change    : $" + String.format("%,d", event.getFundDelta()));
        System.out.println("  Morale change  : " + event.getMoraleDelta());
        System.out.println("  Energy change  : " + event.getEnergyDelta());
        System.out.println();
        System.out.print("  Accept this event? (y/n): ");

        String input = scanner.nextLine().trim().toLowerCase();
        if (input.equals("y")) {
            state.applyFundDelta(event.getFundDelta());
            state.applyMoraleDelta(event.getMoraleDelta());
            state.applyEnergyDelta(event.getEnergyDelta());
            state.applyProgressDelta(event.getProgressDelta());
            System.out.println("  Event accepted.");
        } else {
            System.out.println("  Event declined. The team pushes on.");
        }
    }
}
