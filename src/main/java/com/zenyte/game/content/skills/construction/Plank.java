package com.zenyte.game.content.skills.construction;

import com.zenyte.game.item.Item;

/**
 * @author Tommeh | 29 sep. 2018 | 11:40:52
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public enum Plank {
    WOOD(100, new Item(1511), new Item(960)),
    OAK(250, new Item(1521), new Item(8778)),
    TEAK(500, new Item(6333), new Item(8780)),
    MAHOGANY(1500, new Item(6332), new Item(8782));
    public static final Plank[] values = values();
    private final int cost;
    private final Item base;
    private final Item product;

    Plank(final int cost, final Item base, final Item product) {
        this.cost = cost;
        this.base = base;
        this.product = product;
    }

    public int getCost() {
        return this.cost;
    }

    public Item getBase() {
        return this.base;
    }

    public Item getProduct() {
        return this.product;
    }
}
