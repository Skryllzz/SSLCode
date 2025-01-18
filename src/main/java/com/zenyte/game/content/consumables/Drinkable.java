package com.zenyte.game.content.consumables;

import com.zenyte.game.item.Item;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.GameSetting;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.entity.player.container.impl.Inventory;
import com.zenyte.game.world.region.Area;
import com.zenyte.game.world.region.area.plugins.DrinkablePlugin;
import org.apache.commons.lang3.ArrayUtils;

/**
 * @author Kris | 02/12/2018 13:05
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public interface Drinkable extends Consumable {
    int[] overloadSkills = new int[]{Skills.ATTACK, Skills.STRENGTH, Skills.DEFENCE, Skills.MAGIC, Skills.RANGED};
    Animation anim = new Animation(829);
    Item vial = new Item(229);
    Item cocktailGlass = new Item(2026);
    Item jug = new Item(1935);
    Item beerGlass = new Item(1919);
    SoundEffect sound = new SoundEffect(2401);

    static Restoration[] getRestorations(final float percentage, final int amount, final int... skills) {
        final Consumable.Restoration[] restorations = new Restoration[skills.length];
        for (int i = 0; i < skills.length; i++) {
            restorations[i] = new Restoration(skills[i], percentage, amount);
        }
        return restorations;
    }

    static int[] getAllSkillsExcluding(final int... exclude) {
        final int[] skills = new int[Skills.SKILLS.length - exclude.length];
        int index = 0;
        for (int i = 0; i < Skills.SKILLS.length; i++) {
            if (ArrayUtils.contains(exclude, i)) continue;
            skills[index++] = i;
        }
        return skills;
    }

    static void applyOverload(final Player player) {
        final int type = player.getVariables().getOverloadType();
        final int boost = type == 1 ? 4 : type == 2 ? 5 : 6;
        final float modifier = type == 1 ? 0.1F : type == 2 ? 0.13F : 0.16F;
        final Skills skills = player.getSkills();
        int currentLevel;
        int realLevel;
        for (final int skill : overloadSkills) {
            currentLevel = skills.getLevel(skill);
            realLevel = skills.getLevelForXp(skill);
            skills.setLevel(skill, (int) (Math.min(currentLevel, realLevel) + boost + (realLevel * modifier)));
        }
    }

    static void resetOverload(final Player player) {
        if (player.isDead() || player.isFinished() || !player.isRunning()) return;
        final Skills skills = player.getSkills();
        for (final int skill : overloadSkills) {
            skills.setLevel(skill, skills.getLevelForXp(skill));
        }
        player.heal(50);
        player.sendMessage("<col=ff0000>The effects of overload have worn off, and you feel normal again.");
    }

    String emptyMessage(Player player);

    int getDoses(int id);

    Item getItem(final int dose);

    int[] getIds();

    @Override
    default int healedAmount(Player player) {
        return 0;
    }

    @Override
    default void heal(final Player player) {
        final int heal = healedAmount(player);
        if (heal > 0) {
            player.heal(heal);
        }
    }

    @Override
    default int delay() {
        return 3;
    }

    @Override
    default void onConsumption(Player player) {
    }

    @Override
    default Animation animation() {
        return anim;
    }

    @Override
    default boolean canConsume(Player player) {
        return player.getVariables().getPotionDelay() <= 0;
    }

    @Override
    default void consume(final Player player, final Item item, final int slotId) {
        final Area area = player.getArea();
        if ((area instanceof DrinkablePlugin && !((DrinkablePlugin) area).drink(player, this)) || !canConsume(player)) {
            return;
        }
        final String name = item.getDefinitions().getName().toLowerCase().replaceAll("[([1-4])]", "").replaceAll(" potion", "");
        final Inventory inventory = player.getInventory();
        player.setUnprioritizedAnimation(animation());
        player.getPacketDispatcher().sendSoundEffect(sound);
        final int id = item.getId();
        final String startMessage = startMessage();
        if (startMessage != null) {
            player.sendFilteredMessage(String.format(startMessage, name + " potion"));
        }
        final String endMessage = endMessage(player);
        if (endMessage != null) {
            final int doses = getDoses(id) - 1;
            player.sendFilteredMessage(String.format(doses == 0 ? emptyMessage(player) : endMessage, doses, doses == 1 ? "" : "s"));
        }
        final Item leftover = leftoverItem(item.getId());
        if (leftover != null) {
            if (leftover.getId() == 229 && player.getNumericAttribute(GameSetting.SMASH_VIALS.toString()).intValue() == 1) {
                player.sendFilteredMessage("You quickly smash the empty vial using the trick a Barbarian taught you.");
                inventory.deleteItem(slotId, new Item(id, 1));
            } else {
                inventory.set(slotId, leftover);
            }
        } else {
            inventory.deleteItem(slotId, new Item(id, 1));
        }
        applyEffects(player);
    }

    @Override
    default void applyEffects(final Player player) {
        final int delay = delay();
        if (delay > 0) {
            player.getVariables().setPotionDelay(delay);
        }
        onConsumption(player);
        heal(player);
        final Consumable.Boost[] boosts = boosts();
        if (boosts != null) {
            for (Boost boost : boosts) {
                boost.apply(player);
            }
        }
    }
}
