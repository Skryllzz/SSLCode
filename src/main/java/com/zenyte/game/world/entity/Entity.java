package com.zenyte.game.world.entity;

import com.google.gson.annotations.Expose;
import com.zenyte.Game;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.TimeUnit;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.Position;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.masks.*;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.pathfinding.events.RouteEvent;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.action.combat.CombatType;
import com.zenyte.game.world.entity.player.action.combat.CombatUtilities;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.game.world.region.CharacterLoop;
import com.zenyte.game.world.region.DynamicRegion;
import com.zenyte.utils.IntLinkedList;
import com.zenyte.utils.ProjectileUtils;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import mgi.types.config.ObjectDefinitions;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class Entity implements Position, CharacterLoop {
    public static final int[] MAP_SIZES = {104, 120, 136, 168, 72};
    private static final Logger log = LoggerFactory.getLogger(Entity.class);
    private static final long TWENTY_MINUTES_IN_MILLIS = TimeUnit.MINUTES.toMillis(20);
    private final transient IntList lastMapRegionsIds = new IntArrayList();
    protected final transient Object2ObjectMap<String, List<IntLongPair>> receivedDamage = new Object2ObjectOpenHashMap<>();
    private final transient int[] lastSteps = new int[2];
    private final transient long[] immunities = new long[HitType.values.length];
    @Expose
    protected Toxins toxins = new Toxins(this);
    @Expose
    protected Location location;
    protected transient HitEntryList scheduledHits = new HitEntryList();
    protected transient RouteEvent<?, ?> routeEvent;
    /**
     * A delay that's referenced in {@link CombatUtilities#processHit} - if this value is above the current time in milliseconds, the hit will
     * not be processed - it's used to prevent entities from being hit when insta-leaving areas like raids.
     */
    protected transient long protectionDelay;
    protected transient Location middleTile;
    @Expose
    protected transient int hitpoints;
    protected transient int direction;
    protected transient int lastMovementType;
    protected transient boolean teleported;
    protected transient Location lastLocation;
    protected transient Location nextLocation;
    protected transient long lastAnimation;
    protected transient IntList mapRegionsIds = new IntArrayList();
    protected transient IntLinkedList walkSteps = new IntLinkedList();
    protected transient List<Hit> receivedHits = new ArrayList<>();
    protected transient List<Hit> nextHits = new ArrayList<>();
    protected transient Map<Object, Object> temporaryAttributes = new HashMap<>();
    protected transient UpdateFlags updateFlags = new UpdateFlags(this);
    protected transient List<HitBar> hitBars = new ArrayList<>();
    protected transient int faceEntity = -1;
    protected transient Animation animation;
    protected transient int walkDirection;
    protected transient int runDirection;
    protected transient int crawlDirection;
    @Expose
    protected boolean run;
    @Expose
    protected transient boolean silentRun;
    protected transient HitBar hitBar = new EntityHitBar(this);
    /**
     * A list of possible entities. It's better to keep one list and clear it on request, rather than re-create a list every time this is
     * called.
     */
    protected transient List<Entity> possibleTargets = new ArrayList<>();
    /**
     * Sets a temporary delay during which the entity will not be added to the list of possible targets.
     */
    private transient long findTargetDelay;
    private transient long freezeDelay;
    private transient long freezeImmunity;
    private transient long lockDelay;
    private transient boolean forceAttackable;
    private transient int mapSize;
    private transient int index;
    private transient int lastRegionId;
    private transient int lastChunkId;
    private transient boolean multiArea;
    private transient boolean forceMultiArea;
    private transient boolean finished;
    private final transient Predicate<HitEntry> hitEntryPredicate = hit -> {
        if (isDead()) {
            return true;
        }
        appendHitEntry(hit);
        final int delay = hit.getAndDecrement();
        if (delay <= 0) {
            performDefenceAnimation(hit.getHit().getSource());
        }
        if (delay < 0) {
            CombatUtilities.processHit(this, hit.getHit());
            return true;
        }
        return false;
    };
    private transient boolean isAtDynamicRegion;
    private transient Location lastLoadedMapRegionTile;
    private transient Entity attacking;
    private transient Entity attackedBy;
    private transient long attackedByDelay;
    private transient long lastReceivedHit;
    private final transient BiPredicate<Boolean, Hit> hitPredicate = (locked, hit) -> {
        if (locked && !hit.executeIfLocked()) {
            return false;
        }
        final Predicate<Hit> predicate = hit.getPredicate();
        if (predicate != null) {
            if (predicate.test(hit)) {
                return true;
            }
        }
        this.postProcessHit(hit);
        processHit(hit);
        return true;
    };
    private transient long attackingDelay;
    private transient Location faceLocation;
    private transient Graphics graphics;
    private transient ForceMovement forceMovement;
    private transient ForceTalk forceTalk;
    private transient Tinting tinting;
    private transient int sceneBaseChunkId;
    private transient boolean cantInteract;
    private transient long stunDelay;
    private transient long lastFaceEntityDelay;

    public static int getRoundedDirection(final int baseDirection, final int offset) {
        final int direction = (baseDirection + offset) & 2047;
        if (direction < 128 || direction >= 1920) {
            return 6;
        } else if (direction < 384) {
            return 5;
        } else if (direction < 640) {
            return 3;
        } else if (direction < 896) {
            return 0;
        } else if (direction < 1152) {
            return 1;
        } else if (direction < 1408) {
            return 2;
        } else if (direction < 1664) {
            return 4;
        } else {
            return 7;
        }
    }

    public double getHitpointsAsPercentage() {
        double current = (double) getHitpoints();
        double max = (double) getMaxHitpoints();

        try {
            return (current / max) * 100;
        } catch (ArithmeticException e) {
            return 0.0;
        }
    }

    public abstract int getSize();

    public abstract int getClientIndex();

    public abstract int getMaxHitpoints();

    public abstract boolean isDead();

    public abstract void handleIngoingHit(Hit hit);

    public abstract void postProcessHit(Hit hit);

    public abstract void sendDeath();

    public abstract void autoRetaliate(final Entity source);

    public abstract boolean canAttack(final Player source);

    public abstract boolean startAttacking(final Player source, final CombatType type);

    public abstract int getCombatLevel();

    public abstract EntityType getEntityType();

    public abstract void cancelCombat();

    public abstract void processMovement();

    /**
     * Gets the current middle position of the NPC. NOTE: It reuses the existing tile object and sets its value to the current middle tile,
     * therefore modifications to this object aren't suggested, but instead if you wish to obtain a tile you could modify - such as set its
     * X location to something else, construct a new tile object using this.
     *
     * @return the middle tile of the NPC.
     */
    public abstract Location getMiddleLocation();

    public abstract void handleOutgoingHit(final Entity target, final Hit hit);

    public abstract void performDefenceAnimation(Entity attacker);

    public abstract List<Entity> getPossibleTargets(final EntityType type);

    public abstract int drainSkill(final int skill, final double percentage, final int minimumDrain);

    public abstract int drainSkill(final int skill, final double percentage);

    public abstract int drainSkill(final int skill, final int amount);

    public abstract boolean isRunning();

    public abstract boolean isMaximumTolerance();

    protected abstract boolean isAcceptableTarget(final Entity entity);

    protected abstract boolean isPotentialTarget(final Entity entity);

    public void forceLocation(final Location location) {
        this.location = new Location(location);
    }

    /**
     * Determines whether this entity will trigger opponent auto retaliate if target is a player.
     */
    public boolean triggersAutoRetaliate() {
        return true;
    }

    public boolean canAttackInSingleZone(final Entity target) {
        if (target.isForceAttackable()) {
            return true;
        }
        final Entity attacking = getAttackedBy();
        return attacking == null || attacking == target || getAttackedByDelay() <= Utils.currentTimeMillis() || attacking.isDead() || attacking.isFinished();
    }

    public boolean isMovementRestricted() {
        return isFrozen() || isStunned();
    }

    public boolean setHitpoints(final int amount) {
        final boolean dead = isDead();
        this.hitpoints = amount;
        if (!dead && hitpoints <= 0) {
            sendDeath();
            return true;
        }
        return false;
    }

    public abstract void unlink();

    public boolean isFacing(final Entity target) {
        return this.faceEntity == target.getClientIndex();
    }

    public abstract boolean addWalkStep(final int nextX, final int nextY, final int lastX, final int lastY, final boolean check);

    /**
     * Adds immunity on the character from all damage inflicted by the type specified in parameters.
     *
     * @param type         the hit type.
     * @param milliseconds the duration of the effect in milliseconds.
     */
    public void addImmunity(final HitType type, final long milliseconds) {
        immunities[type.ordinal()] = System.currentTimeMillis() + milliseconds;
    }

    public boolean isImmune(final HitType type) {
        return immunities[type.ordinal()] > System.currentTimeMillis();
    }

    public final int[] getLastWalkTile() {
        if (walkSteps.size() == 0) {
            lastSteps[0] = getX();
            lastSteps[1] = getY();
            return lastSteps;
        }
        final int hash = walkSteps.getLast();
        lastSteps[0] = WalkStep.getNextX(hash);
        lastSteps[1] = WalkStep.getNextY(hash);
        return lastSteps;
    }

    /**
     * Gets the next position of the entity based on its current walksteps list and run mode.
     *
     * @return next walkstep position, or current position if none is present.
     */
    public Location getNextPosition(final int amount) {
        final int size = Math.min(walkSteps.size(), amount);
        if (size == 0) {
            return location;
        }
        final int nextTileHash = walkSteps.nthPeek(size - 1);
        final int x = WalkStep.getNextX(nextTileHash);
        final int y = WalkStep.getNextY(nextTileHash);
        return new Location(x, y, location.getPlane());
    }

    /**
     * Blocks all incoming hits that were scheduled prior to the method call, as well as one tick afterwards for extra protection.
     */
    public void blockIncomingHits() {
        protectionDelay = Utils.currentTimeMillis() + 600;
    }

    public abstract void unclip();

    public abstract void clip();

    protected boolean collides(final List<? extends Entity> list, final int x, final int y) {
        if (list.isEmpty()) return false;
        for (int i = list.size() - 1; i >= 0; i--) {
            final Entity entity = list.get(i);
            if (entity == this || entity.isFinished() || (entity instanceof NPC) && !((NPC) entity).isEntityClipped()) {
                continue;
            }
            if (Utils.collides(entity.getX(), entity.getY(), entity.getSize(), x, y, 1)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Whether to check if this npc is projectile clipped or not. Used for entities that are ontop of clipped tiles (such as objects),
     * allows players to actually attack those.
     */
    public boolean checkProjectileClip(final Player player) {
        return true;
    }

    protected int getNextWalkStep() {
        if (walkSteps.isEmpty()) {
            return 0;
        }
        return walkSteps.remove();
    }

    public void stun(final int ticks) {
        if (isStunned()) {
            return;
        }
        stunDelay = Game.getCurrentCycle() + ticks;
        resetWalkSteps();
        setRouteEvent(null);
    }

    public void removeStun() {
        stunDelay = 0;
    }

    public boolean isStunned() {
        return stunDelay > Game.getCurrentCycle();
    }

    public boolean isFreezeImmune() {
        return freezeImmunity > Game.getCurrentCycle();
    }

    protected boolean isFreezeable() {
        return true;
    }

    public boolean isFrozen() {
        return freezeDelay > Game.getCurrentCycle();
    }

    public void resetFreeze() {
        freezeDelay = 0;
        freezeImmunity = 0;
    }

    public void forceFreezeDelay(final int ticks) {
        this.freezeDelay = Game.getCurrentCycle() + ticks;
    }

    public boolean freeze(final int freezeTicks) {
        return freeze(freezeTicks, 0);
    }

    public boolean freezeWithNotification(final int freezeTicks) {
        return freeze(freezeTicks, 0, entity -> {
            if (entity instanceof Player) {
                ((Player) entity).sendMessage("<col=ef1020>You have been frozen!</col>");
            }
        });
    }

    public boolean freeze(final int freezeTicks, final int immunityTicks) {
        return freeze(freezeTicks, immunityTicks, null);
    }

    public boolean freezeWithNotification(final int freezeTicks, final int immunityTicks) {
        return freeze(freezeTicks, immunityTicks, entity -> {
            if (entity instanceof Player) {
                ((Player) entity).sendMessage("<col=ef1020>You have been frozen!</col>");
            }
        });
    }

    public void addFreezeImmunity(final int immunityTicks) {
        freezeImmunity = Game.getCurrentCycle() + immunityTicks;
    }

    public boolean freeze(final int freezeTicks, final int immunityTicks, @Nullable final Consumer<Entity> onFreezeConsumer) {
        if (!isFreezeable() || isFreezeImmune()) {
            return false;
        }
        final long currentCycle = Game.getCurrentCycle();
        freezeImmunity = currentCycle + freezeTicks + immunityTicks;
        freezeDelay = currentCycle + freezeTicks;
        resetWalkSteps();
        setRouteEvent(null);
        if (onFreezeConsumer != null) {
            onFreezeConsumer.accept(this);
        }
        return true;
    }

    public boolean isNulled() {
        return false;
    }

    public boolean addWalkSteps(final int destX, final int destY) {
        return addWalkSteps(destX, destY, -1);
    }

    public boolean addWalkSteps(final int destX, final int destY, final int maxStepsCount) {
        return addWalkSteps(destX, destY, -1, true);
    }

    public abstract double getMagicPrayerMultiplier();

    public abstract double getRangedPrayerMultiplier();

    public abstract double getMeleePrayerMultiplier();

    public void heal(final int amount) {
        setHitpoints(Math.min((hitpoints + amount), (getMaxHitpoints())));
    }

    public void applyHit(final Hit hit) {
        if (isDead() && hit.getHitType() != HitType.HEALED || isFinished()) {
            return;
        }
        handleIngoingHit(hit);
        receivedHits.add(hit);
        addHitbar();
    }

    protected void addHitbar() {
        if (!getHitBars().contains(hitBar)) {
            getHitBars().add(hitBar);
        }
    }

    public void processReceivedHits() {
        if (receivedHits.isEmpty()) return;
        final Boolean locked = Boolean.valueOf(isLocked());
        receivedHits.removeIf(hit -> hitPredicate.test(locked, hit));
    }

    public boolean isDying() {
        return isDead();
    }

    public void checkMultiArea() {
        multiArea = forceMultiArea || World.isMultiArea(getLocation());
    }

    protected void removeHitpoints(final Hit hit) {
        if (isDead()) {
            return;
        }
        int damage = hit.getDamage();
        if (damage > hitpoints) {
            damage = hitpoints;
        }
        addReceivedDamage(hit.getSource(), damage);
        setHitpoints(hitpoints - damage);
    }

    protected void processHit(final Hit hit) {
        if (hit.getScheduleTime() < protectionDelay) {
            return;
        }
		/*if (isDead()) {
			return;
		}*/
        if (hit.getDamage() > Short.MAX_VALUE) {
            hit.setDamage(Short.MAX_VALUE);
        }
        getUpdateFlags().flag(UpdateFlag.HIT);
        nextHits.add(hit);
        addHitbar();
        lastReceivedHit = Utils.currentTimeMillis();
        if (hit.getHitType() == HitType.HEALED) {
            heal(hit.getDamage());
        } else {
            removeHitpoints(hit);
        }
    }

    public void faceEntity(final Entity target) {
        setFaceLocation(new Location(target.getLocation().getCoordFaceX(target.getSize()), target.getLocation().getCoordFaceY(target.getSize()), target.getPlane()));
    }

    public void addReceivedDamage(final Entity source, final int amount) {
        if (!(source instanceof Player)) {
            return;
        }
        final String username = ((Player) source).getUsername();
        List<IntLongPair> list = receivedDamage.get(username);
        if (list == null) {
            receivedDamage.put(username, list = new ObjectArrayList<>());
        }
        if (amount >= 0) {
            list.add(new IntLongPair(System.currentTimeMillis() + TWENTY_MINUTES_IN_MILLIS, amount));
        }
    }

    public boolean addWalkSteps(final int destX, final int destY, final int maxStepsCount, final boolean check) {
        return WalkStep.addWalkSteps(this, destX, destY, maxStepsCount, check);
    }

    public void appendHitEntry(final HitEntry hitEntry) {
        if (!hitEntry.isFreshEntry()) {
            return;
        }
        hitEntry.setFreshEntry(true);
    }

    public void scheduleHit(final Entity source, @NotNull final Hit hit, final int delay) {
        scheduledHits.add(new HitEntry(source, delay, hit));
    }

    protected final void iterateScheduledHits() {
        final Iterator<HitEntry> each = scheduledHits.iterator();
        while (each.hasNext()) {
            if (hitEntryPredicate.test(each.next())) {
                each.remove();
            }
        }
    }

    public boolean ignoreUnderneathProjectileCheck() {
        return false;
    }

    public void processEntity() {
        iterateScheduledHits();
        processReceivedHits();
        toxins.process();
        processMovement();
    }

    public void resetMasks() {
        if (updateFlags.isUpdateRequired()) {
            updateFlags.reset();
        }
        if (!hitBars.isEmpty()) {
            hitBars.clear();
        }
        if (!nextHits.isEmpty()) {
            nextHits.clear();
        }
        this.walkDirection = this.runDirection = this.crawlDirection = -1;
    }

    public void reset() {
        setHitpoints(getMaxHitpoints());
        receivedHits.clear();
        walkSteps.clear();
        toxins.reset();
        receivedDamage.clear();
        hitBars.clear();
        nextHits.clear();
    }

    protected final boolean needMapUpdate() {
        if (lastLoadedMapRegionTile == null) {
            return false;
        }
        return Math.abs(lastLoadedMapRegionTile.getChunkX() - location.getChunkX()) >= 5 || Math.abs(lastLoadedMapRegionTile.getChunkY() - location.getChunkY()) >= 5;
    }

    public final void faceObject(final WorldObject object) {
        final ObjectDefinitions objectDef = object.getDefinitions();
        final float preciseX = object.getPreciseCoordFaceX(objectDef.getSizeX(), objectDef.getSizeY(), object.getRotation());
        final float preciseY = object.getPreciseCoordFaceY(objectDef.getSizeX(), objectDef.getSizeY(), object.getRotation());
        if (preciseX == getX() && preciseY == getY()) {
            return;
        }
        faceLocation = new Location((int) preciseX, (int) preciseY, getPlane());
        direction = Utils.getFaceDirection(preciseX - getX(), preciseY - getY());
        getUpdateFlags().flag(UpdateFlag.FACE_COORDINATE);
    }

    public void faceDirection(final Direction direction) {
        final Location middle = getMiddleLocation();
        final int size = getSize() / 2;
        switch (direction) {
            case SOUTH:
                setFaceLocation(new Location(middle.getX() + size, middle.getY() - 15, middle.getPlane()));
                return;
            case EAST:
                setFaceLocation(new Location(middle.getX() + 15, middle.getY() + size, middle.getPlane()));
                return;
            case SOUTH_WEST:
                setFaceLocation(new Location(middle.getX() - 15, middle.getY() - 15, middle.getPlane()));
                break;
            case WEST:
                setFaceLocation(new Location(middle.getX() - 15, middle.getY() + size, middle.getPlane()));
                return;
            case NORTH:
                setFaceLocation(new Location(middle.getX() + size, middle.getY() + 15, middle.getPlane()));
                return;
            case NORTH_WEST:
                setFaceLocation(new Location(middle.getX() - 15, middle.getY() + 15, middle.getPlane()));
                return;
            case NORTH_EAST:
                setFaceLocation(new Location(middle.getX() + 15, middle.getY() + 15, middle.getPlane()));
                break;
            case SOUTH_EAST:
                setFaceLocation(new Location(middle.getX() + 15, middle.getY() - 15, middle.getPlane()));
                break;
        }
    }

    public void loadMapRegions() {
        lastMapRegionsIds.clear();
        lastMapRegionsIds.addAll(mapRegionsIds);
        mapRegionsIds.clear();
        isAtDynamicRegion = false;
        final int sceneChunksRadio = MAP_SIZES[mapSize] / 16;
        final int chunkX = location.getChunkX();
        final int chunkY = location.getChunkY();
        final int mapHash = MAP_SIZES[mapSize] >> 4;
        final int minRegionX = (chunkX - mapHash) / 8;
        final int minRegionY = (chunkY - mapHash) / 8;
        final int sceneBaseChunkX = (chunkX - sceneChunksRadio);
        final int sceneBaseChunkY = (chunkY - sceneChunksRadio);
        for (int xCalc = minRegionX < 0 ? 0 : minRegionX; xCalc <= ((chunkX + mapHash) / 8); xCalc++) {
            for (int yCalc = minRegionY < 0 ? 0 : minRegionY; yCalc <= ((chunkY + mapHash) / 8); yCalc++) {
                final int regionId = yCalc + (xCalc << 8);
                if (World.getRegion(regionId, this instanceof Player) instanceof DynamicRegion) {
                    isAtDynamicRegion = true;
                }
                mapRegionsIds.add(regionId);
            }
        }
        lastLoadedMapRegionTile = new Location(getX(), getY(), getPlane());
        sceneBaseChunkId = sceneBaseChunkX | (sceneBaseChunkY << 11);
    }

    public Location getFaceLocation(final Entity target) {
        return getFaceLocation(target, getSize());
    }

    /**
     * Gets the coordinate of the NPC's head, used for large npcs.
     *
     * @return head's location.
     */
    public Location getFaceLocation(final Entity target, final int npcSize) {
        if (target == null) {
            return getLocation();
        }
        final Location middle = getMiddleLocation();
        final float size = npcSize >> 1;
        double degrees = Math.toDegrees(Math.atan2(target.getY() - middle.getY(), target.getX() - middle.getX()));
        if (degrees < 0) {
            degrees += 360;
        }
        final double angle = Math.toRadians(degrees);
        final int px = (int) Math.round(middle.getX() + size * Math.cos(angle));
        final int py = (int) Math.round(middle.getY() + size * Math.sin(angle));
        return new Location(px, py, middle.getPlane());
    }

    public int getRoundedDirection() {
        return getRoundedDirection(0);
    }

    public int getRoundedDirection(final int offset) {
        return getRoundedDirection(this.direction, offset);
    }

    public Location getFaceLocation(final Entity target, final int npcSize, final int offset) {
        if (target == null) {
            return getLocation();
        }
        final Location middle = getMiddleLocation();
        final float size = (float) (npcSize >> 1);
        final Location targetMiddle = target.getMiddleLocation();
        double degrees = Math.toDegrees(((int) ((Math.atan2(targetMiddle.getY() - middle.getY(), targetMiddle.getX() - middle.getX()) * 325.949) + offset) & 2047) / 325.949);
        if (degrees < 0) {
            degrees += 360;
        }
        final double angle = Math.toRadians(degrees);
        final int tileX = (int) Math.round(middle.getX() + size * Math.cos(angle));
        final int tileY = (int) Math.round(middle.getY() + size * Math.sin(angle));
        return new Location(tileX, tileY, middle.getPlane());
    }

    public boolean calcFollow(final Position target, final int maxStepsCount, final boolean calculate, final boolean intelligent, final boolean checkEntities) {
        return WalkStep.calcFollow(this, target, maxStepsCount, calculate, intelligent, checkEntities);
    }

    public boolean isProjectileClipped(final Position target, final boolean closeProximity) {
        return ProjectileUtils.isProjectileClipped(this, target instanceof Entity ? (Entity) target : null, this, target, closeProximity);
    }

    public boolean addWalkStepsInteract(final int destX, final int destY, final int maxStepsCount, final int size, final boolean calculate) {
        return WalkStep.addWalkStepsInteract(this, destX, destY, maxStepsCount, size, size, calculate);
    }

    public int getNextWalkStepPeek() {
        if (walkSteps.isEmpty()) {
            return 0;
        }
        return walkSteps.peek();
    }

    public int getX() {
        return location.getX();
    }

    public int getY() {
        return location.getY();
    }

    public int getPlane() {
        return location.getPlane();
    }

    public void resetWalkSteps() {
        if (!walkSteps.isEmpty()) {
            walkSteps.clear();
        }
    }

    public abstract void setInvalidAnimation(final Animation animation);

    public abstract void setUnprioritizedAnimation(final Animation animation);

    public final Player getMostDamagePlayer() {
        final MutableInt damage = new MutableInt();
        final MutableObject<String> player = new MutableObject<>();
        final MutableInt currentDamage = new MutableInt();
        try {
            receivedDamage.object2ObjectEntrySet().removeIf(entry -> {
                final String source = entry.getKey();
                currentDamage.setValue(0);
                entry.getValue().removeIf(pair -> {
                    if (pair.getLeft() < System.currentTimeMillis()) {
                        return true;
                    }
                    currentDamage.add(pair.getRight());
                    return false;
                });
                if (currentDamage.longValue() > damage.longValue()) {
                    damage.setValue(currentDamage.intValue());
                    player.setValue(source);
                }
                return false;
            });
        } catch (Exception e) {
            log.error("", e);
        }
        final String value = player.getValue();
        return value == null ? null : World.getPlayer(value).orElse(null);
    }

    public final Player getMostDamageNonIronmanPlayer() {
        final MutableInt damage = new MutableInt();
        final MutableObject<String> player = new MutableObject<>();
        final MutableInt currentDamage = new MutableInt();
        try {
            receivedDamage.object2ObjectEntrySet().removeIf(entry -> {
                final String source = entry.getKey();
                final Optional<Player> optionalPlayer = World.getPlayer(source);
                if (!optionalPlayer.isPresent() || optionalPlayer.get().isIronman()) {
                    return false;
                }
                currentDamage.setValue(0);
                entry.getValue().removeIf(pair -> {
                    if (pair.getLeft() < System.currentTimeMillis()) {
                        return true;
                    }
                    currentDamage.add(pair.getRight());
                    return false;
                });
                if (currentDamage.longValue() > damage.longValue()) {
                    damage.setValue(currentDamage.intValue());
                    player.setValue(source);
                }
                return false;
            });
        } catch (Exception e) {
            log.error("", e);
        }
        final String value = player.getValue();
        return value == null ? null : World.getPlayer(value).orElse(null);
    }

    protected boolean hasDealtEnoughDamage(@NotNull final Player killer) {
        final MutableInt playerDamage = new MutableInt();
        final MutableInt totalDamage = new MutableInt();
        try {
            receivedDamage.object2ObjectEntrySet().removeIf(entry -> {
                final String source = entry.getKey();
                final boolean isPlayer = source.equals(killer.getUsername());
                entry.getValue().removeIf(pair -> {
                    if (pair.getLeft() < System.currentTimeMillis()) {
                        return true;
                    }
                    final int operand = pair.getRight();
                    totalDamage.add(operand);
                    if (isPlayer) {
                        playerDamage.add(operand);
                    }
                    return false;
                });
                return false;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return playerDamage.intValue() > (Math.min(totalDamage.intValue(), getMaxHitpoints()) * 0.7F);
    }

    public final Player getMostDamagePlayerCheckIronman() {
        final Player killer = getMostDamagePlayer();
        if (killer == null) {
            return null;
        }
        if (killer.isIronman() && !hasDealtEnoughDamage(killer)) {
            return null;
        }
        return killer;
    }

    public void setRunSilent(final boolean run) {
        silentRun = run;
    }

    public void setRunSilent(final int ticks) {
        setRunSilent(true);
        WorldTasksManager.schedule(() -> setRunSilent(false), ticks);
    }

    public boolean isLocked() {
        return lockDelay > Utils.currentTimeMillis();
    }

    public void lock() {
        lockDelay = Long.MAX_VALUE;
    }

    /**
     * Locks the entity for the requested amount of ticks.
     *
     * @param time in ticks.
     */
    public void lock(final int time) {
        lockDelay = Utils.currentTimeMillis() + (time * 600L);
    }

    public void unlock() {
        lockDelay = 0;
    }

    public boolean hasWalkSteps() {
        return !walkSteps.isEmpty();
    }

    public float getXpModifier(Hit hit) {
        return 1;
    }

    @Override
    public Location getPosition() {
        return getLocation();
    }

    /**
     * Sets a temporary delay during which the entity will not be added to the list of possible targets.
     */
    public long getFindTargetDelay() {
        return this.findTargetDelay;
    }

    /**
     * Sets a temporary delay during which the entity will not be added to the list of possible targets.
     */
    public void setFindTargetDelay(final long findTargetDelay) {
        this.findTargetDelay = findTargetDelay;
    }

    public Toxins getToxins() {
        return this.toxins;
    }

    public long getFreezeDelay() {
        return this.freezeDelay;
    }

    public long getFreezeImmunity() {
        return this.freezeImmunity;
    }

    public long getLockDelay() {
        return this.lockDelay;
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(final Location tile) {
        if (tile == null) {
            return;
        }
        nextLocation = new Location(tile);
    }

    public RouteEvent<?, ?> getRouteEvent() {
        return this.routeEvent;
    }

    public void setRouteEvent(final RouteEvent<?, ?> event) {
        if (event == null) {
            if (routeEvent != null) {
                final Runnable failure = routeEvent.getOnFailure();
                if (failure != null) {
                    failure.run();
                }
            }
        }
        routeEvent = event;
    }

    /**
     * A delay that's referenced in {@link CombatUtilities#processHit} - if this value is above the current time in milliseconds, the hit will
     * not be processed - it's used to prevent entities from being hit when insta-leaving areas like raids.
     */
    public long getProtectionDelay() {
        return this.protectionDelay;
    }

    /**
     * A delay that's referenced in {@link CombatUtilities#processHit} - if this value is above the current time in milliseconds, the hit will
     * not be processed - it's used to prevent entities from being hit when insta-leaving areas like raids.
     */
    public void setProtectionDelay(final long protectionDelay) {
        this.protectionDelay = protectionDelay;
    }

    public boolean isForceAttackable() {
        return this.forceAttackable;
    }

    public void setForceAttackable(final boolean forceAttackable) {
        this.forceAttackable = forceAttackable;
    }

    public int getHitpoints() {
        return this.hitpoints;
    }

    public int getMapSize() {
        return this.mapSize;
    }

    public void setMapSize(final int mapSize) {
        this.mapSize = mapSize;
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(final int index) {
        this.index = index;
    }

    public int getLastRegionId() {
        return this.lastRegionId;
    }

    public void setLastRegionId(final int lastRegionId) {
        this.lastRegionId = lastRegionId;
    }

    public int getLastChunkId() {
        return this.lastChunkId;
    }

    public void setLastChunkId(final int lastChunkId) {
        this.lastChunkId = lastChunkId;
    }

    public int getDirection() {
        return this.direction;
    }

    public void setDirection(final int direction) {
        this.direction = direction;
    }

    public int getLastMovementType() {
        return this.lastMovementType;
    }

    public boolean isMultiArea() {
        return this.multiArea;
    }

    public void setMultiArea(final boolean multiArea) {
        this.multiArea = multiArea;
    }

    public boolean isForceMultiArea() {
        return this.forceMultiArea;
    }

    public void setForceMultiArea(final boolean forceMultiArea) {
        this.forceMultiArea = forceMultiArea;
        checkMultiArea();
    }

    public boolean isTeleported() {
        return this.teleported;
    }

    public void setTeleported(final boolean teleported) {
        this.teleported = teleported;
    }

    public boolean isFinished() {
        return this.finished;
    }

    public void setFinished(final boolean finished) {
        this.finished = finished;
    }

    public boolean isAtDynamicRegion() {
        return this.isAtDynamicRegion;
    }

    public void setAtDynamicRegion(final boolean isAtDynamicRegion) {
        this.isAtDynamicRegion = isAtDynamicRegion;
    }

    public Location getLastLoadedMapRegionTile() {
        return this.lastLoadedMapRegionTile;
    }

    public void setLastLoadedMapRegionTile(final Location lastLoadedMapRegionTile) {
        this.lastLoadedMapRegionTile = lastLoadedMapRegionTile;
    }

    public Location getLastLocation() {
        return this.lastLocation;
    }

    public void setLastLocation(final Location lastLocation) {
        this.lastLocation = lastLocation;
    }

    public Location getNextLocation() {
        return this.nextLocation;
    }

    public Entity getAttacking() {
        return this.attacking;
    }

    public void setAttacking(final Entity attacking) {
        this.attacking = attacking;
    }

    public Entity getAttackedBy() {
        return this.attackedBy;
    }

    public void setAttackedBy(final Entity attackedBy) {
        this.attackedBy = attackedBy;
    }

    public long getAttackedByDelay() {
        return this.attackedByDelay;
    }

    public void setAttackedByDelay(final long attackedByDelay) {
        this.attackedByDelay = attackedByDelay;
    }

    public long getLastReceivedHit() {
        return this.lastReceivedHit;
    }

    public void setLastReceivedHit(final long lastReceivedHit) {
        this.lastReceivedHit = lastReceivedHit;
    }

    public long getLastAnimation() {
        return this.lastAnimation;
    }

    public void setLastAnimation(final long lastAnimation) {
        this.lastAnimation = lastAnimation;
    }

    public long getAttackingDelay() {
        return this.attackingDelay;
    }

    public void setAttackingDelay(final long attackingDelay) {
        this.attackingDelay = attackingDelay;
    }

    public Location getFaceLocation() {
        return this.faceLocation;
    }

    public void setFaceLocation(final Location tile) {
        faceLocation = tile;
        final Location middle = getMiddleLocation();
        direction = Utils.getFaceDirection(tile.getX() - middle.getX(), tile.getY() - middle.getY());
        getUpdateFlags().flag(UpdateFlag.FACE_COORDINATE);
    }

    public IntList getMapRegionsIds() {
        return this.mapRegionsIds;
    }

    public IntList getLastMapRegionsIds() {
        return this.lastMapRegionsIds;
    }

    public IntLinkedList getWalkSteps() {
        return this.walkSteps;
    }

    public List<Hit> getReceivedHits() {
        return this.receivedHits;
    }

    public List<Hit> getNextHits() {
        return this.nextHits;
    }

    public Object2ObjectMap<String, List<IntLongPair>> getReceivedDamage() {
        return this.receivedDamage;
    }

    public Map<Object, Object> getTemporaryAttributes() {
        return this.temporaryAttributes;
    }

    public UpdateFlags getUpdateFlags() {
        return this.updateFlags;
    }

    public List<HitBar> getHitBars() {
        return this.hitBars;
    }

    public int getFaceEntity() {
        return this.faceEntity;
    }

    public void setFaceEntity(final Entity entity) {
        this.lastFaceEntityDelay = System.currentTimeMillis();
        faceEntity = entity == null ? -1 : entity.getClientIndex();
        updateFlags.flag(UpdateFlag.FACE_ENTITY);
    }

    public Animation getAnimation() {
        return this.animation;
    }

    public abstract void setAnimation(final Animation animation);

    public Graphics getGraphics() {
        return this.graphics;
    }

    public void setGraphics(final Graphics graphics) {
        this.graphics = graphics;
        updateFlags.flag(UpdateFlag.GRAPHICS);
    }

    public ForceMovement getForceMovement() {
        return this.forceMovement;
    }

    public void setForceMovement(final ForceMovement movement) {
        forceMovement = movement;
        updateFlags.flag(UpdateFlag.FORCE_MOVEMENT);
    }

    public ForceTalk getForceTalk() {
        return this.forceTalk;
    }

    public void setForceTalk(final String string) {
        setForceTalk(ForceTalk.get(string));
    }

    public void setForceTalk(final ForceTalk talk) {
        forceTalk = talk;
        updateFlags.flag(UpdateFlag.FORCED_CHAT);
    }

    public Tinting getTinting() {
        return tinting;
    }

    public void setTinting(Tinting tinting) {
        this.tinting = tinting;
    }

    public int getWalkDirection() {
        return this.walkDirection;
    }

    public void setWalkDirection(final int walkDirection) {
        this.walkDirection = walkDirection;
    }

    public int getRunDirection() {
        return this.runDirection;
    }

    public void setRunDirection(final int runDirection) {
        this.runDirection = runDirection;
    }

    public int getCrawlDirection() {
        return crawlDirection;
    }

    public void setCrawlDirection(int crawlDirection) {
        this.crawlDirection = crawlDirection;
    }

    public int getSceneBaseChunkId() {
        return this.sceneBaseChunkId;
    }

    public void setSceneBaseChunkId(final int sceneBaseChunkId) {
        this.sceneBaseChunkId = sceneBaseChunkId;
    }

    public boolean isRun() {
        return this.run;
    }

    public void setRun(final boolean run) {
        this.run = run;
        if (this instanceof Player) {
            ((Player) this).getVarManager().sendVar(173, isRun() ? 1 : 0);
        }
    }

    public boolean isSilentRun() {
        return this.silentRun;
    }

    public boolean isCantInteract() {
        return this.cantInteract;
    }

    public void setCantInteract(final boolean cantInteract) {
        this.cantInteract = cantInteract;
    }

    public long getLastFaceEntityDelay() {
        return this.lastFaceEntityDelay;
    }

    public void setLastFaceEntityDelay(final long lastFaceEntityDelay) {
        this.lastFaceEntityDelay = lastFaceEntityDelay;
    }

    public int getXInScene(final Location location) {
        return location.getX() - ((sceneBaseChunkId & 2047) << 3);
        //MapUtils.decode(Structure.CHUNK, entity.getSceneBaseChunkId())[0] * 8;
    }

    public int getYInScene(final Location location) {
        return location.getY() - ((sceneBaseChunkId >> 11 & 2047) << 3);
        //MapUtils.decode(Structure.CHUNK, entity.getSceneBaseChunkId())[1] * 8;
    }

    public enum EntityType {
        PLAYER(Player.class),
        NPC(NPC.class),
        BOTH(Entity.class);
        private final Class<? extends Entity> clazz;

        EntityType(final Class<? extends Entity> clazz) {
            this.clazz = clazz;
        }

        public Class<? extends Entity> getClazz() {
            return this.clazz;
        }
    }
}
