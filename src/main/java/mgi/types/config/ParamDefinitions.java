package mgi.types.config;

import com.zenyte.Game;
import mgi.tools.jagcached.ArchiveType;
import mgi.tools.jagcached.GroupType;
import mgi.tools.jagcached.cache.Archive;
import mgi.tools.jagcached.cache.Cache;
import mgi.tools.jagcached.cache.File;
import mgi.tools.jagcached.cache.Group;
import mgi.types.Definitions;
import mgi.utilities.ByteBuffer;

/**
 * @author Kris | 6. apr 2018 : 19:59.19
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public final class ParamDefinitions implements Definitions {
    public static ParamDefinitions[] definitions;

    @Override
    public void load() {
        final Cache cache = Game.getCacheMgi();
        final Archive configs = cache.getArchive(ArchiveType.CONFIGS);
        final Group params = configs.findGroupByID(GroupType.PARAMS);
        definitions = new ParamDefinitions[params.getHighestFileId()];
        for (int id = 0; id < params.getHighestFileId(); id++) {
            final File file = params.findFileByID(id);
            if (file == null) {
                continue;
            }
            final ByteBuffer buffer = file.getData();
            if (buffer == null) {
                continue;
            }
            definitions[id] = new ParamDefinitions(id, buffer);
        }
    }

    private final int id;
    private char stackType;
    private int defaultInt;
    private String defaultString;
    private boolean autoDisable = true;

    private ParamDefinitions(final int id, final ByteBuffer buffer) {
        this.id = id;
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
        switch (opcode) {
            case 1:
                stackType = buffer.readJagexChar();
                return;
            case 2:
                defaultInt = buffer.readInt();
                return;
            case 4:
                autoDisable = false;
                return;
            case 5:
                defaultString = buffer.readString();
        }
    }

    public static ParamDefinitions get(final int id) {
        if (id < 0 || id >= definitions.length) {
            return null;
        }
        return definitions[id];
    }

    public ParamDefinitions() {
        this.id = 0;
    }

    public int getId() {
        return this.id;
    }

    public char getStackType() {
        return this.stackType;
    }

    public int getDefaultInt() {
        return this.defaultInt;
    }

    public String getDefaultString() {
        return this.defaultString;
    }

    public boolean isAutoDisable() {
        return this.autoDisable;
    }
}
