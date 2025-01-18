package com.zenyte.game.content.skills.woodcutting.actions;

import com.zenyte.game.content.achievementdiary.diaries.*;
import com.zenyte.game.content.skills.firemaking.Firemaking;
import com.zenyte.game.content.skills.woodcutting.AxeDefinitions;
import com.zenyte.game.content.skills.woodcutting.TreeDefinitions;
import com.zenyte.game.content.treasuretrails.ClueItem;
import com.zenyte.game.content.treasuretrails.clues.SherlockTask;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.SkillcapePerk;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.player.Action;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.entity.player.VarManager;
import com.zenyte.game.world.entity.player.container.Container;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import com.zenyte.game.world.entity.player.dailychallenge.challenge.SkillingChallenge;
import com.zenyte.game.world.entity.player.perk.PerkWrapper;
import com.zenyte.game.world.object.WorldObject;
import mgi.types.config.items.ItemDefinitions;

import java.util.Objects;
import java.util.Optional;

/**
 * @author Kris | 13. dets 2017 : 6:07.25
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server
 * profile</a>
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status
 * profile</a>
 */
public class Woodcutting extends Action {
    public static final int SULLIUSCEP_INDEX_VARBIT = 5808;
    public static final Graphics BURN_GFX = new Graphics(86, 0, 180);
    public static final SoundEffect TREE_FALL_SOUND = new SoundEffect(2734);
    private static final int BIRD_NEST_CHANCE = 128;

    static {
        VarManager.appendPersistentVarbit(SULLIUSCEP_INDEX_VARBIT);
    }

    private final WorldObject tree;
    private final TreeDefinitions definitions;
    private final String logName;
    private final Runnable onFall;
    private AxeResult axe;
    private int ticks;

    public Woodcutting(final WorldObject tree, final TreeDefinitions definitions) {
        this(tree, definitions, null);
    }

    public Woodcutting(final WorldObject tree, final TreeDefinitions definitions, final Runnable onFall) {
        this.tree = tree;
        this.definitions = definitions;
        final ItemDefinitions defs = ItemDefinitions.get(definitions.getLogsId());
        logName = defs == null ? "null" : defs.getName().toLowerCase();
        this.onFall = onFall;
    }

    public static Optional<AxeResult> getAxe(final Player player) {
        final int level = player.getSkills().getLevel(Skills.WOODCUTTING);
        final Container inventory = player.getInventory().getContainer();
        final int weapon = player.getEquipment().getId(EquipmentSlot.WEAPON);
        final AxeDefinitions[] values = AxeDefinitions.VALUES;
        for (final AxeDefinitions def : values) {
            if (level < def.getLevelRequired()) continue;
            if (weapon == def.getItemId()) {
                return Optional.of(new AxeResult(def, player.getEquipment().getContainer(), 3, player.getWeapon()));
            }
            for (int slot = 0; slot < 28; slot++) {
                final Item item = inventory.get(slot);
                if (item == null || item.getId() != def.getItemId()) {
                    continue;
                }
                return Optional.of(new AxeResult(def, player.getInventory().getContainer(), slot, item));
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean start() {
        final Optional<Woodcutting.AxeResult> optionalAxe = getAxe(player);
        if (!optionalAxe.isPresent()) {
            player.sendMessage("You do not have an axe which you have the woodcutting level to use.");
            return false;
        }
        this.axe = optionalAxe.get();
        if (definitions.getLevel() > player.getSkills().getLevel(Skills.WOODCUTTING)) {
            player.sendMessage("You need a Woodcutting level of at least " + definitions.getLevel() + " to chop down this tree.");
            return false;
        }
        if (!check()) {
            return false;
        }
        player.sendFilteredMessage("You swing your axe at the " + tree.getName(player).toLowerCase() + ".");
        delay(axe.getDefinitions().getCutTime());
        return true;
    }

    private boolean check() {
        if (definitions.getLevel() > player.getSkills().getLevel(Skills.WOODCUTTING)) {
            player.sendMessage("You need a Woodcutting level of at least " + definitions.getLevel() + " to chop down this tree.");
            return false;
        }
        if (!player.getInventory().hasFreeSlots()) {
            player.sendFilteredMessage("Not enough space in your inventory.");
            return false;
        }
        return tree.exists();
    }

    @Override
    public boolean process() {
        if (ticks++ % 4 == 0) player.setAnimation(axe.getDefinitions().getEmote());
        return check();
    }

    public boolean success() {
        assert definitions.getSpeed() > 0;
        final int level = player.getSkills().getLevel(Skills.WOODCUTTING) + (player.inArea("Woodcutting Guild") ? 7 : 0);
        final int advancedLevels = level - definitions.getSpeed();
        return Math.min(Math.round(advancedLevels * 0.8F) + 20, 70) > Utils.random(100);
    }

    @Override
    public int processWithDelay() {
        if (!success()) {
            return axe.getDefinitions().getCutTime();
        }
        addLog();
        if (Utils.random(definitions.getFallChance() - 1) == 0) {
            if (definitions == TreeDefinitions.SULLIUSCEP_TREE) {
                final int currentIndex = player.getVarManager().getBitValue(SULLIUSCEP_INDEX_VARBIT);
                player.getVarManager().sendBit(SULLIUSCEP_INDEX_VARBIT, currentIndex < 5 ? currentIndex + 1 : 0);
            } else {
                player.getPacketDispatcher().sendSoundEffect(TREE_FALL_SOUND);
                if (onFall == null) {
                    final WorldObject stump = new WorldObject(TreeDefinitions.getStumpId(tree.getId()), tree.getType(), tree.getRotation(), tree.getX(), tree.getY(), tree.getPlane());
                    World.spawnObject(stump);
                    WorldTasksManager.schedule(() -> World.spawnObject(tree), definitions.getRespawnDelay());
                } else {
                    onFall.run();
                }
            }
            player.setAnimation(Animation.STOP);
            return -1;
        }
        if (!player.getInventory().hasFreeSlots()) {
            player.setAnimation(Animation.STOP);
            player.sendFilteredMessage("Not enough space in your inventory.");
            return -1;
        }
        return axe.getDefinitions().getCutTime();
    }

    private void addLog() {
        if (definitions.equals(TreeDefinitions.WILLOW_TREE)) {
            player.getDailyChallengeManager().update(SkillingChallenge.CHOP_WILLOW_LOGS);
            player.getAchievementDiaries().update(LumbridgeDiary.CHOP_WILLOWS);
            player.getAchievementDiaries().update(FaladorDiary.CHOP_BURN_WILLOW_LOGS, 1);
        } else if (definitions.equals(TreeDefinitions.TEAK_TREE)) {
            if (tree.getX() == 3510 && tree.getY() == 3073) {
                player.getAchievementDiaries().update(DesertDiary.CHOP_TEAK_LOGS);
            }
            player.getAchievementDiaries().update(KaramjaDiary.CUT_A_TEAK_LOG);
            player.getAchievementDiaries().update(WesternProvincesDiary.CHOP_AND_BURN_TEAK_LOGS, 1);
        } else if (definitions.equals(TreeDefinitions.MAHOGANY_TREE)) {
            player.getDailyChallengeManager().update(SkillingChallenge.CHOP_MAHOGANY_LOGS);
            player.getAchievementDiaries().update(KaramjaDiary.CUT_A_MAHOGANY_LOG);
            player.getAchievementDiaries().update(WesternProvincesDiary.CHOP_AND_BURN_MAHOGANY_LOGS, 1);
            player.getAchievementDiaries().update(MorytaniaDiary.CHOP_AND_BURN_MAHOGANY_LOGS, 1);
            player.getAchievementDiaries().update(KourendDiary.CHOP_SOME_MAHOGANY);
        } else if (definitions.equals(TreeDefinitions.YEW_TREE)) {
            player.getAchievementDiaries().update(VarrockDiary.CHOP_AND_BURN_YEW_LOGS, 1);
            SherlockTask.CHOP_YEW_TREE.progress(player);
        } else if (definitions.equals(TreeDefinitions.OAK)) {
            player.getAchievementDiaries().update(LumbridgeDiary.CHOP_AND_BURN_LOGS, 1);
            player.getAchievementDiaries().update(FremennikDiary.CHOP_AND_BURN_OAK_LOGS, 1);
        } else if (definitions.equals(TreeDefinitions.MAGIC_TREE)) {
            player.getDailyChallengeManager().update(SkillingChallenge.CHOP_MAGIC_LOGS);
            player.getAchievementDiaries().update(LumbridgeDiary.CHOP_MAGIC_LOGS);
            player.getAchievementDiaries().update(WildernessDiary.CUT_AND_BURN_MAGIC_LOGS, 1);
        } else if (definitions.equals(TreeDefinitions.REDWOOD_TREE)) {
            player.getDailyChallengeManager().update(SkillingChallenge.CHOP_REDWOOD_LOGS);
            player.getAchievementDiaries().update(KourendDiary.CHOP_REDWOODS);
        } else if (definitions.equals(TreeDefinitions.TREE) && tree.getName().equalsIgnoreCase("dying tree")) {
            player.getAchievementDiaries().update(VarrockDiary.CHOP_DOWN_DYING_TREE);
        }
        player.getSkills().addXp(Skills.WOODCUTTING, definitions.getXp());
        awardNest();
        //Incinerate the logs
        if (definitions.getLogsId() != -1 && axe.getItem().getCharges() > 0 && axe.getDefinitions() == AxeDefinitions.INFERNAL && Utils.random(2) == 0) {
            player.getChargesManager().removeCharges(axe.getItem(), 1, axe.getContainer(), axe.getSlot());
            player.setGraphics(BURN_GFX);
            final Firemaking fm = Objects.requireNonNull(Firemaking.MAP.get(definitions.getLogsId()));
            player.sendSound(2596);
            player.getSkills().addXp(Skills.FIREMAKING, fm.getXp() / 2.0F);
        } else {
            if (definitions.getLogsId() != -1) {
                player.sendFilteredMessage("You get some " + logName + ".");
                int amount = player.getPerkManager().isValid(PerkWrapper.LUMBERJACK) && Utils.random(100) <= 20 ? 2 : 1;
                if (amount == 2) {
                    player.getPerkManager().consume(PerkWrapper.LUMBERJACK);
                }
                if (definitions.getLogsId() == 1511 && player.getEquipment().getItem(EquipmentSlot.HELMET) != null && player.getEquipment().getItem(EquipmentSlot.HELMET).getName().contains("Kandarin headgear")) {
                    amount += 1;
                }
                player.getInventory().addItem(definitions.getLogsId(), amount).onFailure(remainder -> World.spawnFloorItem(remainder, player));
            }
        }
        ClueItem.roll(player, definitions.getClueNestBaseChance(), player.getSkills().getLevel(Skills.WOODCUTTING), ClueItem::getClueNest);
    }

    private void awardNest() {
        if (definitions == TreeDefinitions.REDWOOD_TREE || definitions == TreeDefinitions.SULLIUSCEP_TREE) {
            return;
        }
        final boolean isWearingWoodcuttingCape = SkillcapePerk.WOODCUTTING.isEffective(player);
        // woodcutting cape grants 10% higher chance to drop a nest, hence * 0.9
        if (Utils.random((int) (BIRD_NEST_CHANCE * (isWearingWoodcuttingCape ? 0.9 : 1))) == 0) {
            final BirdNests.Nests nest = BirdNests.Nests.rollRandomNest(true);
            //Nests are uncommon and considering the afk-ness of the skill, they should remain on the ground for a longer period of time.
            World.spawnFloorItem(new Item(nest.getNestItemId()), player, 500, 0);
            player.sendMessage("<col=FF0000>A bird's nest falls out of the tree.</col>");
        }
    }

    @Override
    public void stop() {
        player.setAnimation(Animation.STOP);
    }

    public static final class AxeResult {
        private final AxeDefinitions definitions;
        private final Container container;
        private final int slot;
        private final Item item;

        public AxeResult(final AxeDefinitions definitions, final Container container, final int slot, final Item item) {
            this.definitions = definitions;
            this.container = container;
            this.slot = slot;
            this.item = item;
        }

        public AxeDefinitions getDefinitions() {
            return this.definitions;
        }

        public Container getContainer() {
            return this.container;
        }

        public int getSlot() {
            return this.slot;
        }

        public Item getItem() {
            return this.item;
        }
    }
}
