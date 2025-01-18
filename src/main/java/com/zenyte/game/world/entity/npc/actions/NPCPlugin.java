package com.zenyte.game.world.entity.npc.actions;

import com.zenyte.game.content.skills.thieving.PocketData;
import com.zenyte.game.content.skills.thieving.actions.Pickpocket;
import com.zenyte.game.world.entity.ImmutableLocation;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.spawns.NPCSpawn;
import com.zenyte.game.world.entity.npc.spawns.NPCSpawnLoader;
import com.zenyte.game.world.entity.pathfinding.events.player.EntityEvent;
import com.zenyte.game.world.entity.pathfinding.strategy.EntityStrategy;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.action.combat.PlayerCombat;
import mgi.types.config.npcs.NPCDefinitions;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.logging.Logger;

/**
 * @author Kris | 24/11/2018 21:45
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@SuppressWarnings("WeakerAccess")
public abstract class NPCPlugin {
    private static final Logger log = Logger.getLogger(NPCPlugin.class.getName());
    private static final Map<String, NPCPluginHandler> handlerMap = new HashMap<>();
    private static final Map<String, NPCPluginHandler> defaultHandlerMap = new HashMap<>();

    static {
        final NPCPlugin.OptionHandler handler = new OptionHandler() {
            @Override
            public void handle(Player player, NPC npc) {
                if (npc.isAttackable(player)) {
                    PlayerCombat.attackEntity(player, npc, null);
                }
            }

            @Override
            public void click(Player player, NPC npc, final NPCOption option) {
                player.stopAll();
                player.setFaceEntity(npc);
                handle(player, npc);
            }
        };
        setDefault("Attack", handler);
        setDefault("Destroy", handler);
        setDefault("Disturb", handler);
        setDefault("Pickpocket", new OptionHandler() {
            @Override
            public void handle(Player player, NPC npc) {
                player.getActionManager().setAction(new Pickpocket(PocketData.getData(player, npc), npc));
            }

            @Override
            public void execute(final Player player, final NPC npc) {
                player.stopAll();
                player.faceEntity(npc);
                handle(player, npc);
            }
        });
        setDefault("Remove", new OptionHandler() {
            @Override
            public void handle(Player player, NPC npc) {
                player.stopAll();
                player.faceEntity(npc);
                npc.finish();
            }

            @Override
            public void click(final Player player, final NPC npc, final NPCOption option) {
                handle(player, npc);
            }
        });
        setDefault("Teleport to me", new OptionHandler() {
            @Override
            public void handle(Player player, NPC npc) {
                player.stopAll();
                player.faceEntity(npc);
                npc.setLocation(new Location(player.getLocation()));
                npc.setRespawnTile(new ImmutableLocation(npc.getX(), npc.getY(), npc.getPlane()));
                final NPCSpawn spawn = npc.getNpcSpawn();
                spawn.setX(player.getX());
                spawn.setY(player.getY());
                NPCSpawnLoader.save();
                //npc.finish();
            }

            @Override
            public void click(final Player player, final NPC npc, final NPCOption option) {
                handle(player, npc);
            }
        });
        setDefault("Set radius", new OptionHandler() {
            @Override
            public void handle(Player player, NPC npc) {
                player.stopAll();
                player.faceEntity(npc);
                final NPCSpawn spawn = npc.getNpcSpawn();
                player.sendInputInt("Enter radius(Current: " + spawn.getRadius() + ")", value -> {
                    spawn.setRadius(value);
                    NPCSpawnLoader.save();
                });
            }

            @Override
            public void click(final Player player, final NPC npc, final NPCOption option) {
                handle(player, npc);
            }
        });
        setDefault("Remove spawn", new OptionHandler() {
            @Override
            public void handle(Player player, NPC npc) {
                player.stopAll();
                player.faceEntity(npc);
                final NPCSpawn spawn = npc.getNpcSpawn();
                NPCSpawnLoader.DEFINITIONS.remove(spawn);
                npc.finish();
                NPCSpawnLoader.save();
                //npc.finish();
            }

            @Override
            public void click(final Player player, final NPC npc, final NPCOption option) {
                handle(player, npc);
            }
        });
    }

    private final int[] npcs = getNPCs();

    public static NPCPluginHandler getHandler(final int npcId, final String option) {
        final String op = option.toLowerCase();
        final NPCPlugin.NPCPluginHandler handler = handlerMap.get(npcId + "|" + op);
        if (handler != null) {
            return handler;
        }
        return defaultHandlerMap.get(op);
    }

    private static void setDefault(final String option, final OptionHandler handler) {
        defaultHandlerMap.put(option.toLowerCase(), new NPCPluginHandler(null, handler));
    }

    public abstract void handle();

    public void bind(final String option, final OptionHandler handler) {
        verifyIfOptionExists(option);
        final String op = option.toLowerCase();
        for (int i : npcs) {
            if (handlerMap.containsKey(i + "|" + op)) {
                log.info("Overlapping handler found for option: " + option + ", " + getClass().getSimpleName() + ", " + handlerMap.get(i + "|" + op).plugin.getClass().getSimpleName());
            }
            handlerMap.put(i + "|" + op, new NPCPluginHandler(this, handler));
        }
    }

    public void bindOptions(final Predicate<String> optionPredicate, final ExtendedOptionHandler handler) {
        for (final int i : npcs) {
            final NPCDefinitions definitions = NPCDefinitions.get(i);
            assert definitions != null;
            for (final String op : definitions.getOptions()) {
                if (op == null || !optionPredicate.test(op)) continue;
                final String option = op.toLowerCase();
                if (handlerMap.containsKey(i + "|" + option)) {
                    log.info("Overlapping handler found for option: " + option + ", " + getClass().getSimpleName() + ", " + handlerMap.get(i + "|" + option).plugin.getClass().getSimpleName());
                    continue;
                }
                handlerMap.put(i + "|" + option, new NPCPluginHandler(this, handler));
            }
        }
    }

    private void verifyIfOptionExists(final String option) {
        if (npcs.length == 0) {
            return;
        }
        for (final int id : npcs) {
            if (id >= 9000) {
                return;
            }
            final NPCDefinitions definitions = NPCDefinitions.get(id);
            if (definitions == null) {
                continue;
            }
            if (definitions.containsOption(option)) return;
        }
        throw new RuntimeException("None of the npcs enlisted in " + getClass().getSimpleName() + " contains option " + option + ".");
    }

    public abstract int[] getNPCs();


    @FunctionalInterface
    public interface OptionHandler {
        void handle(final Player player, final NPC npc);

        default void click(final Player player, final NPC npc, final NPCOption option) {
            player.setRouteEvent(new EntityEvent(player, new EntityStrategy(npc), () -> execute(player, npc), true));
        }

        default void execute(final Player player, final NPC npc) {
            player.stopAll();
            player.setFaceEntity(npc);
            handle(player, npc);
            npc.setInteractingWith(player);
        }
    }


    @FunctionalInterface
    public interface ExtendedOptionHandler extends OptionHandler {
        @Override
        default void click(final Player player, final NPC npc, final NPCOption option) {
            player.setRouteEvent(new EntityEvent(player, new EntityStrategy(npc), () -> execute(player, npc, option), true));
        }

        default void execute(final Player player, final NPC npc, final NPCOption option) {
            player.stopAll();
            player.setFaceEntity(npc);
            handle(player, npc, option);
            npc.setInteractingWith(player);
        }

        @Deprecated
        default void handle(final Player player, final NPC npc) {
        }

        void handle(final Player player, final NPC npc, final NPCOption option);

        @Deprecated
        default void execute(final Player player, final NPC npc) {
        }
    }


    public static final class NPCOption {
        private final int id;
        private final String option;

        public NPCOption(final int id, final String option) {
            this.id = id;
            this.option = option;
        }

        public int getId() {
            return this.id;
        }

        public String getOption() {
            return this.option;
        }
    }


    public static class NPCPluginHandler {
        private final NPCPlugin plugin;
        private final OptionHandler option;

        public NPCPluginHandler(final NPCPlugin plugin, final OptionHandler option) {
            this.plugin = plugin;
            this.option = option;
        }

        public NPCPlugin getPlugin() {
            return this.plugin;
        }

        public OptionHandler getOption() {
            return this.option;
        }
    }
}
