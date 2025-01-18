package com.zenyte.game.parser.impl;

import com.zenyte.game.parser.Parse;
import com.zenyte.game.util.Examine;
import com.zenyte.game.util.LabelledExamine;
import com.zenyte.game.world.DefaultGson;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;

public class NPCExamineLoader implements Parse {
    public static final Int2ObjectOpenHashMap<Examine> DEFINITIONS = new Int2ObjectOpenHashMap<>();
    private static final Logger log = LoggerFactory.getLogger(NPCExamineLoader.class);

    public static void loadExamines() {
        try {
            new NPCExamineLoader().parse();
        } catch (final Throwable e) {
            log.error("", e);
        }
    }

    public static Examine get(final int npcId) {
        return DEFINITIONS.get(npcId);
    }

    @Override
    public void parse() throws Throwable {
        final BufferedReader br = new BufferedReader(new FileReader("data/examines/NPC examines.json"));
        final Examine[] examines = DefaultGson.fromGson(br, Examine[].class);
        for (final Examine def : examines) {
            if (def != null) {
                DEFINITIONS.put(def.getId(), def);
            }
        }
        parseOverrides();
    }

    private void parseOverrides() throws Throwable {
        final BufferedReader br = new BufferedReader(new FileReader("data/examines/Forced npc examines.json"));
        final LabelledExamine[] examines = DefaultGson.fromGson(br, LabelledExamine[].class);
        for (final LabelledExamine def : examines) {
            DEFINITIONS.put(def.getId(), def);
        }
    }
}
