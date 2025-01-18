package com.zenyte.game.content.minigame.duelarena;

import java.util.function.Function;

import static com.zenyte.game.util.Currency.MILLION;

/**
 * @author Tommeh | 30-11-2018 | 15:31
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public enum Tax {
    SMALL_TAX(pot -> pot < MILLION.get(10), 0.25F),
    MEDIUM_TAX(pot -> pot >= MILLION.get(10) && pot < MILLION.get(100), 0.5F),
    LARGE_TAX(pot -> pot >= MILLION.get(100), 1.0F);
    private static final Tax[] VALUES = values();
    private final Function<Long, Boolean> function;
    private final float rate;

    Tax(final Function<Long, Boolean> function, final float rate) {
        this.function = function;
        this.rate = rate;
    }

    public static Tax getTax(final int amount) {
        for (final Tax tax : VALUES) {
            if (tax.getFunction().apply((long) amount)) {
                return tax;
            }
        }
        throw new RuntimeException("Unable to find tax rate for " + amount + " amount.");
    }

    @Override
    public String toString() {
        return String.format("%.2f", rate) + "%";
    }

    public Function<Long, Boolean> getFunction() {
        return this.function;
    }

    public float getRate() {
        return this.rate;
    }
}
