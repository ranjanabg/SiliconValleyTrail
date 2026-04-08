package com.siliconvalleytrail.core;

import com.siliconvalleytrail.model.ApiEffect;
import com.siliconvalleytrail.model.GameState;

public class WeatherEffectHandler implements ApiEffectHandler {

    @Override
    public void applyAndPrint(ApiEffect effect, GameState state) {
        System.out.println();
        System.out.println(effect.getEmoji() + "  " + effect.getNarrative());

        String deltaLine = buildDeltaLine(effect);
        if (!deltaLine.isBlank()) System.out.println(deltaLine);

        applyToState(effect, state);
    }

    private String buildDeltaLine(ApiEffect effect) {
        StringBuilder deltas = new StringBuilder("     ");
        if (effect.getMoraleDelta() != 0)      deltas.append(String.format("Morale: %+d  ", effect.getMoraleDelta()));
        if (effect.getEnergyDelta() != 0)      deltas.append(String.format("Energy: %+d  ", effect.getEnergyDelta()));
        if (effect.getProgressDelta() != 0)    deltas.append(String.format("Progress: %+d%%  ", effect.getProgressDelta()));
        if (effect.getFundDelta() != 0)        deltas.append(String.format("Fund: $%,d  ", effect.getFundDelta()));
        if (effect.getHypeDelta() != 0)        deltas.append(String.format("Hype: %+d  ", effect.getHypeDelta()));
        if (effect.getConnectionsDelta() != 0) deltas.append(String.format("Connections: %+d  ", effect.getConnectionsDelta()));
        return deltas.toString().stripTrailing();
    }

    private void applyToState(ApiEffect effect, GameState state) {
        state.applyMoraleDelta(effect.getMoraleDelta());
        state.applyEnergyDelta(effect.getEnergyDelta());
        state.applyProgressDelta(effect.getProgressDelta());
        state.applyFundDelta(effect.getFundDelta());
        state.applyHypeDelta(effect.getHypeDelta());
        state.applyConnectionsDelta(effect.getConnectionsDelta());
    }
}
