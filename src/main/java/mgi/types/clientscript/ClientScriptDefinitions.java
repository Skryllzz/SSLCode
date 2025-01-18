package mgi.types.clientscript;

import com.zenyte.Game;
import mgi.tools.jagcached.ArchiveType;
import mgi.tools.jagcached.cache.Archive;
import mgi.tools.jagcached.cache.Cache;
import mgi.tools.jagcached.cache.File;
import mgi.tools.jagcached.cache.Group;
import mgi.types.Definitions;
import mgi.utilities.ByteBuffer;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * @author Tommeh | 11/02/2020 | 11:46
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class ClientScriptDefinitions implements Definitions {
    private static ClientScriptDefinitions[] definitions;

    @Override
    public void load() {
        final Cache cache = Game.getCacheMgi();
        final Archive scripts = cache.getArchive(ArchiveType.CLIENTSCRIPTS);
        definitions = new ClientScriptDefinitions[scripts.getHighestGroupId()];
        for (int id = 0; id < scripts.getHighestGroupId(); id++) {
            final Group scriptGroup = scripts.findGroupByID(id);
            if (scriptGroup == null) {
                continue;
            }
            final File file = scriptGroup.findFileByID(0);
            if (file == null) {
                continue;
            }
            final ByteBuffer buffer = file.getData();
            if (buffer == null) {
                continue;
            }
            buffer.setPosition(0);
            definitions[id] = new ClientScriptDefinitions(id, buffer);
        }
    }

    public int id;
    private int intArgumentCount;
    private int stringArgumentCount;
    public String[] stringArgs;
    public int[] instructions;
    private int anInt1364;
    private int anInt1365;
    public int[] intArgs;

    ClientScriptDefinitions(final int id, final ByteBuffer buffer) {
        this.id = id;
        decode(buffer);
    }

    public static ClientScriptDefinitions get(final int id) {
        return definitions[id];
    }

    @Override
    public void decode(final ByteBuffer buffer) {
        buffer.setPosition(buffer.getBuffer().length - 2);
        int i_13_ = buffer.readUnsignedShort();
        int i_14_ = buffer.getBuffer().length - 2 - i_13_ - 12;
        buffer.setPosition(i_14_);
        int i_32_ = buffer.readInt();
        intArgumentCount = buffer.readUnsignedShort();
        stringArgumentCount = buffer.readUnsignedShort();
        anInt1364 = buffer.readUnsignedShort();
        anInt1365 = buffer.readUnsignedShort();
        int someCount = buffer.readUnsignedByte();
        if (someCount > 0) {
            for (int i = 0; i < someCount; i++) {
                int i_18_ = buffer.readUnsignedShort();
                while (i_18_-- > 0) {
                    buffer.readInt();
                    buffer.readInt();
                }
            }
        }
        buffer.setPosition(0);
        buffer.readString();
        instructions = new int[i_32_];
        intArgs = new int[i_32_];
        stringArgs = new String[i_32_];
        int i_33_ = 0;
        while (buffer.getPosition() < i_14_) {
            int i_34_ = buffer.readUnsignedShort();
            if (i_34_ == 3) stringArgs[i_33_] = buffer.readString();
            else if (i_34_ < 100 && i_34_ != 21 && i_34_ != 38 && i_34_ != 39) intArgs[i_33_] = buffer.readInt();
            else intArgs[i_33_] = buffer.readUnsignedByte();
            instructions[i_33_++] = i_34_;
        }
    }

    public int getId() {
        return this.id;
    }

    public int getIntArgumentCount() {
        return this.intArgumentCount;
    }

    public int getStringArgumentCount() {
        return this.stringArgumentCount;
    }

    public String[] getStringArgs() {
        return this.stringArgs;
    }

    public int[] getInstructions() {
        return this.instructions;
    }

    public int getAnInt1364() {
        return this.anInt1364;
    }

    public int getAnInt1365() {
        return this.anInt1365;
    }

    public int[] getIntArgs() {
        return this.intArgs;
    }

    @NotNull
    @Override
    public String toString() {
        return "ClientScriptDefinitions(id=" + this.getId() + ", intArgumentCount=" + this.getIntArgumentCount() + ", stringArgumentCount=" + this.getStringArgumentCount() + ", stringArgs=" + Arrays.deepToString(this.getStringArgs()) + ", instructions=" + Arrays.toString(this.getInstructions()) + ", anInt1364=" + this.getAnInt1364() + ", anInt1365=" + this.getAnInt1365() + ", intArgs=" + Arrays.toString(this.getIntArgs()) + ")";
    }

    public ClientScriptDefinitions() {
    }
}
