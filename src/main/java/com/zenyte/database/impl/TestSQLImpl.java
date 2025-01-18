package com.zenyte.database.impl;

import com.zenyte.database.DatabaseCredential;
import com.zenyte.database.DatabasePool;
import com.zenyte.database.SQLRunnable;
import com.zenyte.game.world.entity.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class TestSQLImpl extends SQLRunnable {
    private static final Logger log = LoggerFactory.getLogger(TestSQLImpl.class);
    private final Player player;

    public TestSQLImpl(final Player player) {
        this.player = player;
    }

    @Override
    public void execute(final DatabaseCredential auth) {
        try {
            final Connection con = DatabasePool.getConnection(auth, "zenyte_main");
            if (con != null) {
                final PreparedStatement pst = con.prepareStatement("INSERT INTO test (user, data) VALUES (?, ?)");
                pst.setString(1, player.getPlayerInformation().getDisplayname());
                pst.setString(2, "some-test-data");
                pst.executeUpdate();
                pst.close();
                con.close();
            }
        } catch (final Exception e) {
            log.error("", e);
        }
    }
}
