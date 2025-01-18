package com.zenyte.game.content.kebos.alchemicalhydra;

import com.zenyte.game.content.kebos.alchemicalhydra.npc.combat.HydraPhaseSequence;
import com.zenyte.game.content.kebos.alchemicalhydra.npc.combat.phases.EnragedPhase;
import com.zenyte.game.content.kebos.alchemicalhydra.npc.combat.phases.FlamePhase;
import com.zenyte.game.content.kebos.alchemicalhydra.npc.combat.phases.LightningPhase;
import com.zenyte.game.content.kebos.alchemicalhydra.npc.combat.phases.PoisonPhase;
import com.zenyte.game.world.entity.masks.Animation;

/**
 * @author Tommeh | 02/11/2019 | 16:59
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public enum HydraPhase {
    POISON(PoisonPhase.class, -1, -1, 8615, null, null),
    LIGHTNING(LightningPhase.class, 3, 8616, 8619, new Animation(8237), new Animation(8238)),
    FLAME(FlamePhase.class, 2, 8617, 8620, new Animation(8244), new Animation(8245)),
    ENRAGED(EnragedPhase.class, 3, 8618, 8621, new Animation(8251), new Animation(8252));
    private static final HydraPhase[] values = values();
    private final Class<? extends HydraPhaseSequence> phaseSequence;
    private final int sequenceDelay;
    private final int preTransformation;
    private final int postTransformation;
    private final Animation preTransformationAnim;
    private final Animation postTransformationAnim;

    HydraPhase(final Class<? extends HydraPhaseSequence> phaseSequence, final int sequenceDelay, final int preTransformation, final int postTransformation, final Animation preTransformationAnim, final Animation postTransformationAnim) {
        this.phaseSequence = phaseSequence;
        this.sequenceDelay = sequenceDelay;
        this.preTransformation = preTransformation;
        this.postTransformation = postTransformation;
        this.preTransformationAnim = preTransformationAnim;
        this.postTransformationAnim = postTransformationAnim;
    }

    public HydraPhase next() {
        if (this.equals(ENRAGED)) {
            throw new IllegalArgumentException("Next phase doesn't exist for the last phase.");
        }
        return values[ordinal() + 1];
    }

    public HydraPhase previous() {
        if (this.equals(POISON)) {
            throw new IllegalArgumentException("Previous phase doesn't exist for the first phase.");
        }
        return values[ordinal() - 1];
    }

    public Class<? extends HydraPhaseSequence> getPhaseSequence() {
        return this.phaseSequence;
    }

    public int getSequenceDelay() {
        return this.sequenceDelay;
    }

    public int getPreTransformation() {
        return this.preTransformation;
    }

    public int getPostTransformation() {
        return this.postTransformation;
    }

    public Animation getPreTransformationAnim() {
        return this.preTransformationAnim;
    }

    public Animation getPostTransformationAnim() {
        return this.postTransformationAnim;
    }
}
