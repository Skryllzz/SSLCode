package com.zenyte.game.world.entity.player.punishments;

import com.zenyte.game.util.Colour;
import com.zenyte.game.util.Utils;

import java.time.Instant;
import java.util.Date;

/**
 * @author Kris | 09/03/2019 19:45
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class Punishment {
    private final PunishmentType type;
    private final String reporter;
    private final String offender;
    private final String ip;
    private final String macAddress;
    private final Date timeOfPunishment;
    private final int durationInHours;
    private final Date expirationDate;
    private final String reason;

    public Punishment(final PunishmentType type, final String reporter, final String offender, final String ip, final String macAddress, final Date timeOfPunishment, final int durationInHours, final Date expirationDate, final String reason) {
        this.type = type;
        this.reporter = reporter;
        this.offender = offender;
        this.ip = ip;
        this.macAddress = macAddress;
        this.timeOfPunishment = timeOfPunishment;
        this.durationInHours = durationInHours;
        this.expirationDate = expirationDate;
        this.reason = reason;
    }

    boolean isExpired() {
        return expirationDate != null && expirationDate.before(Date.from(Instant.now()));
    }

    @Override
    public String toString() {
        return Colour.RS_GREEN.wrap(type.getFormattedString()) + " by " + Colour.RS_GREEN.wrap(Utils.formatString(reporter)) + " expires " + Colour.RS_GREEN.wrap((expirationDate == null ? "Never" : expirationDate.toString()));
    }

    public String toLoginString() {
        final String formattedString = type.getFormattedString();
        return Colour.RS_GREEN.wrap(formattedString + (type.getCategory() == PunishmentCategory.MUTE ? "d" : "ned")) + " by " + Colour.RS_GREEN.wrap(Utils.formatString(reporter)) + " - expires " + Colour.RS_GREEN.wrap((expirationDate == null ? "Never" : expirationDate.toString()));
    }

    public PunishmentType getType() {
        return this.type;
    }

    public String getReporter() {
        return this.reporter;
    }

    public String getOffender() {
        return this.offender;
    }

    public String getIp() {
        return this.ip;
    }

    public String getMacAddress() {
        return this.macAddress;
    }

    public Date getTimeOfPunishment() {
        return this.timeOfPunishment;
    }

    public int getDurationInHours() {
        return this.durationInHours;
    }

    public Date getExpirationDate() {
        return this.expirationDate;
    }

    public String getReason() {
        return this.reason;
    }
}
