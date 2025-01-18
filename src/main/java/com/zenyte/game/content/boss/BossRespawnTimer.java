package com.zenyte.game.content.boss;

import com.zenyte.game.world.entity.player.Player;
import com.zenyte.plugins.dialogue.OptionsMenuD;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kris | 12/09/2019 17:30
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public enum BossRespawnTimer {
    GENERAL_GRAARDOR("General graardor", new MutableInt(150)),
    KRIL_TSUTSAROTH("K'ril tsutsaroth", new MutableInt(150)),
    COMMANDER_ZILYANA("Commander zilyana", new MutableInt(150)),
    KREE_ARRA("Kree'arra", new MutableInt(150)),
    CHAOS_ELEMENTAL("Chaos elemental", new MutableInt(50)),
    KING_BLACK_DRAGON("King black dragon", new MutableInt(15)),
    VETION("Vet'ion", new MutableInt(50)),
    VENENATIS("Venenatis", new MutableInt(50)),
    ALCHEMICAL_HYDRA("Alchemical Hydra", new MutableInt(42));
    public static final BossRespawnTimer[] values = values();
    private final String formattedName;
    private final MutableInt timer;
    private final int defaultTimer;

    BossRespawnTimer(@NotNull final String formattedName, @NotNull final MutableInt timer) {
        this.formattedName = formattedName;
        this.timer = timer;
        this.defaultTimer = timer.intValue();
    }

    public static void open(@NotNull final Player player) {
        final ObjectArrayList<String> options = new ObjectArrayList<>(values.length + 1);
        for (final BossRespawnTimer value : values) {
            options.add(value.formattedName + "(current: " + value.timer.intValue() + ", default: " + value.defaultTimer + ")");
        }
        options.add("Reset all to default");
        player.getDialogueManager().start(new OptionsMenuD(player, "Select boss whose respawn timer to edit.", options.toArray(new String[0])) {
            @Override
            public void handleClick(final int slotId) {
                player.getDialogueManager().finish();
                if (slotId < values.length) {
                    final BossRespawnTimer value = values[slotId];
                    player.sendInputInt("Enter new timer(current: " + value.timer.intValue() + ", default: " + value.defaultTimer + ")", time -> {
                        value.timer.setValue(time);
                        player.sendMessage(value.formattedName + " respawn timer set to " + value.timer.intValue() + " ticks.");
                    });
                } else if (slotId == values.length) {
                    for (final BossRespawnTimer value : values) {
                        value.timer.setValue(value.defaultTimer);
                    }
                    player.sendMessage("All respawn timers reset to their defaults.");
                }
            }

            @Override
            public boolean cancelOption() {
                return true;
            }
        });
    }

    public String getFormattedName() {
        return this.formattedName;
    }

    public MutableInt getTimer() {
        return this.timer;
    }

    public int getDefaultTimer() {
        return this.defaultTimer;
    }
}
