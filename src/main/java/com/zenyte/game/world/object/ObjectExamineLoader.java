package com.zenyte.game.world.object;

import com.zenyte.game.parser.Parse;
import com.zenyte.game.util.Examine;
import com.zenyte.game.util.LabelledExamine;
import com.zenyte.game.world.DefaultGson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class ObjectExamineLoader implements Parse {
    public static final Map<Integer, Examine> DEFINITIONS = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(ObjectExamineLoader.class);

    public static void loadExamines() {
        try {
            new ObjectExamineLoader().parse();
        } catch (final Throwable e) {
            log.error("", e);
        }
    }

    @Override
    public void parse() throws Throwable {
        final BufferedReader br = new BufferedReader(new FileReader("data/examines/Object examines.json"));
        final Examine[] examines = DefaultGson.fromGson(br, Examine[].class);
        for (final Examine def : examines) {
            if (def != null) DEFINITIONS.put(def.getId(), def);
        }
        parseOverrides();
    }

    private void parseOverrides() throws Throwable {
        final BufferedReader br = new BufferedReader(new FileReader("data/examines/Forced object examines.json"));
        final LabelledExamine[] examines = DefaultGson.fromGson(br, LabelledExamine[].class);
        for (final LabelledExamine def : examines) {
            DEFINITIONS.put(def.getId(), def);
        }
    }
}
