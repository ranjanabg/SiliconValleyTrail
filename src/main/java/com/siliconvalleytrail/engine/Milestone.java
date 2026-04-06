package com.siliconvalleytrail.engine;

public class Milestone {

    private final String cityName;
    private final int progressThreshold;
    private final String storyMoment;
    private final int fundBonus;
    private final int moraleBonus;
    private final int energyBonus;

    public Milestone(String cityName, int progressThreshold, String storyMoment,
                     int fundBonus, int moraleBonus, int energyBonus) {
        this.cityName = cityName;
        this.progressThreshold = progressThreshold;
        this.storyMoment = storyMoment;
        this.fundBonus = fundBonus;
        this.moraleBonus = moraleBonus;
        this.energyBonus = energyBonus;
    }

    public String getCityName() { return cityName; }
    public int getProgressThreshold() { return progressThreshold; }
    public String getStoryMoment() { return storyMoment; }
    public int getFundBonus() { return fundBonus; }
    public int getMoraleBonus() { return moraleBonus; }
    public int getEnergyBonus() { return energyBonus; }
}
