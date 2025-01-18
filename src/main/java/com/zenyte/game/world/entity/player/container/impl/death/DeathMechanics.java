package com.zenyte.game.world.entity.player.container.impl.death;

import com.zenyte.game.content.ItemRetrievalService;
import com.zenyte.game.content.consumables.Consumable;
import com.zenyte.game.content.follower.Follower;
import com.zenyte.game.content.follower.plugin.Probita;
import com.zenyte.game.content.skills.prayer.Prayer;
import com.zenyte.game.content.skills.thieving.CoinPouch;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.item.containers.LootingBag;
import com.zenyte.game.packet.PacketDispatcher;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Colour;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.broadcasts.BroadcastType;
import com.zenyte.game.world.broadcasts.WorldBroadcasts;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.player.GameMode;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.MemberRank;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.Container;
import com.zenyte.game.world.entity.player.container.ContainerPolicy;
import com.zenyte.game.world.entity.player.container.impl.ContainerType;
import com.zenyte.game.world.entity.player.container.impl.Inventory;
import com.zenyte.game.world.region.Area;
import com.zenyte.game.world.region.area.plugins.DeathPlugin;
import mgi.types.config.items.ItemDefinitions;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Kris | 20/01/2019 20:56
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class DeathMechanics {
    final transient Container kept;
    final transient Container lost;
    private final Logger hcimDeathLogger = LoggerFactory.getLogger("HCIM Death Logger");
    private final transient LinkedList<Item> list;
    transient Player player;
    transient Entity killer;
    transient boolean destroyLootingBag;
    transient boolean destroyRunePouch;
    transient boolean destroySecondaryRunePouch;

    public DeathMechanics(final Player player) {
        this.player = player;
        kept = new Container(ContainerPolicy.NORMAL, ContainerType.MAXIMUM_SIZE_CONTAINER, Optional.of(player));
        lost = new Container(ContainerPolicy.NORMAL, ContainerType.MAXIMUM_SIZE_CONTAINER, Optional.of(player));
        list = new LinkedList<>();
    }

    private void set(final boolean filter, final boolean service) {
        clear();
        removePets(filter);
        prefill();
        sort();
        if (service) {
            fillAlwaysLostItems();
            fillKeptItems();
            fillLostItems();
        } else {
            filter(list, DeathFilters.preFillFilters, filter, service);
            fillAlwaysLostItems();
            fillKeptItems();
            fillLostItems();
            filter(lost.getItems().values(), DeathFilters.postFillFilters, filter, service);
        }
        sort(kept);
        sort(lost);
    }

    public void death(final Entity killer, final Location tile) {
        if (safe() || player.isLoggedOut()) {
            return;
        }
        this.killer = killer;
        final String inventory = player.getInventory().getContainer().getItems().toString();
        final String equipment = player.getEquipment().getContainer().getItems().toString();
        set(true, false);
        clearContainers();
        removeCharges();
        final Player receiver = this.killer instanceof Player ? ((Player) killer) : this.player;
        final Location location = new Location(tile == null ? player.getLocation() : tile);
        player.log(LogLevel.INFO, "Player death: \nInititial inventory: " + inventory + "\nInitial equipment: " + equipment + "\nKept items: " + kept.getItems() + "\nLost items: " + lost.getItems());
        kept.getItems().values().forEach(item -> player.getInventory().addItem(item).onFailure(remainder -> WorldTasksManager.schedule(() -> {
            World.spawnFloorItem(remainder, player);
            player.sendMessage(Colour.RED.wrap(remainder.getAmount() + " x " + remainder.getName() + " dropped on the floor due to lack of inventory space."));
        })));
        final boolean wildy = player.inArea("Wilderness");
        lost.getItems().values().forEach(item -> {
            final Player receivingUser = !item.isTradable() ? this.player : receiver;
            final int invisibleTicks = player.getGameMode() == GameMode.ULTIMATE_IRON_MAN ? 6000 : (300 + (player.isMember() ? (getExtraMinutes() * 100) : 0));
            final ItemDefinitions definitions = item.getDefinitions();
            final boolean consumable = definitions.containsOption("Eat") || definitions.containsOption("Drink") || Consumable.consumables.containsKey(item.getId());
            World.spawnFloorItem(item, location, -1, this.player, receivingUser, (wildy && consumable ? 500 : 0) + invisibleTicks, wildy && consumable ? -1 : 200);
        });
        World.spawnFloorItem(new Item(ItemId.BONES), player, 0, 300);
        if (player.getGameMode().equals(GameMode.HARDCORE_IRON_MAN)) {
            final Object source = player.getTemporaryAttributes().get("killing blow hit");
            WorldBroadcasts.broadcast(player, BroadcastType.HCIM_DEATH, source);
            player.sendMessage("You have fallen as a Hardcore Iron Man, your Hardcore status has been revoked.");
            player.setGameMode(GameMode.STANDARD_IRON_MAN);
            final String killerLabel = (source == player) ? "self-inflicted damage" : (source instanceof Player) ? ((Player) source).getName() : (source instanceof NPC) ? ((((NPC) source).getDefinitions().getName() + " (lvl-" + ((NPC) source).getDefinitions().getCombatLevel()) + ")") : "unknown damage";
            hcimDeathLogger.info(player.getName() + "(total lvl-" + player.getSkills().getTotalLevel() + ") fell to " + killerLabel + " at " + player.getLocation() + " with a logout count of " + player.getLogoutCount() + ".");
        }
    }

    private int getExtraMinutes() {
        final MemberRank memberRank = player.getMemberRank();
        if (memberRank.eligibleTo(MemberRank.ZENYTE_MEMBER)) {
            return 8;
        } else if (memberRank.eligibleTo(MemberRank.ONYX_MEMBER)) {
            return 7;
        } else if (memberRank.eligibleTo(MemberRank.DRAGONSTONE_MEMBER)) {
            return 6;
        } else if (memberRank.eligibleTo(MemberRank.DIAMOND_MEMBER)) {
            return 5;
        } else if (memberRank.eligibleTo(MemberRank.RUBY_MEMBER)) {
            return 4;
        } else if (memberRank.eligibleTo(MemberRank.EMERALD_MEMBER)) {
            return 3;
        } else if (memberRank.eligibleTo(MemberRank.SAPPHIRE_MEMBER)) {
            return 2;
        }
        return 0;
    }

    public void service(@NotNull final ItemRetrievalService.RetrievalServiceType type, final Entity killer) {
        if (safe() || player.isLoggedOut()) {
            return;
        }
        this.killer = killer;
        final String inventory = player.getInventory().getContainer().getItems().toString();
        final String equipment = player.getEquipment().getContainer().getItems().toString();
        set(true, true);
        clearContainers();
        player.log(LogLevel.INFO, "Player death: \nInititial inventory: " + inventory + "\nInitial equipment: " + equipment + "\nKept items: " + kept.getItems() + "\nLost items: " + lost.getItems());
        kept.getItems().values().forEach(item -> player.getInventory().addItem(item));
        final ItemRetrievalService service = player.getRetrievalService();
        final Container container = service.getContainer();
        container.clear();
        lost.getItems().values().forEach(item -> {
            if (CoinPouch.ITEMS.keySet().contains(item.getId())) {
                final com.zenyte.game.content.skills.thieving.CoinPouch pouch = CoinPouch.ITEMS.get(item.getId());
                item = new Item(995, com.zenyte.plugins.item.CoinPouch.getCoinAmount(pouch, item.getAmount()));
            }
            if (!isLootingBag(item))
                container.add(item);
        });
        service.setType(type);
        service.setLocked(!type.isFree());
        if (player.getGameMode().equals(GameMode.HARDCORE_IRON_MAN)) {
            WorldBroadcasts.broadcast(player, BroadcastType.HCIM_DEATH, player.getTemporaryAttributes().get("killing blow hit"));
            player.sendMessage("You have fallen as a Hardcore Iron Man, your Hardcore status has been revoked.");
            player.setGameMode(GameMode.STANDARD_IRON_MAN);
        }
    }

    private void removePets(final boolean execute) {
        if (!execute) {
            return;
        }
        final Follower follower = player.getFollower();
        if (follower != null && follower.getPet().itemId() < 30000) {
            //30000+ = custom pet
            player.setFollower(null);
        }
        final Inventory inventory = player.getInventory();
        for (int slot = 0; slot < 28; slot++) {
            final Item item = inventory.getItem(slot);
            if (item == null) {
                continue;
            }
            final int id = item.getId();
            if (Probita.insurablePets.containsKey(id)) {
                inventory.deleteItem(item);
            }
        }
    }

    private void removeCharges() {
        lost.getItems().values().forEach(item -> item.setAttributes(null));
    }

    private boolean safe() {
        final Area area = player.getArea();
        final DeathPlugin plugin = area instanceof DeathPlugin ? (DeathPlugin) area : null;
        return plugin != null && plugin.isSafe();
    }

    private void clearContainers() {
        player.getInventory().clear();
        player.getEquipment().clear();
        if (destroyLootingBag)
            player.getLootingBag().clear();
        if (destroyRunePouch)
            player.getRunePouch().clear();
        if (destroySecondaryRunePouch)
            player.getSecondaryRunePouch().clear();
    }

    private void filter(final Collection<Item> collection, final DeathFilters.Function<Item, DeathMechanics> function, final boolean execute, final boolean service) {
        if (!execute || collection.isEmpty()) {
            return;
        }
        final ArrayList<Item> removed = new ArrayList<>();
        collection.forEach(item -> {
            if (function.test(item, DeathMechanics.this, service)) {
                removed.add(item);
            }
        });
        collection.removeAll(removed);
    }

    public void refreshInterface() {
        set(false, false);
        final long value = getLostItemsValue();
        final PacketDispatcher dispatcher = player.getPacketDispatcher();
        dispatcher.sendUpdateItemContainer(lost, ContainerType.ITEMS_LOST_ON_DEATH);
        dispatcher.sendUpdateItemContainer(kept, ContainerType.ITEMS_KEPT_ON_DEATH);
        int safe = 0;
        String message = "";
        final Area area = player.getArea();
        if (area instanceof DeathPlugin plugin) {
            safe = plugin.isSafe() ? 1 : 0;
            message = Utils.getOrDefault(plugin.getDeathInformation(), message);
        }
        dispatcher.sendClientScript(118, safe, message, getKeptCount(), player.inArea("Wilderness") ? 1 : 0, player.getGameMode().equals(GameMode.ULTIMATE_IRON_MAN) ? 1 : 0, Utils.format(value));
    }

    private void clear() {
        destroyLootingBag = false;
        destroyRunePouch = false;
        destroySecondaryRunePouch = false;
        if (!list.isEmpty()) {
            list.clear();
        }
        if (!kept.isEmpty()) {
            kept.clear();
        }
        if (!lost.isEmpty()) {
            lost.clear();
        }
    }

    private void prefill() {
        clear(player.getInventory().getContainer());
        clear(player.getEquipment().getContainer());

        if (player.getInventory().containsAnyOf(LootingBag.OPENED.getId(), LootingBag.CLOSED.getId())) {
            destroyLootingBag = true;
            clear(player.getLootingBag().getContainer());
        }

        if (player.getInventory().containsItem(ItemId.RUNE_POUCH)) {
            destroyRunePouch = true;
            clear(player.getRunePouch().getContainer());
        }

        if (player.getInventory().containsItem(30006)) {
            destroySecondaryRunePouch = true;
            clear(player.getSecondaryRunePouch().getContainer());
        }
    }

    private void clear(Container container) {
        if (!container.isEmpty())
            container.getItems().values().forEach(item -> list.add(new Item(item)));
    }

    private void sort() {
        if (list.isEmpty()) {
            return;
        }
        list.sort(Comparator.comparingInt((Item item) -> {
            final int priceA = item.getSellPrice();
            final int priceB = getPrice(item);
            return Math.max(priceA, priceB);
        }).reversed());
    }

    private void fillKeptItems() {
        if (list.isEmpty() || player.getGameMode().equals(GameMode.ULTIMATE_IRON_MAN)) {
            return;
        }
        int count = getKeptCount();
        while (count-- > 0) {
            final Item item = list.peekFirst();
            if (item == null) continue;
            kept.add(new Item(item.getId(), 1, item.getAttributesCopy()));
            final int amount = item.getAmount();
            if (amount == 1) {
                list.removeFirst();
                continue;
            }
            item.setAmount(amount - 1);
        }
    }

    private void fillAlwaysLostItems() {
        if (list.isEmpty()) {
            return;
        }
        list.removeIf(item -> {
            final boolean isLost = isLootingBag(item) || isRunePouch(item);
            if (isLost) {
                lost.add(item);
            }
            return isLost;
        });
    }

    private boolean isLootingBag(Item item) {
        return destroyLootingBag && (item.getId() == LootingBag.CLOSED.getId() || item.getId() == LootingBag.OPENED.getId());
    }

    private boolean isRunePouch(Item item) {
        return (destroyRunePouch && item.getId() == ItemId.RUNE_POUCH)
                || (destroySecondaryRunePouch && item.getId() == 30006);
    }

    private void fillLostItems() {
        if (list.isEmpty()) {
            return;
        }
        list.forEach(lost::add);
        list.clear();
    }

    private void sort(final Container container) {
        if (container.isEmpty()) {
            return;
        }
        final List<Item> sorted = container.getItems().values().stream()
                .sorted(Comparator.comparingInt((Item item) -> {
                    final int priceA = item.getSellPrice();
                    final int priceB = getPrice(item);
                    return Math.max(priceA, priceB);
                }).reversed()).collect(Collectors.toList());
        container.clear();
        sorted.forEach(container::add);
    }

    private int getPrice(@NotNull final Item item) {
        final int id = ItemVariationMapping.map(item.getId());
        if (id == ItemId.COINS_995) {
            return 1;
        }
        if (id == ItemId.PLATINUM_TOKEN) {
            return 1000;
        }
        //Salve amulet apparently has a protection value of 220k, an arbitrary constant.
        if (id == ItemId.SALVE_AMULETEI) {
            return 220000;
        }
        int price = 0;
        for (int mappedId : ItemMapping.map(id)) {
            price += ItemDefinitions.getSellPrice(mappedId);
        }
        return price;
    }

    private int getKeptCount() {
        int count = 0;
        if (!player.getVariables().isSkulled()) {
            count += 3;
        }
        if (player.getPrayerManager().isActive(Prayer.PROTECT_ITEM)) {
            count++;
        }
        return count;
    }

    private long getLostItemsValue() {
        if (lost.isEmpty()) {
            return 0;
        }
        long value = 0;
        for (final Item item : lost.getItems().values()) {
            value += (long) Math.max(item.getSellPrice(), getPrice(item)) * item.getAmount();
        }
        return value;
    }
}
