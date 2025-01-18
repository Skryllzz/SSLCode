package com.zenyte.plugins.renewednpc;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.content.bountyhunter.BountyHunter;
import com.zenyte.game.item.Item;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.npc.NpcId;
import com.zenyte.game.world.entity.npc.actions.NPCPlugin;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.impl.Inventory;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.plugins.dialogue.PlainChat;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import mgi.types.config.items.ItemDefinitions;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kris | 07/05/2019 19:42
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class EdgevilleEmblemTrader extends NPCPlugin {
    @Override
    public void handle() {
        bind("Rewards", (player, npc) -> GameInterface.BOUNTY_HUNTER_STORE.open(player));
        bind("Talk-to", (player, npc) -> player.getDialogueManager().start(new Dialogue(player, npc) {
            @Override
            public void buildDialogue() {
                npc("Hello, wanderer.");
                npc("Don't suppose you've come across any strange... emblems or artifacts along your journey?");
                if (hasEmblem(player)) {
                    player("Yes, yes I have.");
                    npc("Would you like to sell them all to me for " + Utils.format(getEmblemsCost(player)) + " Bounty Hunter points?");
                    options("Sell all mysterious emblems?", new DialogueOption("Sell all emblems.", () -> sellEmblems(player)), new DialogueOption("No, keep the emblems."));
                    return;
                }
                player("Not that I've seen.");
                npc("If you do, please do let me know. I'll reward you handsomely.");
                final ObjectArrayList<Dialogue.DialogueOption> optionList = new ObjectArrayList<>();
                optionList.add(new DialogueOption("What rewards have you got?", () -> GameInterface.BOUNTY_HUNTER_STORE.open(player)));
                if (player.getVariables().isSkulled()) {
                    optionList.add(new DialogueOption("Can you make my PK skull last longer?", () -> promptSkull(player)));
                } else {
                    optionList.add(new DialogueOption("Can I have a PK skull, please?", () -> promptSkull(player)));
                }
                optionList.add(new DialogueOption("That's nice.", key(25)));
                options(TITLE, optionList.toArray(new DialogueOption[0]));
                player(25, "That's nice.");
            }
        }));
        bind("Skull", (player, npc) -> promptSkull(player));
    }

    private final void promptSkull(@NotNull final Player player) {
        player.getDialogueManager().finish();
        player.getDialogueManager().start(new Dialogue(player) {
            @Override
            public void buildDialogue() {
                options("Obtain a PK skull?", new DialogueOption("Yes, skull me.", () -> player.getVariables().setSkull(true)), new DialogueOption("No, don't skull me."));
            }
        });
    }

    private final boolean hasEmblem(@NotNull final Player player) {
        final Inventory inventory = player.getInventory();
        for (int i = 0; i < 28; i++) {
            final Item item = inventory.getItem(i);
            if (item == null || !(item.getId() >= 12746 && item.getId() <= 12756)) {
                continue;
            }
            return true;
        }
        return false;
    }

    private final int getEmblemsCost(@NotNull final Player player) {
        final Inventory inventory = player.getInventory();
        int cost = 0;
        for (int i = 0; i < 28; i++) {
            final Item item = inventory.getItem(i);
            if (item == null || !(item.getId() >= 12746 && item.getId() <= 12756)) {
                continue;
            }
            cost += item.getAmount() * Emblem.getCost(item.getId());
        }
        return cost;
    }

    private final void sellEmblems(@NotNull final Player player) {
        final Inventory inventory = player.getInventory();
        final BountyHunter bounty = player.getBountyHunter();
        for (int i = 0; i < 28; i++) {
            final Item item = inventory.getItem(i);
            if (item == null || !(item.getId() >= 12746 && item.getId() <= 12756)) {
                continue;
            }
            final int count = inventory.deleteItem(item).getSucceededAmount();
            final int cost = count * Emblem.getCost(item.getId());
            bounty.setPoints(bounty.getPoints() + cost);
        }
        player.getDialogueManager().start(new PlainChat(player, "You exchange all your mysterious emblems for Bounty Hunter points. You now have " + Utils.format(bounty.getPoints()) + " Bounty Hunter points."));
    }

    @Override
    public int[] getNPCs() {
        return new int[]{NpcId.EMBLEM_TRADER, NpcId.EMBLEM_TRADER_7943};
    }


    private enum Emblem {
        TIER_ONE(12746, 50000),
        TIER_TWO(12748, 100000),
        TIER_THREE(12749, 200000),
        TIER_FOUR(12750, 400000),
        TIER_FIVE(12751, 750000),
        TIER_SIX(12752, 1200000),
        TIER_SEVEN(12753, 1750000),
        TIER_EIGHT(12754, 2500000),
        TIER_NINE(12755, 3500000),
        TIER_TEN(12756, 5000000);
        private static final Emblem[] values = values();
        private final int id;
        private final int cost;

        private static int getCost(final int item) {
            final int unnoted = ItemDefinitions.getOrThrow(item).getUnnotedOrDefault();
            for (final EdgevilleEmblemTrader.Emblem value : values) {
                if (value.id == unnoted) {
                    return value.cost;
                }
            }
            throw new IllegalStateException();
        }

        Emblem(final int id, final int cost) {
            this.id = id;
            this.cost = cost;
        }
    }
}
