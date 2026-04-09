package com.siliconvalleytrail.api.news;

import com.siliconvalleytrail.api.ExternalEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the mock path (no NEWS_API_KEY set).
 * MOCK_TRIGGER_CHANCE = 0.25, so fetch() returns null ~75% of the time.
 * Tests verify non-null results are valid and that the mock triggers eventually.
 */
class NewsApiClientTest {

    private NewsApiClient client;

    @BeforeEach
    void setUp() {
        client = new NewsApiClient();
    }

    @Test
    void fetch_returnsNullOrValidEvent() {
        ExternalEvent event = client.fetch();
        if (event != null) {
            assertFalse(event.getEmoji().isBlank());
            assertFalse(event.getNarrative().isBlank());
        }
    }

    @Test
    void fetch_eventuallyReturnsNonNullEvent() {
        // P(all null in 50 calls) = 0.75^50 ≈ 1.3e-6 — statistically reliable
        boolean gotNonNull = false;
        for (int i = 0; i < 50; i++) {
            if (client.fetch() != null) {
                gotNonNull = true;
                break;
            }
        }
        assertTrue(gotNonNull, "Expected at least one non-null event in 50 fetches");
    }

    @Test
    void fetch_nonNullEventHasNonEmptyEmoji() {
        ExternalEvent event = getNonNullEvent();
        assertFalse(event.getEmoji().isBlank());
    }

    @Test
    void fetch_nonNullEventHasNonEmptyNarrative() {
        ExternalEvent event = getNonNullEvent();
        assertFalse(event.getNarrative().isBlank());
    }

    @Test
    void fetch_doesNotRepeatConsecutiveEvent() {
        // Run many fetches and collect non-null events — no two consecutive should be the same
        ExternalEvent prev = null;
        for (int i = 0; i < 50; i++) {
            ExternalEvent current = client.fetch();
            if (current != null && prev != null) {
                assertNotSame(prev, current, "Consecutive non-null events should not be the same instance");
            }
            if (current != null) prev = current;
        }
    }

    private ExternalEvent getNonNullEvent() {
        for (int i = 0; i < 100; i++) {
            ExternalEvent event = client.fetch();
            if (event != null) return event;
        }
        fail("Expected a non-null event within 100 fetches");
        return null;
    }
}
