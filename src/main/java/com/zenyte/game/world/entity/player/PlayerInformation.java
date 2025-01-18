package com.zenyte.game.world.entity.player;

import com.google.gson.annotations.Expose;
import com.zenyte.game.HardwareInfo;
import com.zenyte.game.packet.Session;
import com.zenyte.game.util.Utils;
import com.zenyte.network.login.packet.LoginPacketIn;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class PlayerInformation {
    private final transient HardwareInfo hardware;
    /**
     * The channel which this connection communicates through.
     */
    private transient Session session;
    /**
     * The player's user name
     */
    private String username;
    /**
     * The player's display name.
     */
    private String displayname;
    private String plainPassword;
    /**
     * The last known login address of a player
     */
    @Expose
    private String ip;
    /**
     * The register date of the player.
     */
    @Expose
    private LocalDate registryDate;
    /**
     * The display mode of the user.
     */
    @Expose
    private int mode;
    /**
     * This id is binded to the player and will NEVER change. This is their branded id.
     */
    @Expose
    private int userIdentifier;

    public PlayerInformation(final Session session, final LoginPacketIn query) {
        this.session = session;
        username = query.getUsername();
        setDisplayname(username);
        //password = query.getPassword();
        this.plainPassword = query.getPassword();
        mode = query.getMode();
        hardware = query.getHardwareInfo();
        userIdentifier = -1;
        registryDate = LocalDate.now();
    }

    public void setPlayerInformation(final PlayerInformation details) {
        username = details.getUsername();
        setPlainPassword(details.getPlainPassword());
        setDisplayname(details.getDisplayname());
        setUserIdentifier(details.getUserIdentifier());
        setIp(details.getIp());
        setRegistryDate(details.getRegistryDate());
    }

    public int getDaysSinceRegistry() {
        return (int) registryDate.until(LocalDate.now(), ChronoUnit.DAYS);
    }

    public String getIpFromChannel() {
        final SocketAddress remoteAddress = session.getChannel().remoteAddress();
        if (remoteAddress instanceof InetSocketAddress socketAddress) {
            return socketAddress.getAddress().getHostAddress();
        }
        return "null";
    }

    public boolean isOnMobile() {
        return false;
    }

    /**
     * The channel which this connection communicates through.
     */
    public Session getSession() {
        return this.session;
    }

    /**
     * The channel which this connection communicates through.
     */
    public void setSession(final Session session) {
        this.session = session;
    }

    /**
     * The player's user name
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * The player's display name.
     */
    public String getDisplayname() {
        return this.displayname;
    }

    private void setDisplayname(final String displayName) {
        this.displayname = Utils.formatString(displayName == null || displayName.isEmpty() ? username : displayName);
    }

    public String getPlainPassword() {
        return this.plainPassword;
    }

    public void setPlainPassword(String plainPassword) {
        this.plainPassword = plainPassword;
    }

    /**
     * The last known login address of a player
     */
    public String getIp() {
        return this.ip;
    }

    /**
     * The last known login address of a player
     */
    public void setIp(final String ip) {
        this.ip = ip;
    }

    /**
     * The register date of the player.
     */
    public LocalDate getRegistryDate() {
        return this.registryDate;
    }

    /**
     * The register date of the player.
     */
    public void setRegistryDate(final LocalDate registryDate) {
        this.registryDate = registryDate;
    }

    /**
     * The display mode of the user.
     */
    public int getMode() {
        return this.mode;
    }

    /**
     * The display mode of the user.
     */
    public void setMode(final int mode) {
        this.mode = mode;
    }

    public HardwareInfo getHardware() {
        return this.hardware;
    }

    /**
     * This id is binded to the player and will NEVER change. This is their branded id.
     */
    public int getUserIdentifier() {
        return this.userIdentifier;
    }

    /**
     * This id is binded to the player and will NEVER change. This is their branded id.
     */
    public void setUserIdentifier(final int userIdentifier) {
        this.userIdentifier = userIdentifier;
    }

}
