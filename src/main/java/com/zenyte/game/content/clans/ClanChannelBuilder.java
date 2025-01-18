package com.zenyte.game.content.clans;

import com.zenyte.game.packet.ServerProt;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Privilege;
import com.zenyte.network.game.packet.GamePacketOut;
import com.zenyte.network.io.RSBuffer;

/**
 * @author Kris | 28/01/2019 15:56
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public abstract class ClanChannelBuilder implements AutoCloseable {
    GamePacketOut packetOut;
    ClanChannel channel;
    Player clanOwner;

    ClanChannelBuilder(final ServerProt prot, final ClanChannel channel, final Player clanOwner) {
        packetOut = new GamePacketOut(prot, new RSBuffer(prot));
        this.channel = channel;
        this.clanOwner = clanOwner;
    }

    int getRank(final ClanRank rank, final Player member, final Player owner) {
        if (owner.getUsername().equals(member.getUsername())) {
            return 7;
        } else if (member.getPrivilege().eligibleTo(Privilege.ADMINISTRATOR)) {
            return 127;
        } else if (rank != null) {
            if (rank == ClanRank.FRIENDS) {
                return owner.getSocialManager().containsFriend(member.getUsername()) ? 0 : -1;
            }
            return rank.getId();
        }
        return owner.getSocialManager().containsFriend(member.getUsername()) ? 0 : -1;
    }

    public final void build() {
        build(channel, clanOwner, packetOut);
    }

    protected abstract void build(ClanChannel channel, Player clanOwner, GamePacketOut packetOut);

    public GamePacketOut encode() {
        return this.packetOut;
    }

    public ClanChannel getChannel() {
        return this.channel;
    }

    public void retain() {
        this.packetOut.getBuffer().retain();
    }

    @Override
    public void close() {
        this.packetOut.getBuffer().release();
    }

}
