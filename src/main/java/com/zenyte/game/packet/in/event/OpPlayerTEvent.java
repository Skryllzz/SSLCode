package com.zenyte.game.packet.in.event;

import com.zenyte.game.content.skills.magic.Magic;
import com.zenyte.game.content.skills.magic.SpellDefinitions;
import com.zenyte.game.content.skills.magic.spells.PlayerSpell;
import com.zenyte.game.packet.in.ClientProtEvent;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.region.Area;
import com.zenyte.game.world.region.area.plugins.SpellPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 25-1-2019 | 20:49
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class OpPlayerTEvent implements ClientProtEvent {
    private final int interfaceId;
    private final int componentId;
    private final int index;
    private final boolean run;

    public OpPlayerTEvent(final int interfaceId, final int componentId, final int index, final boolean run) {
        this.interfaceId = interfaceId;
        this.componentId = componentId;
        this.index = index;
        this.run = run;
    }

    @Override
    public void log(@NotNull final Player player) {
        final Player target = World.getPlayers().get(index);
        if (target == null) {
            log(player, "Index: " + index + ", interface: " + interfaceId + ", component: " + componentId + ", run: " + run + "; name: null");
            return;
        }
        final Location tile = target.getLocation();
        log(player, "Index: " + index + ", interface: " + interfaceId + ", component: " + componentId + ", run: " + run + "; name: " + target.getUsername() + ", location: x" + tile.getX() + ", y" + tile.getY() + ", z: " + tile.getPlane());
    }

    @Override
    public LogLevel level() {
        return LogLevel.HIGH_PACKET;
    }

    @Override
    public void handle(Player player) {
        final Player target = World.getPlayers().get(index);
        if (target == null || target == player || target.isFinished() || !target.isRunning()) {
            return;
        }
        if (interfaceId == 218) {
            final PlayerSpell spell = Magic.getSpell(player.getCombatDefinitions().getSpellbook(), SpellDefinitions.getSpellName(componentId), PlayerSpell.class);
            if (spell == null) {
                return;
            }
            final Area area = player.getArea();
            if (area instanceof SpellPlugin && !((SpellPlugin) area).canCast(player, spell)) {
                return;
            }
            spell.execute(player, target);
        }
    }
}
