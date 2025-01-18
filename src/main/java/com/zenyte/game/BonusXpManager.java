package com.zenyte.game;

import com.zenyte.Constants;
import com.zenyte.game.parser.scheduled.ScheduledExternalizable;
import com.zenyte.game.ui.testinterfaces.GameNoticeboardInterface;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Kris | 30/11/2019
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class BonusXpManager implements ScheduledExternalizable {
    private static final Logger log = LoggerFactory.getLogger(BonusXpManager.class);
    public static long expirationDate;

    public static void set(final long time) {
        expirationDate = time;
        GameNoticeboardInterface.refreshBonusXP();
    }

    public static void checkIfFlip() {
        if (Constants.BOOSTED_XP) {
            if (expirationDate < System.currentTimeMillis()) {
                Constants.BOOSTED_XP = false;
                expirationDate = 0;
                GameNoticeboardInterface.refreshBonusXP();
                for (final Player player : World.getPlayers()) {
                    player.sendMessage("<col=FF0000><shad=000000>Experience is no longer boosted by 50%!</col></shad>");
                }
            }
        } else {
            if (expirationDate > 0) {
                Constants.BOOSTED_XP = true;
                GameNoticeboardInterface.refreshBonusXP();
                final String date = new Date(BonusXpManager.expirationDate).toString();
                for (final Player player : World.getPlayers()) {
                    player.sendMessage("<col=00FF00><shad=000000>Experience is boosted by 50% until " + date + "!</col></shad>");
                }
            }
        }
    }

    @Override
    public Logger getLog() {
        return log;
    }

    @Override
    public int writeInterval() {
        return 5;
    }

    @Override
    public void read(BufferedReader reader) {
        final Calendar expirationDate = World.getGson().fromJson(reader, Calendar.class);
        BonusXpManager.expirationDate = expirationDate.getTimeInMillis();
    }

    @Override
    public void ifFileNotFoundOnRead() {
        write();
    }

    @Override
    public void write() {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(expirationDate);
        out(getGSON().toJson(calendar));
    }

    @Override
    public String path() {
        return "data/bonusxpinfo.json";
    }

}
