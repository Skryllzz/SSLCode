package com.zenyte.game.content.grandexchange;

import com.zenyte.game.parser.scheduled.ScheduledExternalizable;
import com.zenyte.game.util.TimeUnit;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import mgi.types.config.items.ItemDefinitions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author Kris | 16/08/2019 15:31
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class GrandExchangePriceManager implements ScheduledExternalizable {

    private static final Logger log = LoggerFactory.getLogger(GrandExchangePriceManager.class);
    private static final Object $lock = new Object();
    private static final float PERCENTAGE_PER_DAY = 5.0F;
    private static final int HOUR_INTERVAL = 6;
    private static GrandExchangePriceManager singleton;
    private final Int2ObjectMap<ItemPrice> prices = new Int2ObjectOpenHashMap<>();
    private final Int2ObjectMap<List<CompletedOffer>> completedOffers = new Int2ObjectOpenHashMap<>();

    public GrandExchangePriceManager() {
        //Setting the singleton through initializer as this whole class is automatically instantiated through reflection.
        assert singleton == null;
        singleton = this;
    }

    public static void post(final int id, final int amount, final int price) {
        if (singleton == null) {
            return;
        }
        synchronized ($lock) {
            singleton.completedOffers.computeIfAbsent(id, i -> new ArrayList<>()).add(new CompletedOffer(amount, price));
        }
    }

    public static void rewrite() {
        if (singleton == null) {
            return;
        }
        singleton.write();
    }

    public static void forcePrice(final int id, final int value) {
        assert ItemDefinitions.getOrThrow(id).isGrandExchange();
        assert singleton != null;
        singleton.prices.computeIfAbsent(id, a -> new ItemPrice(id, value)).price = value;
        singleton._write();
    }

    private final void updatePrices() {
        Int2ObjectMap<List<CompletedOffer>> completedOffers;
        synchronized ($lock) {
            completedOffers = new Int2ObjectOpenHashMap<>(this.completedOffers);
            this.completedOffers.clear();
        }
        final float percentageMultiplier = PERCENTAGE_PER_DAY / (24.0F / HOUR_INTERVAL) / 100.0F;
        completedOffers.forEach((id, offerList) -> {
            final GrandExchangePriceManager.ItemPrice itemPrice = prices.computeIfAbsent(id.intValue(), a -> new ItemPrice(id, ItemDefinitions.getSellPrice(id)));
            double currentPrice = itemPrice.price;
            double totalPrice = 0;
            int count = 0;
            for (final GrandExchangePriceManager.CompletedOffer offer : offerList) {
                totalPrice += offer.price * offer.amount;
                count += offer.amount;
            }
            double averageUpdatedPrice = totalPrice / count;
            if (averageUpdatedPrice > currentPrice) {
                if (averageUpdatedPrice > (currentPrice + (currentPrice * percentageMultiplier))) {
                    averageUpdatedPrice = (currentPrice + (currentPrice * percentageMultiplier));
                }
                currentPrice = averageUpdatedPrice;
            } else if (averageUpdatedPrice < currentPrice) {
                if (averageUpdatedPrice < (currentPrice - (currentPrice * percentageMultiplier))) {
                    averageUpdatedPrice = (currentPrice - (currentPrice * percentageMultiplier));
                }
                currentPrice = averageUpdatedPrice;
            }
            itemPrice.price = currentPrice;
        });
    }

    @Override
    public Logger getLog() {
        return log;
    }

    @Override
    public int writeInterval() {
        return (int) TimeUnit.HOURS.toMinutes(HOUR_INTERVAL);
    }

    @Override
    public void ifFileNotFoundOnRead() {
        log.error("File not found: " + getClass().getName() + ": " + path());
        for (final JSONGEItemDefinitions price : JSONGEItemDefinitionsLoader.definitions.values()) {
            prices.put(price.getId(), new ItemPrice(price.getId(), price.getPrice()));
        }
        write();
    }

    @Override
    public void read(final BufferedReader reader) {
        final GrandExchangePriceManager.ItemPrice[] prices = getGSON().fromJson(reader, ItemPrice[].class);
        for (final GrandExchangePriceManager.ItemPrice price : prices) {
            this.prices.put(price.id, price);
        }
    }

    @Override
    public void write() {
        updatePrices();
        _write();
    }

    private void _write() {
        final ArrayList<GrandExchangePriceManager.ItemPrice> list = new ArrayList<>(this.prices.values());
        list.sort(Comparator.comparingInt(a -> a.id));
        out(getGSON().toJson(list));
        list.forEach(itemPrice -> {
            JSONGEItemDefinitions price = JSONGEItemDefinitionsLoader.lookup(itemPrice.id);
            if (price == null) {
                JSONGEItemDefinitionsLoader.definitions.put(itemPrice.id, price = new JSONGEItemDefinitions(itemPrice.id, ItemDefinitions.getOrThrow(itemPrice.id).getName(), 0, Instant.now()));
            }
            price.setPrice((int) Math.round(itemPrice.price));
        });
        JSONGEItemDefinitionsLoader.save();
    }

    @Override
    public String path() {
        return "data/grandexchange/preciseprices.json";
    }

    private static final class ItemPrice {
        private final int id;
        private double price;

        public ItemPrice(final int id, final double price) {
            this.id = id;
            this.price = price;
        }
    }


    private static final class CompletedOffer {
        private final int amount;
        private final int price;

        public CompletedOffer(final int amount, final int price) {
            this.amount = amount;
            this.price = price;
        }
    }
}
