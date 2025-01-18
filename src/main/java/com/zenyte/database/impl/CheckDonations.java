package com.zenyte.database.impl;

import com.zenyte.database.DatabaseCredential;
import com.zenyte.database.DatabasePool;
import com.zenyte.database.SQLRunnable;
import com.zenyte.game.content.donation.DonationHandler;
import com.zenyte.game.item.Item;
import com.zenyte.game.world.entity.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Noele | Jun 19, 2018 : 12:14:15 AM
 * @see https://noeles.life || noele@zenyte.com
 */
public class CheckDonations extends SQLRunnable {
    private static final Logger log = LoggerFactory.getLogger(CheckDonations.class);
    private final Player player;

    public CheckDonations(final Player player) {
        this.player = player;
    }

    @Override
    public void execute(final DatabaseCredential auth) {
        final List<Item> rewards = new ArrayList<>();
        try (
                Connection con = DatabasePool.getConnection(auth, "zenyte_main");
                PreparedStatement stmt = con.prepareStatement("SELECT * FROM store_purchases WHERE claimed = 0 AND userid = ?");
                PreparedStatement stmt2 = con.prepareStatement("UPDATE store_purchases SET claimed = 1 WHERE userid = ? and claimed = 0")) {
            stmt.setInt(1, player.getPlayerInformation().getUserIdentifier());
            try (ResultSet set = stmt.executeQuery()) {
                while (set.next()) {
                    final int id = set.getInt(set.findColumn("item_id"));
                    final int amount = set.getInt(set.findColumn("quantity"));
                    player.sendMessage("Item: " + id + " amount: " + amount);
                    if (id == 0 || amount == 0) continue;
                    rewards.add(new Item(id, amount));
                }
                stmt2.setInt(1, player.getPlayerInformation().getUserIdentifier());
                stmt2.execute();
            }
            DonationHandler.claim(player, rewards);
        } catch (final Exception e) {
            log.error("", e);
        }
    }

    @Override
    public void prepare() {
        DatabasePool.submit(this);
    }
}
