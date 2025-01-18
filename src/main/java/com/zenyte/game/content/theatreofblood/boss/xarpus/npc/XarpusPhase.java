package com.zenyte.game.content.theatreofblood.boss.xarpus.npc;

import org.apache.commons.lang3.mutable.MutableInt;

/**
 * @author Chris
 * @since August 25 2020
 */
public abstract class XarpusPhase {
    protected final Xarpus xarpus;
    protected final MutableInt ticks = new MutableInt();

    public XarpusPhase process() {
        if (isPhaseComplete()) {
            final var nextPhase = advance();
            if (nextPhase != null) {
                nextPhase.onPhaseStart();
                nextPhase.onTick();
                return nextPhase;
            }
        }
        onTick();
        ticks.increment();
        return this;
    }

    abstract void onPhaseStart();

    abstract void onTick();

    abstract boolean isPhaseComplete();

    abstract XarpusPhase advance();

    public XarpusPhase(final Xarpus xarpus) {
        this.xarpus = xarpus;
    }
}
