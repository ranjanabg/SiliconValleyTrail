package com.siliconvalleytrail.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiEffect {
    private final String emoji;
    private final String narrative;
    private final int moraleDelta;
    private final int energyDelta;
    private final int progressDelta;
    private final int fundDelta;
    private final int hypeDelta;
    private final int connectionsDelta;
}
