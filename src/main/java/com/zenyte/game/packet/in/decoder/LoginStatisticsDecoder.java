package com.zenyte.game.packet.in.decoder;

import com.zenyte.game.packet.ClientProtDecoder;
import com.zenyte.game.packet.in.event.LoginStatisticsEvent;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.io.RSBuffer;

public final class LoginStatisticsDecoder implements ClientProtDecoder<LoginStatisticsEvent> {

    @Override
    public LoginStatisticsEvent decode(Player player, int opcode, RSBuffer buffer) {
        int field4237 = buffer.readUnsignedShort(); // timer.field4237
        int field4233 = buffer.readUnsignedShort(); // timer.field4233
        int field4238 = buffer.readUnsignedShort(); // timer.field4238
        int field4242 = buffer.readUnsignedShort(); // timer.field4242
        int field4240 = buffer.readUnsignedShort(); // timer.field4240
        int field4241 = buffer.readUnsignedShort(); // timer.field4241
        int field4236 = buffer.readUnsignedShort(); // timer.field4236
        return new LoginStatisticsEvent(
                field4237,
                field4233,
                field4238,
                field4242,
                field4240,
                field4241,
                field4236
        );
    }

}
