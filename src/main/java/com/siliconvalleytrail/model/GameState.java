package com.siliconvalleytrail.model;

import lombok.Getter;

@Getter
public class GameState {

    private int day = 1;
    private int fund = 80000;
    private int morale = 100;
    private int energy = 100;
    private int progress = 0;
    private boolean gameOver = false;

    public void applyFundDelta(int delta) { fund += delta; }
    public void applyMoraleDelta(int delta) { morale = clamp(morale + delta, 0, 100); }
    public void applyEnergyDelta(int delta) { energy = clamp(energy + delta, 0, 100); }
    public void applyProgressDelta(int delta) { progress = clamp(progress + delta, 0, 100); }

    public void advanceDay() { day++; }
    public void endGame() { gameOver = true; }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
