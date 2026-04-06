package com.siliconvalleytrail.engine;

import java.util.Arrays;
import java.util.List;

public class MilestoneTracker {

    private final List<Milestone> milestones = Arrays.asList(
        new Milestone(
            "Sunnyvale", 20,
            "You've reached Sunnyvale — home to tech giants like Yahoo and LinkedIn.\nYour team breathes in the valley air for the first time. The journey feels real now.",
            5000, 10, 0
        ),
        new Milestone(
            "Cupertino", 40,
            "You've reached Cupertino — Apple's home turf.\nThe pressure to innovate is palpable. Your team visits the Apple campus and leaves recharged.",
            0, 0, 15
        ),
        new Milestone(
            "Palo Alto", 60,
            "You've reached Palo Alto — Stanford territory and VC Row on Sand Hill Road.\nA passing investor glances at your pitch deck and hands you a check.",
            10000, 0, 0
        ),
        new Milestone(
            "Menlo Park", 80,
            "You've reached Menlo Park — Meta HQ and late-stage startup culture.\nSan Francisco is within sight. The team feels the finish line pulling them forward.",
            0, 15, 10
        )
    );

    private int nextMilestoneIndex = 0;

    public void check(GameState state) {
        while (nextMilestoneIndex < milestones.size() &&
               state.getProgress() >= milestones.get(nextMilestoneIndex).getProgressThreshold()) {

            Milestone milestone = milestones.get(nextMilestoneIndex);
            printMilestone(milestone);
            applyBonus(milestone, state);
            nextMilestoneIndex++;
        }
    }

    private void printMilestone(Milestone milestone) {
        System.out.println();
        System.out.println("*** MILESTONE REACHED: " + milestone.getCityName() + " ***");
        System.out.println(milestone.getStoryMoment());
        System.out.println();
        System.out.println("  Milestone Bonus:");
        if (milestone.getFundBonus() != 0)
            System.out.printf("    Fund    : +$%,d%n", milestone.getFundBonus());
        if (milestone.getMoraleBonus() != 0)
            System.out.println("    Morale  : +" + milestone.getMoraleBonus());
        if (milestone.getEnergyBonus() != 0)
            System.out.println("    Energy  : +" + milestone.getEnergyBonus());
    }

    private void applyBonus(Milestone milestone, GameState state) {
        state.applyFundDelta(milestone.getFundBonus());
        state.applyMoraleDelta(milestone.getMoraleBonus());
        state.applyEnergyDelta(milestone.getEnergyBonus());
    }
}
