package com.siliconvalleytrail.api;

import com.siliconvalleytrail.game.GameState;

public interface ExternalEventHandler {
    void execute(final ExternalEvent event, final GameState state);
}
