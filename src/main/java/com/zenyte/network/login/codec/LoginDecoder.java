package com.zenyte.network.login.codec;

import com.zenyte.game.HardwareInfo;
import com.zenyte.game.util.Utils;
import com.zenyte.network.ClientResponse;
import com.zenyte.network.NetworkConstants;
import com.zenyte.network.io.ByteBufUtil;
import com.zenyte.network.io.security.ISAACCipher;
import com.zenyte.network.io.security.ISAACCipherPair;
import com.zenyte.network.login.packet.LoginPacketIn;
import com.zenyte.network.login.packet.inc.LoginType;
import com.zenyte.utils.Ordinal;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

import static com.zenyte.network.io.ByteBufUtil.readIntIME;
import static com.zenyte.network.io.ByteBufUtil.readIntME;

/**
 * @author Tommeh | 27 jul. 2018 | 19:21:02
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server
 * profile</a>}
 */
public final class LoginDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(final ChannelHandlerContext ctx, final ByteBuf in, final List<Object> out) throws Exception {
        if (in.readableBytes() < 3) {
            return;
        }
        in.markReaderIndex();
        final LoginType type = LoginType.get(in.readUnsignedByte());
        final int size = in.readUnsignedShort();
        if (in.readableBytes() < size) {
            in.resetReaderIndex();
            return;
        }
        final int version = in.readInt();
        final int subVersion = in.readInt();
        final int clientType = in.readUnsignedByte();
        in.skipBytes(2);

        final ByteBuf rsaBuf = ByteBufUtil.encipherRSA(in, NetworkConstants.RSA_EXPONENT, NetworkConstants.RSA_MODULUS);
        if (rsaBuf == null) {
            ctx.writeAndFlush(ClientResponse.MALFORMED_LOGIN_PACKET).addListener(ChannelFutureListener.CLOSE);
            return;
        }
        final short rsaKey = rsaBuf.readUnsignedByte();
        final int[] xteaKeys = new int[4];
        final int[] serverKeys = new int[4];
        for (int i = 0; i < 4; i++) {
            xteaKeys[i] = rsaBuf.readInt();
            serverKeys[i] = xteaKeys[i] + 50;
        }
        final long serverSeed = rsaBuf.readLong();
        AuthType authType = null;
        int authenticatorCode = -1;
        int pcIdentifier = -1;
        final int[] previousXteaKeys = new int[4];
        String password = "";
        if (type.equals(LoginType.RECONNECT_LOGIN_CONNECTION)) {
            for (int i = 0; i < 4; i++) {
                previousXteaKeys[i] = rsaBuf.readInt();
            }
        } else {
            authType = AuthType.values[rsaBuf.readUnsignedByte()];
            if (authType == AuthType.NORMAL) {
                rsaBuf.skipBytes(4);
            } else if (authType == AuthType.UNTRUSTED_AUTHENTICATION || authType == AuthType.TRUSTED_AUTHENTICATION) {
                authenticatorCode = rsaBuf.readUnsignedMedium();
                rsaBuf.skipBytes(1);
            } else if (authType == AuthType.TRUSTED_COMPUTER) {
                pcIdentifier = rsaBuf.readInt();
            }
            rsaBuf.skipBytes(1);
            password = ByteBufUtil.readString(rsaBuf);
        }
        final ByteBuf xteaBuf = ByteBufUtil.decipherXTEA(in, xteaKeys);
        final String username = ByteBufUtil.readString(xteaBuf);
        final short clientProperties = xteaBuf.readUnsignedByte();
        final boolean lowMemory = (clientProperties & 1) == 1;
        final int mode = clientProperties >> 1;
        final int width = xteaBuf.readUnsignedShort();
        final int height = xteaBuf.readUnsignedShort();
        xteaBuf.skipBytes(24); // cacheUID
        final String sessionToken = ByteBufUtil.readString(xteaBuf);
        final int affiliateId = xteaBuf.readInt();
        final HardwareInfo hardwareInfo = new HardwareInfo(xteaBuf);
        final boolean supportsJs = xteaBuf.readUnsignedByte() == 1;
        xteaBuf.readInt();
        final int[] crc = new int[Math.min(255, xteaBuf.readableBytes() / 4)];
        readCRCs(crc, xteaBuf);
        final ISAACCipherPair isaacPair = new ISAACCipherPair(new ISAACCipher(serverKeys), new ISAACCipher(xteaKeys));
        out.add(new LoginPacketIn(type, version, subVersion, Utils.formatUsername(username), password, mode, crc, sessionToken, authenticatorCode, pcIdentifier, authType, hardwareInfo, isaacPair, rsaKey, xteaKeys, previousXteaKeys));
    }

    @Override
    public boolean isSingleDecode() {
        return true;
    }

    private static void readCRCs(final int[] crc, final ByteBuf buf) {
        crc[12] = readIntIME(buf);
        crc[2] = readIntIME(buf);
        crc[10] = readIntIME(buf);
        crc[11] = buf.readIntLE();
        crc[5] = readIntME(buf);
        crc[16] = buf.readIntLE();
        crc[14] = buf.readIntLE();
        crc[19] = buf.readInt();
        crc[18] = readIntME(buf);
        crc[20] = buf.readInt();
        crc[0] = readIntIME(buf);
        crc[3] = buf.readIntLE();
        crc[8] = readIntME(buf);
        crc[13] = buf.readInt();
        crc[6] = readIntME(buf);
        crc[15] = readIntME(buf);
        crc[9] = readIntIME(buf);
        crc[7] = readIntIME(buf);
        crc[17] = buf.readIntLE();
        crc[4] = buf.readInt();
        crc[1] = buf.readIntLE();
    }

    @Ordinal
    public enum AuthType {
        TRUSTED_COMPUTER,
        TRUSTED_AUTHENTICATION,
        NORMAL,
        UNTRUSTED_AUTHENTICATION;
        private static final AuthType[] values = values();

        AuthType() {
        }
    }
}
