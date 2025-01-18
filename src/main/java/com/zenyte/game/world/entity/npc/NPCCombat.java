package com.zenyte.game.world.entity.npc;

import com.zenyte.game.util.Utils;
import com.zenyte.game.world.Position;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.pathfinding.events.npc.NPCCollidingEvent;
import com.zenyte.game.world.entity.pathfinding.strategy.EntityStrategy;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.region.CharacterLoop;

public class NPCCombat {
    protected NPC npc;
    protected int combatDelay;
    protected Entity target;
    private boolean forceRetaliate;

    public NPCCombat(final NPC npc) {
        this.npc = npc;
    }

    public boolean process() {
        if (combatDelay > 0) {
            combatDelay--;
        }
        if (target == null) {
            return false;
        }
        if (!checkAll()) {
            removeTarget();
            return false;
        }
        if (!npc.isFacing(target)) {
            npc.setFaceEntity(target);
        }
        if (combatDelay <= 0) {
            combatDelay = combatAttack();
        }
        return true;
    }

    public int combatAttack() {
        if (target == null) {
            return 0;
        }
        final boolean melee = npc.getCombatDefinitions().isMelee();
        if (npc.isProjectileClipped(target, melee)) {
            return 0;
        }
        int distance = melee || npc.isForceFollowClose() ? 0 : npc.getAttackDistance();
        if (Utils.collides(npc.getX(), npc.getY(), npc.getSize(), target.getX(), target.getY(), target.getSize())) {
            return 0;
        }
        if (outOfRange(target, distance, target.getSize(), melee)) {
            return 0;
        }
        addAttackedByDelay(target);
        return CombatScriptsHandler.specialAttack(npc, target);
    }

    public void removeTarget() {
        if (target == null) {
            return;
        }
        target = null;
        npc.setFaceEntity(null);
    }

    public void reset() {
        combatDelay = 0;
        removeTarget();
    }

    public boolean underCombat() {
        return target != null;
    }

    public void forceTarget(final Entity target) {
        if (target instanceof Player) {
            //Make every other creature within viewport radius of the player unaggressive towards the player, as this monster needs to take aggression over.
            CharacterLoop.forEach(target.getLocation(), 15, NPC.class, n -> {
                NPCCombat combat = n.getCombat();
                Entity t = combat.getTarget();
                if (t == target) {
                    combat.setTarget(null);
                }
            });
        }
        npc.getCombat().reset();
        if (target instanceof NPC) {
            ((NPC) target).getCombat().reset();
        }
        npc.setAttackedBy(null);
        target.setAttackedBy(null);
        npc.setAttackedByDelay(0);
        target.setAttackedByDelay(0);
        npc.setAttackingDelay(0);
        target.setAttackingDelay(0);
        setTarget(target);
    }

    protected boolean checkAll() {
        if (target == null || target.isFinished() || target.isNulled() || target.isDead() || npc.isDead() || npc.isFinished() || npc.isLocked() || npc.getPlane() != target.getPlane()) {
            return false;
        }
        if (npc.getRetreatMechanics().process(target)) {
            return false;
        }
        /*if (retreat()) {
            npc.forceWalkRespawnTile();
            return false;
        }*/
        if (!attackable()) {
            return false;
        }
        if (npc.isMovementRestricted()) {
            return true;
        }
        if (colliding()) {
            //TODO: Change into a more efficent pathfinding formula or write a non-pf structure.
            npc.setRouteEvent(new NPCCollidingEvent(npc, new EntityStrategy(target)));
            return true;
        }
        return appendMovement();
    }

    private boolean retreat() {
        final Location location = npc.getLocation();
        final Location respawn = npc.getRespawnTile();
        final int dx = location.getX() - respawn.getX();
        final int dy = location.getY() - respawn.getY();
        final int size = npc.getSize();
        final int distance = npc.getMaxDistance();
        return dx > size + distance || dx < -1 - distance || dy > size + distance || dy < -1 - distance;
    }

    protected boolean attackable() {
        return target.isMultiArea() || (npc.canAttackInSingleZone(target) && target.canAttackInSingleZone(npc));
    }

    protected boolean colliding() {
        return Utils.collides(npc.getX(), npc.getY(), npc.getSize(), target.getX(), target.getY(), target.getSize());
    }

    protected boolean appendMovement() {
        final boolean melee = npc.getCombatDefinitions().isMelee();
        final int maxDistance = npc.isForceFollowClose() || melee ? 0 : npc.getAttackDistance();
        if (npc.isProjectileClipped(target, npc.isForceFollowClose() || melee) || outOfRange(target, maxDistance, target.getSize(), melee)) {
            npc.resetWalkSteps();
            npc.calcFollow(target, npc.isRun() ? 2 : 1, true, npc.isIntelligent(), npc.isEntityClipped());
        }
        return true;
    }

    public boolean outOfRange(final Position targetPosition, final int maximumDistance, final int targetSize, final boolean checkDiagonal) {
        final Location target = targetPosition.getPosition();
        final int distanceX = npc.getX() - target.getX();
        final int distanceY = npc.getY() - target.getY();
        final int npcSize = npc.getSize();
        if (checkDiagonal) {
            if (distanceX == -npcSize && distanceY == -npcSize || distanceX == targetSize && distanceY == targetSize || distanceX == -npcSize && distanceY == targetSize || distanceX == targetSize && distanceY == -npcSize) {
                return true;
            }
        }
        return distanceX > targetSize + maximumDistance || distanceY > targetSize + maximumDistance || distanceX < -npcSize - maximumDistance || distanceY < -npcSize - maximumDistance;
    }

    public void addAttackedByDelay(final Entity target) {
        target.setAttackedBy(npc);
        target.setAttackedByDelay(Utils.currentTimeMillis() + 4800);
        npc.setAttackingDelay(Utils.currentTimeMillis() + 4800);
    }

    public NPC getNpc() {
        return this.npc;
    }

    public int getCombatDelay() {
        return this.combatDelay;
    }

    public void setCombatDelay(final int combatDelay) {
        this.combatDelay = combatDelay;
    }

    public Entity getTarget() {
        return this.target;
    }

    public void setTarget(final Entity target) {
        this.target = target;
        npc.setFaceEntity(target);
        if (!checkAll()) {
            removeTarget();
            return;
        }
        if (target == null) {
            return;
        }
        npc.resetWalkSteps();
        target.setAttackedBy(npc);
        npc.setRandomWalkDelay(1);
        target.setFindTargetDelay(Utils.currentTimeMillis() + 5000);
    }

    public boolean isForceRetaliate() {
        return this.forceRetaliate;
    }

    public void setForceRetaliate(final boolean forceRetaliate) {
        this.forceRetaliate = forceRetaliate;
    }
}
