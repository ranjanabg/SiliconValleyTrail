package com.siliconvalleytrail.api.weather;

import com.siliconvalleytrail.api.GameImpact;
import com.siliconvalleytrail.api.GameImpactHandler;
import com.siliconvalleytrail.game.GameState;

public class WeatherImpactHandler implements GameImpactHandler {

    @Override
    public void applyAndPrint(final GameImpact impact, final GameState state) {
        System.out.println("\n" + impact.getEmoji() + "  " + impact.getNarrative());

        final String deltaLine = buildDeltaLine(impact);
        if (!deltaLine.isBlank()) {
            System.out.println(deltaLine);
        }

        applyToState(impact, state);
    }

    private String buildDeltaLine(final GameImpact impact) {
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
        return deltas.toString().stripTrailing();
    }

    private void applyToState(final GameImpact impact, final GameState state) {
        state.applyMoraleDelta(impact.getMoraleDelta());
        state.applyEnergyDelta(impact.getEnergyDelta());
        state.applyProgressDelta(impact.getProgressDelta());
        state.applyFundDelta(impact.getFundDelta());
        state.applyHypeDelta(impact.getHypeDelta());
        state.applyConnectionsDelta(impact.getConnectionsDelta());
    }
}
