package com.zenyte.plugins.object;

import com.zenyte.game.content.achievementdiary.diaries.KourendDiary;
import com.zenyte.game.content.skills.construction.objects.chapel.Altar;
import com.zenyte.game.content.skills.magic.Spellbook;
import com.zenyte.game.content.skills.prayer.actions.Bones;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemOnObjectAction;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.ObjectId;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.plugins.dialogue.PlainChat;

import java.util.ArrayList;

/**
 * @author Tommeh | 9-3-2019 | 16:10
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class ZenyteAltarObject implements ObjectAction, ItemOnObjectAction {

    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        if (option.equals("Pray")) {
            if (player.getPrayerManager().getPrayerPoints() >= player.getSkills().getLevelForXp(Skills.PRAYER)) {
                player.sendMessage("You already have full prayer points.");
                return;
            }
            player.lock();
            player.sendMessage("You pray to the gods...");
            player.sendSound(2674);
            player.setAnimation(AltarOPlugin.PRAY_ANIM);
            WorldTasksManager.schedule(() -> {
                player.getAchievementDiaries().update(KourendDiary.PRAY_AT_ALTAR);
                player.getPrayerManager().restorePrayerPoints(99);
                player.sendMessage("... and recharge your prayer.");
                player.unlock();
            });
        } else if (option.equalsIgnoreCase("Regular")) {
            setSpellbook(player, Spellbook.NORMAL);
        } else if (option.equalsIgnoreCase("Ancient")) {
            setSpellbook(player, Spellbook.ANCIENT);
        } else if (option.equalsIgnoreCase("Lunar")) {
            setSpellbook(player, Spellbook.LUNAR);
        } else if (option.equalsIgnoreCase("Arceuus")) {
            setSpellbook(player, Spellbook.ARCEUUS);
        } else if (option.equals("Change-spellbook")) {
            //Option no longer available but leaving code in it just in case we need in the future.
            player.getDialogueManager().start(new Dialogue(player) {

                @Override
                public void buildDialogue() {
                    options("Select a spellbook", getOptions(player)).onOptionOne(() -> {
                        if (!isDifferentSpellbook(player, 1)) {
                            setKey(10);
                            return;
                        }
                        setKey(5);
                        player.getCombatDefinitions().setSpellbook(Spellbook.NORMAL, true);
                    }).onOptionTwo(() -> {
                        if (!isDifferentSpellbook(player, 2)) {
                            setKey(10);
                            return;
                        }
                        setKey(5);
                        player.getCombatDefinitions().setSpellbook(Spellbook.ANCIENT, true);
                    }).onOptionThree(() -> {
                        if (!isDifferentSpellbook(player, 3)) {
                            setKey(10);
                            return;
                        }
                        setKey(5);
                        player.getCombatDefinitions().setSpellbook(Spellbook.LUNAR, true);
                    }).onOptionFour(() -> {
                        if (!isDifferentSpellbook(player, 4)) {
                            setKey(10);
                            return;
                        }
                        setKey(5);
                        player.getCombatDefinitions().setSpellbook(Spellbook.ARCEUUS, true);
                    });
                    plain(5, "Your spellbook has been successfully switched.");
                    plain(10, "You are already on this spellbook currently, choose a different one.");
                }
            });
        }
    }

    private static void setSpellbook(final Player player, final Spellbook spellbook) {
        if (player.getCombatDefinitions().getSpellbook().equals(spellbook)) {
            player.getDialogueManager().start(new PlainChat(player, "You are already on this spellbook currently, choose a different one."));
            return;
        }
        player.getCombatDefinitions().setSpellbook(spellbook, true);
        player.getDialogueManager().start(new PlainChat(player, "Your spellbook has been successfully switched."));
    }

    private static String[] getOptions(final Player player) {
        final String[] options = new String[4];
        for (int index = 0; index < Spellbook.VALUES.length; index++) {
            final Spellbook spellbook = Spellbook.VALUES[index];
            final StringBuilder builder = new StringBuilder();
            if (player.getCombatDefinitions().getSpellbook().equals(spellbook)) {
                builder.append("<str>");
                builder.append(spellbook);
                builder.append("</str>");
            } else {
                builder.append(spellbook);
            }
            options[index] = builder.toString();
        }
        return options;
    }

    private static boolean isDifferentSpellbook(final Player player, final int option) {
        final Spellbook spellbook = Spellbook.VALUES[option - 1];
        return !player.getCombatDefinitions().getSpellbook().equals(spellbook);
    }

    @Override
    public void handleItemOnObjectAction(final Player player, final Item item, int slot, final WorldObject object) {
        if (object.getId() == 411 && object.getRegionId() != 11835) {
            player.sendMessage("Nothing interesting happens.");
            return;
        }
        final Bones bone = Bones.getBone(item.getId());
        if (bone == null) {
            player.sendMessage("You can only offer bones to the gods.");
            return;
        }
        final int[][] offsets = Altar.LIGHTER_OFFSETS[object.getRotation()];
        final Location leftBurner = new Location(object.getX() + (offsets[0][0]), object.getY() + (offsets[0][1]), object.getPlane());
        final Location rightBurner = new Location(object.getX() + (offsets[1][0]), object.getY() + (offsets[1][1]), object.getPlane());
        player.getActionManager().setAction(new Altar.OfferingAction(bone, item, object, leftBurner, rightBurner));
    }

    @Override
    public Object[] getItems() {
        final ArrayList<Object> list = new ArrayList<>(Bones.VALUES.length);
        for (final Bones bone : Bones.VALUES) {
            for (final Item b : bone.getBones()) {
                list.add(b.getId());
            }
        }
        return list.toArray(new Object[0]);
    }

    @Override
    public Object[] getObjects() {
        return new Object[] { ObjectId.ALTAR_18258 };
    }
}
