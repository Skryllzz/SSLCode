package com.zenyte.game.model.ui.chat_channel

import com.zenyte.game.world.entity.player.Player

val Player.chatChannelInterfaceType: ChatChannelInterfaceType
    get() = ChatChannelInterfaceType.Regular

var Player.selectedChatChannelType: ChatChannelType
    set(value) {
        val socialTabsInterface = chatChannelInterfaceType
        if (socialTabsInterface.components.contains(value)) {
            attributes["selectedSocialTabType"] = value.name
            socialTabsInterface.varCollection.update(this)
            value.gameInterface.open(this)
        } else sendMessage(
            "Could not set `selectedSocialTabType` attribute because $value is not supported for your game-mode."
        )
    }
    get() {
        val availableComponents = chatChannelInterfaceType.components
        return (attributes["selectedSocialTabType"] as? String)
            .let { enumName -> availableComponents.find { it.name == enumName } }
            ?: availableComponents.first()
    }

fun Player.sendSocialTabs() {
    chatChannelInterfaceType.gameInterface.open(this)
    selectedChatChannelType.gameInterface.open(this)
}
