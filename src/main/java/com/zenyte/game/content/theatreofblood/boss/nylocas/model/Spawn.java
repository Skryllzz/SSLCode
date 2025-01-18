package com.zenyte.game.content.theatreofblood.boss.nylocas.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author Tommeh | 6/12/2020 | 7:33 PM
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class Spawn {
    public static int globalIndex;
    private int index;
    private NylocasType type;
    private boolean aggressive;
    private boolean large;
    private PillarLocation target;
    private List<NylocasType> transformations;

    public Spawn(final Spawn spawn) {
        index = spawn.index;
        type = spawn.type;
        aggressive = spawn.aggressive;
        large = spawn.large;
        target = spawn.target;
        if (spawn.transformations != null) {
            transformations = new ArrayList<>(spawn.transformations);
        }
    }

    public static Spawn of(final NylocasType type) {
        final var spawn = new Spawn();
        spawn.index = globalIndex++;
        spawn.type = type;
        return spawn;
    }

    public Spawn large() {
        this.large = true;
        return this;
    }

    public Spawn aggressive() {
        this.aggressive = true;
        return this;
    }

    public Spawn target(final PillarLocation location) {
        this.target = location;
        return this;
    }

    public Spawn transformations(final NylocasType... transformations) {
        this.transformations = new ArrayList<>(Arrays.asList(transformations));
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(index);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Spawn)) {
            return false;
        }
        final var spawn = (Spawn) obj;
        return spawn.index == index;
    }

    public int getIndex() {
        return this.index;
    }

    public NylocasType getType() {
        return this.type;
    }

    public boolean isAggressive() {
        return this.aggressive;
    }

    public boolean isLarge() {
        return this.large;
    }

    public PillarLocation getTarget() {
        return this.target;
    }

    public List<NylocasType> getTransformations() {
        return this.transformations;
    }

    public Spawn() {
    }

    public void setIndex(final int index) {
        this.index = index;
    }
}
