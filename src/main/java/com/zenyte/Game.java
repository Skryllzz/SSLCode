package com.zenyte;

import com.zenyte.cores.WorldThread;
import com.zenyte.game.packet.ClientProtDecoder;
import com.zenyte.game.packet.ClientProtLoader;
import com.zenyte.game.util.Huffman;
import com.zenyte.game.util.huffman.HuffmanManager;
import mgi.tools.jagcached.cache.Cache;
import mgi.types.Definitions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * @author Tommeh | 28 jul. 2018 | 13:03:30
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class Game {
    private static final Logger log = LoggerFactory.getLogger(Game.class);
    private static Cache cacheMgi;
    public static ByteBuffer checksumBuffer;
    public static ClientProtDecoder[] decoders = new ClientProtDecoder[256];
    public static int[] crc;

    public static long getCurrentCycle() {
        return WorldThread.WORLD_CYCLE;
    }

    public static void load() {
        load(Cache.openCache("./data/cache/", false));
    }

    public static void load(Cache cache) {
        cacheMgi = cache;
        crc = cacheMgi.getCrcs();
        byte[] buffer = cacheMgi.generateInformationStoreDescriptor().getBuffer();
        checksumBuffer = ByteBuffer.allocateDirect(buffer.length);
        checksumBuffer.put(buffer);
        checksumBuffer.flip();
        HuffmanManager.load(cacheMgi);
        Huffman.load();
        ClientProtLoader.load();
        for (final Class<?> clazz : Definitions.highPriorityDefinitions) {
            Definitions.load(clazz).run();
        }
    }

    public static Cache getCacheMgi() {
        return Game.cacheMgi;
    }

    public static void setCacheMgi(final Cache cacheMgi) {
        Game.cacheMgi = cacheMgi;
    }

    public static ByteBuffer getChecksumBuffer() {
        return Game.checksumBuffer;
    }

    public static ClientProtDecoder[] getDecoders() {
        return Game.decoders;
    }
}
