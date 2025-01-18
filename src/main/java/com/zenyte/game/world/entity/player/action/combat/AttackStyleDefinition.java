package com.zenyte.game.world.entity.player.action.combat;

import static com.zenyte.game.world.entity.npc.combatdefs.AttackType.*;
import static com.zenyte.game.world.entity.player.action.combat.AttackStyle.AttackExperienceType.*;

public enum AttackStyleDefinition {

    UNARMED("Unarmed", new AttackStyle(CRUSH, ATTACK_XP), new AttackStyle(CRUSH, STRENGTH_XP), new AttackStyle(CRUSH, DEFENCE_XP)),
    AXE("Axe", new AttackStyle(SLASH, ATTACK_XP), new AttackStyle(SLASH, STRENGTH_XP), new AttackStyle(CRUSH, STRENGTH_XP), new AttackStyle(SLASH, DEFENCE_XP)),
    MAUL("Blunt", new AttackStyle(CRUSH, ATTACK_XP), new AttackStyle(CRUSH, STRENGTH_XP), new AttackStyle(CRUSH, DEFENCE_XP)),
    BOW("Bow", new AttackStyle(RANGED, RANGED_XP), new AttackStyle(RANGED, RANGED_XP), new AttackStyle(RANGED, RANGED_DEFENCE_XP)),
    CLAWS("Claw", new AttackStyle(SLASH, ATTACK_XP), new AttackStyle(SLASH, STRENGTH_XP), new AttackStyle(STAB, SHARED_XP), new AttackStyle(SLASH, DEFENCE_XP)),
    CROSSBOW("Crossbow", new AttackStyle(RANGED, RANGED_XP), new AttackStyle(RANGED, RANGED_XP), new AttackStyle(RANGED, RANGED_DEFENCE_XP)),
    LIZARD("Salamander", new AttackStyle(SLASH, STRENGTH_XP), new AttackStyle(RANGED, RANGED_XP), new AttackStyle(MAGIC, MAGIC_XP)),
    CHINCHOMPA("Chinchompas", new AttackStyle(RANGED, RANGED_XP), new AttackStyle(RANGED, RANGED_XP), new AttackStyle(RANGED, RANGED_DEFENCE_XP)),
    BAZOOKA("Gun", new AttackStyle(RANGED, RANGED_XP), new AttackStyle(CRUSH, STRENGTH_XP)),
    SCIMITAR("Slash Sword", new AttackStyle(SLASH, ATTACK_XP), new AttackStyle(SLASH, STRENGTH_XP), new AttackStyle(STAB, SHARED_XP), new AttackStyle(SLASH, DEFENCE_XP)),
    TWO_HANDED("2h Sword", new AttackStyle(SLASH, ATTACK_XP), new AttackStyle(SLASH, STRENGTH_XP), new AttackStyle(CRUSH, STRENGTH_XP), new AttackStyle(SLASH, DEFENCE_XP)),
    PICKAXE("Pickaxe", new AttackStyle(STAB, ATTACK_XP), new AttackStyle(STAB, STRENGTH_XP), new AttackStyle(CRUSH, STRENGTH_XP), new AttackStyle(STAB, DEFENCE_XP)),
    HALBERD("Polearm", new AttackStyle(STAB, SHARED_XP), new AttackStyle(SLASH, STRENGTH_XP), new AttackStyle(STAB, DEFENCE_XP)),
    NON_AUTOCAST_STAFF("Polestaff", new AttackStyle(CRUSH, ATTACK_XP), new AttackStyle(CRUSH, STRENGTH_XP), new AttackStyle(CRUSH, DEFENCE_XP)),
    SCYTHE("Scythe", new AttackStyle(SLASH, ATTACK_XP), new AttackStyle(STAB, STRENGTH_XP), new AttackStyle(CRUSH, STRENGTH_XP), new AttackStyle(SLASH, DEFENCE_XP)),
    SPEAR("Spear", new AttackStyle(STAB, SHARED_XP), new AttackStyle(SLASH, SHARED_XP), new AttackStyle(CRUSH, SHARED_XP), new AttackStyle(STAB, DEFENCE_XP)),
    MACE("Spiked", new AttackStyle(CRUSH, ATTACK_XP), new AttackStyle(CRUSH, STRENGTH_XP), new AttackStyle(STAB, SHARED_XP), new AttackStyle(CRUSH, DEFENCE_XP)),
    DAGGER("Stab Sword", new AttackStyle(STAB, ATTACK_XP), new AttackStyle(STAB, STRENGTH_XP), new AttackStyle(SLASH, STRENGTH_XP), new AttackStyle(STAB, DEFENCE_XP)),
    WAND("Staff", new AttackStyle(CRUSH, ATTACK_XP), new AttackStyle(CRUSH, STRENGTH_XP), new AttackStyle(CRUSH, DEFENCE_XP)),
    THROWN("Thrown", new AttackStyle(RANGED, RANGED_XP), new AttackStyle(RANGED, RANGED_XP), new AttackStyle(RANGED, RANGED_DEFENCE_XP)),
    WHIP("Whip", new AttackStyle(SLASH, ATTACK_XP), new AttackStyle(SLASH, SHARED_XP), new AttackStyle(SLASH, DEFENCE_XP)),
    STAFF("Bladed Staff", new AttackStyle(STAB, ATTACK_XP), new AttackStyle(SLASH, STRENGTH_XP), new AttackStyle(CRUSH, STRENGTH_XP)),
    UNUSED_SCIMITAR("Unused", new AttackStyle(SLASH, ATTACK_XP), new AttackStyle(SLASH, STRENGTH_XP), new AttackStyle(CRUSH, STRENGTH_XP), new AttackStyle(SLASH, DEFENCE_XP)),
    THROWN_MAGIC("Powered staff", new AttackStyle(MAGIC, MAGIC_XP), new AttackStyle(MAGIC, MAGIC_XP), new AttackStyle(MAGIC, MAGIC_DEFENCE_XP)),
    UNUSED_SPEAR("Banner", new AttackStyle(STAB, ATTACK_XP), new AttackStyle(SLASH, STRENGTH_XP), new AttackStyle(CRUSH, SHARED_XP), new AttackStyle(STAB, DEFENCE_XP)),
    UNUSED_HALBERD("Unused", new AttackStyle(STAB, SHARED_XP), new AttackStyle(SLASH, STRENGTH_XP), new AttackStyle(STAB, DEFENCE_XP)),
    UNUSED_MAUL("Bludgeon", new AttackStyle(CRUSH, STRENGTH_XP), new AttackStyle(CRUSH, STRENGTH_XP), new AttackStyle(CRUSH, STRENGTH_XP)),
    DINHS_BULWARK("Bulwark", new AttackStyle(CRUSH, ATTACK_XP), null);

    public static final AttackStyleDefinition[] values = values();
    private final AttackStyle[] styles;

    public String getPrettyName() {
        return prettyName;
    }

    private final String prettyName;

    AttackStyleDefinition(String prettyName, final AttackStyle... styles) {
        this.prettyName = prettyName;
        this.styles = styles;
    }

    public AttackStyle[] getStyles() {
        return styles;
    }


}
