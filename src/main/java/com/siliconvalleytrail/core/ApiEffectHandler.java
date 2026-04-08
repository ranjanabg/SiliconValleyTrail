package com.siliconvalleytrail.core;

import com.siliconvalleytrail.model.ApiEffect;
import com.siliconvalleytrail.model.GameState;

public interface ApiEffectHandler {
    void applyAndPrint(ApiEffect effect, GameState state);
}
