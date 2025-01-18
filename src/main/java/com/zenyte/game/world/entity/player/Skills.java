package com.zenyte.game.world.entity.player;

import com.google.common.collect.ImmutableList;
import com.google.gson.annotations.Expose;
import com.zenyte.Constants;
import com.zenyte.api.client.query.adventurerslog.AdventurersLogIcon;
import com.zenyte.game.content.achievementdiary.DiaryReward;
import com.zenyte.game.content.follower.impl.SkillingPet;
import com.zenyte.game.content.minigame.wintertodt.RewardCrate;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.packet.PacketDispatcher;
import com.zenyte.game.ui.InterfacePosition;
import com.zenyte.game.util.AccessMask;
import com.zenyte.game.util.IntListUtils;
import com.zenyte.game.world.broadcasts.BroadcastType;
import com.zenyte.game.world.broadcasts.WorldBroadcasts;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.masks.UpdateFlag;
import com.zenyte.game.world.entity.player.container.Container;
import com.zenyte.game.world.region.Area;
import com.zenyte.game.world.region.area.SlayerTower;
import com.zenyte.game.world.region.area.plugins.ExperiencePlugin;
import com.zenyte.plugins.dialogue.LevelUpDialogue;
import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntLists;
import mgi.types.config.enums.EnumDefinitions;

import java.util.List;

public final class Skills {
    public static final int COUNT = 23;
    public static final int MAXIMUM_EXP = 200000000;
    public static final int ATTACK = 0;
    public static final int DEFENCE = 1;
    public static final int STRENGTH = 2;
    public static final int HITPOINTS = 3;
    public static final int RANGED = 4;
    public static final int PRAYER = 5;
    public static final int MAGIC = 6;
    public static final int COOKING = 7;
    public static final int WOODCUTTING = 8;
    public static final int FLETCHING = 9;
    public static final int FISHING = 10;
    public static final int FIREMAKING = 11;
    public static final int CRAFTING = 12;
    public static final int SMITHING = 13;
    public static final int MINING = 14;
    public static final int HERBLORE = 15;
    public static final int AGILITY = 16;
    public static final int THIEVING = 17;
    public static final int SLAYER = 18;
    public static final int FARMING = 19;
    public static final int RUNECRAFTING = 20;
    public static final int HUNTER = 21;
    public static final int CONSTRUCTION = 22;
    public static final IntLists.UnmodifiableList SKILL_IDS = IntListUtils.range(ATTACK, CONSTRUCTION);
    public static final int SKILL_GUIDE_INTERFACE = 214;
    public static final String[] SKILLS = {"Attack", "Defence", "Strength", "Hitpoints", "Ranged", "Prayer", "Magic", "Cooking", "Woodcutting", "Fletching", "Fishing", "Firemaking", "Crafting", "Smithing", "Mining", "Herblore", "Agility", "Thieving", "Slayer", "Farming", "Runecraft", "Hunter", "Construction"};
    private static final List<Integer> MILESTONES = ImmutableList.of(50, 100, 150, 200, 300, 400, 500, 600, 700, 800, 900, 1000, 1100, 1200, 1300, 1400, 1500, 1600, 1700, 1800, 1900, 2000, 2100, 2200);
    private static final Graphics LEVEL_UP_99 = new Graphics(1388);
    private static final Graphics LEVEL_UP = new Graphics(199);
    private static final int[] EXPERIENCE_TABLE = new int[100];

    static {
        int points = 0;
        int output;
        for (int lvl = 1; lvl <= 99; lvl++) {
            points += Math.floor(lvl + 300.0 * Math.pow(2.0, lvl / 7.0));
            output = points >> 2;
            EXPERIENCE_TABLE[lvl] = output - 1;
        }
    }

    private final transient Player player;
    @Expose
    private final int[] level = new int[23];
    @Expose
    private final double[] experience = new double[23];
    private final transient Int2DoubleMap fakeExperienceMap = new Int2DoubleOpenHashMap();

    public Skills(final Player player) {
        this.player = player;
        resetAll();
    }

    public static int getXPForLevel(final int level) {
        int points = 0;
        int output = 0;
        for (int lvl = 1; lvl <= level; lvl++) {
            points += Math.floor(lvl + 300.0 * Math.pow(2.0, lvl / 7.0));
            if (lvl >= level) {
                return output;
            }
            output = points >> 2;
        }
        return 0;
    }

    public static int getLevelForXP(final double experience) {
        if (experience >= EXPERIENCE_TABLE[99]) {
            return 99;
        }
        for (int level = 99; level >= 0; level--) {
            if (EXPERIENCE_TABLE[level] < experience) {
                return level + 1;
            }
        }
        return 1;
    }

    public static String getSkillName(final int skill) {
        return EnumDefinitions.get(680).getStringValue(skill);
    }

    public static int getSkill(final String skill) {
        return EnumDefinitions.get(680).getKeyForValue(skill);
    }

    public static int getInternalId(final int id) {
        return EnumDefinitions.get(681).getKeyForValue(id);
    }

    public static int getReverseInternalId(final int id) {
        return EnumDefinitions.get(681).getIntValue(id);
    }

    public static boolean isCombatSkill(final int skill) {
        return skill <= 6;
    }

    public void sendQueuedFakeExperienceDrops() {
        if (fakeExperienceMap.isEmpty()) {
            return;
        }
        final PacketDispatcher dispatcher = player.getPacketDispatcher();
        int count = 0;
        for (final Int2DoubleMap.Entry entry : fakeExperienceMap.int2DoubleEntrySet()) {
            final int skill = entry.getIntKey();
            final int experience = (int) Math.min(16000, entry.getDoubleValue());
            dispatcher.sendClientScript(2091, skill, experience);
            //No more than 7 different skills will be displayed in a tick anyways, therefore skip the rest.
            if (++count >= 7) {
                break;
            }
        }
        fakeExperienceMap.clear();
    }

    public void resetAll() {
        for (int i = 0; i < 23; i++) {
            level[i] = 1;
            experience[i] = 0;
        }
        level[HITPOINTS] = 10;
        experience[HITPOINTS] = 1184;
        level[HERBLORE] = 3;
        experience[HERBLORE] = 175;
    }

    public boolean checkRealLevel(final int skill, final int levelRequired, final String action) {
        if (getLevelForXp(skill) < levelRequired) {
            player.sendMessage("You need at least level " + levelRequired + " " + SKILLS[skill] + " to " + action + ".");
            return false;
        }
        return true;
    }

    public boolean checkLevel(final int skill, final int levelRequired, final String action) {
        if (getLevel(skill) < levelRequired) {
            player.sendMessage("You need at least level " + levelRequired + " " + SKILLS[skill] + " to " + action + ".");
            return false;
        }
        return true;
    }

    public void drainCombatSkills(final int amount) {
        for (int i = 0; i < 7; i++) {
            if (i == 3 || i == 5) {
                continue;
            }
            final int level = getLevel(i);
            setLevel(i, level < 2 ? 0 : (level - 2));
        }
    }

    /**
     * Drains the specified skill for a given percentage and returns the amount it drained for.
     *
     * @param skill
     * @param percentage
     * @return
     */
    public int drainSkill(final int skill, final double percentage, final int minimumDrain) {
        final int currentLevel = getLevel(skill);
        final int amt = Math.max(minimumDrain, (int) (currentLevel * (percentage / 100)));
        final int newLevel = currentLevel - amt;
        setLevel(skill, Math.max(0, newLevel));
        return amt;
    }

    /**
     * Drains the specified skill for a given amount and returns the amount drained; cannot go below zero.
     *
     * @param skill
     * @param amount
     * @return
     */
    public int drainSkill(final int skill, final int amount) {
        final int currentLevel = getLevel(skill);
        final int amt = (currentLevel - amount) < 0 ? currentLevel : amount;
        final int newLevel = currentLevel - amt;
        setLevel(skill, newLevel);
        return amt;
    }

    /**
     * Boosts the specified skill for a given percentage.
     *
     * @param skill
     * @param percentage
     * @return
     */
    public void boostSkill(final int skill, final double percentage) {
        final int maxLevel = getLevelForXp(skill);
        final int currentLevel = getLevel(skill);
        final int level = currentLevel > maxLevel ? maxLevel : currentLevel;
        final int amt = (int) (level * (percentage / 100));
        final int newLevel = level + amt;
        setLevel(skill, newLevel);
    }

    /**
     * Boosts the specified skill for a given amount.
     *
     * @param skill
     * @param amount
     * @return
     */
    public void boostSkill(final int skill, final int amount) {
        final int maxLevel = getLevelForXp(skill);
        final int currentLevel = getLevel(skill);
        final int level = currentLevel > maxLevel ? maxLevel : currentLevel;
        final int newLevel = level + amount;
        if (newLevel > currentLevel) {
            setLevel(skill, newLevel);
        }
    }

    public int getLevel(final int skill) {
        return level[skill];
    }

    public void setLevel(final int skill, final int level) {
        this.level[skill] = level;
        refresh(skill);
    }

    public double getExperience(final int skill) {
        return experience[skill];
    }

    /**
     * Method has been optimized to better suit a private server; Fast level lookup times.
     *
     * @param skill the skill for which to get the base level.
     * @return the base level in the given skill.
     */
    public int getLevelForXp(final int skill) {
        final double experience = this.experience[skill];
        if (experience >= EXPERIENCE_TABLE[99]) {
            return 99;
        }
        for (int level = 99; level >= 0; level--) {
            if (EXPERIENCE_TABLE[level] < experience) {
                return level + 1;
            }
        }
        return 1;
    }

    public int getTotalXp() {
        int total = 0;
        for (int i = 0; i < 23; i++) {
            total += experience[i];
        }
        return total;
    }

    public int getTotalLevel() {
        int total = 0;
        for (int i = 0; i < 23; i++) {
            total += getLevelForXp(i);
        }
        return total;
    }

    /**
     * @param skill the skill xp is being added to.
     * @param exp   the xp being added to the skill.
     */
    public void addXp(final int skill, double exp) {
        addXp(skill, exp, true);
    }

    public void addXp(final int skill, final double exp, final boolean additionalBoost) {
        addXp(skill, exp, additionalBoost, false);
    }

    /**
     * @param skill           the skill xp is being added to.
     * @param exp             the xp being added to the skill.
     * @param additionalBoost whether or not to award additional exp from boosts such as skilling outfits.
     */
    public void addXp(final int skill, double exp, final boolean additionalBoost, final boolean fake) {
        final Area area = player.getArea();
        ExperiencePlugin plugin = area instanceof ExperiencePlugin ? ((ExperiencePlugin) area) : null;
        final boolean xpLocked = plugin != null && !plugin.enabled();
        if (xpLocked) {
            fakeExperienceMap.put(skill, fakeExperienceMap.get(skill) + exp);
            return;
        }
        final double baseXp = exp;
        final int oldLevel = getLevelForXp(skill);
        final double oldExperience = getExperience(skill);
        exp *= player.getExperienceRate(skill);
        if (Constants.BOOSTED_XP || player.getVariables().getBonusXP() > 0) {
            exp *= (1.0F + Constants.BOOSTED_XP_MODIFIER / 100.0);
        }
        if (additionalBoost) {
            exp *= getAdditionalBoost(skill);
        }
        if (skill == FARMING && player.inArea("Falador Farming Area") && DiaryReward.FALADOR_SHIELD2.eligibleFor(player)) {
            exp *= 1.1;
        }
        if (skill == SLAYER && player.inArea(SlayerTower.class)) {
            if (DiaryReward.MORYTANIA_LEGS4.eligibleFor(player)) {
                exp *= 1.1;
            } else if (DiaryReward.MORYTANIA_LEGS3.eligibleFor(player)) {
                exp *= 1.075;
            } else if (DiaryReward.MORYTANIA_LEGS2.eligibleFor(player)) {
                exp *= 1.05;
            } else if (DiaryReward.MORYTANIA_LEGS1.eligibleFor(player)) {
                exp *= 1.025;
            }
        }
        if (!fake) {
            experience[skill] += exp;
            if (experience[skill] >= MAXIMUM_EXP) {
                experience[skill] = MAXIMUM_EXP;
            }
        }
        final int lv = getLevelForXp(skill);
        if (lv > oldLevel) {
            level[skill] += lv - oldLevel;
            final int levels = lv - oldLevel;
            if (lv == 99) {
                player.sendMessage("Congratulations, you've reached the highest possible " + getSkillName(skill) + " level of 99.");
                WorldBroadcasts.broadcast(player, BroadcastType.LVL_99, skill);
                if (player.getNumericAttribute("first_99_skill").intValue() == -1) {
                    player.addAttribute("first_99_skill", skill);
                }
                if (isMaxed()) {
                    WorldBroadcasts.broadcast(player, BroadcastType.MAXED);
                }
            } else {
                if (levels > 1) {
                    player.sendMessage("Congratulations, you just advanced " + levels + " " + getSkillName(skill) + " levels. You are now level " + getLevelForXp(skill) + ".");
                } else {
                    player.sendMessage("Congratulations, you just advanced your " + getSkillName(skill) + " level. You are now level " + getLevelForXp(skill) + ".");
                }
            }
            //player.sendMessage(lv == 99 ? "Congratulatins, you've reached the highest possible " + getSkillName(skill) + " level of 99."
            //		: "Congratulations, you just advanced " + getSkillName(skill) + " level. You are now level " + lv + ".");
            if (MILESTONES.contains(getTotalLevel())) {
                player.sendMessage("Congratulations, you've reached a total level of " + getTotalLevel() + ".");
            }
            if (getTotalLevel() % 500 == 0) {
                player.sendAdventurersEntry(AdventurersLogIcon.OVERALL_SKILLING, player.getName() + " has just reached a total level of " + getTotalLevel() + "!");
            }
            if (getLevelForXp(skill) >= player.getNumericAttribute(GameSetting.LEVEL_99_DIALOGUES.toString()).intValue()) {
                LevelUpDialogue.sendSkillDialogue(player, skill);
            }
            player.setGraphics(lv == 99 ? LEVEL_UP_99 : LEVEL_UP);
            if (skill <= MAGIC) {
                player.getVarManager().sendBit(13027, getCombatLevel());
                if (skill == HITPOINTS) {
                    player.heal(lv - oldLevel);
                }
                if (skill == PRAYER) {
                    player.getPrayerManager().restorePrayerPoints(lv - oldLevel);
                }
            }
            player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
        } else {
            if (oldExperience < 200000000 && experience[skill] >= 200000000) {
                WorldBroadcasts.broadcast(player, BroadcastType.XP_200M, skill);
            }
        }
        if (fake || oldExperience >= 200000000) {
            fakeExperienceMap.put(skill, fakeExperienceMap.get(skill) + (exp / Math.max(1, player.getVarManager().getValue(3504))));
        } else {
            refresh(skill);
        }
        if (skill == RUNECRAFTING || fake) {
            //rc pets are handled differently
            return;
        }
        final SkillingPet pet = SkillingPet.getBySkill(skill);
        if (pet == null) {
            return;
        }
        //The chance will be 1/5m xp on average on 1x xp rate.
        final double chance = baseXp > 2500000 ? 0.5F : (baseXp / 5000000);
        pet.roll(player, (int) (1.0F / chance) - 1);
    }

    private float getAdditionalBoost(final int skill) {
        return ExperienceBoost.getBoost(player, skill);
    }

    public void refresh(final int skill) {
        if (skill < 0 || skill > 23) {
            return;
        }
        player.getPacketDispatcher().sendSkillUpdate(skill);
    }

    public void refresh() {
        final PacketDispatcher dispatcher = player.getPacketDispatcher();
        for (int i = 0; i < 23; i++) {
            dispatcher.sendSkillUpdate(i);
        }
    }

    /**
     * Gets the combat level of the player.
     *
     * @return
     */
    public int getCombatLevel() {
        final int attack = getLevelForXp(ATTACK);
        final int defence = getLevelForXp(DEFENCE);
        final int strength = getLevelForXp(STRENGTH);
        final int hp = getLevelForXp(HITPOINTS);
        final int prayer = getLevelForXp(PRAYER);
        final int ranged = getLevelForXp(RANGED);
        final int magic = getLevelForXp(MAGIC);
        double baseLevel = ((defence + hp + Math.floor(prayer / 2.0F)) * 0.25F);
        final double melee = (attack + strength) * 0.325;
        final double ranger = Math.floor(ranged * 1.5) * 0.325;
        final double mage = Math.floor(magic * 1.5) * 0.325;
        if (melee >= ranger && melee >= mage) {
            baseLevel += melee;
        } else if (ranger >= melee && ranger >= mage) {
            baseLevel += ranger;
        } else if (mage >= melee && mage >= ranger) {
            baseLevel += mage;
        }
        return (int) Math.min(126, baseLevel);
    }

    public void sendSkillMenu(final int skill, final int subCategory) {
        player.getInterfaceHandler().sendInterface(InterfacePosition.CENTRAL, SKILL_GUIDE_INTERFACE);
        player.getVarManager().sendBit(4371, skill);
        player.getVarManager().sendBit(4372, subCategory);
        player.getPacketDispatcher().sendComponentSettings(SKILL_GUIDE_INTERFACE, 8, 0, 75, AccessMask.CLICK_OP1);
        player.getTemporaryAttributes().put("viewingSkill", skill);
    }

    public void setSkill(final int skill, final int lvl, final double exp) {
        forceSkill(skill, lvl, exp);
        refresh(skill);
    }

    public void forceSkill(final int skill, final int lvl, final double exp) {
        level[skill] = lvl;
        experience[skill] = exp;
    }

    public void setSkills(final Skills skills) {
        for (int skillId = 0; skillId < 23; skillId++) {
            forceSkill(skillId, skills.getLevel(skillId), skills.getExperience(skillId));
        }
    }

    public boolean isMaxed() {
        for (int i = 0; i < EnumDefinitions.get(680).getSize(); i++) {
            if (i == CONSTRUCTION) {
                continue;
            }
            if (getLevelForXp(i) < 99) {
                return false;
            }
        }
        return true;
    }

    public Player getPlayer() {
        return this.player;
    }

    public double[] getExperience() {
        return this.experience;
    }

    public enum ExperienceBoost {
        LUMBERJACK(Skills.WOODCUTTING, 0.005F, new BoostItem(0.004F, 10941), new BoostItem(0.008F, 10939), new BoostItem(0.006F, 10940), new BoostItem(0.002F, 10933)),
        PROSPECTOR(Skills.MINING, 0.005F, new BoostItem(0.004F, 12013), new BoostItem(0.008F, 12014, ItemId.VARROCK_ARMOUR_4), new BoostItem(0.006F, 12015), new BoostItem(0.002F, 12016)),
        ANGLER(Skills.FISHING, 0.005F, new BoostItem(0.004F, 13258), new BoostItem(0.008F, 13259), new BoostItem(0.006F, 13260), new BoostItem(0.002F, 13261)),
        FARMER(Skills.FARMING, 0.005F, new BoostItem(0.004F, 13646, 13647, 21253, 21254), new BoostItem(0.008F, 13642, 13643), new BoostItem(0.006F, 13640, 13641), new BoostItem(0.002F, 13644, 13645)),
        PYROMANCER(Skills.FIREMAKING, 0.005F, new BoostItem(0.004F, RewardCrate.PYROMANCER_HOOD), new BoostItem(0.008F, RewardCrate.PYROMANCER_GARB), new BoostItem(0.006F, RewardCrate.PYROMANCER_ROBE), new BoostItem(0.002F, RewardCrate.PYROMANCER_BOOTS));
        private static final ExperienceBoost[] values = values();
        private final int skillId;
        private final float setBoost;
        private final BoostItem[] items;

        ExperienceBoost(final int skillId, final float setBoost, final BoostItem... items) {
            this.skillId = skillId;
            this.setBoost = setBoost;
            this.items = items;
        }

        private static float getBoost(final Player player, final int skill) {
            float totalBoost = 1.0F;
            ExperienceBoost boost;
            for (int i = values.length - 1; i >= 0; i--) {
                boost = values[i];
                if (boost.skillId != skill) continue;
                final Container equipment = player.getEquipment().getContainer();
                BoostItem item;
                int id;
                int count = 0;
                for (int j = boost.items.length - 1; j >= 0; j--) {
                    item = boost.items[j];
                    for (int k = item.ids.length - 1; k >= 0; k--) {
                        id = item.ids[k];
                        if (equipment.contains(id, 1)) {
                            totalBoost += item.boost;
                            count++;
                            break;
                        }
                    }
                }
                if (count == boost.items.length) totalBoost += boost.setBoost;
                break;
            }
            return totalBoost;
        }

        public boolean hasFull(final Player player) {
            final Container equipment = player.getEquipment().getContainer();
            int count = 0;
            for (int j = items.length - 1; j >= 0; j--) {
                final Skills.ExperienceBoost.BoostItem item = items[j];
                for (int index = item.ids.length - 1; index >= 0; index--) {
                    if (equipment.contains(item.ids[index], 1)) {
                        count++;
                        break;
                    }
                }
            }
            return count == items.length;
        }

        private static final class BoostItem {
            private final int[] ids;
            private final float boost;

            private BoostItem(final float boost, final int... ids) {
                this.ids = ids;
                this.boost = boost;
            }
        }
    }
}
