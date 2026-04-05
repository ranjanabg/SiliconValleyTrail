package com.siliconvalleytrail.engine;

public class Choice {

    private final String description;
    private final int fundDelta;
    private final int moraleDelta;
    private final int energyDelta;
    private final int progressDelta;

    public Choice(String description, int fundDelta, int moraleDelta, int energyDelta, int progressDelta) {
        this.description = description;
        this.fundDelta = fundDelta;
        this.moraleDelta = moraleDelta;
        this.energyDelta = energyDelta;
        this.progressDelta = progressDelta;
    }

    public String getDescription() { return description; }
    public int getFundDelta() { return fundDelta; }
    public int getMoraleDelta() { return moraleDelta; }
    public int getEnergyDelta() { return energyDelta; }
    public int getProgressDelta() { return progressDelta; }
}
