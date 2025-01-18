package com.zenyte.game.parser.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zenyte.game.parser.Parse;
import mgi.types.config.items.JSONItemDefinitions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class JSONItemDefinitionsLoader implements Parse {
    public static final Map<Integer, JSONItemDefinitions> DEFINITIONS = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(JSONItemDefinitionsLoader.class);
    private static final Gson GSON = new Gson();

    public static void main(final String[] args) {
        try {
            new JSONItemDefinitionsLoader().parse();
        } catch (final Throwable e) {
            log.error("", e);
        }
    }

    /**
     * Looks up a definition based on the key value in the map.
     *
     * @param itemId the key value we using to search for the respective
     *               definition.
     * @return
     */
    public static JSONItemDefinitions lookup(final int itemId) {
        return getDefinitions().get(itemId);
    }

    /**
     * Gets the definitions map.
     *
     * @return
     */
    public static Map<Integer, JSONItemDefinitions> getDefinitions() {
        return DEFINITIONS;
    }

    public static void save() {
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        final Collection<JSONItemDefinitions> values = DEFINITIONS.values();
        final Comparator<JSONItemDefinitions> comparator = (npc1, npc2) -> {
            if (npc1 == null || npc2 == null) {
                return 0;
            }
            return npc1.getId() > npc2.getId() ? 1 : -1;
        };
        final List<JSONItemDefinitions> list = new ArrayList<>(values);
        Collections.sort(list, comparator);
        final String toJson = gson.toJson(list);
        try {
            final PrintWriter pw = new PrintWriter("data/items/ItemDefinitions.json", StandardCharsets.UTF_8);
            pw.println(toJson);
            pw.close();
        } catch (final Exception e) {
            log.error("", e);
        }
    }

    @Override
    public void parse() throws Throwable {
        final BufferedReader reader = new BufferedReader(new FileReader("data/items/ItemDefinitions.json"));
        final JSONItemDefinitions[] definitions = GSON.fromJson(reader, JSONItemDefinitions[].class);
        for (final JSONItemDefinitions def : definitions) {
            if (def != null) {
                DEFINITIONS.put(def.getId(), def);
            }
        }
    }
}
