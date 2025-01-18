package com.zenyte.game.content.theatreofblood.boss.verzikvitur.npc;

import org.apache.commons.lang3.mutable.MutableInt;

public abstract class VerzikPhase {
    protected final VerzikVitur verzik;
    protected final MutableInt ticks = new MutableInt();
    protected final int ordinal;

    public VerzikPhase process() {
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

    protected void resetTicks() {
        this.ticks.setValue(0);
    }

    public abstract void onPhaseStart();

    public abstract void onTick();

    public abstract boolean isPhaseComplete();

    public abstract VerzikPhase advance();

    public int getOrdinal() {
        return this.ordinal;
    }

    public VerzikPhase(final VerzikVitur verzik, final int ordinal) {
        this.verzik = verzik;
        this.ordinal = ordinal;
    }
}
