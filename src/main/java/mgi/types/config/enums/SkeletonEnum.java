package mgi.types.config.enums;

import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * @author Kris | 26. juuli 2018 : 22:57:40
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public class SkeletonEnum {
    private int id;
    private String keyType;
    private String valType;
    private String defaultString;
    private int defaultInt;
    private Map<Integer, Object> values;
    public static final ImmutableMap<String, Character> REVERSE_TYPE_MAP = ImmutableMap.<String, Character>builder().put("seq", 'A').put("int", 'i').put("boolean", '1').put("string", 's').put("inv", 'v').put("char", 'z').put("namedobj", 'O').put("midi", 'M').put("idkit", 'K').put("obj", 'o').put("npc", 'n').put("coordgrid", 'c').put("stat", 'S').put("model", 'm').put("graphic", 'd').put("struct", 'J').put("fontmetrics", 'f').put("component", 'I').put("chatchar", 'k').put("enum", 'g').put("location", 'l').build();

    public final char getKeyType() {
        final Character c = REVERSE_TYPE_MAP.get(keyType);
        if (c == null) {
            throw new RuntimeException("Unable to find a matching type for " + keyType + ".");
        }
        return c;
    }

    public final char getValueType() {
        final Character c = REVERSE_TYPE_MAP.get(valType);
        if (c == null) {
            throw new RuntimeException("Unable to find a matching type for " + valType + ".");
        }
        return c;
    }

    @NotNull
    @Override
    public String toString() {
        return "SkeletonEnum(id=" + this.getId() + ", keyType=" + this.getKeyType() + ", valType=" + this.valType + ", defaultString=" + this.getDefaultString() + ", defaultInt=" + this.getDefaultInt() + ", values=" + this.getValues() + ")";
    }

    public int getId() {
        return this.id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public void setKeyType(final String keyType) {
        this.keyType = keyType;
    }

    public void setValType(final String valType) {
        this.valType = valType;
    }

    public String getDefaultString() {
        return this.defaultString;
    }

    public void setDefaultString(final String defaultString) {
        this.defaultString = defaultString;
    }

    public int getDefaultInt() {
        return this.defaultInt;
    }

    public void setDefaultInt(final int defaultInt) {
        this.defaultInt = defaultInt;
    }

    public Map<Integer, Object> getValues() {
        return this.values;
    }

    public void setValues(final Map<Integer, Object> values) {
        this.values = values;
    }
}
