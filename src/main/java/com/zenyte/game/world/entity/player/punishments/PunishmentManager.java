package com.zenyte.game.world.entity.player.punishments;

import com.zenyte.Constants;
import com.zenyte.GameEngine;
import com.zenyte.api.client.query.SubmitUserPunishment;
import com.zenyte.api.client.webhook.RevokePunishmentWebhook;
import com.zenyte.api.model.PunishmentLog;
import com.zenyte.cores.CoresManager;
import com.zenyte.game.parser.scheduled.ScheduledExternalizable;
import com.zenyte.game.util.Colour;
import com.zenyte.game.util.TimeUnit;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Privilege;
import com.zenyte.plugins.dialogue.OptionsMenuD;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;

/**
 * @author Kris | 09/03/2019 19:36
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class PunishmentManager implements ScheduledExternalizable {

    public static final String PATH = "data/punishments.json";
    private static final Logger log = LoggerFactory.getLogger(PunishmentManager.class);
    private static final Map<String, List<Punishment>> punishments = new Object2ObjectOpenHashMap<>();
    private static final Function<String, List<Punishment>> function = name -> new ObjectArrayList<>();

    private static Optional<List<Punishment>> getPunishments(@NotNull final String identifier) {
        return Optional.ofNullable(punishments.get(identifier));
    }

    public static Optional<Punishment> isPunishmentActive(@NotNull final Player player, @NotNull final PunishmentType type) {
        return isPunishmentActive(player.getUsername(), player.getIP(), type);
    }

    public static Optional<Punishment> isPunishmentActive(final String username, final String ip, @NotNull final PunishmentType type) {
        final Punishment regularPunishment = getPunishment(username, type);
        if (regularPunishment != null) {
            return Optional.of(regularPunishment);
        }
        final Punishment ipPunishment = getPunishment(ip, type);
        if (ipPunishment != null) {
            return Optional.of(ipPunishment);
        }
        return Optional.empty();
    }

    private static Punishment getPunishment(final String identifier, @NotNull final PunishmentType type) {
        if (identifier == null) {
            return null;
        }
        final Optional<List<Punishment>> optionalPunishments = getPunishments(identifier);
        if (!optionalPunishments.isPresent()) {
            return null;
        }
        final PunishmentCategory category = type.getCategory();
        final List<Punishment> punishments = optionalPunishments.get();
        Punishment longestPunishment = null;
        for (final Punishment punishment : punishments) {
            final PunishmentType punishmentType = punishment.getType();
            if (category.equals(punishmentType.getCategory()) && !punishment.isExpired() && punishmentType.ordinal() >= type.ordinal()) {
                final Date date = longestPunishment == null ? null : longestPunishment.getExpirationDate();
                final Date newDate = punishment.getExpirationDate();
                if (longestPunishment == null || date != null && (newDate == null || newDate.after(date))) {
                    longestPunishment = punishment;
                }
            }
        }
        return longestPunishment;
    }

    public static void requestPunishment(@NotNull final Player reporter, @NotNull final PunishmentType type) {
        reporter.sendInputName("Enter name of the player to " + type.getFormattedString() + ":", n -> {
            final String name = n.trim();
            requestPunishment(reporter, name, type);
        });
    }

    public static void requestPunishment(final Player reporter, final String targetName, final PunishmentType type) {
        CoresManager.getLoginManager().load(targetName, true, target -> {
            if (!target.isPresent()) {
                reporter.sendMessage("Request to " + type.getFormattedString() + " user " + Utils.formatString(targetName) + " " + Colour.RED.wrap("failed") + " - Account does not exist.");
                return;
            }
            final Player offender = target.get();
            reporter.sendInputInt("Enter the duration of the " + type.getFormattedString() + " in hours(0 for permanent): ", hours -> {
                reporter.sendInputString("Enter the reasoning: ", reason -> {
                    final Punishment punishment = new Punishment(type, reporter.getUsername(), Utils.formatUsername(targetName), offender.getIP(), offender.getMACAddress(), new Date(), hours, hours == 0 ? null : Date.from(Instant.now().plusMillis(TimeUnit.HOURS.toMillis(hours))), reason);
                    PunishmentManager.appendPunishment(reporter, targetName, punishment, offender);
                });
            });
        });
    }

    public static void revokePunishments(@NotNull Player reporter) {
        reporter.sendInputName("Enter name of the player whose punishment(s) to revoke:", n -> {
            final String name = n.trim();
            CoresManager.getLoginManager().load(name, true, optionalOffender -> {
                if (!optionalOffender.isPresent()) {
                    reporter.sendMessage("Request to revoke the punishments of user " + Utils.formatString(name) + " " + Colour.RED.wrap("failed") + " - Account does not exist.");
                    return;
                }
                final Player offender = optionalOffender.get();
                final ArrayList<Punishment> list = new ArrayList<>();
                final ArrayList<List<Punishment>> listOfPunishmentLists = new ArrayList<>();
                final List<Punishment> regularPunishments = punishments.get(Utils.formatUsername(name));
                if (regularPunishments != null) {
                    listOfPunishmentLists.add(regularPunishments);
                }
                final List<Punishment> ipPunishments = punishments.get(offender.getIP());
                if (ipPunishments != null) {
                    listOfPunishmentLists.add(ipPunishments);
                }
                final List<Punishment> macPunishments = punishments.get(offender.getMACAddress());
                if (macPunishments != null) {
                    listOfPunishmentLists.add(macPunishments);
                }
                for (final List<Punishment> punishmentList : listOfPunishmentLists) {
                    for (final Punishment punishment : punishmentList) {
                        if (!punishment.isExpired()) {
                            list.add(punishment);
                        }
                    }
                }
                if (list.isEmpty()) {
                    reporter.sendMessage("Request to revoke the punishments of user " + Utils.formatString(name) + " " + Colour.RED.wrap("failed") + " - No active punishments found.");
                    return;
                }
                final ArrayList<String> optionsList = new ArrayList<>(list.size());
                for (final Punishment activePunishment : list) {
                    optionsList.add(activePunishment.toString());
                }
                reporter.getDialogueManager().start(new OptionsMenuD(reporter, "Select the punishment to revoke", optionsList.toArray(new String[0])) {
                    @Override
                    public void handleClick(int slotId) {
                        final Punishment punishment = Objects.requireNonNull(list.get(slotId));
                        if (punishment.getType().getCategory().equals(PunishmentCategory.BAN)) {
                            if (!reporter.getPrivilege().eligibleTo(Privilege.MODERATOR)) {
                                reporter.sendMessage("Supports cannot revoke ban-category punishments.");
                                return;
                            }
                        }
                        reporter.sendInputString("Enter revoke reason:", reason -> {
                            final int previousSize = listOfPunishmentLists.size();
                            listOfPunishmentLists.removeIf(list -> list.remove(punishment));
                            if (previousSize == listOfPunishmentLists.size()) {
                                reporter.sendMessage("Request to revoke the punishment '" + punishment + "' " + Colour.RED.wrap("failed") + " - Punishment has already been revoked.");
                            } else {
                                reporter.sendMessage("Request to revoke the punishment '" + punishment + "' " + Colour.RS_GREEN.wrap("succeeded") + ".");
                                new RevokePunishmentWebhook(reporter, reason, punishment).execute();
                            }
                        });
                    }

                    @Override
                    public boolean cancelOption() {
                        return true;
                    }
                });
            });
        });
    }

    private static void appendPunishment(@NotNull final Player punisher, @NotNull final String target, @NotNull final Punishment punishment, Player offlinePlayer) {
        final PunishmentType type = punishment.getType();
        final List<Punishment> list = punishments.computeIfAbsent(isUserTargeted(type) ? punishment.getOffender() : isIPTargeted(type) ? punishment.getIp() : punishment.getMacAddress(), function);
        final Date requestedDate = punishment.getExpirationDate();
        final Optional<Player> onlineOffender = World.getPlayer(target);
        for (final Punishment previousPunishment : list) {
            if (previousPunishment.getType().equals(type)) {
                final Date date = previousPunishment.getExpirationDate();
                if (date == null || requestedDate != null && date.after(requestedDate)) {
                    punisher.sendMessage("Request to " + punishment.getType().getFormattedString() + " user " + Utils.formatString(target) + " " + Colour.RED.wrap("failed") + " - Existing punishment outlasts requested.");
                    return;
                }
            }
        }
        if (isMACTargeted(type)) {
            if (punishment.getMacAddress() == null || punishment.getMacAddress().length() <= 0) {
                punisher.sendMessage("Unable to punish the user as their mac address is not valid.");
                return;
            }
        }
        final PunishmentCategory category = punishment.getType().getCategory();
        if (category == PunishmentCategory.BAN) {
            try {
                final ObjectOpenHashSet<Player> setOfPeopleToKick = new ObjectOpenHashSet<>();
                onlineOffender.ifPresent(setOfPeopleToKick::add);
                if (type == PunishmentType.IP_BAN) {
                    final String ip = onlineOffender.map(Player::getIP).orElse(offlinePlayer == null ? "" : offlinePlayer.getIP());
                    if (!ip.isEmpty()) {
                        for (final Player player : World.getPlayers()) {
                            if (player == null || player.isNulled() || player.isFinished() || !player.getIP().equals(ip)) {
                                continue;
                            }
                            setOfPeopleToKick.add(player);
                        }
                    }
                } else if (type == PunishmentType.MAC_BAN) {
                    final String mac = onlineOffender.map(Player::getMACAddress).orElse(offlinePlayer == null ? "" : offlinePlayer.getMACAddress());
                    if (!mac.isEmpty()) {
                        for (final Player player : World.getPlayers()) {
                            if (player == null || player.isNulled() || player.isFinished() || !player.getMACAddress().equals(mac)) {
                                continue;
                            }
                            setOfPeopleToKick.add(player);
                        }
                    }
                }
                for (final Player player : setOfPeopleToKick) {
                    if (player == null || player.isFinished() || player.isNulled()) {
                        continue;
                    }
                    player.logout(true);
                }
                setOfPeopleToKick.clear();
            } catch (Exception e) {
                GameEngine.logger.error("", e);
            }
        }
        list.removeIf(p -> p.getType().equals(type) && (requestedDate == null || requestedDate.after(p.getExpirationDate())));
        list.add(punishment);
        final String formattedExpiry = requestedDate == null ? "Never" : requestedDate.toString();
        punisher.sendMessage("Request to " + punishment.getType().getFormattedString() + " user " + Utils.formatString(target) + " " + Colour.RS_GREEN.wrap("succeeded") + " - expires: " + formattedExpiry + ".");
        submitPunishment(new PunishmentLog(punisher.getPlayerInformation().getUserIdentifier(), punisher.getName(), onlineOffender.map(p -> p.getPlayerInformation().getUserIdentifier()).orElse(offlinePlayer == null ? -1 : offlinePlayer.getPlayerInformation().getUserIdentifier()), target, onlineOffender.map(Player::getIP).orElse(offlinePlayer == null ? "" : offlinePlayer.getIP()), onlineOffender.map(Player::getMACAddress).orElse(offlinePlayer == null ? "" : offlinePlayer.getMACAddress()), punishment.getType().getFormattedString(), formattedExpiry, punishment.getReason()));
    }

    private static boolean isUserTargeted(final PunishmentType type) {
        return type == PunishmentType.BAN || type == PunishmentType.MUTE || type == PunishmentType.YELL_MUTE;
    }

    private static boolean isIPTargeted(final PunishmentType type) {
        return type == PunishmentType.IP_BAN || type == PunishmentType.IP_MUTE || type == PunishmentType.IP_YELL_MUTE;
    }

    private static boolean isMACTargeted(final PunishmentType type) {
        return type == PunishmentType.MAC_BAN || type == PunishmentType.MAC_MUTE || type == PunishmentType.MAC_YELL_MUTE;
    }

    private static void submitPunishment(final PunishmentLog punishment) {
        if (!Constants.WORLD_PROFILE.getApi().isEnabled() || Constants.WORLD_PROFILE.isPrivate() || Constants.WORLD_PROFILE.isBeta()) {
            return;
        }
        CoresManager.getServiceProvider().submit(() -> {
            new SubmitUserPunishment(punishment).execute();
        });
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
    public void read(final BufferedReader reader) {
        final Punishment[] punishmentsArray = World.getGson().fromJson(reader, Punishment[].class);
        for (final Punishment punishment : punishmentsArray) {
            final String identifier = punishment.getOffender();
            final List<Punishment> list = punishments.computeIfAbsent(identifier, function);
            punishments.put(identifier, list);
            list.add(punishment);
        }
    }

    @Override
    public void write() {
        punishments.forEach((name, list) -> list.removeIf(Punishment::isExpired));
        punishments.entrySet().removeIf(entry -> entry.getValue().isEmpty());
        final ArrayList<Punishment> list = new ArrayList<>(punishments.size());
        punishments.forEach((key, l) -> list.addAll(l));
        out(World.getGson().toJson(list.toArray()));
    }

    @Override
    public String path() {
        return PATH;
    }

}
