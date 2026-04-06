package com.siliconvalleytrail.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TeamMember {
    private final String memberId;
    private final String name;
    private final String jobTitle;
}
