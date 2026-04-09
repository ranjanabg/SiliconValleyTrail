package com.siliconvalleytrail.milestone;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Milestone {
    private final String cityName;
    private final int progressThreshold;
    private final String storyMoment;
    private final int fundBonus;
    private final int moraleBonus;
    private final int energyBonus;
    private final int connectionsBonus;
    private final int hypeBonus;
}
