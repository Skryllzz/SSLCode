package com.zenyte.database.impl;

import com.zenyte.database.DatabaseCredential;
import com.zenyte.database.DatabasePool;
import com.zenyte.database.DatabaseUtil;
import com.zenyte.database.SQLRunnable;
import com.zenyte.database.structs.ClanChatMessage;
import com.zenyte.game.world.entity.player.PlayerInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class ClanChatLog extends SQLRunnable {
    private static final Logger log = LoggerFactory.getLogger(ClanChatLog.class);

    public ClanChatLog() {
    }

    @Override
    public void execute(DatabaseCredential auth) {
        final String query = DatabaseUtil.buildBatch("INSERT INTO logs_clan_chat ( user, user_ip, message, clan, world, time_added ) VALUES  ( ?, ?, ?, ?, ?, ? )", ClanChatMessage.list.size(), 6);
        try (
                Connection con = DatabasePool.getConnection(auth, "zenyte_main");
                PreparedStatement pst = con.prepareStatement(query)) {
            int index = 0;
            for (ClanChatMessage log : ClanChatMessage.list) {
                final PlayerInformation info = log.getPlayer().getPlayerInformation();
                pst.setString(++index, info.getUsername());
                pst.setString(++index, info.getIp());
                pst.setString(++index, log.getMessage());
                pst.setString(++index, log.getClan());
                pst.setInt(++index, 0);
                pst.setTimestamp(++index, log.getDate());
            }
            pst.execute();
            ClanChatMessage.list.clear();
        } catch (final Exception e) {
            log.error("", e);
        }
    }
}
