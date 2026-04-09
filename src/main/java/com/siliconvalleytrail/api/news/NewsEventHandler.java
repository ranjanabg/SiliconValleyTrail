package com.siliconvalleytrail.api.news;

import com.siliconvalleytrail.api.ExternalEvent;
import com.siliconvalleytrail.api.ExternalEventHandler;
import com.siliconvalleytrail.game.GameState;

public class NewsEventHandler implements ExternalEventHandler {

    @Override
    public void execute(final ExternalEvent event, final GameState state) {
        if (hasNoImpact(event)) {
            return;
        }

        System.out.println("\n" +event.getEmoji() + "  " + event.getNarrative());

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

        System.out.println(deltas.toString().stripTrailing());

        state.applyMoraleDelta(event.getMoraleDelta());
        state.applyEnergyDelta(event.getEnergyDelta());
        state.applyProgressDelta(event.getProgressDelta());
        state.applyFundDelta(event.getFundDelta());
        state.applyHypeDelta(event.getHypeDelta());
        state.applyConnectionsDelta(event.getConnectionsDelta());
    }

    private boolean hasNoImpact(final ExternalEvent event) {
        return event.getMoraleDelta() == 0 && event.getEnergyDelta() == 0 &&
               event.getProgressDelta() == 0 && event.getFundDelta() == 0 &&
               event.getHypeDelta() == 0 && event.getConnectionsDelta() == 0;
    }
}
