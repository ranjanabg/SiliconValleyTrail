package com.siliconvalleytrail.core;

import com.siliconvalleytrail.model.GameState;
import com.siliconvalleytrail.model.Milestone;

import java.util.Arrays;
import java.util.List;

public class MilestoneTracker {

    private final List<Milestone> milestones = Arrays.asList(
        new Milestone(
            "Santa Clara", 9,
            "You've reached Santa Clara — home to Intel, NVIDIA, and AMD.\nThe hardware roots of Silicon Valley surround you. Your team feels the weight of the giants who built this place.",
            3000, 0, 5, 0, 0
        ),
        new Milestone(
            "Sunnyvale", 18,
            "You've reached Sunnyvale — home to tech giants like Yahoo and LinkedIn.\nYour team breathes in the valley air for the first time. The journey feels real now.",
            5000, 10, 0, 0, 0
        ),
        new Milestone(
            "Mountain View", 27,
            "You've reached Mountain View — Google's hometown.\nThe Googleplex stretches out beside the road. Its sheer scale is humbling. One teammate whispers: 'We could be that big someday.'",
            0, 5, 0, 0, 10
        ),
        new Milestone(
            "Cupertino", 36,
            "You've reached Cupertino — Apple's home turf.\nThe pressure to innovate is palpable. Your team visits the Apple campus and leaves recharged.",
            0, 0, 15, 0, 0
        ),
        new Milestone(
            "Los Altos", 45,
            "You've reached Los Altos — where HP was founded in a garage.\nYour team stands in front of that very garage. Every billion-dollar company started somewhere small. This is your reminder.",
            0, 10, 0, 5, 0
        ),
        new Milestone(
            "Palo Alto", 54,
            "You've reached Palo Alto — Stanford territory and VC Row on Sand Hill Road.\nA passing investor glances at your pitch deck and hands you a check.",
            10000, 0, 0, 0, 0
        ),
        new Milestone(
            "Menlo Park", 63,
            "You've reached Menlo Park — Meta HQ and late-stage startup culture.\nSan Francisco is within sight. The team feels the finish line pulling them forward.",
            0, 15, 10, 0, 0
        ),
        new Milestone(
            "Redwood City", 72,
            "You've reached Redwood City — Oracle territory and home to Electronic Arts.\nEnterprise culture hangs in the air. A B2B founder stops to chat and introduces you to three new contacts.",
            5000, 0, 0, 10, 0
        ),
        new Milestone(
            "San Mateo", 81,
            "You've reached San Mateo — a quieter corner of the valley but full of grit.\nA founder at a local coffee shop shares hard-won advice. The team leaves feeling more grounded than inspired.",
            0, 10, 0, 8, 0
        ),
        new Milestone(
            "Burlingame", 90,
            "You've reached Burlingame — the final stretch before San Francisco.\nThe city skyline is visible on the horizon. The team goes quiet, each lost in thought. You're almost there.",
            0, 10, 0, 0, 15
        )
    );

    private int nextMilestoneIndex = 0;

    public void reset() {
        nextMilestoneIndex = 0;
    }

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
        System.out.println("📍 MILESTONE REACHED: " + milestone.getCityName());
        System.out.println(milestone.getStoryMoment());
        System.out.println();
        System.out.println("  Milestone Bonus:");
        if (milestone.getFundBonus() != 0)
            System.out.printf("    Fund        : +$%,d%n", milestone.getFundBonus());
        if (milestone.getMoraleBonus() != 0)
            System.out.println("    Morale      : +" + milestone.getMoraleBonus());
        if (milestone.getEnergyBonus() != 0)
            System.out.println("    Energy      : +" + milestone.getEnergyBonus());
        if (milestone.getConnectionsBonus() != 0)
            System.out.println("    Connections : +" + milestone.getConnectionsBonus());
        if (milestone.getHypeBonus() != 0)
            System.out.println("    Hype        : +" + milestone.getHypeBonus());
    }

    private void applyBonus(Milestone milestone, GameState state) {
        state.applyFundDelta(milestone.getFundBonus());
        state.applyMoraleDelta(milestone.getMoraleBonus());
        state.applyEnergyDelta(milestone.getEnergyBonus());
        state.applyConnectionsDelta(milestone.getConnectionsBonus());
        state.applyHypeDelta(milestone.getHypeBonus());
    }
}
