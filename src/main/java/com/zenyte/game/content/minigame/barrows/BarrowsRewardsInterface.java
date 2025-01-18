package com.zenyte.game.content.minigame.barrows;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.content.achievementdiary.diaries.MorytaniaDiary;
import com.zenyte.game.item.Item;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.util.AccessMask;
import com.zenyte.game.util.Examine;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.action.combat.CombatUtilities;
import com.zenyte.game.world.entity.player.container.Container;
import com.zenyte.game.world.entity.player.container.impl.ContainerType;

import java.util.ArrayList;
import java.util.Optional;

/**
 * @author Kris | 21/10/2018 10:23
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@SuppressWarnings("unused")
public class BarrowsRewardsInterface extends Interface {
    @Override
    protected void attach() {
        put(3, "Examine");
    }

    @Override
    public void open(Player player) {
        player.getInterfaceHandler().closeInterfaces();

        final Barrows barrows = player.getBarrows();
        barrows.setLooted(true);
        barrows.refreshShaking();
        barrows.calculateLoot();
        barrows.getContainer().setFullUpdate(true);
        player.getPacketDispatcher().sendUpdateItemContainer(barrows.getContainer());
        if (CombatUtilities.hasAnyBarrowsSet(player)) {
            player.getAchievementDiaries().update(MorytaniaDiary.LOOT_BARROWS_CHEST);
        }
        player.getNotificationSettings().increaseKill("barrows");
        player.getNotificationSettings().sendBossKillCountNotification("barrows");

        player.getInterfaceHandler().sendInterface(getInterface());
        player.getPacketDispatcher().sendComponentSettings(getId(), getComponent("Examine"), 0, Container.getSize(ContainerType.BARROWS_CHEST), AccessMask.CLICK_OP10);
    }

    @Override
    public void close(final Player player, final Optional<GameInterface> replacement) {
        final ArrayList<Item> equipmentPieces = new ArrayList<>();
        player.getBarrows().getContainer().getItems().int2ObjectEntrySet().fastForEach(loot -> {
            // check if loot is barrows piece or amulet of the damned
            if (BarrowsWight.ALL_WIGHT_EQUIPMENT.contains(loot.getValue()) || loot.getValue().getId() == 12851) {
                equipmentPieces.add(loot.getValue());
            }
        });
        if (equipmentPieces.size() > 0) {
            final int chestCount = player.getNotificationSettings().getKillcount("barrows");
            final String icon = equipmentPieces.get(0).getId() + ".png"; // use the first piece as the adv log entry icon
            final ArrayList<String> equipmentPieceNames = new ArrayList<>(equipmentPieces.size());
            for (final Item piece : equipmentPieces) {
                equipmentPieceNames.add(piece.getName()); // no stream, you're welcome Kris
            }
            final String joinedEquipmentLootString = String.join(", ", equipmentPieceNames);
            player.sendAdventurersEntry(icon, player.getName() + " opened Barrows chest " + chestCount + " and received: " + joinedEquipmentLootString, false);
        }
        player.getBarrows().addLoot();
    }

    @Override
    protected void build() {
        bind("Examine", ((player, slotId, itemId, option) -> Examine.sendItemExamine(player, itemId)));
    }

    @Override
    public GameInterface getInterface() {
        return GameInterface.BARROWS_REWARDS;
    }

    @Override
    public boolean closeInCombat() {
        return false;
    }

}
