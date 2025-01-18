package com.zenyte.game.shop;

import com.zenyte.game.item.Item;
import com.zenyte.game.world.entity.player.Player;
//@RequiredArgsConstructor

/**
 * @author Kris | 26. sept 2018 : 02:35:12
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>
 */
enum ShopCurrency implements CurrencyPalette {
    COINS {
        @Override
        public int getAmount(Player player) {
            return player.getInventory().getAmountOf(id());
        }

        @Override
        public boolean isStackable() {
            return true;
        }

        @Override
        public boolean isPhysical() {
            return true;
        }

        @Override
        public int id() {
            return 995;
        }

        @Override
        public void remove(Player player, int amount) {
            player.getInventory().deleteItem(new Item(id(), amount));
        }

        @Override
        public void add(Player player, int amount) {
            player.getInventory().addItem(new Item(id(), amount));
        }
    },
    TOKKUL {
        @Override
        public int getAmount(Player player) {
            return player.getInventory().getAmountOf(id());
        }

        @Override
        public boolean isStackable() {
            return true;
        }

        @Override
        public boolean isPhysical() {
            return true;
        }

        @Override
        public int id() {
            return 6529;
        }

        @Override
        public void remove(Player player, int amount) {
            player.getInventory().deleteItem(new Item(id(), amount));
        }

        @Override
        public void add(Player player, int amount) {
            player.getInventory().addItem(new Item(id(), amount));
        }
    },
    GOLD_NUGGETS {
        @Override
        public int getAmount(Player player) {
            return player.getInventory().getAmountOf(id());
        }

        @Override
        public boolean isStackable() {
            return true;
        }

        @Override
        public boolean isPhysical() {
            return true;
        }

        @Override
        public int id() {
            return 12012;
        }

        @Override
        public void remove(Player player, int amount) {
            player.getInventory().deleteItem(new Item(id(), amount));
        }

        @Override
        public void add(Player player, int amount) {
            player.getInventory().addItem(new Item(id(), amount));
        }

        @Override
        public String toString() {
            return "golden nuggets";
        }
    },
    CASTLE_WARS_TICKETS {
        @Override
        public int getAmount(final Player player) {
            return player.getInventory().getAmountOf(id());
        }

        @Override
        public boolean isStackable() {
            return true;
        }

        @Override
        public boolean isPhysical() {
            return true;
        }

        @Override
        public int id() {
            return 4067;
        }

        @Override
        public void remove(final Player player, final int amount) {
            player.getInventory().deleteItem(new Item(id(), amount));
        }

        @Override
        public void add(final Player player, final int amount) {
            player.getInventory().addItem(new Item(id(), amount));
        }

        @Override
        public String toString() {
            return "castle wars tickets";
        }
    },
    MARK_OF_GRACE {
        @Override
        public int getAmount(Player player) {
            return player.getInventory().getAmountOf(id());
        }

        @Override
        public boolean isStackable() {
            return true;
        }

        @Override
        public boolean isPhysical() {
            return true;
        }

        @Override
        public int id() {
            return 11849;
        }

        @Override
        public void remove(Player player, int amount) {
            player.getInventory().deleteItem(new Item(id(), amount));
        }

        @Override
        public void add(Player player, int amount) {
            player.getInventory().addItem(new Item(id(), amount));
        }

        @Override
        public String toString() {
            return "marks of grace";
        }
    },
    VOTE_POINTS {
        @Override
        public int getAmount(Player player) {
            return player.getNumericAttribute("vote_points").intValue();
        }

        @Override
        public boolean isStackable() {
            return false;
        }

        @Override
        public boolean isPhysical() {
            return false;
        }

        @Override
        public int id() {
            return -1;
        }

        @Override
        public void remove(Player player, int amount) {
            final int currentAmount = player.getNumericAttribute("vote_points").intValue();
            player.addAttribute("vote_points", currentAmount - amount);
        }

        @Override
        public void add(Player player, int amount) {
            final int currentAmount = player.getNumericAttribute("vote_points").intValue();
            player.addAttribute("vote_points", currentAmount + amount);
        }

        @Override
        public String toString() {
            return "vote points";
        }
    },
    LOYALTY_POINTS {
        @Override
        public int getAmount(Player player) {
            return player.getLoyaltyManager().getLoyaltyPoints();
        }

        @Override
        public boolean isStackable() {
            return false;
        }

        @Override
        public boolean isPhysical() {
            return false;
        }

        @Override
        public int id() {
            return -1;
        }

        @Override
        public void remove(Player player, int amount) {
            final int currentAmount = player.getLoyaltyManager().getLoyaltyPoints();
            player.getLoyaltyManager().setLoyaltyPoints(Math.max(0, currentAmount - amount));
        }

        @Override
        public void add(Player player, int amount) {
            final int currentAmount = player.getLoyaltyManager().getLoyaltyPoints();
            player.getLoyaltyManager().setLoyaltyPoints(Math.min(currentAmount + amount, Integer.MAX_VALUE));
        }

        @Override
        public String toString() {
            return "loyalty points";
        }
    };
    private final String formattedString = name().toLowerCase();

    @Override
    public String toString() {
        return formattedString;
    }
}
