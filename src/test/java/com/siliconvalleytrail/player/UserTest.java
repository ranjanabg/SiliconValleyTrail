package com.siliconvalleytrail.player;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void createNewStoresUserId() {
        final User user = User.createNew("Ranjana");
        assertEquals("Ranjana", user.getUserId());
    }

    @Test
    void createNewAssignsFounderRole() {
        final User user = User.createNew("Ranjana");
        assertEquals(UserRole.FOUNDER, user.getRole());
    }

    @Test
    void createNewWithDifferentNames() {
        assertEquals("Alice", User.createNew("Alice").getUserId());
        assertEquals("Bob", User.createNew("Bob").getUserId());
    }
}
