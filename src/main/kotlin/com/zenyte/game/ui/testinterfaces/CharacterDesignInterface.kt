package com.zenyte.game.ui.testinterfaces

import com.zenyte.game.constants.GameInterface
import com.zenyte.game.ui.Interface
import com.zenyte.game.world.entity.masks.UpdateFlag
import com.zenyte.game.world.entity.player.Player
import mgi.types.config.identitykit.IdentityKitDefinitions

class CharacterDesignInterface : Interface() {
    override fun attach() {
        put(12, "Previous head")
        put(13, "Next head")
        put(16, "Previous jaw")
        put(17, "Next jaw")
        put(20, "Previous torso")
        put(21, "Next torso")
        put(24, "Previous arms")
        put(25, "Next arms")
        put(28, "Previous hands")
        put(29, "Next hands")
        put(32, "Previous legs")
        put(33, "Next legs")
        put(36, "Previous feet")
        put(37, "Next feet")

        put(43, "Previous hair colour")
        put(44, "Next hair colour")
        put(47, "Previous torso colour")
        put(48, "Next torso colour")
        put(51, "Previous legs colour")
        put(52, "Next legs colour")
        put(55, "Previous feet colour")
        put(56, "Next feet colour")
        put(59, "Previous skin colour")
        put(60, "Next skin colour")

        put(65, "Male")
        put(66, "Female")
        put(68, "Confirm")
    }

    override fun build() {
        operator fun String.invoke(id: Int, stuff: Player.() -> Unit) = bind(this) { player: Player -> player.stuff() }

        "Previous head"(12) { changeDesign(CharacterCreatorDesign.Head, false) }
        "Next head"(13) { changeDesign(CharacterCreatorDesign.Head, true) }
        "Previous jaw"(16) { changeDesign(CharacterCreatorDesign.Jaw, false) }
        "Next jaw"(17) { changeDesign(CharacterCreatorDesign.Jaw, true) }
        "Previous torso"(20) { changeDesign(CharacterCreatorDesign.Torso, false) }
        "Next torso"(21) { changeDesign(CharacterCreatorDesign.Torso, true) }
        "Previous arms"(24) { changeDesign(CharacterCreatorDesign.Arms, false) }
        "Next arms"(25) { changeDesign(CharacterCreatorDesign.Arms, true) }
        "Previous hands"(28) { changeDesign(CharacterCreatorDesign.Hands, false) }
        "Next hands"(29) { changeDesign(CharacterCreatorDesign.Hands, true) }
        "Previous legs"(32) { changeDesign(CharacterCreatorDesign.Legs, false) }
        "Next legs"(33) { changeDesign(CharacterCreatorDesign.Legs, true) }
        "Previous feet"(36) { changeDesign(CharacterCreatorDesign.Feet, false) }
        "Next feet"(37) { changeDesign(CharacterCreatorDesign.Feet, true) }

        "Previous hair colour"(43) { changeColour(CharacterCreatorColour.Hair, false) }
        "Next hair colour"(44) { changeColour(CharacterCreatorColour.Hair, true) }
        "Previous torso colour"(47) { changeColour(CharacterCreatorColour.Torso, false) }
        "Next torso colour"(48) { changeColour(CharacterCreatorColour.Torso, true) }
        "Previous legs colour"(51) { changeColour(CharacterCreatorColour.Legs, false) }
        "Next legs colour"(52) { changeColour(CharacterCreatorColour.Legs, true) }
        "Previous feet colour"(55) { changeColour(CharacterCreatorColour.Feet, false) }
        "Next feet colour"(56) { changeColour(CharacterCreatorColour.Feet, true) }
        "Previous skin colour"(59) { changeColour(CharacterCreatorColour.Skin, false) }
        "Next skin colour"(60) { changeColour(CharacterCreatorColour.Skin, true) }

        "Male"(65) { changeGender(true) }
        "Female"(66) { changeGender(false) }
        "Confirm"(68) { confirm() }
    }

    override fun getInterface() = GameInterface.CHARACTER_DESIGN

    companion object {
        fun Player.changeDesign(design: CharacterCreatorDesign, next: Boolean) {
            if (design == CharacterCreatorDesign.Jaw && !appearance.isMale) return
            val kits = IdentityKitDefinitions.DEFINITIONS
            var current = appearance.appearance[design.id].toInt()
            val genderOffset = if (appearance.isMale) 0 else 7
            var nextKit: IdentityKitDefinitions?
            do {
                if (next) {
                    current++
                    if (current >= kits.size) current = 0
                } else {
                    current--
                    if (current < 0) current = kits.size - 1
                }
                nextKit = IdentityKitDefinitions.get(current)
                val partId = nextKit?.bodyPartId
            } while (nextKit == null || !nextKit.isSelectable || partId == null || (design.id + genderOffset) != partId)
            appearance.appearance[design.id] = nextKit.id.toShort()
            updateFlags.flag(UpdateFlag.APPEARANCE)
        }

        fun Player.changeColour(colour: CharacterCreatorColour, next: Boolean) {
            var current = appearance.colours[colour.id].toInt()
            var found: Boolean
            do {
                if (next) {
                    current++
                    if (current >= colour.count) current = 0
                } else {
                    current--
                    if (current < 0) current = colour.count - 1
                }
                val skinColour = SkinColour[current]
                val hasSkinColourUnlocked = skinColour != null
                found = colour != CharacterCreatorColour.Skin || current < 8 || hasSkinColourUnlocked
            } while (!found)
            appearance.colours[colour.id] = current.toByte()
            updateFlags.flag(UpdateFlag.APPEARANCE)
        }

        fun Player.changeGender(male: Boolean) {
            appearance.isMale = male
            varManager.sendBitInstant(14021, if (male) 0 else 1)
            for (design in enumValues<CharacterCreatorDesign>()) {
                if (design == CharacterCreatorDesign.Jaw) {
                    appearance.appearance[design.id] = if (male) 10 else 1000
                    continue
                }
                val parts = if (!male) design.maleParts else design.femaleParts
                val oppositeParts = if (!male) design.femaleParts else design.maleParts
                val current = IdentityKitDefinitions.get(appearance.appearance[design.id].toInt())
                val index = parts.indexOf(current)
                val next = oppositeParts.getOrNull(index) ?: oppositeParts.first()
                appearance.appearance[design.id] = next.id.toShort()
            }
            updateFlags.flag(UpdateFlag.APPEARANCE)
        }

        fun Player.confirm() = interfaceHandler.closeInterfaces()

        enum class CharacterCreatorDesign(val id: Int) {
            Head(0),
            Jaw(1),
            Torso(2),
            Arms(3),
            Hands(4),
            Legs(5),
            Feet(6);

            private val filteredConfigValues: List<IdentityKitDefinitions> get() = IdentityKitDefinitions.DEFINITIONS.filterNot { !it.isSelectable }
            val maleParts = filteredConfigValues.filter { it.bodyPartId == id }
            val femaleParts = filteredConfigValues.filter { it.bodyPartId == id + 7 }
        }

        enum class CharacterCreatorColour(val id: Int, val count: Int) {
            Hair(0, 25),
            Torso(1, 29),
            Legs(2, 29),
            Feet(3, 6),
            Skin(4, 13),
        }

        private enum class SkinColour(val index: Int) {
            BLACK(9),
            WHITE(10),
            GREEN(8),
            TURQOISE(11),
            PURPLE(12);

            companion object {
                private val all: Array<SkinColour> = values()
                private val map: MutableMap<Int, SkinColour> = HashMap(all.size)

                init {
                    for (colour in all) {
                        map[colour.index] = colour
                    }
                }

                operator fun get(index: Int): SkinColour? {
                    return map[index]
                }
            }
        }
    }
}