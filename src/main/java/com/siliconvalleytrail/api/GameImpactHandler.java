package com.siliconvalleytrail.api;

import com.siliconvalleytrail.game.GameState;

public interface GameImpactHandler {
    void applyAndPrint(final GameImpact impact, final GameState state);
}
