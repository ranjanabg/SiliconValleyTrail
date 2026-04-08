package com.siliconvalleytrail.core;

import com.siliconvalleytrail.model.ApiEffect;
import com.siliconvalleytrail.model.GameState;

public class NewsEffectHandler implements ApiEffectHandler {

    @Override
    public void applyAndPrint(ApiEffect effect, GameState state) {
        if (hasNoImpact(effect)) return;

        System.out.println();
        System.out.println(effect.getEmoji() + "  " + effect.getNarrative());

        StringBuilder deltas = new StringBuilder("     ");
        if (effect.getMoraleDelta() != 0)      deltas.append(String.format("Morale: %+d  ", effect.getMoraleDelta()));
        if (effect.getEnergyDelta() != 0)      deltas.append(String.format("Energy: %+d  ", effect.getEnergyDelta()));
        if (effect.getProgressDelta() != 0)    deltas.append(String.format("Progress: %+d%%  ", effect.getProgressDelta()));
        if (effect.getFundDelta() != 0)        deltas.append(String.format("Fund: $%,d  ", effect.getFundDelta()));
        if (effect.getHypeDelta() != 0)        deltas.append(String.format("Hype: %+d  ", effect.getHypeDelta()));
        if (effect.getConnectionsDelta() != 0) deltas.append(String.format("Connections: %+d  ", effect.getConnectionsDelta()));
        System.out.println(deltas.toString().stripTrailing());

        state.applyMoraleDelta(effect.getMoraleDelta());
        state.applyEnergyDelta(effect.getEnergyDelta());
        state.applyProgressDelta(effect.getProgressDelta());
        state.applyFundDelta(effect.getFundDelta());
        state.applyHypeDelta(effect.getHypeDelta());
        state.applyConnectionsDelta(effect.getConnectionsDelta());
    }

    private boolean hasNoImpact(ApiEffect effect) {
        return effect.getMoraleDelta() == 0 && effect.getEnergyDelta() == 0 &&
               effect.getProgressDelta() == 0 && effect.getFundDelta() == 0 &&
               effect.getHypeDelta() == 0 && effect.getConnectionsDelta() == 0;
    }
}
