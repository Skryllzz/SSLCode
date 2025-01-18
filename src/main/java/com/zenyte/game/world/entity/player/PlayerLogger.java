package com.zenyte.game.world.entity.player;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.Date;
import java.util.Locale;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

/**
 * @author Kris | 24/05/2019 23:20
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public final class PlayerLogger {
    public static final LogLevel WRITE_LEVEL = LogLevel.HIGH_PACKET;
    private static final Logger log = LoggerFactory.getLogger(PlayerLogger.class);
    private static final int BUFFER_CAPACITY = 8192;
    private static final SimpleDateFormat loggerDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private static final DateTimeFormatter folderFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private final transient Player player;
    private final transient Queue<String> messageQueue = new ConcurrentLinkedQueue<>();
    private transient Writer logWriter;
    private transient int bytes;
    private transient ForkJoinTask<?> task;
    private boolean closed;

    public PlayerLogger(final Player player) {
        this.player = player;
    }

    void build() {
        try {
            final DayOfWeek firstDayOfWeek = WeekFields.of(Locale.getDefault()).getFirstDayOfWeek();
            final DayOfWeek lastDayOfWeek = DayOfWeek.of(((firstDayOfWeek.getValue() + 5) % DayOfWeek.values().length) + 1);
            final LocalDate firstDate = LocalDate.now().with(TemporalAdjusters.previousOrSame(firstDayOfWeek));
            final LocalDate lastDate = LocalDate.now().with(TemporalAdjusters.nextOrSame(lastDayOfWeek));
            final String folderLabel = folderFormatter.format(firstDate) + " - " + folderFormatter.format(lastDate);
            final File folder = new File("data/logs/player logs/" + folderLabel + "/");
            //noinspection ResultOfMethodCallIgnored
            folder.mkdirs();
            final File file = new File(folder.getPath() + "/" + player.getUsername() + ".log");
            logWriter = new BufferedWriter(new FileWriter(file, true));
        } catch (IOException e) {
            log.error("", e);
        }
    }

    void shutdown() {
        try {
            if (task == null || task.isDone()) {
                write();
            }
            //Wait for the last write process to end.
            task.get();
            closed = true;
            logWriter.flush();
            logWriter.close();
        } catch (Exception e) {
            log.error("", e);
        }
    }

    public void log(final LogLevel level, final String message) {
        if (level.getPriority() < WRITE_LEVEL.getPriority()) {
            return;
        }
        final String queuedMessage = String.format("%s [%s] %s - %s%s", loggerDateFormat.format(new Date()), Thread.currentThread().getName(), level.name(), message, System.lineSeparator());
        messageQueue.add(queuedMessage);
        bytes += queuedMessage.length();
        if (bytes >= BUFFER_CAPACITY) {
            if (task != null && !task.isDone()) {
                return;
            }
            write();
        }
    }

    private void write() {
        task = ForkJoinPool.commonPool().submit(() -> {
            try {
                if (closed) {
                    return;
                }
                String message;
                while ((message = messageQueue.poll()) != null) {
                    logWriter.write(message);
                    bytes -= message.length();
                }
                logWriter.flush();
            } catch (Exception e) {
                log.error("", e);
            }
        });
    }
}
