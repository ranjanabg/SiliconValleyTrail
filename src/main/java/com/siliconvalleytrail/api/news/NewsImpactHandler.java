package com.siliconvalleytrail.api.news;

import com.siliconvalleytrail.api.GameImpact;
import com.siliconvalleytrail.api.GameImpactHandler;
import com.siliconvalleytrail.game.GameState;

public class NewsImpactHandler implements GameImpactHandler {

    @Override
    public void applyAndPrint(final GameImpact impact, final GameState state) {
        if (hasNoImpact(impact)) {
            return;
        }

        System.out.println("\n" +impact.getEmoji() + "  " + impact.getNarrative());

        StringBuilder deltas = new StringBuilder("     ");
        if (impact.getMoraleDelta() != 0) {
            deltas.append(String.format("Morale: %+d  ", impact.getMoraleDelta()));
        }
        if (impact.getEnergyDelta() != 0) {
            deltas.append(String.format("Energy: %+d  ", impact.getEnergyDelta()));
        }
        if (impact.getProgressDelta() != 0) {
            deltas.append(String.format("Progress: %+d%%  ", impact.getProgressDelta()));
        }
        if (impact.getFundDelta() != 0) {
            deltas.append(String.format("Fund: $%,d  ", impact.getFundDelta()));
        }
        if (impact.getHypeDelta() != 0) {
            deltas.append(String.format("Hype: %+d  ", impact.getHypeDelta()));
        }
        if (impact.getConnectionsDelta() != 0) {
            deltas.append(String.format("Connections: %+d  ", impact.getConnectionsDelta()));
        }

        System.out.println(deltas.toString().stripTrailing());

        state.applyMoraleDelta(impact.getMoraleDelta());
        state.applyEnergyDelta(impact.getEnergyDelta());
        state.applyProgressDelta(impact.getProgressDelta());
        state.applyFundDelta(impact.getFundDelta());
        state.applyHypeDelta(impact.getHypeDelta());
        state.applyConnectionsDelta(impact.getConnectionsDelta());
    }

    private boolean hasNoImpact(final GameImpact impact) {
        return impact.getMoraleDelta() == 0 && impact.getEnergyDelta() == 0 &&
               impact.getProgressDelta() == 0 && impact.getFundDelta() == 0 &&
               impact.getHypeDelta() == 0 && impact.getConnectionsDelta() == 0;
    }
}
