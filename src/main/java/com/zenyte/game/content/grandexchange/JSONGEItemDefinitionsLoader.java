package com.zenyte.game.content.grandexchange;

import com.zenyte.Constants;
import com.zenyte.api.client.query.SendItemPrices;
import com.zenyte.game.parser.Parse;
import com.zenyte.game.world.World;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;

public class JSONGEItemDefinitionsLoader implements Parse {
    public static final Int2ObjectMap<JSONGEItemDefinitions> definitions = new Int2ObjectOpenHashMap<>();
    private static final Logger log = LoggerFactory.getLogger(JSONGEItemDefinitionsLoader.class);

    public static void main(String[] args) {
        try {
            new JSONGEItemDefinitionsLoader().parse();
            final BufferedReader reader = new BufferedReader(new FileReader(new File("newprices.txt")));
            String line;
            while ((line = reader.readLine()) != null) {
                final String[] split = line.split("\t");
                final int id = Integer.parseInt(split[0].trim());
                final int price = Integer.parseInt(split[1].replaceAll("[,.]", "").trim());
                JSONGEItemDefinitions definition = lookup(id);
                if (definition == null) {
                    definition = new JSONGEItemDefinitions();
                    definition.setId(id);
                    definition.setTime(Instant.now());
                }
                definition.setPrice(price);
            }
            save();
        } catch (Throwable e) {
            log.error("", e);
        }
    }

    public static void save() {
        final ArrayList<JSONGEItemDefinitions> list = new ArrayList<>(definitions.values());
        list.sort(Comparator.comparingInt(JSONGEItemDefinitions::getId));
        final String toJson = World.getGson().toJson(list);
        try {
            final PrintWriter pw = new PrintWriter("data/grandexchange/prices.json", StandardCharsets.UTF_8);
            pw.println(toJson);
            pw.close();
        } catch (final Exception e) {
            log.error("", e);
        }
        if (Constants.WORLD_PROFILE.isDevelopment() || Constants.WORLD_PROFILE.isPrivate() || Constants.WORLD_PROFILE.isBeta()) {
            return;
        }
        new SendItemPrices(list).execute();
    }

    /**
     * Looks up a definition based on the key value in the map.
     *
     * @param itemId the key value we using to search for the respective
     *               definition.
     * @return
     */
    public static JSONGEItemDefinitions lookup(int itemId) {
        return definitions.get(itemId);
    }

    @Override
    public void parse() throws Throwable {
        final BufferedReader br = new BufferedReader(new FileReader("data/grandexchange/prices.json"));
        final JSONGEItemDefinitions[] priceDefinitions = World.getGson().fromJson(br, JSONGEItemDefinitions[].class);
        for (final JSONGEItemDefinitions def : priceDefinitions) {
            if (def == null) {
                continue;
            }
            definitions.put(def.getId(), def);
        }
    }
}
