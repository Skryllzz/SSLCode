package com.zenyte.game.world.entity.player.container.impl.death;

import com.zenyte.game.content.itemtransportation.masterscrolls.MasterScrollBookInterface;
import com.zenyte.game.content.skills.thieving.CoinPouch;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.item.containers.GemBag;
import com.zenyte.game.item.containers.HerbSack;
import com.zenyte.game.item.degradableitems.DegradableItem;
import com.zenyte.game.item.degradableitems.RepairableItem;
import com.zenyte.game.item.enums.DismantleableItem;
import com.zenyte.game.item.enums.UpgradeKit;
import com.zenyte.game.world.entity.player.GameMode;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.impl.LootingBag;
import com.zenyte.game.world.entity.player.container.impl.RunePouch;
import com.zenyte.game.world.entity.player.container.impl.SeedBox;
import com.zenyte.game.world.region.area.wilderness.WildernessArea;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import mgi.types.config.items.ItemDefinitions;

import java.util.List;
import java.util.OptionalInt;

/**
 * @author Kris | 20/01/2019 23:49
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
class DeathFilters {
    static final Function<Item, DeathMechanics> preFillFilters;
    static final Function<Item, DeathMechanics> postFillFilters;

    static {
        //functions executed before populating the lost and kept items. Executed on the list collection.
        preFillFilters = (item, mechs, service) -> {
            final int id = item.getId();
            final OptionalInt optionalLevel = WildernessArea.getWildernessLevel(mechs.player.getLocation());
            final int level = optionalLevel.isPresent() ? optionalLevel.getAsInt() : -1;
            final ItemDefinitions def = ItemDefinitions.get(id);
            //Seed pod always kept
            if (id == 19564 || id == ItemId.TORN_CLUE_SCROLL_PART_1 || id == ItemId.TORN_CLUE_SCROLL_PART_2 || id == ItemId.TORN_CLUE_SCROLL_PART_3) {
                mechs.kept.add(item);
                return true;
            }
            //Rune pouch filter
            if (id == 12791) {
                if (!mechs.player.inArea("Wilderness")) {
                    mechs.kept.add(item);
                    final RunePouch runePouch = mechs.player.getRunePouch();
                    for (final Item rune : runePouch.getContainer().getItems().values()) {
                        if (rune == null) continue;
                        mechs.kept.add(rune);
                    }
                    runePouch.clear();
                    return true;
                }
                final RunePouch runePouch = mechs.player.getRunePouch();
                for (final Item rune : runePouch.getContainer().getItems().values()) {
                    if (rune == null) continue;
                    mechs.lost.add(rune);
                }
                runePouch.clear();
                return true;
            }
            if (id == ItemId.MASTER_SCROLL_BOOK || id == ItemId.MASTER_SCROLL_BOOK_EMPTY) {
                final List<Item> items = MasterScrollBookInterface.toItemList(item);
                for (final Item scroll : items) {
                    mechs.lost.add(scroll);
                }
                item.resetAttributes();
                item.setId(ItemId.MASTER_SCROLL_BOOK_EMPTY);
                mechs.lost.add(item);
                return true;
            }
            if (id == ItemId.GRANITE_CANNONBALL && !(mechs.killer instanceof Player)) {
                mechs.kept.add(item);
                return true;
            }
            //Herb sack
            if (id == 13226) {
                final HerbSack sack = mechs.player.getHerbSack();
                for (final Int2ObjectMap.Entry<Item> herb : sack.getHerbs().int2ObjectEntrySet()) {
                    mechs.lost.add(herb.getValue());
                }
                sack.clear();
                return false;
            }
            //Seed box
            if (id == 13639) {
                final SeedBox box = mechs.player.getSeedBox();
                for (final Int2ObjectMap.Entry<Item> seed : box.getSeeds().int2ObjectEntrySet()) {
                    mechs.lost.add(seed.getValue());
                }
                box.clear();
                return false;
            }
            //Gem bag
            if (id == GemBag.GEM_BAG.getId()) {
                final GemBag bag = mechs.player.getGemBag();
                for (final Int2ObjectMap.Entry<Item> gem : bag.getGems().int2ObjectEntrySet()) {
                    mechs.lost.add(gem.getValue());
                }
                bag.clear();
                return false;
            }
            //Looting bag
            if (LootingBag.isBag(item.getId())) {
                for (final Int2ObjectMap.Entry<Item> entry : mechs.player.getLootingBag().getContainer().getItems().int2ObjectEntrySet()) {
                    final Item i = entry.getValue();
                    if (i == null) {
                        continue;
                    }
                    mechs.lost.add(i);
                }
                mechs.player.getLootingBag().setOpen(false);
                mechs.player.getLootingBag().clear();
                return true;
            }
            //Bonecrusher.
            if (id == 13116) {
                if (level < 21) {
                    mechs.kept.add(item);
                    return true;
                }
            }
            //Bracelet of ethereum
            if (id == 21816 || id == 21817) {
                mechs.lost.add(new Item(21817, 1));
                if (item.getCharges() > 0) {
                    mechs.lost.add(new Item(21820, item.getCharges()));
                }
                return true;
            }
            //Bonds
            if (id == 13190 || id == 13192) {
                if (!mechs.player.getGameMode().equals(GameMode.ULTIMATE_IRON_MAN)) {
                    mechs.kept.add(item);
                    return true;
                }
            }
            //Graceful
            if (def != null && def.getName().contains("Graceful")) {
                if (level < 21) {
                    mechs.kept.add(item);
                    return true;
                }
            }
            if (id == 21776 | id == 21778 || id == 21780 || id == 21782 || id == 21784 || id == 21786 || id == 21791 || id == 21793 || id == 21795 || id == 13331 || id == 13333 || id == 13335 || id == 13342 || id == 20760) {
                if (level < 21) {
                    mechs.kept.add(item);
                    return true;
                }
            }
            return false;
        };
        final Int2IntOpenHashMap imbuedRingMap = new Int2IntOpenHashMap();
        imbuedRingMap.put(20657, 20655);
        imbuedRingMap.put(19710, 19550);
        imbuedRingMap.put(11770, 6731);
        imbuedRingMap.put(11771, 6733);
        imbuedRingMap.put(11773, 6737);
        imbuedRingMap.put(11772, 6735);
        imbuedRingMap.put(12692, 12605);
        imbuedRingMap.put(12691, 12603);
        imbuedRingMap.put(13202, 12601);
        imbuedRingMap.put(21752, 21739);
        //Functions executed after populating the lost and kept items. Executed on the lost collection.
        postFillFilters = (item, mechs, service) -> {
            try {
                int id = item.getId();
                final OptionalInt optionalLevel = WildernessArea.getWildernessLevel(mechs.player.getLocation());
                final int level = optionalLevel.isPresent() ? optionalLevel.getAsInt() : -1;
                if (!(mechs.killer instanceof Player)) {
                    //Chinchompas
                    if (id == 9976 || id == 9977 || id == 10033 || id == 10034 || id == 11959) {
                        return true;
                    }
                }
                if (mechs.killer instanceof Player || level > 20) {
                    item.setId(id = imbuedRingMap.getOrDefault(id, id));
                    //Slayer helmet.
                    if (item.getName().toLowerCase().contains("slayer helmet")) {
                        item.setId(id = 8921);
                    }
                    //Imbued black masks.
                    if (id >= 11774 && id <= 11784) {
                        item.setId(id = 8921);
                    }
                } else {
                    if (imbuedRingMap.containsKey(id) || item.getName().toLowerCase().contains("slayer helmet") || (id >= 11774 && id <= 11784)) {
                        mechs.kept.add(item);
                        return true;
                    }
                }
                // Coin pouches
                if (CoinPouch.ITEMS.keySet().contains(id)) {
                    final com.zenyte.game.content.skills.thieving.CoinPouch pouch = CoinPouch.ITEMS.get(id);
                    mechs.lost.add(new Item(995, com.zenyte.plugins.item.CoinPouch.getCoinAmount(pouch, item.getAmount())));
                    return true;
                }
                if (id == 22550 || id == 22545 || id == 22555) {
                    if (id == 22550) {
                        item.setId(22547);
                    } else if (id == 22545) {
                        item.setId(22542);
                    } else {
                        item.setId(22552);
                    }
                    if (item.getCharges() > 0) {
                        mechs.lost.add(new Item(21820, item.getCharges()));
                        item.setAttributes(null);
                        return false;
                    }
                    return false;
                }
                if (id == 21728 && mechs.killer instanceof Player) {
                    item.setId(2);
                    return false;
                }
                if (id == ItemId.FEROCIOUS_GLOVES && mechs.killer instanceof Player) {
                    item.setId(ItemId.HYDRA_LEATHER);
                    return false;
                }
                //Repairables
                final RepairableItem repairable = RepairableItem.getItem(item);
                if (repairable != null) {
                    if (!repairable.isTradeable()) {
                        final int[] ids = repairable.getIds();
                        if (service) {
                            return false;
                        }
                        if (ids.length < 2 || id == ids[ids.length - 1] || !optionalLevel.isPresent()) {
                            mechs.kept.add(item);
                            return true;
                        }
                        if (level < 21) {
                            item.setId(ids[1]);
                            mechs.kept.add(item);
                        }
                        mechs.lost.add(new Item(995, (int) (repairable.getRepairCost() * 0.75F)));
                        return true;
                    }
                }
                //Degradables
                final DegradableItem degradable = DegradableItem.ITEMS.get(id);
                /*if (degradable == DegradableItem.FULL_TRIDENT_OF_THE_SEAS || degradable == DegradableItem.TRIDENT_OF_THE_SEAS
                 || degradable == DegradableItem.TRIDENT_OF_THE_SWAMP || degradable == DegradableItem.BLOWPIPE || degradable == DegradableItem.SERPENTINE_HELM
                || degradable == DegradableItem.TANZANITE_HELM || degradable == DegradableItem.MAGMA_HELM) {
                    mechs.kept.add(item);
                    return true;
                }*/
                if (degradable != null) {
                    if (level == -1) {
                        mechs.kept.add(item);
                        return true;
                    }
                    final java.util.function.Function<Item, Item[]> function = degradable.getFunction();
                    if (function != null) {
                        final Item[] items = function.apply(item);
                        if (items != null) {
                            for (final Item it : items) {
                                if (it != null && it.getAmount() > 0) {
                                    mechs.lost.add(it);
                                }
                            }
                        }
                    }
                    final int degraded = DegradableItem.getCompletelyDegradedId(item.getId());
                    item.setId(degraded);
                    return false;
                }
                //Dismantleables
                final DismantleableItem dismantleable = DismantleableItem.MAPPED_VALUES.get(id);
                if (dismantleable != null) {
                    if (level == -1) {
                        mechs.kept.add(item);
                        return true;
                    }
                    if (dismantleable.isSplitOnDeath()) {
                        mechs.lost.add(new Item(dismantleable.getBaseItem()));
                        mechs.lost.add(new Item(dismantleable.getKit()));
                        return true;
                    }
                }
                //Upgradeable
                final UpgradeKit upgradeable = UpgradeKit.MAPPED_VALUES.get(id);
                if (upgradeable != null) {
                    if (level == -1) {
                        mechs.kept.add(item);
                        return true;
                    }
                    mechs.lost.add(new Item(upgradeable.getBaseItem()));
                    return true;
                }
                if (!item.isTradable()) {
                    if (optionalLevel.isPresent()) {
                        final int amount = item.getAmount() * (item.getName().toLowerCase().contains("max cape") ? 569250 : item.getDefinitions().getPrice());
                        mechs.lost.add(new Item(995, amount));
                        return true;
                    }
                    mechs.kept.add(item);
                    return true;
                }
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        };
    }

    interface Function<K, V> {
        boolean test(K k, V v, boolean service);
    }
}
