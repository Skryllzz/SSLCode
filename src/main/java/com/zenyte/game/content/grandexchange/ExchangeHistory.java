package com.zenyte.game.content.grandexchange;

/**
 * @author Tommeh | 18 sep. 2018 | 17:18:22
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class ExchangeHistory {
    private final int id;
    private final int quantity;
    private final int price;
    private final ExchangeType type;

    public ExchangeHistory(final int id, final int quantity, final int price, final ExchangeType type) {
        this.id = id;
        this.quantity = quantity;
        this.price = price;
        this.type = type;
    }

    public int getId() {
        return this.id;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public int getPrice() {
        return this.price;
    }

    public ExchangeType getType() {
        return this.type;
    }
}
