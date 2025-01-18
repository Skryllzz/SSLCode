package com.zenyte.game.content.godwars.objects;

import com.zenyte.game.item.ItemId;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Setting;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.object.NullObjectID;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;

/**
 * @author Tommeh | 24-3-2019 | 13:55
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class GodwarsSaradominRopeObject implements ObjectAction {

    private static final Location MAIN_AREA = new Location(2912, 5300, 2);

    private static final Location FIRST_MAIN_AREA = new Location(2920, 5276, 1);

    private static final Location SARA_FIRST_AREA = new Location(2914, 5300, 1);

    private static final Location SARA_FINAL_AREA = new Location(2920, 5274, 0);

    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        switch(object.getId()) {
            case //first rope down
            26561:
                if (option.equals("Tie-rope")) {
                    if (player.getInventory().containsItem(ItemId.ROPE)) {
                        player.getSettings().toggleSetting(Setting.SARADOMIN_TOP_ROPE);
                        player.getInventory().deleteItem(ItemId.ROPE, 1);
                    } else {
                        player.sendMessage("You aren't carrying a rope with you.");
                    }
                } else {
                    if (player.getSkills().getLevel(Skills.AGILITY) < 70) {
                        player.sendMessage("You need an Agility level of at least 70 to climb down this rope.");
                        return;
                    }
                    player.useStairs(828, SARA_FIRST_AREA, 1, 0, "You climb down the rope.");
                }
                return;
            case //first rope up
            26371:
                if (player.getSkills().getLevel(Skills.AGILITY) < 70) {
                    player.sendMessage("You need an Agility level of at least 70 to climb up this rope.");
                    return;
                }
                player.useStairs(828, MAIN_AREA, 1, 0, "You climb up the rope.");
                break;
            case //second rope down
            26562:
                if (option.equals("Tie-rope")) {
                    if (player.getInventory().containsItem(ItemId.ROPE)) {
                        player.getSettings().toggleSetting(Setting.SARADOMIN_BOTTOM_ROPE);
                        player.getInventory().deleteItem(ItemId.ROPE, 1);
                    } else {
                        player.sendMessage("You aren't carrying a rope with you.");
                    }
                } else {
                    if (player.getSkills().getLevel(Skills.AGILITY) < 70) {
                        player.sendMessage("You need an Agility level of at least 70 to climb down this rope.");
                        return;
                    }
                    player.useStairs(828, SARA_FINAL_AREA, 1, 0, "You climb down the rope.");
                }
                break;
            case //second rope up
            26375:
                if (player.getSkills().getLevel(Skills.AGILITY) < 70) {
                    player.sendMessage("You need an Agility level of at least 70 to climb up this rope.");
                    return;
                }
                player.useStairs(828, FIRST_MAIN_AREA, 1, 0, "You climb up the rope.");
                break;
        }
    }

    @Override
    public Object[] getObjects() {
        return new Object[] { NullObjectID.NULL_26561, NullObjectID.NULL_26371, NullObjectID.NULL_26562, NullObjectID.NULL_26375 };
    }
}
