package com.zenyte.game.packet.out.clan_channel

enum class ChatChannelType(val packetIdentifier: Int) {
    GUEST(-1),
    CLAN(0),
    GIM(1)
}
