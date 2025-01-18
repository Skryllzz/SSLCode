package com.zenyte.game.world.entity.player.update;

import com.zenyte.Constants;
import com.zenyte.game.packet.GamePacketEncoder;
import com.zenyte.game.packet.ServerProt;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.masks.UpdateFlags;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.update.mask.*;
import com.zenyte.game.world.region.CharacterLoop;
import com.zenyte.network.game.packet.GamePacketOut;
import com.zenyte.network.io.BitBuffer;
import com.zenyte.network.io.RSBuffer;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;

/**
 * @author Kris | 1. veebr 2018 : 22:11.29
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server
 * profile</a>
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status
 * profile</a>
 */
public final class NPCInfo implements GamePacketEncoder {
    /**
     * The maximum number of NPCs that can be seen in a player's viewport at any given time.
     */
    private static final int SV_LOCAL_NPCS_LIMIT = 250;
    /**
     * An array of update masks in the respective order they're read in the client. PS: Order must be preserved!
     */
    private static final UpdateMask[] masks = new UpdateMask[]{
            new FaceEntityMask(),
            new AnimationMask(),
            new HideOptionsMask(),
            new ForceMovementMask(),
            new FaceLocationMask(),
            new CombatLevelChangeMask(),
            new NameChangeMask(),
            new HitMask(),
            new ForceChatMask(),
            new TintingMask(),
            new GraphicsMask(),
            new TransformationMask()
    };
    /**
     * The length of the masks.
     */
    private static final int length = masks.length;
    private Player player;
    /**
     * A linked hash set used to keep an ordered collection of npcs currently in the player's viewport.
     */
    private ObjectLinkedOpenHashSet<NPC> localNPCs;
    /**
     * The main cache buffer that's transmitted to the client.
     */
    private RSBuffer cache;
    /**
     * The bitbuffer used for writing bit information of the npcs.
     */
    private BitBuffer bitBuffer;
    /**
     * The small mask buffer that is used per-npc basis and cleared after every npc.
     */
    private RSBuffer smallMaskBuffer;
    /**
     * The large mask buffer to which all of the small buffer are written.
     */
    private RSBuffer largeMaskBuffer;

    public NPCInfo(final Player player) {
        this.player = player;
        localNPCs = new ObjectLinkedOpenHashSet<>();
        bitBuffer = new BitBuffer(255, Constants.MAX_SERVER_BUFFER_SIZE);
        smallMaskBuffer = new RSBuffer(255, Constants.MAX_SERVER_BUFFER_SIZE);
        this.largeMaskBuffer = new RSBuffer(255, Constants.MAX_SERVER_BUFFER_SIZE);
        this.cache = new RSBuffer(255, Constants.MAX_SERVER_BUFFER_SIZE);
    }

    public void reset() {
        player = null;
        localNPCs.clear();
        localNPCs = null;
        cache.clear();
        cache = null;
        bitBuffer.reset();
        bitBuffer = null;
        smallMaskBuffer.clear();
        smallMaskBuffer = null;
        largeMaskBuffer.clear();
        largeMaskBuffer = null;
    }

    /**
     * Prepares and caches the GNI buffer for the player, processing local and external npcs respectively.
     *
     * @return this for chaining.
     */
    public synchronized NPCInfo cache() {
        cache.clear();
        largeMaskBuffer.clear();
        bitBuffer.reset();
        processNPCs();
        final int length = cache.readableBytes();
        cache.writeBits(bitBuffer);
        if (length == cache.readableBytes()) {
            throw new IllegalStateException("Unable to write bytes from bitbuffer: " + bitBuffer.getReaderIndex() + ", " + bitBuffer.getWriterIndex());
        }
        cache.writeBytes(largeMaskBuffer);
        return this;
    }

    /**
     * Processes local and external npcs respectively. Writes an additional 15 bits in the very end if any masks were
     * written.
     */
    private void processNPCs() {
        processLocalNPCs();
        processExternalNPCs();
        if (largeMaskBuffer.isReadable()) {
            bitBuffer.write(16, 65535);
        }
    }

    /**
     * Processes the local npcs, removes them from the viewport if necessary.
     */
    private void processLocalNPCs() {
        bitBuffer.write(8, localNPCs.size());
        localNPCs.removeIf(npc -> {
            if (npc.isFinished() || !player.isVisibleInViewport(npc) || npc.isTeleported()) {
                bitBuffer.write(1, 1);
                bitBuffer.write(2, 3);
                return true;
            }
            final boolean needUpdate = npc.getUpdateFlags().isUpdateRequired();
            final int walkDirection = npc.getWalkDirection();
            final int runDirection = npc.getRunDirection();
            final int crawlDirection = npc.getCrawlDirection();
            final boolean walkUpdate = walkDirection != -1 || crawlDirection != -1 || runDirection != -1;
            if (needUpdate) {
                appendUpdateBlock(npc, false);
            }
            bitBuffer.write(1, needUpdate || walkUpdate ? 1 : 0);
            if (walkUpdate) {
                if (crawlDirection != -1) {
                    bitBuffer.write(2, 2);
                    bitBuffer.write(1, 0);
                    bitBuffer.write(3, Utils.getNPCWalkingDirection(crawlDirection));
                } else if (runDirection != -1) {
                    bitBuffer.write(2, 2);
                    bitBuffer.write(1, 1);
                    bitBuffer.write(3, Utils.getNPCWalkingDirection(walkDirection));
                    bitBuffer.write(3, Utils.getNPCWalkingDirection(runDirection));
                } else {
                    bitBuffer.write(2, 1);
                    bitBuffer.write(3, Utils.getNPCWalkingDirection(walkDirection));
                }
                bitBuffer.write(1, needUpdate ? 1 : 0);
            } else if (needUpdate) {
                bitBuffer.write(2, 0);
            }
            return false;
        });
    }

    /**
     * Processes the external npcs, adds them to the viewport if necessary.
     */
    private void processExternalNPCs() {
        final boolean largeSceneView = player.getViewDistance() > 15;
        final int numberOfBits = largeSceneView ? 8 : 5;
        final int distance = largeSceneView ? 255 : 31;
        CharacterLoop.forEach(player.getLocation(), player.getViewDistance(), NPC.class, npc -> {
            if (localNPCs.size() >= SV_LOCAL_NPCS_LIMIT || !player.isVisibleInViewport(npc) || !localNPCs.add(npc))
                return;
            final boolean needUpdate = npc.getUpdateFlags().isUpdateRequired() || npc.getFaceEntity() != -1 || player.updateNPCOptions(npc);
            if (needUpdate) {
                appendUpdateBlock(npc, true);
            }
            final int x = npc.getX() - player.getX();
            final int y = npc.getY() - player.getY();
            bitBuffer.write(16, npc.getIndex());
            bitBuffer.write(3, npc.getRoundedDirection());
            bitBuffer.write(numberOfBits, x & distance);
            bitBuffer.write(1, needUpdate ? 1 : 0);
            bitBuffer.write(1, 0); // used to write the npc's spawn cycle, steam client.
            bitBuffer.write(1, npc.isTeleported() ? 1 : 0);
            bitBuffer.write(numberOfBits, y & distance);
            bitBuffer.write(14, npc.getId());
        });
    }

    /**
     * Processes the update blocks of this npc.
     *
     * @param npc   the npc whose update blocks are processed
     * @param added whether or not the npc was just added to the viewport.
     */
    private void appendUpdateBlock(final NPC npc, boolean added) {
        final UpdateFlags flags = npc.getUpdateFlags();
        int flag = 0;
        smallMaskBuffer.clear();
        for (int i = 0; i < length; i++) {
            final UpdateMask mask = masks[i];
            if (mask.apply(player, npc, flags, added)) {
                flag |= mask.getFlag().getNpcMask();
                mask.writeNPC(smallMaskBuffer, player, npc);
            }
        }
        if (flag >= 0xff) {
            flag |= 0x2;
        }
        if (flag >= 0xffff) {
            flag |= 0x4000;
        }
        largeMaskBuffer.writeByte(flag);
        if (flag >= 0xff) {
            largeMaskBuffer.writeByte(flag >> 8);
        }
        if (flag >= 0xffff) {
            largeMaskBuffer.writeByte(flag >> 16);
        }
        largeMaskBuffer.writeBytes(smallMaskBuffer);
    }

    @Override
    public GamePacketOut encode() {
        return new GamePacketOut(player.getViewDistance() > 15 ? ServerProt.NPC_INFO_LARGE : ServerProt.NPC_INFO_SMALL, cache);
    }

    @Override
    public LogLevel level() {
        return LogLevel.LOW_PACKET;
    }
}
