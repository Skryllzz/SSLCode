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
 * @author Kris | 13. march 2018 : 1:57.09
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public final class InventoryDefinitions implements Definitions {
    public static InventoryDefinitions[] definitions;

    @Override
    public void load() {
        final Cache cache = Game.getCacheMgi();
        final Archive configs = cache.getArchive(ArchiveType.CONFIGS);
        final Group invs = configs.findGroupByID(GroupType.INV);
        definitions = new InventoryDefinitions[invs.getHighestFileId()];
        for (int id = 0; id < invs.getHighestFileId(); id++) {
            final File file = invs.findFileByID(id);
            if (file == null) {
                continue;
            }
            final ByteBuffer buffer = file.getData();
            if (buffer == null) {
                continue;
            }
            definitions[id] = new InventoryDefinitions(id, buffer);
        }
    }

    private final int id;
    private int size;

    private InventoryDefinitions(final int id, final ByteBuffer buffer) {
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
        if (opcode == 2) {
            size = buffer.readUnsignedShort();
        }
    }

    public static InventoryDefinitions get(final int id) {
        if (id < 0 || id >= definitions.length) {
            return null;
        }
        return definitions[id];
    }

    @Override
    public ByteBuffer encode() {
        // TODO Auto-generated method stub
        return null;
    }

    public InventoryDefinitions() {
        this.id = 0;
    }

    public int getId() {
        return this.id;
    }

    public int getSize() {
        return this.size;
    }
}
