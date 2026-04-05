package com.siliconvalleytrail.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class User {

    private final String userId;
    private final UserRole role;
    private final List<TeamMember> team;
    private final String createdAt;

    private User(String userId, UserRole role, List<TeamMember> team, String createdAt) {
        this.userId = userId;
        this.role = role;
        this.team = team;
        this.createdAt = createdAt;
    }

    public static User createNew(String userId) {
        return new User(userId, UserRole.FOUNDER, new ArrayList<>(), Instant.now().toString());
    }

    public String getUserId() { return userId; }
    public UserRole getRole() { return role; }
    public List<TeamMember> getTeam() { return team; }
    public String getCreatedAt() { return createdAt; }
}
