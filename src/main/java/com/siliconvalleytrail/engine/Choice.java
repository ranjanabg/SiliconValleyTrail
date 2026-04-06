package com.siliconvalleytrail.engine;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Choice {
    private final String description;
    private final int fundDelta;
    private final int moraleDelta;
    private final int energyDelta;
    private final int progressDelta;
}
