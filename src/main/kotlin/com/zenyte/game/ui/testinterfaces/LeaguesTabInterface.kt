package com.zenyte.game.ui.testinterfaces

import com.zenyte.game.constants.GameInterface
import com.zenyte.game.ui.Interface
import com.zenyte.game.ui.PaneType
import com.zenyte.game.world.entity.player.Player

class LeaguesTabInterface : Interface() {

    override fun open(player: Player) {
        player.interfaceHandler.sendInterface(getInterface().id, 33, PaneType.JOURNAL_TAB_HEADER, true)
    }

    override fun attach() {
        put(32, "Relics")
    }

    override fun build() {
        bind("Relics") { player: Player? ->
            GameInterface.RELIC.open(
                player
            )
            //shield wall
            val dispatcher = player!!.packetDispatcher
            dispatcher.sendComponentText(GameInterface.RELIC, 34, "Shield Wall")
            dispatcher.sendComponentText(GameInterface.RELIC, 40, "Shield Wall")
            dispatcher.sendComponentText(GameInterface.RELIC, 41, "Shield Wall")
            dispatcher.sendComponentText(
                GameInterface.RELIC,
                42,
                "While using a shield 20% increased accuracy and defences"
            )
            dispatcher.sendComponentVisibility(GameInterface.RELIC, 52, true)
            dispatcher.sendComponentText(GameInterface.RELIC, 55, "Combat 2")
            dispatcher.sendComponentVisibility(GameInterface.RELIC, 56, true)
            dispatcher.sendComponentText(GameInterface.RELIC, 59, "Combat 3")
            dispatcher.sendComponentVisibility(GameInterface.RELIC, 60, true)
            dispatcher.sendComponentText(GameInterface.RELIC, 63, "Combat 4")
            dispatcher.sendComponentVisibility(GameInterface.RELIC, 64, true)
            dispatcher.sendComponentText(GameInterface.RELIC, 67, "Combat 5")
            dispatcher.sendComponentVisibility(GameInterface.RELIC, 68, true)
            dispatcher.sendComponentText(GameInterface.RELIC, 71, "Combat 6")
            dispatcher.sendComponentVisibility(GameInterface.RELIC, 72, true)
            dispatcher.sendComponentText(GameInterface.RELIC, 75, "Combat 7")
            dispatcher.sendComponentVisibility(GameInterface.RELIC, 76, true)
            dispatcher.sendComponentText(GameInterface.RELIC, 79, "Combat 8")
            dispatcher.sendComponentVisibility(GameInterface.RELIC, 80, true)
            dispatcher.sendComponentText(GameInterface.RELIC, 83, "Combat 9")
            dispatcher.sendComponentVisibility(GameInterface.RELIC, 84, true)
            dispatcher.sendComponentText(GameInterface.RELIC, 87, "Combat 10")
            dispatcher.sendComponentVisibility(GameInterface.RELIC, 88, true)
            dispatcher.sendComponentText(GameInterface.RELIC, 91, "Combat 11")

        }
    }

    override fun getInterface() = GameInterface.LEAGUES_TAB

}