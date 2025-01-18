package mgi.types.config.identitykit;

import com.zenyte.Game;
import mgi.tools.jagcached.ArchiveType;
import mgi.tools.jagcached.GroupType;
import mgi.tools.jagcached.cache.Archive;
import mgi.tools.jagcached.cache.Cache;
import mgi.tools.jagcached.cache.File;
import mgi.tools.jagcached.cache.Group;
import mgi.types.Definitions;
import mgi.utilities.ByteBuffer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kris | 27. veebr 2018 : 2:10.50
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server
 * profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status
 * profile</a>}
 */
public final class IdentityKitDefinitions implements Definitions {
    public static IdentityKitDefinitions[] DEFINITIONS;
    private static final List<Integer> HAIRSTYLES = new ArrayList<>(25);
    private static final List<Integer> BEARDSTYLES = new ArrayList<>(16);
    private static final List<Integer> BODYSTYLES = new ArrayList<>(15);
    private static final List<Integer> ARMSTYLES = new ArrayList<>(13);
    private static final List<Integer> LEGSSTYLES = new ArrayList<>(12);

    @Override
    public void load() {
        final Cache cache = Game.getCacheMgi();
        final Archive configs = cache.getArchive(ArchiveType.CONFIGS);
        final Group identityKits = configs.findGroupByID(GroupType.IDENTKIT);
        DEFINITIONS = new IdentityKitDefinitions[identityKits.getHighestFileId()];
        for (int id = 0; id < identityKits.getHighestFileId(); id++) {
            final File file = identityKits.findFileByID(id);
            if (file == null) {
                continue;
            }
            final ByteBuffer buffer = file.getData();
            if (buffer == null) {
                continue;
            }
            DEFINITIONS[id] = new IdentityKitDefinitions(id, buffer);
        }
    }

    private final int id;
    private int[] headModels;
    private int[] modelIds;
    private int bodyPartId;
    private boolean selectable;
    private short[] originalColours;
    private short[] originalTextures;
    private short[] replacementColours;
    private short[] replacementTextures;

    private IdentityKitDefinitions(final int id, final ByteBuffer buffer) {
        this.id = id;
        setDefaults();
        decode(buffer);
        if (selectable) {
            final int part = bodyPartId;
            if (part == 0) {
                HAIRSTYLES.add(id);
            } else if (part == 1) {
                BEARDSTYLES.add(id);
            } else if (part == 2) {
                BODYSTYLES.add(id);
            } else if (part == 3) {
                ARMSTYLES.add(id);
            } else if (part == 5) {
                LEGSSTYLES.add(id);
            }
        }
    }

    private void setDefaults() {
        bodyPartId = -1;
        headModels = new int[]{-1, -1, -1, -1, -1};
        selectable = true;
    }

    public static IdentityKitDefinitions get(final int id) {
        if (id < 0 || id >= DEFINITIONS.length) {
            return null;
        }
        return DEFINITIONS[id];
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
                bodyPartId = buffer.readUnsignedByte();
                return;
            case 2:
                modelIds = new int[buffer.readUnsignedByte()];
                for (int i = 0; i < modelIds.length; i++) {
                    modelIds[i] = buffer.readUnsignedShort();
                }
                return;
            case 3:
                selectable = false;
                return;
            case 40: {
                final int length = buffer.readUnsignedByte();
                originalColours = new short[length];
                replacementColours = new short[length];
                for (int i = 0; i < length; i++) {
                    originalColours[i] = (short) buffer.readUnsignedShort();
                    replacementColours[i] = (short) buffer.readUnsignedShort();
                }
                return;
            }
            case 41: {
                final int length = buffer.readUnsignedByte();
                originalTextures = new short[length];
                replacementTextures = new short[length];
                for (int i = 0; i < length; i++) {
                    originalTextures[i] = (short) buffer.readUnsignedShort();
                    replacementTextures[i] = (short) buffer.readUnsignedShort();
                }
                return;
            }
            case 60:
            case 61:
            case 62:
            case 63:
            case 64:
                headModels[opcode - 60] = buffer.readUnsignedShort();
        }
    }

    public static int getHairstyle(final int index) {
        if (index < 0 || index >= HAIRSTYLES.size()) {
            return 0;
        }
        return HAIRSTYLES.get(index);
    }

    public static int getBeardstyle(final int index) {
        if (index < 0 || index >= BEARDSTYLES.size()) {
            return 0;
        }
        return BEARDSTYLES.get(index);
    }

    public static int getBodystyle(final int index) {
        if (index < 0 || index >= BODYSTYLES.size()) {
            return 0;
        }
        return BODYSTYLES.get(index);
    }

    public static int getLegsstyle(final int index) {
        if (index < 0 || index >= LEGSSTYLES.size()) {
            return 0;
        }
        return LEGSSTYLES.get(index);
    }

    public static int getArmstyle(final int index) {
        if (index < 0 || index >= ARMSTYLES.size()) {
            return 0;
        }
        return ARMSTYLES.get(index);
    }

    public IdentityKitDefinitions() {
        this.id = 0;
    }

    public int getId() {
        return this.id;
    }

    public int[] getHeadModels() {
        return this.headModels;
    }

    public int[] getModelIds() {
        return this.modelIds;
    }

    public int getBodyPartId() {
        return this.bodyPartId;
    }

    public boolean isSelectable() {
        return this.selectable;
    }

    public short[] getOriginalColours() {
        return this.originalColours;
    }

    public short[] getOriginalTextures() {
        return this.originalTextures;
    }

    public short[] getReplacementColours() {
        return this.replacementColours;
    }

    public short[] getReplacementTextures() {
        return this.replacementTextures;
    }
}
