package com.siliconvalleytrail.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RandomEvent {
    private final String description;
    private final int fundDelta;
    private final int moraleDelta;
    private final int energyDelta;
    private final int progressDelta;
}
