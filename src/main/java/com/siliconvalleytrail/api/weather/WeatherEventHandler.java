package com.siliconvalleytrail.api.weather;

import com.siliconvalleytrail.api.ExternalEvent;
import com.siliconvalleytrail.api.ExternalEventHandler;
import com.siliconvalleytrail.game.GameState;

public class WeatherEventHandler implements ExternalEventHandler {

    @Override
    public void execute(final ExternalEvent event, final GameState state) {
        System.out.println("\n" + event.getEmoji() + "  " + event.getNarrative());

        final String deltaLine = buildDeltaLine(event);
        if (!deltaLine.isBlank()) {
            System.out.println(deltaLine);
        }

        applyToState(event, state);
    }

    private String buildDeltaLine(final ExternalEvent event) {
        StringBuilder deltas = new StringBuilder("     ");
        if (event.getMoraleDelta() != 0) {
            deltas.append(String.format("Morale: %+d  ", event.getMoraleDelta()));
        }
        if (event.getEnergyDelta() != 0) {
            deltas.append(String.format("Energy: %+d  ", event.getEnergyDelta()));
        }
        if (event.getProgressDelta() != 0) {
            deltas.append(String.format("Progress: %+d%%  ", event.getProgressDelta()));
        }
        if (event.getFundDelta() != 0) {
            deltas.append(String.format("Fund: $%,d  ", event.getFundDelta()));
        }
        if (event.getHypeDelta() != 0) {
            deltas.append(String.format("Hype: %+d  ", event.getHypeDelta()));
        }
        if (event.getConnectionsDelta() != 0) {
            deltas.append(String.format("Connections: %+d  ", event.getConnectionsDelta()));
        }
        return deltas.toString().stripTrailing();
    }

    private void applyToState(final ExternalEvent event, final GameState state) {
        state.applyMoraleDelta(event.getMoraleDelta());
        state.applyEnergyDelta(event.getEnergyDelta());
        state.applyProgressDelta(event.getProgressDelta());
        state.applyFundDelta(event.getFundDelta());
        state.applyHypeDelta(event.getHypeDelta());
        state.applyConnectionsDelta(event.getConnectionsDelta());
    }
}
