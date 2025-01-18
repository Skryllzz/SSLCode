package com.zenyte.network.login.packet;

import com.zenyte.game.HardwareInfo;
import com.zenyte.network.PacketIn;
import com.zenyte.network.io.security.ISAACCipherPair;
import com.zenyte.network.login.codec.LoginDecoder;
import com.zenyte.network.login.packet.inc.LoginType;

/**
 * @author Tommeh | 27 jul. 2018 | 19:47:40
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class LoginPacketIn implements PacketIn {
    private final LoginType type;
    private final int version;
    private final int subVersion;
    private final int authenicatorCode;
    private final int pcIdentifier;
    private final int rsaKey;
    private final LoginDecoder.AuthType authType;
    private final String username;
    private final String password;
    private final int mode;
    private final int[] crc;
    private final int[] xteaKeys;
    private final int[] previousXteaKeys;
    private final String sessionToken;
    private final HardwareInfo hardwareInfo;
    private final ISAACCipherPair isaacCipherPair;

    public LoginPacketIn(final LoginType type, final int version, int subVersion, final String username, final String password, final int mode, final int[] crc, final String sessionToken, final int authenicatorCode, final int pcIdentifier, final LoginDecoder.AuthType trusted, final HardwareInfo hardwareInfo, final ISAACCipherPair isaacCipherPair, final int rsaKey, final int[] xteaKeys, final int[] previousXteaKeys) {
        this.type = type;
        this.version = version;
        this.subVersion = subVersion;
        this.username = username;
        this.password = password;
        this.mode = mode;
        this.crc = crc;
        this.sessionToken = sessionToken;
        this.authenicatorCode = authenicatorCode;
        this.pcIdentifier = pcIdentifier;
        this.authType = trusted;
        this.hardwareInfo = hardwareInfo;
        this.isaacCipherPair = isaacCipherPair;
        this.rsaKey = rsaKey;
        this.xteaKeys = xteaKeys;
        this.previousXteaKeys = previousXteaKeys;
    }

    public LoginType getType() {
        return this.type;
    }

    public int getVersion() {
        return this.version;
    }

    public int getSubVersion() {
        return this.subVersion;
    }

    public int getAuthenicatorCode() {
        return this.authenicatorCode;
    }

    public int getPcIdentifier() {
        return this.pcIdentifier;
    }

    public int getRsaKey() {
        return this.rsaKey;
    }

    public LoginDecoder.AuthType getAuthType() {
        return this.authType;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public int getMode() {
        return this.mode;
    }

    public int[] getCrc() {
        return this.crc;
    }

    public int[] getXteaKeys() {
        return this.xteaKeys;
    }

    public int[] getPreviousXteaKeys() {
        return this.previousXteaKeys;
    }

    public String getSessionToken() {
        return this.sessionToken;
    }

    public HardwareInfo getHardwareInfo() {
        return this.hardwareInfo;
    }

    public ISAACCipherPair getIsaacCipherPair() {
        return this.isaacCipherPair;
    }

}
