package mgi.tools.parser;

import com.moandjiezana.toml.Toml;
import mgi.tools.parser.readers.*;
import mgi.types.Definitions;
import mgi.types.config.GraphicsDefinitions;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Tommeh | 22/01/2020 | 18:56
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public interface TypeReader {
    TypeReader[] readers = {new ItemReader(), new NPCReader(), new ObjectReader(), new EnumReader(),
            new StructReader(), new AnimationReader(), new SpriteReader(), new ComponentReader(),
            new GraphicsReader(), new VarbitReader()};
    Map<String, TypeReader> readersMap = Arrays.stream(readers).collect(Collectors.toMap(TypeReader::getType, e -> e));

    default ArrayList<Definitions> read(final Map<String, Object> properties) throws NoSuchFieldException, IllegalAccessException, CloneNotSupportedException, InstantiationException {
        return new ArrayList<>();
    }

    default ArrayList<Definitions> read(final Toml toml) throws NoSuchFieldException, IllegalAccessException, CloneNotSupportedException {
        return new ArrayList<>();
    }

    String getType();

    static void setFields(final Definitions definitions, final Map<String, Object> properties) throws IllegalAccessException, NoSuchFieldException {
        final Class<? extends Definitions> clazz = definitions.getClass();
        for (final Field field : clazz.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            field.setAccessible(true);
            final TypeProperty property = TypeProperty.get(field.getName());
            if (property == null) {
                continue;
            }
            if (definitions instanceof GraphicsDefinitions && property.equals(TypeProperty.REPLACEMENT_COLOURS)) {
                continue;
            }
            final String identifier = property.getIdentifier();
            if (!properties.containsKey(identifier)) {
                continue;
            }
            if (property.equals(TypeProperty.DEFAULT_INT) || property.equals(TypeProperty.DEFAULT_STRING)) {
                final Object value = properties.get(identifier);
                if (value instanceof Long) {
                    final Field f = clazz.getDeclaredField(TypeProperty.DEFAULT_INT.getField());
                    f.setAccessible(true);
                    f.set(definitions, ((Long) value).intValue());
                } else {
                    final Field f = clazz.getDeclaredField(TypeProperty.DEFAULT_STRING.getField());
                    f.setAccessible(true);
                    f.set(definitions, value);
                }
                continue;
            }
            if (field.getType() == String.class) {
                field.set(definitions, properties.get(identifier));
            } else if (field.getType() == int.class) {
                field.set(definitions, ((Long) properties.get(identifier)).intValue());
            } else if (field.getType() == boolean.class) {
                field.set(definitions, properties.get(identifier));
            } else if (field.getType() == int[].class) {
                final ArrayList<Long> list = (ArrayList<Long>) properties.get(identifier);
                field.set(definitions, list.stream().mapToInt(Long::intValue).toArray());
            } else if (field.getType() == short[].class) {
                final ArrayList<Long> list = (ArrayList<Long>) properties.get(identifier);
                final short[] array = new short[list.size()];
                for (int index = 0; index < array.length; index++) {
                    array[index] = list.get(index).shortValue();
                }
                field.set(definitions, array);
            } else if (field.getType() == String[].class) {
                final ArrayList<String> list = (ArrayList<String>) properties.get(identifier);
                if ((property.equals(TypeProperty.OPTIONS_ITEM) || property.equals(TypeProperty.OPTIONS_NPC_OBJECT) || property.equals(TypeProperty.FILTERED_OPTIONS)) && list.isEmpty()) {
                    field.set(definitions, new String[5]);
                } else {
                    final String[] array = new String[5];
                    for (int index = 0; index < array.length; index++) {
                        final String option = list.get(index);
                        array[index] = option.isEmpty() ? null : option;
                    }
                    field.set(definitions, array);
                }
            }
        }
    }
}
