package com.zenyte.game.world.entity.player.action.combat;

import com.zenyte.cores.WorldThread;
import com.zenyte.game.content.achievementdiary.diaries.DesertDiary;
import com.zenyte.game.content.skills.hunter.npc.ImplingNPC;
import com.zenyte.game.content.skills.magic.SpellState;
import com.zenyte.game.content.skills.magic.Spellbook;
import com.zenyte.game.content.skills.magic.spells.lunar.SpellbookSwap;
import com.zenyte.game.content.skills.prayer.Prayer;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.item.degradableitems.DegradeType;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.Projectile;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.Toxins;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.combatdefs.AttackType;
import com.zenyte.game.world.entity.pathfinding.events.player.CombatEntityEvent;
import com.zenyte.game.world.entity.pathfinding.strategy.PredictedEntityStrategy;
import com.zenyte.game.world.entity.player.Bonuses;
import com.zenyte.game.world.entity.player.CombatDefinitions;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.entity.player.action.combat.magic.CombatSpell;
import com.zenyte.game.world.entity.player.action.combat.magic.spelleffect.SpellEffect;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import com.zenyte.game.world.entity.player.variables.TickVariable;
import com.zenyte.game.world.region.Area;
import com.zenyte.game.world.region.area.plugins.EntityAttackPlugin;
import com.zenyte.game.world.region.area.plugins.PlayerCombatPlugin;
import com.zenyte.plugins.item.TomeOfFire;
import mgi.types.config.items.ItemDefinitions;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

import static com.zenyte.game.world.entity.player.action.combat.CombatUtilities.AHRIMS_SET_GFX;

/**
 * @author Kris | 03/03/2019 16:53
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class MagicCombat extends PlayerCombat {

    private static final CombatSpell[] fireSpells = new CombatSpell[]{CombatSpell.FIRE_BLAST, CombatSpell.FIRE_BOLT, CombatSpell.FIRE_STRIKE, CombatSpell.FIRE_SURGE, CombatSpell.FIRE_WAVE};
    private static final Projectile teleblock = new Projectile(1300, 43, 31, 46, 23, 29, 64, 5);
    private static final EnumSet<CombatSpell> multiSpells = EnumSet.of(CombatSpell.ICE_BURST, CombatSpell.SMOKE_BURST, CombatSpell.BLOOD_BURST, CombatSpell.SHADOW_BURST, CombatSpell.ICE_BARRAGE, CombatSpell.SMOKE_BARRAGE, CombatSpell.BLOOD_BARRAGE, CombatSpell.SHADOW_BARRAGE);
    protected final CombatSpell spell;
    private final CastType castType;
    protected boolean interrupt;
    private SpellState state;
    private boolean splash;

    public MagicCombat(@NotNull final Entity target, @NotNull final CombatSpell spell, @NotNull final CastType castType) {
        super(target);
        this.spell = spell;
        this.castType = castType;
    }

    @Override
    int fireProjectile() {
        final Projectile projectile = spell.getProjectile();
        if (projectile.getGraphicsId() != -1 && spell != CombatSpell.TELE_BLOCK) {
            if (spell == CombatSpell.SMOKE_BURST || spell == CombatSpell.ICE_BURST || spell == CombatSpell.SMOKE_BARRAGE || spell == CombatSpell.ICE_BARRAGE) {
                World.sendProjectile(target.getMiddleLocation(), target, projectile);
            } else {
                World.sendProjectile(player, target, projectile);
            }
        }
        return projectile.getTime(player.getLocation(), target.getLocation());
    }

    @Override
    public Hit getHit(Player player, Entity target, double accuracyModifier, double passiveModifier, double activeModifier, final boolean ignorePrayers) {
        final Hit hit = new Hit(player, getRandomHit(player, target, getMaxHit(player, passiveModifier, activeModifier, false), accuracyModifier), HitType.MAGIC);
        hit.setWeapon(spell);
        return hit;
    }

    @Override
    public int getRandomHit(Player player, Entity target, int maxhit, double modifier) {
        return getRandomHit(player, target, maxhit, modifier, AttackType.MAGIC);
    }

    @Override
    public int getRandomHit(Player player, Entity target, int maxhit, double modifier, AttackType oppositeIndex) {
        double a = Math.floor(player.getSkills().getLevel(Skills.MAGIC) * player.getPrayerManager().getMagicBoost(Skills.ATTACK));
        final CombatDefinitions combatDefinitions = player.getCombatDefinitions();
        if (combatDefinitions.getStyleDefinition() == AttackStyleDefinition.THROWN_MAGIC) {
            final int style = combatDefinitions.getStyle();
            //Accurate
            if (style == 0) {
                a += 3;
            } else
                //Defensive longrange
                if (style > 1) {
                    a += 1;
                }
        }
        a += 8;
        if (CombatUtilities.hasFullMagicVoid(player, false)) {
            a *= 1.45F;
        }
        a = Math.floor(a);
        final int b = player.getBonuses().getBonus(Bonuses.Bonus.ATT_MAGIC);
        double result = a * (b + 64.0F);
        final int amuletId = player.getEquipment().getId(EquipmentSlot.AMULET);
        if ((amuletId == 12017 || amuletId == 12018) && CombatUtilities.SALVE_AFFECTED_NPCS.contains(name)) {
            result *= amuletId == 12017 ? 1.15F : 1.2F;
        } else if (player.getSlayer().isCurrentAssignment(target)) {
            final int helmId = player.getEquipment().getId(EquipmentSlot.HELMET);
            final ItemDefinitions definitions = ItemDefinitions.get(helmId);
            final String name = definitions == null ? null : definitions.getName().toLowerCase();
            if (name != null && (name.contains("black mask") || name.contains("slayer helm"))) {
                result *= 1.15F;
            }
        }
        result = Math.floor(result);
        //If the weapon is smoke battlestaff and the player is on normal spellbook.
        final int weapon = player.getEquipment().getId(EquipmentSlot.WEAPON);
        if (weapon == 11998 || weapon == 12000) {
            if (player.getCombatDefinitions().getSpellbook() == Spellbook.NORMAL) {
                modifier += 0.1F;
            }
        }
        if (isThammaronsSceptre()) {
            if (target instanceof NPC && ((NPC) target).isInWilderness()) {
                final Item wep = player.getWeapon();
                if (wep.getCharges() > 1000) {
                    modifier += 1;
                }
            }
        }
        result *= modifier;
        result = Math.floor(result);
        if (CombatUtilities.isCombatDummy(target)) {
            return maxhit;
        }
        final int targetRoll = getTargetDefenceRoll(player, target, oppositeIndex);
        final double accuracy = result > targetRoll ? (1.0F - (targetRoll + 2.0F) / (2.0F * (result + 1.0F))) : (result / (2.0F * (targetRoll + 1.0F))) * (target instanceof Player ? 1.1F : 1.0F);
        //Multiply accuracy by 10% boost cus rsps and players are gay.
        sendDebug(accuracy, maxhit);
        splash = accuracy < Utils.randomDouble();
        if (splash) {
            return 0;
        }
        return Utils.random(maxhit);
    }

    @Override
    protected boolean isWithinAttackDistance() {
        final boolean immediateCast = castType == CastType.MANUAL_CAST || spell == null || spell.getSpellbook() == null;
        if (target.checkProjectileClip(player) && isProjectileClipped(immediateCast, false)) {
            return false;
        }
        final Location nextTile = target.getNextLocation();
        final Location tile = nextTile != null ? nextTile : target.getLocation();
        final int distanceX = player.getX() - tile.getX();
        final int distanceY = player.getY() - tile.getY();
        final int size = target.getSize();
        int maxDistance = getAttackDistance();
        final Location nextLocation = target.getNextPosition(target.isRun() ? 2 : 1);
        if (player.hasWalkSteps()) {
            final int dist = getTileDistance(false);
            final int postWalkDistance = getTileDistance(true);
            //If the player is about to move, but his movement doesn't help him progress towards the target as much as it could, we only append as many tiles as the player moves
            //towards the target.
            maxDistance += Math.min(player.isRun() ? 2 : 1, Math.abs(postWalkDistance - dist));
        }
        if ((player.isFrozen() || player.isStunned()) && (Utils.collides(player.getX(), player.getY(), player.getSize(), nextLocation.getX(), nextLocation.getY(), target.getSize()) || !withinRange(target, maxDistance, target.getSize()))) {
            return false;
        }
        return distanceX <= size + maxDistance && distanceX >= -1 - maxDistance && distanceY <= size + maxDistance && distanceY >= -1 - maxDistance;
    }

    protected int baseDamage() {
        return spell.getMaxHit();
    }

    @Override
    public int getMaxHit(Player player, double passiveModifier, double activeModifier, final boolean ignorePrayers) {
        int damage = baseDamage();
        if (spell == CombatSpell.MAGIC_DART) {
            final int staff = player.getEquipment().getId(EquipmentSlot.WEAPON);
            //4170, 21255
            final int magic = player.getSkills().getLevel(Skills.MAGIC);
            if (staff == 4170 || !(player.getSlayer().isCurrentAssignment(target) || CombatUtilities.isCombatDummy(target))) {
                damage = (int) Math.floor((magic / 10.0F) + 10);
            } else {
                damage = (int) Math.floor((magic / 6.0F) + 13);
            }
        }
        if (player.getVariables().getTime(TickVariable.CHARGE) > 0) {
            final int cape = player.getEquipment().getId(EquipmentSlot.CAPE);
            if (spell == CombatSpell.CLAWS_OF_GUTHIX && (cape == 2413 || cape == 21793 || cape == 13335 || cape == 21784) || spell == CombatSpell.SARADOMIN_STRIKE && (cape == 2412 || cape == 21791 || cape == 13331 || cape == 21776) || spell == CombatSpell.FLAMES_OF_ZAMORAK && (cape == 2414 || cape == 21795 || cape == 13333 || cape == 21780)) {
                damage = 30;
            }
        }
        if (player.getEquipment().getId(EquipmentSlot.HANDS) == 777) {
            if (spell == CombatSpell.EARTH_BOLT || spell == CombatSpell.FIRE_BOLT || spell == CombatSpell.WATER_BOLT || spell == CombatSpell.WIND_BOLT) {
                damage += 3;
            }
        }
        //Start calculating the modifer; it is only multiplied by the damage once.
        double modifier = 1 + (player.getBonuses().getBonus(Bonuses.Bonus.MAGIC_DAMAGE) / 100.0F);
        //If the weapon is smoke battlestaff and the player is on normal spellbook.
        final int weapon = player.getEquipment().getId(EquipmentSlot.WEAPON);
        if (weapon == 11998 || weapon == 12000) {
            if (player.getCombatDefinitions().getSpellbook() == Spellbook.NORMAL) {
                modifier += 0.1F;
            }
        }
        //If the player has full elite magic void.
        if (CombatUtilities.hasFullMagicVoid(player, true)) {
            modifier += 0.025F;
        }
        //Multiply with the damage modifiers.
        damage *= modifier;
        modifier = 1;
        //If the player is wearing an imbued salve amulet and attacking an undead NPC.
        final int amuletId = player.getEquipment().getId(EquipmentSlot.AMULET);
        final boolean salveAffected = (amuletId == 12017 || amuletId == 12018) && (CombatUtilities.SALVE_AFFECTED_NPCS.contains(name) || CombatUtilities.isUndeadCombatDummy(target));
        if (salveAffected) {
            modifier += amuletId == 12017 ? 0.15F : 0.2F;
        } else if (player.getSlayer().isCurrentAssignment(target) || CombatUtilities.isUndeadCombatDummy(target)) {
            //Imbue black mask/slayer helmet effect.
            final int helmId = player.getEquipment().getId(EquipmentSlot.HELMET);
            final ItemDefinitions definitions = ItemDefinitions.get(helmId);
            final String name = definitions == null ? null : definitions.getName().toLowerCase();
            if (name != null && (name.contains("black mask") || name.contains("slayer helm")) && name.endsWith("(i)")) {
                modifier += 0.15F;
            }
        }
        //Multiply with the damage modifiers.
        damage *= modifier;
        if (isThammaronsSceptre()) {
            if (target instanceof NPC && ((NPC) target).isInWilderness()) {
                final Item wep = player.getWeapon();
                if (wep.getCharges() > 1000) {
                    passiveModifier += 0.25F;
                }
            }
        }
        damage = (int) Math.floor(damage);
        if (!ignorePrayers) {
            if (target instanceof Player) {
                if (((Player) target).getPrayerManager().isActive(Prayer.PROTECT_FROM_MAGIC)) {
                    damage *= target.getMagicPrayerMultiplier();
                }
            }
        }
        damage *= passiveModifier;
        //Tome of fire effect.
        final Item shield = player.getEquipment().getItem(EquipmentSlot.SHIELD);
        if (shield != null && shield.getId() == TomeOfFire.TOME_OF_FIRE && shield.hasCharges()) {
            if (ArrayUtils.contains(fireSpells, spell)) {
                damage *= 1.5F;
            }
        }
        //Castle wars bracelet effect
        if (player.getTemporaryAttributes().containsKey("castle wars bracelet effect") && player.inArea("Castle wars instance")) {
            if (target instanceof Player) {
                final int targetWeapon = ((Player) target).getEquipment().getId(EquipmentSlot.WEAPON);
                if (targetWeapon == 4037 || targetWeapon == 4039) {
                    damage *= 1.2F;
                }
            }
        }
        return damage;
    }

    @Override
    public boolean start() {
        if (!spell.canCast(player, target)) {
            return false;
        }
        state = new SpellState(player, spell.getLevel(), spell.getRunes());
        player.setCombatEvent(new CombatEntityEvent(player, new PredictedEntityStrategy(target)));
        player.setLastTarget(target);
        notifyIfFrozen();
        player.setFaceEntity(target);
        if (initiateCombat(player)) {
            return true;
        }
        player.setFaceEntity(null);
        return false;
    }

    private boolean isTargetDead() {
        return target instanceof Player || castType == CastType.AUTO_CAST ? target.isDead() : (target.isDead() && ((NPC) target).getTimeOfDeath() != WorldThread.WORLD_CYCLE);
    }

    private boolean initiateCombat(final Player player) {
        if (player.isDead() || player.isFinished() || player.isLocked() || player.isStunned() || player.isFullMovementLocked()) {
            return false;
        }
        if (isTargetDead() || target.isFinished() || target.isCantInteract()) {
            return false;
        }
        if (spell == CombatSpell.MAGIC_DART && player.getSkills().getLevel(Skills.SLAYER) < 50) {
            player.sendMessage("You need a Slayer level of at least 50 to cast Slayer Dart.");
            return false;
        }
        final int distanceX = player.getX() - target.getX();
        final int distanceY = player.getY() - target.getY();
        final int size = target.getSize();
        final int viewDistance = player.getViewDistance();
        if (player.getPlane() != target.getPlane() || distanceX > size + viewDistance || distanceX < -1 - viewDistance || distanceY > size + viewDistance || distanceY < -1 - viewDistance) {
            return false;
        }
        if (target.getEntityType() == Entity.EntityType.PLAYER) {
            if (!player.isCanPvp() || !((Player) target).isCanPvp()) {
                player.sendMessage("You can't attack someone in a safe zone.");
                return false;
            }
        }
        if (player.isFrozen() || player.isMovementLocked(false)) {
            return true;
        }
        if (colliding()) {
            player.getCombatEvent().process();
            return true;
        }
        if (handleDragonfireShields(player, false)) {
            if (!canAttack()) {
                return false;
            }
            handleDragonfireShields(player, true);
            player.getActionManager().addActionDelay(4);
            return true;
        }
        return pathfind();
    }

    @Override
    public boolean process() {
        return !interrupt && initiateCombat(player);
    }

    @Override
    protected int getAttackDistance() {
        return 9;
    }

    @Override
    public int processWithDelay() {
        if (!isWithinAttackDistance()) {
            return 0;
        }
        if (!canAttack()) {
            return -1;
        }
        state = new SpellState(player, spell);
        if (!state.check()) {
            return -1;
        }
        splash = false;
        addAttackedByDelay(player, target);
        final Area area = player.getArea();
        if (area instanceof PlayerCombatPlugin) {
            ((PlayerCombatPlugin) area).onAttack(player, target, "Magic");
        }
        if (player.getCombatDefinitions().isUsingSpecial()) {
            final int delay = useSpecial(player, SpecialType.MAGIC);
            if (delay >= 0) {
                return delay;
            }
        }
        final Item shield = player.getEquipment().getItem(EquipmentSlot.SHIELD);
        if (shield != null && shield.getId() == TomeOfFire.TOME_OF_FIRE && shield.hasCharges()) {
            if (ArrayUtils.contains(fireSpells, spell)) {
                player.getChargesManager().removeCharges(shield, 1, player.getEquipment().getContainer(), EquipmentSlot.SHIELD.getSlot());
            }
        }
        extra();
        final int delay = fireProjectile();
        hit(delay);
        addBaseXP();
        degrade();
        animate();
        if (player.getEquipment().getId(EquipmentSlot.WEAPON) != ItemId.KODAI_WAND || Utils.random(100) >= 15) {
            state.remove();
        }
        if (spell.equals(CombatSpell.ICE_BARRAGE)) {
            player.getAchievementDiaries().update(DesertDiary.CAST_ICE_BARRAGE);
        }
        //player.getAchievementDiaries().update(ArdougneDiary.CAST_ICE_BARRAGE_ON_PLAYER_IN_CW);
        if (spell == CombatSpell.TELE_BLOCK) {
            World.sendProjectile(player, target, this.splash ? teleblock : spell.getProjectile());
        }
        if (spell == CombatSpell.BLOOD_BLITZ || spell == CombatSpell.BLOOD_BARRAGE || spell == CombatSpell.BLOOD_BURST || spell == CombatSpell.BLOOD_RUSH) {
            if (!splash) {
                player.sendFilteredMessage("You drain some of your opponent's health.");
            }
        }
        if (spell.getCastSound() != null) {
            player.getPacketDispatcher().sendSoundEffect(spell.getCastSound());
        }
        if (Utils.random(3) == 0 && CombatUtilities.hasFullBarrowsSet(player, "Ahrim's")) {
            target.setGraphics(AHRIMS_SET_GFX);
            target.drainSkill(Skills.STRENGTH, 5);
        }
        addToxinTask(delay);
        resetFlag();
        SpellbookSwap.checkSpellbook(player);
        if (castType == CastType.MANUAL_CAST) {
            interrupt = true;
        }
        checkIfShouldTerminate();
        return attackSpeed();
    }

    protected int attackSpeed() {
        return 4;
    }

    private boolean isThammaronsSceptre() {
        return player.getEquipment().getId(EquipmentSlot.WEAPON) == 22555;
    }

    protected void hit(final int delay) {
        final int amuletId = player.getEquipment().getId(EquipmentSlot.AMULET);
        final float passiveModifier = (amuletId == 12851 || amuletId == 12853) && CombatUtilities.hasFullBarrowsSet(player, "Ahrim's") ? 1.3F : 1;
        final Hit primaryHit = getHit(player, target, 1, passiveModifier, 1, false);
        final Projectile projectile = spell.getProjectile();
        final int clientCycles = projectile.getProjectileDuration(player.getLocation(), target.getLocation());
        applyHit(target, primaryHit, splash, delay, clientCycles);
        if (multiSpells.contains(spell)) {
            attackTarget(getMultiAttackTargets(player), originalTarget -> {
                if (target == originalTarget) {
                    return true;
                }
                final Hit hit = getHit(player, target, 1, passiveModifier, 1, false);
                applyHit(target, hit, splash, delay, clientCycles);
                return true;
            });
        }
    }

    protected void applyHit(final Entity target, final Hit hit, final boolean splash, final int delay, final int clientcycles) {
        final Graphics gfx = splash ? new Graphics(85, -1, 124) : spell.getHitGfx();
        if (gfx != null) {
            target.setGraphics(new Graphics(gfx.getId(), clientcycles, gfx.getHeight()));
        }
        final SoundEffect hitSound = splash ? new SoundEffect(227, 10, -1) : spell.getHitSound();
        if (hitSound != null) {
            World.sendSoundEffect(target.getLocation(), new SoundEffect(hitSound.getId(), hitSound.getRadius(), hitSound.getDelay() == -1 ? clientcycles : hitSound.getDelay()));
            if (spell == CombatSpell.IBAN_BLAST) {
                World.sendSoundEffect(target.getLocation(), new SoundEffect(hitSound.getId(), hitSound.getRadius(), hitSound.getDelay() == -1 ? clientcycles : hitSound.getDelay()));
            }
        }
        if (!splash) {
            final boolean isFreezingImpling = (spell == CombatSpell.BIND || spell == CombatSpell.SNARE || spell == CombatSpell.ENTANGLE) && target instanceof ImplingNPC;
            if (!isFreezingImpling && spell != CombatSpell.TELE_BLOCK) {
                this.delayHit(target, delay, hit);
            }
            final SpellEffect effect = spell.getEffect();
            if (effect != null) {
                effect.spellEffect(player, target, hit.getDamage());
            }
        } else {
            final boolean isFreezingImpling = (spell == CombatSpell.BIND || spell == CombatSpell.SNARE || spell == CombatSpell.ENTANGLE) && target instanceof ImplingNPC;
            if (isFreezingImpling) {
                return;
            }
            WorldTasksManager.schedule(() -> {
                target.autoRetaliate(player);
                if (target instanceof NPC) {
                    ((NPC) target).flinch();
                }
            }, delay);
        }
    }

    private void addToxinTask(final int delay) {
        final boolean isFreezingImpling = (spell == CombatSpell.BIND || spell == CombatSpell.SNARE || spell == CombatSpell.ENTANGLE) && target instanceof ImplingNPC;
        if (isFreezingImpling) {
            return;
        }
        final int weapon = player.getEquipment().getId(EquipmentSlot.WEAPON);
        if (weapon != 12904) {
            return;
        }
        final boolean success = (target instanceof NPC && CombatUtilities.isWearingSerpentineHelmet(player)) || Utils.random(3) == 0;
        if (!success) {
            return;
        }
        WorldTasksManager.schedule(() -> target.getToxins().applyToxin(Toxins.ToxinType.VENOM, 6), delay);
    }

    protected void animate() {
        final Animation animation = spell.getAnimation();
        final Graphics graphics = spell.getCastGfx();
        if (animation != null) {
            player.setAnimation(animation);
        }
        if (graphics != null) {
            player.setGraphics(graphics);
        }
    }

    private void addBaseXP() {
        spell.addXp(player, spell.getExperience());
    }

    protected void degrade() {
        player.getChargesManager().removeCharges(DegradeType.SPELL);
    }

    protected boolean canAttack() {
        if (!attackable()) {
            return false;
        }
        final boolean isFreezingImpling = (spell == CombatSpell.BIND || spell == CombatSpell.SNARE || spell == CombatSpell.ENTANGLE) && target instanceof ImplingNPC;
        if (!isFreezingImpling && !target.canAttack(player)) {
            return false;
        }
        final Area area = player.getArea();
        if ((area instanceof EntityAttackPlugin && !((EntityAttackPlugin) area).attack(player, target))) {
            return false;
        }
        if ((area instanceof PlayerCombatPlugin && !((PlayerCombatPlugin) area).processCombat(player, target, "Magic")) || !player.getControllerManager().processPlayerCombat(target, "Magic")) {
            return false;
        }
        return !isTargetDead();
    }

    public enum CastType {
        AUTO_CAST,
        MANUAL_CAST
    }
}
