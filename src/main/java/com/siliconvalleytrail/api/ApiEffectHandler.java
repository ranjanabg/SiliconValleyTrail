package com.siliconvalleytrail.api;

import com.siliconvalleytrail.game.GameState;

public interface ApiEffectHandler {
    void applyAndPrint(ApiEffect effect, GameState state);
}
