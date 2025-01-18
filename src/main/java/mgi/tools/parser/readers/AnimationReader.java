package mgi.tools.parser.readers;

import mgi.tools.parser.TypeReader;
import mgi.types.Definitions;
import mgi.types.config.AnimationDefinitions;

import java.util.ArrayList;
import java.util.Map;

/**
 * @author Tommeh | 27/01/2020 | 17:46
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class AnimationReader implements TypeReader {
    @Override
    public ArrayList<Definitions> read(final Map<String, Object> properties) throws NoSuchFieldException, IllegalAccessException {
        final ArrayList<Definitions> defs = new ArrayList<>();
        if (properties.containsKey("inherit")) {
            final Object inherit = properties.get("inherit");
            defs.add(AnimationDefinitions.get(((Long) inherit).intValue()));
        } else {
            defs.add(new AnimationDefinitions());
        }
        for (final Definitions definition : defs) {
            final AnimationDefinitions animation = (AnimationDefinitions) definition;
            TypeReader.setFields(animation, properties);
        }
        return defs;
    }

    @Override
    public String getType() {
        return "animation";
    }
}
