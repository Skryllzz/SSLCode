package com.zenyte.plugins.item;

import com.google.common.primitives.Ints;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.item.pluginextensions.ItemPlugin;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.entity.player.container.Container;
import com.zenyte.game.world.entity.player.container.impl.ContainerType;
import com.zenyte.plugins.dialogue.ItemChat;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Kris | 06/06/2019 18:46
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class RunecraftingPouch extends ItemPlugin {
    public static final List<Integer> pouches = Arrays.stream(Pouch.POUCHES).mapToInt(p -> p.id).boxed().collect(Collectors.toList());


    private enum Pouch {
        SMALL(ItemId.SMALL_POUCH, 3, 0),
        MEDIUM(ItemId.MEDIUM_POUCH, 6, 25),
        LARGE(ItemId.LARGE_POUCH, 9, 50),
        GIANT(ItemId.GIANT_POUCH, 12, 75);
        private final int id;
        private final int capacity;
        private final int level;
        private static final Pouch[] POUCHES = values();

        Pouch(final int id, final int capacity, final int level) {
            this.id = id;
            this.capacity = capacity;
            this.level = level;
        }
    }

    @Override
    public void handle() {
        bind("Check", (player, item, container, slotId) -> {
            final int pureEssence = item.getNumericAttribute("pure essence").intValue();
            final int runeEssence = item.getNumericAttribute("rune essence").intValue();
            player.sendMessage("Your pouch currently contains " + runeEssence + " rune essence and " + pureEssence + " pure essence.");
        });
        bind("Fill", (player, item, container, slotId) -> {
            fill(player, item, container, slotId);
        });
        bind("Empty", (player, item, container, slotId) -> {
            final int pureEssence = item.getNumericAttribute("pure essence").intValue();
            final int runeEssence = item.getNumericAttribute("rune essence").intValue();
            if (pureEssence <= 0 && runeEssence <= 0) {
                player.sendMessage("Your pouch is empty.");
                return;
            }
            if (pureEssence > 0) {
                final int succeededPure = player.getInventory().addItem(new Item(ItemId.PURE_ESSENCE, pureEssence)).getSucceededAmount();
                item.setAttribute("pure essence", pureEssence - succeededPure);
            }
            if (runeEssence > 0) {
                final int succeededRune = player.getInventory().addItem(new Item(ItemId.RUNE_ESSENCE, runeEssence)).getSucceededAmount();
                item.setAttribute("rune essence", runeEssence - succeededRune);
            }
        });
    }

    public static void fill(final Player player, final Item item, final Container container, final int slotId) {
        final RunecraftingPouch.Pouch pouch = Objects.requireNonNull(Utils.findMatching(Pouch.POUCHES, p -> p.id == item.getId()));
        if (player.getSkills().getLevelForXp(Skills.RUNECRAFTING) < pouch.level) {
            player.getDialogueManager().start(new ItemChat(player, item, "You need a Runecrafting level of at least " + pouch.level + " to use this pouch."));
            return;
        }
        final int currentCount = item.getNumericAttribute("pure essence").intValue() + item.getNumericAttribute("rune essence").intValue();
        if (currentCount >= pouch.capacity) {
            player.sendMessage("Your pouch is already full of essence.");
            return;
        }
        final int pureInInventory = container.getAmountOf(ItemId.PURE_ESSENCE);
        final int runeInInventory = container.getAmountOf(ItemId.RUNE_ESSENCE);
        if (pureInInventory == 0 && runeInInventory == 0) {
            player.sendMessage("You have no essence in your " + container.getType() + " to fill the pouch with.");
            return;
        }
        final int addablePure = Math.min(pouch.capacity - currentCount, pureInInventory);
        final int addableRune = Math.min(pouch.capacity - currentCount - addablePure, runeInInventory);
        if (addablePure == 0 && addableRune == 0) {
            return;
        }
        final boolean isBank = container.getType() == ContainerType.BANK;
        final int addedPure = isBank ? player.getBank().remove(new Item(ItemId.PURE_ESSENCE, addablePure)).getSucceededAmount() : player.getInventory().deleteItem(new Item(ItemId.PURE_ESSENCE, addablePure)).getSucceededAmount();
        final int addedRune = isBank ? player.getBank().remove(new Item(ItemId.RUNE_ESSENCE, addableRune)).getSucceededAmount() : player.getInventory().deleteItem(new Item(ItemId.RUNE_ESSENCE, addableRune)).getSucceededAmount();
        if (isBank) {
            player.getBank().refreshContainer();
        }
        item.setAttribute("pure essence", item.getNumericAttribute("pure essence").intValue() + addedPure);
        item.setAttribute("rune essence", item.getNumericAttribute("rune essence").intValue() + addedRune);
    }

    @Override
    public int[] getItems() {
        return Ints.toArray(pouches);
    }
}
