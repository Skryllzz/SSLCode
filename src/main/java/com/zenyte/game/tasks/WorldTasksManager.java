package com.zenyte.game.tasks;

import com.zenyte.game.util.TimeUnit;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

/**
 * @author Kris | 4. apr 2018 : 21:33.45
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>
 */
public class WorldTasksManager {
    static final Map<WorldTask, WorldTaskInformation> MAIN_TASKS = new Object2ObjectOpenHashMap<>();
    private static final Logger log = LoggerFactory.getLogger(WorldTasksManager.class);
    private static final Object $LOCK = new Object[0];
    private static final Map<WorldTask, WorldTaskInformation> PENDING_ADD_TASKS = new Object2ObjectOpenHashMap<>();
    private static final Set<WorldTask> PENDING_REMOVE_TASKS = new ObjectOpenHashSet<>();

    public static void processTasks() {
        synchronized (WorldTasksManager.$LOCK) {
            MAIN_TASKS.putAll(PENDING_ADD_TASKS);
            PENDING_ADD_TASKS.clear();
            for (final Map.Entry<WorldTask, WorldTasksManager.WorldTaskInformation> entry : MAIN_TASKS.entrySet()) {
                final WorldTasksManager.WorldTaskInformation value = entry.getValue();
                if (value.continueCount > 0) {
                    value.continueCount--;
                    continue;
                }
                final WorldTask key = entry.getKey();
                try {
                    key.run();
                } catch (final Exception e) {
                    log.error("", e);
                    PENDING_REMOVE_TASKS.add(key);
                    continue;
                }
                if (value.continueMaxCount != -1) {
                    value.continueCount = value.continueMaxCount;
                    continue;
                }
                PENDING_REMOVE_TASKS.add(key);
            }
            MAIN_TASKS.keySet().removeAll(PENDING_REMOVE_TASKS);
            PENDING_REMOVE_TASKS.clear();
        }
    }

    public static int count() {
        return MAIN_TASKS.size();
    }

    public static void schedule(final WorldTask task) {
        synchronized (WorldTasksManager.$LOCK) {
            if (task == null) {
                return;
            }
            PENDING_ADD_TASKS.put(task, new WorldTaskInformation(0, -1));
        }
    }

    public static void schedule(final WorldTask task, final int delayCount) {
        synchronized (WorldTasksManager.$LOCK) {
            if (task == null || delayCount < 0) {
                return;
            }
            PENDING_ADD_TASKS.put(task, new WorldTaskInformation(delayCount, -1));
        }
    }

    public static void schedule(final WorldTask task, final int delayCount, final int periodCount) {
        synchronized (WorldTasksManager.$LOCK) {
            if (task == null || delayCount < 0 || periodCount < 0) {
                return;
            }
            PENDING_ADD_TASKS.put(task, new WorldTaskInformation(delayCount, periodCount));
        }
    }

    /**
     * Schedules the task if the delay is above zero, otherwise executes it immediately.
     *
     * @param task  the task to execute.
     * @param delay the delay in {@link TimeUnit#TICKS } until the task is executed.
     */
    public static void scheduleOrExecute(@NotNull final WorldTask task, final int delay) {
        synchronized (WorldTasksManager.$LOCK) {
            if (delay < 0) {
                task.run();
            } else {
                PENDING_ADD_TASKS.put(task, new WorldTaskInformation(delay, -1));
            }
        }
    }

    /**
     * Schedules the task if the delay is above zero, otherwise executes it immediately.
     *
     * @param task  the task to execute.
     * @param delay the delay in {@link TimeUnit#TICKS } until the task is executed.
     */
    public static void scheduleOrExecute(@NotNull final WorldTask task, final int delay, final int additionalDelay) {
        synchronized (WorldTasksManager.$LOCK) {
            if (delay < 0) {
                task.run();
            } else {
                PENDING_ADD_TASKS.put(task, new WorldTaskInformation(delay, additionalDelay));
            }
        }
    }


    static final class WorldTaskInformation {
        int continueMaxCount;
        private int continueCount;

        WorldTaskInformation(final int continueCount, final int continueMaxCount) {
            this.continueCount = continueCount;
            this.continueMaxCount = continueMaxCount;
        }
    }
}
