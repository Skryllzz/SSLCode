package mgi.types.worldmap;

import com.zenyte.Game;
import mgi.tools.jagcached.ArchiveType;
import mgi.tools.jagcached.GroupType;
import mgi.tools.jagcached.cache.Archive;
import mgi.tools.jagcached.cache.Cache;
import mgi.tools.jagcached.cache.File;
import mgi.tools.jagcached.cache.Group;
import mgi.types.Definitions;
import mgi.utilities.ByteBuffer;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * @author Tommeh | 6-12-2018 | 23:21
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class MapElementDefinitions implements Definitions {
    private static MapElementDefinitions[] definitions;
    private int id;
    private int spriteId;
    private int field3306;//always -1
    private String text;
    private int colour;
    private int textSize;
    private String[] options;
    private String optionName;
    private int[] field3312;//always null
    private int field3313;//always 2147483647
    private int field3314;//always 2147483647
    private int field3315;//always -2147483648
    private int field3316;//always -2147483648
    private int horizontalAlignment;
    private int verticalAlignment;
    private int[] field3307;//always null
    private byte[] field3320;//always null
    //Used for getting string value from enum 1713
    private int tooltipId;

    public int getGroupId() {
        return id << 8 | 10;//10 = index of the string, Open is first so it's 10; effectively 10 + index.
    }

    @Override
    public void load() {
        final Cache cache = Game.getCacheMgi();
        final Archive configs = cache.getArchive(ArchiveType.CONFIGS);
        final Group labels = configs.findGroupByID(GroupType.MAP_LABELS);
        definitions = new MapElementDefinitions[labels.getHighestFileId()];
        for (int id = 0; id < labels.getHighestFileId(); id++) {
            final File file = labels.findFileByID(id);
            if (file == null) {
                continue;
            }
            final ByteBuffer buffer = file.getData();
            if (buffer == null) {
                continue;
            }
            definitions[id] = new MapElementDefinitions(id, buffer);
        }
    }

    public static MapElementDefinitions get(final int id) {
        return definitions[id];
    }

    public MapElementDefinitions(final int id, final ByteBuffer buffer) {
        this.id = id;
        this.tooltipId = -1;
        this.spriteId = -1;
        this.field3306 = -1;
        this.textSize = 0;
        this.horizontalAlignment = 1;
        this.verticalAlignment = 1;
        this.options = new String[5];
        this.field3313 = Integer.MAX_VALUE;
        this.field3314 = Integer.MAX_VALUE;
        this.field3315 = Integer.MIN_VALUE;
        this.field3316 = Integer.MIN_VALUE;
        decode(buffer);
    }

    @Override
    public void decode(final ByteBuffer buffer) {
        while (true) {
            final int opcode = buffer.readUnsignedByte();
            if (opcode == 0) {
                return;
            }
            decode(buffer, opcode);
        }
    }

    @Override
    public void decode(final ByteBuffer buffer, final int opcode) {
        int var3;
        int var4;
        switch (opcode) {
            case 1:
                spriteId = buffer.readBigSmart();
                return;
            case 2:
                field3306 = buffer.readBigSmart();
                return;
            case 3:
                text = buffer.readString();
                return;
            case 4:
                colour = buffer.readMedium();
                return;
            case 5:
                buffer.readMedium();
                return;
            case 6:
                textSize = buffer.readUnsignedByte();
                return;
            case 7:
                var3 = buffer.readUnsignedByte();
                return;
            case 8:
                buffer.readByte();
                return;
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
                options[opcode - 10] = buffer.readString();
                return;
            case 15:
                var3 = buffer.readUnsignedByte();
                field3312 = new int[var3 * 2];
                for (int index = 0; index < var3 * 2; index++) {
                    field3312[index] = buffer.readShort();
                }
                buffer.readInt();
                var4 = buffer.readUnsignedByte();
                field3307 = new int[var4];
                for (int index = 0; index < field3307.length; index++) {
                    field3307[index] = buffer.readInt();
                }
                field3320 = new byte[var3];
                for (int index = 0; index < var3; index++) {
                    field3320[index] = buffer.readByte();
                }
                return;
            case 17:
                optionName = buffer.readString();
                return;
            case 18:
                buffer.readBigSmart();
                return;
            case 19:
                tooltipId = buffer.readUnsignedShort();
                return;
            case 21:
            case 22:
                buffer.readInt();
                return;
            case 23:
                buffer.readByte();
                buffer.readByte();
                buffer.readByte();
                return;
            case 24:
                buffer.readShort();
                buffer.readShort();
                return;
            case 25:
                buffer.readBigSmart();
                return;
            case 28:
                buffer.readByte();
                return;
            case 29:
                horizontalAlignment = buffer.readUnsignedByte();
                return;
            case 30:
                verticalAlignment = buffer.readUnsignedByte();
        }
    }

    @Override
    public ByteBuffer encode() {
        final ByteBuffer buffer = new ByteBuffer(4096);
        buffer.writeByte(1);
        buffer.writeBigSmart(spriteId);
        buffer.writeByte(2);
        buffer.writeBigSmart(field3306);
        buffer.writeByte(3);
        buffer.writeString(text);
        buffer.writeByte(4);
        buffer.writeMedium(colour);
        buffer.writeByte(5);
        buffer.writeMedium(-1);
        buffer.writeByte(6);
        buffer.writeByte(textSize);
        buffer.writeByte(7);
        buffer.writeByte(-1);
        buffer.writeByte(8);
        buffer.writeByte(-1);
        for (int opcode = 10; opcode <= 14; opcode++) {
            if (options[opcode - 10] != null) {
                buffer.writeByte(opcode);
                buffer.writeString(options[opcode - 10]);
            }
        }
        /*
        buffer.writeByte(15);
        if (field3312 != null) {
            buffer.writeByte((field3312.length / 2));
            for (int index = 0; index < field3312.length / 2; index++) {
                buffer.writeShort(field3312[index]);
            }
        }
        buffer.putInt(-1);
        if (field3307 != null) {
            buffer.writeByte(field3307.length);
            for (int index = 0; index < field3307.length; index++) {
                buffer.putInt(field3307[index]);
            }
        }

        if (field3320 != null) {
            for (int index = 0; index < field3312.length; index++) {
                buffer.put(field3320[index]);
            }
        }*/
        if (optionName != null) {
            buffer.writeByte(17);
            buffer.writeString(optionName);
        }
        buffer.writeByte(18);
        buffer.writeBigSmart(-1);
        buffer.writeByte(19);
        buffer.writeShort(tooltipId);
        buffer.writeByte(21);
        buffer.writeInt(-1);
        buffer.writeByte(23);
        buffer.writeByte(-1);
        buffer.writeByte(-1);
        buffer.writeByte(-1);
        buffer.writeByte(24);
        buffer.writeShort(-1);
        buffer.writeShort(-1);
        buffer.writeByte(25);
        buffer.writeBigSmart(-1);
        buffer.writeByte(28);
        buffer.writeByte(-1);
        buffer.writeByte(29);
        buffer.writeByte(horizontalAlignment);
        buffer.writeByte(30);
        buffer.writeByte(verticalAlignment);
        buffer.writeByte(0);
        return buffer;
    }

    @NotNull
    @Override
    public String toString() {
        return "MapElementDefinitions(id=" + this.getId() + ", spriteId=" + this.getSpriteId() + ", field3306=" + this.getField3306() + ", text=" + this.getText() + ", colour=" + this.getColour() + ", textSize=" + this.getTextSize() + ", options=" + Arrays.deepToString(this.getOptions()) + ", optionName=" + this.getOptionName() + ", field3312=" + Arrays.toString(this.getField3312()) + ", field3313=" + this.getField3313() + ", field3314=" + this.getField3314() + ", field3315=" + this.getField3315() + ", field3316=" + this.getField3316() + ", horizontalAlignment=" + this.getHorizontalAlignment() + ", verticalAlignment=" + this.getVerticalAlignment() + ", field3307=" + Arrays.toString(this.getField3307()) + ", field3320=" + Arrays.toString(this.getField3320()) + ", tooltipId=" + this.getTooltipId() + ")";
    }

    public void setId(final int id) {
        this.id = id;
    }

    public void setSpriteId(final int spriteId) {
        this.spriteId = spriteId;
    }

    public void setField3306(final int field3306) {
        this.field3306 = field3306;
    }

    public void setText(final String text) {
        this.text = text;
    }

    public void setColour(final int colour) {
        this.colour = colour;
    }

    public void setTextSize(final int textSize) {
        this.textSize = textSize;
    }

    public void setOptions(final String[] options) {
        this.options = options;
    }

    public void setOptionName(final String optionName) {
        this.optionName = optionName;
    }

    public void setField3312(final int[] field3312) {
        this.field3312 = field3312;
    }

    public void setField3313(final int field3313) {
        this.field3313 = field3313;
    }

    public void setField3314(final int field3314) {
        this.field3314 = field3314;
    }

    public void setField3315(final int field3315) {
        this.field3315 = field3315;
    }

    public void setField3316(final int field3316) {
        this.field3316 = field3316;
    }

    public void setHorizontalAlignment(final int horizontalAlignment) {
        this.horizontalAlignment = horizontalAlignment;
    }

    public void setVerticalAlignment(final int verticalAlignment) {
        this.verticalAlignment = verticalAlignment;
    }

    public void setField3307(final int[] field3307) {
        this.field3307 = field3307;
    }

    public void setField3320(final byte[] field3320) {
        this.field3320 = field3320;
    }

    public void setTooltipId(final int tooltipId) {
        this.tooltipId = tooltipId;
    }

    public int getId() {
        return this.id;
    }

    public int getSpriteId() {
        return this.spriteId;
    }

    public int getField3306() {
        return this.field3306;
    }

    public String getText() {
        return this.text;
    }

    public int getColour() {
        return this.colour;
    }

    public int getTextSize() {
        return this.textSize;
    }

    public String[] getOptions() {
        return this.options;
    }

    public String getOptionName() {
        return this.optionName;
    }

    public int[] getField3312() {
        return this.field3312;
    }

    public int getField3313() {
        return this.field3313;
    }

    public int getField3314() {
        return this.field3314;
    }

    public int getField3315() {
        return this.field3315;
    }

    public int getField3316() {
        return this.field3316;
    }

    public int getHorizontalAlignment() {
        return this.horizontalAlignment;
    }

    public int getVerticalAlignment() {
        return this.verticalAlignment;
    }

    public int[] getField3307() {
        return this.field3307;
    }

    public byte[] getField3320() {
        return this.field3320;
    }

    public int getTooltipId() {
        return this.tooltipId;
    }

    public MapElementDefinitions() {
    }
}
