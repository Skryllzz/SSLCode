package mgi.tools.parser.readers;

import com.zenyte.game.util.Utils;
import mgi.tools.parser.TypeParser;
import mgi.tools.parser.TypeReader;
import mgi.types.Definitions;
import mgi.types.config.GraphicsDefinitions;

import java.util.ArrayList;
import java.util.Map;

/**
 * @author Tommeh | 14/04/2020 | 22:46
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class GraphicsReader implements TypeReader {
    @Override
    public ArrayList<Definitions> read(final Map<String, Object> properties) throws NoSuchFieldException, IllegalAccessException {
        final ArrayList<Definitions> defs = new ArrayList<>();
        if (properties.containsKey("inherit")) {
            final Object inherit = properties.get("inherit");
            final GraphicsDefinitions def = GraphicsDefinitions.get(((Long) inherit).intValue());
            defs.add(TypeParser.KRYO.copy(def));
        } else {
            defs.add(new GraphicsDefinitions());
        }
        for (final Definitions definition : defs) {
            final GraphicsDefinitions graphicsDef = (GraphicsDefinitions) definition;
            TypeReader.setFields(graphicsDef, properties);
            if (properties.containsKey("replacementcolours")) {
                final ArrayList<ArrayList<Long>> list = (ArrayList<ArrayList<Long>>) properties.get("replacementcolours");
                final short[] replacementColours = new short[list.size()];
                for (int index = 0; index < list.size(); index++) {
                    final ArrayList<Long> colours = list.get(index);
                    replacementColours[index] = (short) Utils.rgbToHSL16(colours.get(0).intValue(), colours.get(1).intValue(), colours.get(2).intValue());
                }
                graphicsDef.setReplacementColours(replacementColours);
            }
        }
        return defs;
    }

    @Override
    public String getType() {
        return "graphics";
    }
}
