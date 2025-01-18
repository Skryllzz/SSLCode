package com.zenyte.game.item;

import com.zenyte.game.world.entity.player.Player;

/**
 * @author Kris | 10. nov 2017 : 23:58.26
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 */
public interface ItemOnItemAction {
    void handleItemOnItemAction(final Player player, final Item from, final Item to, final int fromSlot, final int toSlot);

    int[] getItems();

    default boolean allItems() {
        return false;
    }

    default ItemPair[] getMatchingPairs() {
        return null;
    }

    /**
     * @return Whether or not you want the script to include same item id usage; by default it's set to false.
     * Ex. using id of 900 on another item with id of 900 will not launch the script unless this
     * is true.
     */
    default boolean includeEquivalentItems() {
        return false;
    }


    final class ItemPair {
        final int left;
        final int right;

        public ItemPair(final int left, final int right) {
            this.left = left;
            this.right = right;
        }

        public static ItemPair of(final int left, final int right) {
            return new ItemPair(left, right);
        }

        public int getLeft() {
            return this.left;
        }

        public int getRight() {
            return this.right;
        }
    }
}
