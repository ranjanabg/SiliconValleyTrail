package com.siliconvalleytrail.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RandomEvent {
    private final String description;
    private final RandomEventChoice choiceA;
    private final RandomEventChoice choiceB;
}
