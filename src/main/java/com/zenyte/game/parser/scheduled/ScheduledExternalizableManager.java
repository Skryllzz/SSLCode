package com.zenyte.game.parser.scheduled;

import com.zenyte.Constants;
import com.zenyte.cores.CoresManager;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Kris | 16. juuni 2018 : 16:22:39
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class ScheduledExternalizableManager {
    public static final MutableBoolean status = new MutableBoolean();
    private static final Logger log = LoggerFactory.getLogger(ScheduledExternalizableManager.class);
    private static final List<ScheduledExternalizable> scheduled = new LinkedList<>();
    private static int minutesPassed;

    static {
        CoresManager.slowExecutor.scheduleAtFixedRate(() -> {
            save();
            minutesPassed++;
        }, 1, 1, TimeUnit.MINUTES);
    }

    public static synchronized void save() {
        try {
            if (Constants.WORLD_PROFILE.isDevelopment()) {
                return;
            }
            status.setTrue();
            for (final ScheduledExternalizable scheduled : ScheduledExternalizableManager.scheduled) {
                final int interval = scheduled.writeInterval();
                if (interval <= 0) {
                    continue;
                }
                if (minutesPassed % interval == 0) {
                    try {
                        scheduled.writeAndOutput();
                    } catch (final Exception e) {
                        log.error("", e);
                    }
                }
            }
            status.setFalse();
        } catch (Exception e) {
            log.error("", e);
        }
    }

    public static void add(final Class<? extends ScheduledExternalizable> c) {
        try {
            if (c.isAnonymousClass() || c.isMemberClass()) {
                return;
            }
            final ScheduledExternalizable instance = c.newInstance();
            scheduled.add(instance);
            instance.read();
        } catch (final Exception e) {
            log.error("", e);
        }
    }
}
