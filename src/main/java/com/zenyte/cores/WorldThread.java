package com.zenyte.cores;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zenyte.Constants;
import com.zenyte.game.BonusXpManager;
import com.zenyte.game.GameClock;
import com.zenyte.game.RuneDate;
import com.zenyte.game.content.partyroom.FaladorPartyRoom;
import com.zenyte.game.shop.Shop;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.ui.testinterfaces.TournamentViewerInterface;
import com.zenyte.game.util.TimeUnit;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.Container;
import com.zenyte.game.world.entity.player.login.LoginManager;
import com.zenyte.game.world.region.GlobalAreaManager;
import com.zenyte.game.world.region.area.wilderness.WildernessArea;
import io.netty.channel.Channel;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.NotNull;
import org.jire.zenytersps.threads.MainThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public final class WorldThread extends MainThread {
    private static final Logger log = LoggerFactory.getLogger(WorldThread.class);
    public static long WORLD_CYCLE;
    private static int pidSwapDelay = Utils.random(100, 150);
    private final Logger tickLogger = LoggerFactory.getLogger("Tick logger");
    private final List<String> publicStaff = new ObjectArrayList<>();
    private final List<String> privateStaff = new ObjectArrayList<>();
    private final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
    private int dayOfYear;

    public WorldThread(@NotNull String name) {
        super(name);
    }

    @Override
    public void cycle() {
        try {
            WORLD_CYCLE++;
            final long nano = System.nanoTime();
            boolean resetDailies = false;
            try {
                final int currentDayOfYear = dayOfYear;
                dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
                if (currentDayOfYear != dayOfYear) {
                    resetDailies = true;
                }
            } catch (Exception e) {
                log.error("", e);
            }
            try {
                publicStaff.clear();
                privateStaff.clear();
            } catch (Exception e) {
                log.error("", e);
            }
            try {
                Container.resetContainer();
            } catch (Exception e) {
                log.error("", e);
            }
            final long shopNano = System.nanoTime();
            try {
                Shop.process();
            } catch (Exception e) {
                log.error("", e);
            }
            final long areaManagerNano = System.nanoTime();
            try {
                GlobalAreaManager.process();
            } catch (Exception e) {
                log.error("", e);
            }
            //World.processLogins();
            final long worldTaskProcessNano = System.nanoTime();
            try {
                WorldTasksManager.processTasks();
            } catch (Exception e) {
                log.error("", e);
            }
            final long gameClockNano = System.nanoTime();
            try {
                GameClock.process();
                BonusXpManager.checkIfFlip();
            } catch (Exception e) {
                log.error("", e);
            }
            //System.out.println("test: " + World.USED_PIDS.values());
            final long npcProcessNano = System.nanoTime();
            try {
                for (final NPC npc : World.getNPCs()) {
                    try {
                        if (npc == null) {
                            continue;
                        }
                        npc.processEntity();
                    } catch (final Exception e) {
                        log.error("", e);
                    }
                }
            } catch (Exception e) {
                log.error("", e);
            }
            final long npcRemovalNano = System.nanoTime();
            try {
                for (final NPC removed : World.pendingRemovedNPCs) {
                    try {
                        if (removed == null) {
                            continue;
                        }
                        World.getNPCs().remove(removed);
                    } catch (final Exception e) {
                        log.error("", e);
                    }
                }
            } catch (Exception e) {
                log.error("", e);
            }
            try {
                World.pendingRemovedNPCs.clear();
            } catch (Exception e) {
                log.error("", e);
            }
            try {
                NPC.clearPendingAggressions();
            } catch (Exception e) {
                log.error("", e);
            }
            final long playerProcessNano = System.nanoTime();
            try {
                for (final Player player : World.USED_PIDS.values()) {
                    try {
                        if (player == null || player.isNulled() || !player.isRunning() || player.isFinished()) {
                            continue;
                        }
                        player.processEntity();
                        /*final GameNoticeboardInterface.StaffStatus status = GameNoticeboardInterface.getStaffStatus(player);
                        if (status == GameNoticeboardInterface.StaffStatus.PUBLIC) {
                            publicStaff.add(player.getName());
                        } else if (status == GameNoticeboardInterface.StaffStatus.NOT_PUBLIC) {
                            privateStaff.add(player.getName());
                        }*/
                        if (resetDailies) {
                            RuneDate.checkDate(player);
                        }
                    } catch (final Exception e) {
                        log.error("", e);
                    }
                }
            } catch (Exception e) {
                log.error("", e);
            }
            try {
                for (final Player player : World.USED_PIDS.values()) {
                    try {
                        if (player == null || player.isNulled() || !player.isRunning() || player.isFinished()) {
                            continue;
                        }
                        player.postProcess();
                    } catch (final Exception e) {
                        log.error("", e);
                    }
                }
            } catch (Exception e) {
                log.error("", e);
            }
            final long partyRoomNano = System.nanoTime();
            try {
                final Optional<FaladorPartyRoom> area = GlobalAreaManager.getOptionalArea(FaladorPartyRoom.class);
                area.ifPresent(FaladorPartyRoom::processBalloons);
            } catch (Exception e) {
                log.error("", e);
            }
            try {
                GlobalAreaManager.postProcess();
            } catch (Exception e) {
                log.error("", e);
            }
            final long playerFlushNano = System.nanoTime();
            try {
                World.USED_PIDS.values().parallelStream().forEach(player -> {
                    try {
                        if (player == null || player.isNulled() || !player.isRunning() || player.isFinished()) {
                            return;
                        }
                        if (player.getTemporaryAttributes().containsKey("tournament_spectating")) {
                            TournamentViewerInterface.refreshSpectator(player);
                        }
                        player.processEntityUpdate();
                    } catch (final Exception e) {
                        log.error("", e);
                    }
                });
            } catch (Exception e) {
                log.error("", e);
            }
            final long playerLogoutNano = System.nanoTime();
            try {
                final long milliseconds = System.currentTimeMillis();
                for (final Player player : World.USED_PIDS.values()) {
                    try {
                        final Channel channel = player.getSession().getChannel();
                        final boolean inWildy = player.getArea() instanceof WildernessArea;
                        final long playerExpirationTime = milliseconds - TimeUnit.TICKS.toMillis(inWildy ? 100 : 25);
                        final boolean isExpired = player.getLastReceivedPacket() < playerExpirationTime;
                        if (isExpired || player.isLoggedOut() || !channel.isActive() || !channel.isOpen()) {
                            if (player.getLastDisconnectionTime() == 0) {
                                player.setLastDisconnectionTime(Utils.currentTimeMillis());
                            }
                            if (!isExpired && (player.isUnderCombat() || player.isLocked()) && player.getLogoutCount() < (inWildy ? 100 : 25)) {
                                player.setLogoutCount(player.getLogoutCount() + 1);
                                continue;
                            }
                            player.logout(true);
                            player.getSession().getChannel().flush();
                            player.getSession().getChannel().closeFuture();
                            World.unregisterPlayer(player);
                        }
                    } catch (final Exception e) {
                        log.error("", e);
                    }
                }
            } catch (Exception e) {
                log.error("", e);
            }
            final long maskResetNano = System.nanoTime();
            try {
                World.getNPCs().parallelStream().forEach(npc -> {
                    try {
                        if (npc == null || npc.isFinished()) {
                            return;
                        }
                        npc.resetMasks();
                    } catch (final Exception e) {
                        log.error("", e);
                    }
                });
            } catch (Exception e) {
                log.error("", e);
            }
            try {
                World.USED_PIDS.values().parallelStream().forEach(player -> {
                    try {
                        if (player == null || player.isFinished()) return;
                        player.resetMasks();
                    } catch (final Exception e) {
                        log.error("", e);
                    }
                });
            } catch (Exception e) {
                log.error("", e);
            }
            try {
                if (--pidSwapDelay == 0) {
                    try {
                        World.shufflePids();
                    } catch (final Exception e) {
                        log.error("", e);
                    }
                    pidSwapDelay = Utils.random(100, 150);
                }
            } catch (Exception e) {
                log.error("", e);
            }
            final long purgeNano = System.nanoTime();
            try {
                if (Constants.PURGING_CHUNKS) {
                    if (WORLD_CYCLE % 500 == 0) {
                        World.purgeChunks();
                    }
                }
            } catch (Exception e) {
                log.error("", e);
            }
            final long playerSaveNano = System.nanoTime();
            try {
                final LoginManager loginManager = CoresManager.getLoginManager();
                final Set<Player> awaitingSave = loginManager.getAwaitingSave();
                if (!awaitingSave.isEmpty()) {
                    for (final Player player : awaitingSave) {
                        loginManager.save(player);
                    }
                    awaitingSave.clear();
                }
            } catch (Exception e) {
                log.error("", e);
            }
            final long finishNano = System.nanoTime();
            final WorldThread.TickLog tickLog = new TickLog(new Date(), WorldThread.WORLD_CYCLE, shopNano - nano, areaManagerNano - shopNano, worldTaskProcessNano - areaManagerNano, gameClockNano - worldTaskProcessNano, npcProcessNano - gameClockNano, npcRemovalNano - npcProcessNano, playerProcessNano - npcRemovalNano, partyRoomNano - playerProcessNano, playerFlushNano - partyRoomNano, playerLogoutNano - playerFlushNano, maskResetNano - playerLogoutNano, purgeNano - maskResetNano, WORLD_CYCLE % 500 == 0 ? (playerSaveNano - purgeNano) : 0, finishNano - playerSaveNano, finishNano - nano, World.getPlayers().size(), World.getNPCs().size(), WorldTasksManager.count(), publicStaff, privateStaff);
            tickLogger.info(gson.toJson(tickLog));
            if (Constants.CYCLE_DEBUG) {
                System.out.println("Cycle took: " + ((System.nanoTime() - nano) / 1000000.0F) + " ms. Players: " + World.getPlayers().size() + ". NPCs: " + World.getNPCs().size());
            }
            if (CoresManager.isShutdown()) {
                try {
                    World.shutdown();
                } catch (Exception e) {
                    log.error("", e);
                }
            }
        } catch (final Exception e) {
            log.error("", e);
        }
    }

    public static class TickLog {
        private final Date date;
        private final long tick;
        private final long containerT;
        private final long shopT;
        private final long areaT;
        private final long worldTaskT;
        private final long gameClockT;
        private final long npcProcessT;
        private final long npcRemovalT;
        private final long playerProcessT;
        private final long partyRoomT;
        private final long playerFlushT;
        private final long playerLogoutT;
        private final long maskResetT;
        private final long purgeT;
        private final long playerSaveT;
        private final long totalT;
        private final int players;
        private final int npcs;
        private final int tasks;
        private final List<String> publicStaff;
        private final List<String> privateStaff;

        public TickLog(final Date date, final long tick, final long containerT, final long shopT, final long areaT, final long worldTaskT, final long gameClockT, final long npcProcessT, final long npcRemovalT, final long playerProcessT, final long partyRoomT, final long playerFlushT, final long playerLogoutT, final long maskResetT, final long purgeT, final long playerSaveT, final long totalT, final int players, final int npcs, final int tasks, final List<String> publicStaff, final List<String> privateStaff) {
            this.date = date;
            this.tick = tick;
            this.containerT = containerT;
            this.shopT = shopT;
            this.areaT = areaT;
            this.worldTaskT = worldTaskT;
            this.gameClockT = gameClockT;
            this.npcProcessT = npcProcessT;
            this.npcRemovalT = npcRemovalT;
            this.playerProcessT = playerProcessT;
            this.partyRoomT = partyRoomT;
            this.playerFlushT = playerFlushT;
            this.playerLogoutT = playerLogoutT;
            this.maskResetT = maskResetT;
            this.purgeT = purgeT;
            this.playerSaveT = playerSaveT;
            this.totalT = totalT;
            this.players = players;
            this.npcs = npcs;
            this.tasks = tasks;
            this.publicStaff = publicStaff;
            this.privateStaff = privateStaff;
        }

        public Date getDate() {
            return this.date;
        }

        public long getTick() {
            return this.tick;
        }

        public long getContainerT() {
            return this.containerT;
        }

        public long getShopT() {
            return this.shopT;
        }

        public long getAreaT() {
            return this.areaT;
        }

        public long getWorldTaskT() {
            return this.worldTaskT;
        }

        public long getGameClockT() {
            return this.gameClockT;
        }

        public long getNpcProcessT() {
            return this.npcProcessT;
        }

        public long getNpcRemovalT() {
            return this.npcRemovalT;
        }

        public long getPlayerProcessT() {
            return this.playerProcessT;
        }

        public long getPartyRoomT() {
            return this.partyRoomT;
        }

        public long getPlayerFlushT() {
            return this.playerFlushT;
        }

        public long getPlayerLogoutT() {
            return this.playerLogoutT;
        }

        public long getMaskResetT() {
            return this.maskResetT;
        }

        public long getPurgeT() {
            return this.purgeT;
        }

        public long getPlayerSaveT() {
            return this.playerSaveT;
        }

        public long getTotalT() {
            return this.totalT;
        }

        public int getPlayers() {
            return this.players;
        }

        public int getNpcs() {
            return this.npcs;
        }

        public int getTasks() {
            return this.tasks;
        }

        public List<String> getPublicStaff() {
            return this.publicStaff;
        }

        public List<String> getPrivateStaff() {
            return this.privateStaff;
        }
    }
}
