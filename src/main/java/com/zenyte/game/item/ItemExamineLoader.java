package com.zenyte.game.item;

import com.zenyte.game.parser.Parse;
import com.zenyte.game.util.Examine;
import com.zenyte.game.util.LabelledExamine;
import com.zenyte.game.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class ItemExamineLoader implements Parse {
    public static final Map<Integer, Examine> DEFINITIONS = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(ItemExamineLoader.class);

    public static void loadExamines() {
        try {
            new ItemExamineLoader().parse();
        } catch (final Throwable e) {
            log.error("", e);
        }
    }

    @Override
    public void parse() throws Throwable {
        final BufferedReader br = new BufferedReader(new FileReader("data/examines/Item examines.json"));
        final Examine[] examines = World.getGson().fromJson(br, Examine[].class);
        for (final Examine def : examines) {
            if (def != null) DEFINITIONS.put(def.getId(), def);
        }
        parseOverrides();
    }

    private void parseOverrides() throws Throwable {
        final BufferedReader br = new BufferedReader(new FileReader("data/examines/Forced item examines.json"));
        final LabelledExamine[] examines = World.getGson().fromJson(br, LabelledExamine[].class);
        for (final LabelledExamine def : examines) {
            DEFINITIONS.put(def.getId(), def);
        }
    }
}
