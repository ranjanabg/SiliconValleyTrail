package com.siliconvalleytrail.model;

import lombok.Getter;

@Getter
public class User {

    private final String userId;
    private final UserRole role;

    private User(String userId, UserRole role) {
        this.userId = userId;
        this.role = role;
    }

    public static User createNew(String userId) {
        return new User(userId, UserRole.FOUNDER);
    }
}
