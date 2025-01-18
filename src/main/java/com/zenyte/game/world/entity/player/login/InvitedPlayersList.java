package com.zenyte.game.world.entity.player.login;

import com.zenyte.game.parser.scheduled.ScheduledExternalizable;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.util.Set;

/**
 * @author Kris | 24/07/2019 06:01
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class InvitedPlayersList implements ScheduledExternalizable {

    public static final Set<String> invitedPlayers = new ObjectOpenHashSet<>();
    private static final Logger log = LoggerFactory.getLogger(InvitedPlayersList.class);

    @Override
    public Logger getLog() {
        return log;
    }

    @Override
    public int writeInterval() {
        return 5;
    }

    @Override
    public void read(final BufferedReader reader) {
        for (final String name : getGSON().fromJson(reader, String[].class)) {
            invitedPlayers.add(name.toLowerCase());
        }
    }

    @Override
    public void write() {
        out(getGSON().toJson(invitedPlayers.toArray()));
    }

    @Override
    public String path() {
        return "data/invitedplayers.json";
    }

}
