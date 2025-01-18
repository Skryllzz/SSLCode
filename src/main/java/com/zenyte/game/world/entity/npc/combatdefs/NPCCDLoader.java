package com.zenyte.game.world.entity.npc.combatdefs;

import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * @author Kris | 18/11/2018 02:58
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class NPCCDLoader {
    private static final Logger log = LoggerFactory.getLogger(NPCCDLoader.class);
    public static Int2ObjectOpenHashMap<NPCCombatDefinitions> definitions;

    public static void parse() {
        try {
            final BufferedReader br = new BufferedReader(new FileReader("data/npcs/combatDefs.json"));
            final NPCCombatDefinitions[] array = World.getGson().fromJson(br, NPCCombatDefinitions[].class);
            Utils.populateMap(array, definitions = new Int2ObjectOpenHashMap<>(array.length), NPCCombatDefinitions::getId);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    public static void save() {
        final ArrayList<NPCCombatDefinitions> defs = new ArrayList<>(definitions.values());
        defs.sort(Comparator.comparingInt(NPCCombatDefinitions::getId));
        final String toJson = World.getGson().toJson(defs);
        try {
            final PrintWriter pw = new PrintWriter("data/npcs/combatDefs.json", StandardCharsets.UTF_8);
            pw.println(toJson);
            pw.close();
        } catch (final Exception e) {
            log.error("", e);
        }
    }

    public static NPCCombatDefinitions get(final int id) {
        return definitions.get(id);
    }
}
