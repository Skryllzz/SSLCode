package com.zenyte.game.content.theatreofblood.boss.verzikvitur.npc;

import com.zenyte.game.content.theatreofblood.boss.verzikvitur.VerzikRoom;
import com.zenyte.game.content.theatreofblood.plugin.entity.TheatreNPC;
import com.zenyte.game.util.Direction;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.masks.UpdateFlag;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.NpcId;
import com.zenyte.game.world.entity.npc.combat.CombatScript;
import com.zenyte.game.world.entity.npc.combatdefs.NPCCDLoader;
import com.zenyte.game.world.entity.npc.combatdefs.NPCCombatDefinitions;
import com.zenyte.game.world.entity.player.Player;
import org.apache.commons.lang3.mutable.MutableInt;

public class VerzikVitur extends TheatreNPC<VerzikRoom> implements CombatScript {
    public static final Location VERZIK_SPAWN_LOCATION = new Location(3167, 4324, 0);
    private final MutableInt ticks = new MutableInt();
    private VerzikPhase phase;
    public boolean attackAble;
    public boolean walkAble;

    public VerzikVitur(VerzikRoom room) {
        super(room.getRaid(), room, NpcId.VERZIK_VITUR_8369, room.getVerzikSpawnLocation(), Direction.SOUTH);
    }

    @Override
    public NPC spawn() {
        super.spawn();
        if (phase == null) {
            World.removeObject(VerzikRoom.verzikObjectPlaceholder);
            setPhase(new VerzikDialoguePhase(this));
        }
        phase.onPhaseStart();
        return this;
    }

    @Override
    public void performDefenceAnimation(Entity attacker) {
    }

    @Override
    public void processNPC() {
        super.processNPC();
        if (hitpoints <= 0 || !attackAble) {
            blockIncomingHits();
        }
        if (phase != null) {
            phase = phase.process();
        }
        ticks.increment();
    }

    @Override
    public int getMaxHitpoints() {
        final var pS = raid.getParty().getSize();
        if (getPhase() == null) {
            return 0;
        } else {
            switch (getPhase().getOrdinal()) {
            case 2: 
            case 3: 
                if (getId() == 8371) {
                    return 1;
                }
            case 4: 
                return (int) (2000 * (pS == 1 ? 0.4 : pS == 2 ? 0.6 : pS == 3 ? 0.75 : pS == 4 ? 0.875 : 1));
            case 1: 
            default: 
                return 1;
            }
        }
    }

    @Override
    public void setTransformation(final int id) {
        nextTransformation = id;
        setId(id);
        if (id == NpcId.VERZIK_VITUR_8370 || id == NpcId.VERZIK_VITUR_8372 || id == NpcId.VERZIK_VITUR_8374) {
            setStats();
            final var cachedDefs = NPCCombatDefinitions.clone(id, NPCCDLoader.get(id));
            setCombatDefinitions(cachedDefs);
        }
        size = definitions.getSize();
        updateFlags.flag(UpdateFlag.TRANSFORMATION);
    }

    @Override
    public void setLocation(Location location) {
        super.setLocation(location);
        //setTeleported(true);
    }

    @Override
    public int attack(Entity target) {
        return 0;
    }

    @Override
    public double getMagicPrayerMultiplier() {
        return 0.5;
    }

    @Override
    public double getMeleePrayerMultiplier() {
        return 0.5;
    }

    @Override
    public double getRangedPrayerMultiplier() {
        return 0.5;
    }

    @Override
    public boolean addWalkStep(final int nextX, final int nextY, final int lastX, final int lastY, boolean check) {
        if (!walkAble) {
            return false;
        }
        return super.addWalkStep(nextX, nextY, lastX, lastY, check);
    }

    @Override
    protected void removeHitpoints(final Hit hit) {
        super.removeHitpoints(hit);
        room.refreshHealthBar(raid);
    }

    public boolean setHitpoints(final int amount) {
        final var dead = isDead();
        this.hitpoints = amount;
        return false;
    }

    @Override
    public void handleIngoingHit(Hit hit) {
        if (phase.getOrdinal() == 2) {
            hit.setHitType(HitType.VERZIK_SHIELD);
        } else {
            if (phase == null) {
                hit.setHitType(HitType.MISSED);
            }
        }
        if (phase.getOrdinal() == 3) {
            boolean siphon = room.getVerzik().getTemporaryAttributes().getOrDefault("siphon", false).equals(true);
            if (siphon) {
                hit.setHitType(HitType.HEALED);
            }
        }
        super.handleIngoingHit(hit);
    }

    @Override
    public void heal(final int amount) {
        super.heal(amount);
        room.refreshHealthBar(raid);
    }

    @Override
    public boolean canAttack(final Player source) {
        if (!attackAble) {
            return false;
        }
        return true;
    }

    @Override
    public void finish() {
        if (phase == null) {
            return;
        }
        for (final var p : room.getPlayers()) {
            if (!raid.getSpectators().contains(p.getUsername())) {
                if (p.getBooleanAttribute("PopItTask") && !p.getBooleanAttribute("master-combat-achievement55")) {
                    p.putBooleanAttribute("master-combat-achievement55", true);
                    //MasterTasks.sendMasterCompletion(p, 55);
                }
                if (p.getBooleanAttribute("PerfectVerzik") && !p.getBooleanAttribute("master-combat-achievement63")) {
                    p.putBooleanAttribute("master-combat-achievement63", true);
                    //MasterTasks.sendMasterCompletion(p, 63);
                }
            }
        }
        super.finish();
        if (getRaid().getParty().getTargetablePlayers().size() > 0) {
            room.onCompletion();
        }
    }

    public void spawnPillars() {
        room.getPillars().forEach((l, p) -> {
            World.spawnObject(p.getObject());
            p.spawn();
        });
    }

    public MutableInt getTicks() {
        return this.ticks;
    }

    public VerzikPhase getPhase() {
        return this.phase;
    }

    public void setPhase(final VerzikPhase phase) {
        this.phase = phase;
    }

    public boolean isAttackAble() {
        return this.attackAble;
    }

    public void setAttackAble(final boolean attackAble) {
        this.attackAble = attackAble;
    }

    public boolean isWalkAble() {
        return this.walkAble;
    }

    public void setWalkAble(final boolean walkAble) {
        this.walkAble = walkAble;
    }
}
