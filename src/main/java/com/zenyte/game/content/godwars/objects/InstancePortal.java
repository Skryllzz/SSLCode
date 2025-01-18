package com.zenyte.game.content.godwars.objects;

import com.zenyte.game.HintArrow;
import com.zenyte.game.content.clans.ClanChannel;
import com.zenyte.game.content.clans.ClanManager;
import com.zenyte.game.content.clans.ClanRank;
import com.zenyte.game.content.godwars.GodwarsInstanceManager;
import com.zenyte.game.content.godwars.GodwarsInstancePortal;
import com.zenyte.game.content.godwars.PortalTeleport;
import com.zenyte.game.content.godwars.instance.GodwarsInstance;
import com.zenyte.game.content.godwars.instance.InstanceConstants;
import com.zenyte.game.content.godwars.npcs.DyingKnight;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Colour;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.game.world.region.dynamicregion.AllocatedArea;
import com.zenyte.game.world.region.dynamicregion.MapBuilder;
import com.zenyte.game.world.region.dynamicregion.OutOfSpaceException;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Kris | 13/04/2020
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class InstancePortal implements ObjectAction {
    private static final Logger log = LoggerFactory.getLogger(InstancePortal.class);
    private final Class<?>[] parameters = new Class[]{String.class, AllocatedArea.class};

    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        if (!option.equals("Use")) {
            return;
        }
        if (!DyingKnight.canUsePortal(player)) {
            World.findNPC(10023, player.getLocation(), 10).ifPresent(npc -> player.getPacketDispatcher().sendHintArrow(new HintArrow(npc)));
            player.getDialogueManager().start(new Dialogue(player, 10023) {
                @Override
                public void buildDialogue() {
                    npc("Dying Knight", "Psst! Hey! Come talk to me first.");
                }
            });
            return;
        }
        final GodwarsInstancePortal portal = Objects.requireNonNull(Utils.findMatching(GodwarsInstancePortal.getValues(), v -> v.getPortalObjectId() == object.getId()));
        if (player.getArea() instanceof GodwarsInstance) {
            player.getDialogueManager().start(new Dialogue(player) {
                @Override
                public void buildDialogue() {
                    plain("Are you sure you wish to leave?<br>If there are no players left in the instance, " + Colour.RED.wrap("it will collapse") + ".");
                    options(new DialogueOption("Yes.", () -> {
                        player.lock();
                        player.addWalkSteps(object.getX(), object.getY(), 1, false);
                        WorldTasksManager.schedule(() -> new PortalTeleport(portal.getGodwarsPortalLocation()).teleport(player), 1);
                    }), new DialogueOption("No."));
                }
            });
            return;
        }
        final ClanChannel channel = player.getSettings().getChannel();
        if (channel == null) {
            player.sendMessage("You need to be in a clan chat channel to start or join an instance.");
            return;
        }
        final Optional<GodwarsInstance> instance = GodwarsInstanceManager.getManager().findInstance(player, portal.getGod());
        if (instance.isPresent()) {
            player.lock();
            player.addWalkSteps(object.getX(), object.getY(), 1, false);
            WorldTasksManager.schedule(() -> new PortalTeleport(instance.get().getLocation(portal.getPortalLocation())).teleport(player), 1);
            return;
        }
        final ClanRank rank = ClanManager.getRank(player, channel);
        if (rank.getId() < channel.getKickRank().getId()) {
            player.sendMessage("Clan members ranked as " + Utils.formatString(channel.getKickRank().toString()) + " or above can only start a clan instance.");
            return;
        }
        player.getDialogueManager().start(new Dialogue(player) {
            @Override
            public void buildDialogue() {
                plain("Pay " + Utils.format(InstanceConstants.INSTANCE_COST) + " to start an instance for your clan?");
                options(new DialogueOption("Yes.", () -> {
                    final int amountInInventory = player.getInventory().getAmountOf(ItemId.COINS_995);
                    final int amountInBank = player.getBank().getAmountOf(ItemId.COINS_995);
                    if ((long) amountInBank + amountInInventory >= InstanceConstants.INSTANCE_COST) {
                        if (GodwarsInstanceManager.getManager().findInstance(player, portal.getGod()).isPresent()) {
                            setKey(75);
                            return;
                        }
                        player.lock();
                        player.getInventory().deleteItem(new Item(995, InstanceConstants.INSTANCE_COST)).onFailure(remainder -> player.getBank().remove(remainder));
                        try {
                            final AllocatedArea allocatedArea = MapBuilder.findEmptyChunk(8, 8);
                            final GodwarsInstance area = portal.getInstanceClass().getDeclaredConstructor(parameters).newInstance(player.getSettings().getChannel().getOwner(), allocatedArea);
                            area.constructRegion();
                            player.addWalkSteps(object.getX(), object.getY(), 1, false);
                            WorldTasksManager.schedule(() -> new PortalTeleport(area.getLocation(portal.getPortalLocation())).teleport(player), 1);
                        } catch (OutOfSpaceException | InstantiationException | InvocationTargetException |
                                 NoSuchMethodException | IllegalAccessException e) {
                            log.error("", e);
                        }
                        return;
                    }
                    setKey(50);
                }), new DialogueOption("No."));
                plain(50, "You don't have enough coins with you or in your bank.");
                plain(75, "Someone in your clan has already initiated an instance.");
            }
        });
    }

    @Override
    public Object[] getObjects() {
        final ObjectArrayList<Object> list = new ObjectArrayList<>();
        for (final GodwarsInstancePortal portal : GodwarsInstancePortal.getValues()) {
            list.add(portal.getPortalObjectId());
        }
        return list.toArray();
    }
}
