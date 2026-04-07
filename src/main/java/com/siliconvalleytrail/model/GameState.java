package com.siliconvalleytrail.model;

import lombok.Getter;

@Getter
public class GameState {

    private int day = 1;
    private int fund = 50000;
    private int morale = 100;
    private int energy = 100;
    private int progress = 0;
    private int connections = 10;
    private int hype = 10;
    private int techDebt = 0;
    private int lastInvestorMeetingDay = -10;
    private int nextMilestoneIndex = 0;
    private boolean gameOver = false;

    public void applyFundDelta(int delta) { fund = Math.max(0, fund + delta); }
    public void applyMoraleDelta(int delta) { morale = clamp(morale + delta, 0, 100); }
    public void applyEnergyDelta(int delta) { energy = clamp(energy + delta, 0, 100); }
    public void applyProgressDelta(int delta) { progress = clamp(progress + delta, 0, 100); }
    public void applyConnectionsDelta(int delta) { connections = clamp(connections + delta, 0, 100); }
    public void applyHypeDelta(int delta) { hype = clamp(hype + delta, 0, 100); }
    public void applyTechDebtDelta(int delta) { techDebt = clamp(techDebt + delta, 0, 100); }

    public void advanceDay() { day++; }
    public void endGame() { gameOver = true; }
    public void recordInvestorMeeting() { lastInvestorMeetingDay = day; }
    public void advanceMilestone() { nextMilestoneIndex++; }

    public void repairMissingFields() {
        if (lastInvestorMeetingDay == 0) lastInvestorMeetingDay = -10;
    }

    public void reset() {
        day = 1;
        fund = 50000;
        morale = 100;
        energy = 100;
        progress = 0;
        connections = 10;
        hype = 10;
        techDebt = 0;
        lastInvestorMeetingDay = -10;
        nextMilestoneIndex = 0;
        gameOver = false;
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
