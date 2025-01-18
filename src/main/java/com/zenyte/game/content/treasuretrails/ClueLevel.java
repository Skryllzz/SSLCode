package com.zenyte.game.content.treasuretrails;

import com.zenyte.game.content.treasuretrails.rewards.ClueReward;
import com.zenyte.game.content.treasuretrails.rewards.ClueRewardTable;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.util.Colour;
import com.zenyte.game.world.entity.player.Emote;
import com.zenyte.game.world.entity.player.Player;
import kotlin.ranges.IntRange;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * @author Kris | 29. march 2018 : 20:04.19
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public enum ClueLevel {
    BEGINNER(new IntRange(1, 3), ClueReward.getBeginnerTable(), -1, null),
    EASY(new IntRange(2, 4), ClueReward.getEasyTable(), 500, player -> addItem(player, new Item(ItemId.LARGE_SPADE))),
    MEDIUM(new IntRange(3, 5), ClueReward.getMediumTable(), 400, player -> addItem(player, new Item(ItemId.CLUELESS_SCROLL))),
    HARD(new IntRange(4, 6), ClueReward.getHardTable(), 300, player -> {
        if (!player.getEmotesHandler().isUnlocked(Emote.URI_TRANSFORM)) {
            player.getEmotesHandler().unlock(Emote.URI_TRANSFORM);
            player.sendMessage(Colour.RED.wrap("Congratulations! You have unlocked the Uri Transform milestone reward!"));
        }
    }),
    ELITE(new IntRange(5, 7), ClueReward.getEliteTable(), 200, player -> addItem(player, new Item(ItemId.HEAVY_CASKET))),
    MASTER(new IntRange(6, 8), ClueReward.getMasterTable(), 100, player -> addItem(player, new Item(ItemId.SCROLL_SACK)));
    public static final ClueLevel[] values = values();
    private final IntRange stepsRange;
    private final ClueRewardTable table;
    private final int milestoneRequirement;
    private final Consumer<Player> milestoneRewardConsumer;

    ClueLevel(final IntRange stepsRange, final ClueRewardTable table, final int milestoneRequirement, final Consumer<Player> milestoneRewardConsumer) {
        this.stepsRange = stepsRange;
        this.table = table;
        this.milestoneRequirement = milestoneRequirement;
        this.milestoneRewardConsumer = milestoneRewardConsumer;
    }

    private static void addItem(@NotNull final Player player, @NotNull final Item item) {
        if (player.containsItem(item.getId())) {
            return;
        }
        player.sendMessage(Colour.RED.wrap("Congratulations! You have unlocked the " + item.getName().toLowerCase() + " milestone reward."));
        player.getInventory().addOrDrop(item);
        player.getCollectionLog().add(item);
    }

    public IntRange getStepsRange() {
        return this.stepsRange;
    }

    public ClueRewardTable getTable() {
        return this.table;
    }

    public int getMilestoneRequirement() {
        return this.milestoneRequirement;
    }

    public Consumer<Player> getMilestoneRewardConsumer() {
        return this.milestoneRewardConsumer;
    }
}
