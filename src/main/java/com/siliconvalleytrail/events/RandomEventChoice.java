package com.siliconvalleytrail.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RandomEventChoice {
    private final String label;
    private final String outcome;
    private final int fundDelta;
    private final int moraleDelta;
    private final int energyDelta;
    private final int progressDelta;
    private final int connectionsDelta;
    private final int hypeDelta;
    private final int techDebtDelta;
}
