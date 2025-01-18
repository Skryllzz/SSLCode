package com.zenyte.game.world.entity.player.collectionlog;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.Subscribe;
import com.zenyte.cores.WorldThread;
import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.packet.PacketDispatcher;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.util.AccessMask;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.calog.CABossType;
import com.zenyte.game.world.entity.player.calog.CALogBossOverviewInterface;
import com.zenyte.game.world.entity.player.container.Container;
import com.zenyte.plugins.events.LoginEvent;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import mgi.types.config.StructDefinitions;
import mgi.types.config.enums.EnumDefinitions;
import mgi.types.config.enums.IntEnum;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author Kris | 12/03/2019 23:03
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class CollectionLogInterface extends Interface {

    public static final String CATEGORY_ATTR_KEY = "COLLECTION_LOG_CATEGORY";
    public static final String SUB_CATEGORY_ATTR_KEY = "COLLECTION_LOG_SUB_CATEGORY";
    private static final int CATEGORY_SCRIPT = 2388;
    private static final int BUILD_INTERFACE_SCRIPT = 2730;
    public static final int STRUCT_POINTER_ENUM_CAT = 683;
    public static final int STRUCT_POINTER_SUB_ENUM_CAT_NAME = 689;
    public static final int STRUCT_POINTER_SUB_ENUM_CAT = 690;

    private static final int SCROLL_LAYER_COMPONENT_PARAM = 685;
    private static final int ELEMENTS_LAYER_COMPONENT_PARAM = 686;
    private static final int ELEMENT_NAME_COMPONENT_PARAM = 687;
    private static final int ELEMENTS_SCROLLBAR_COMPONENT = 688;
    private static final int COLLECTION_LOG_TOTAL_UNLOCKED_VARP = 2943;
    private static final int COLLECTION_LOG_TOTAL_UNLOCKABLE_VARP = 2944;
    private static final String SEARCH_LETTERS = "abcdefghijklmnopqrstuvwxyz \t";

    @Override
    protected void attach() {
        put(4, CLCategoryType.BOSS.category());
        put(5, CLCategoryType.RAIDS.category());
        put(6, CLCategoryType.CLUES.category());
        put(7, CLCategoryType.MINIGAMES.category());
        put(8, CLCategoryType.OTHER.category());
        put(20, "Combat achievements");
        put(79, "Close");
        int id = 41;
        for (char c : SEARCH_LETTERS.toCharArray()) {
            put(id++, "Search letter " + c);
        }

        for (CLCategoryType type : CLCategoryType.values) {
            final StructDefinitions struct = Objects.requireNonNull(StructDefinitions.get(type.struct()));
            final int layerComponent = getStructParam(struct, ELEMENTS_LAYER_COMPONENT_PARAM);
            put(layerComponent, type + " layer");
        }
    }

    @Override
    public void open(Player player) {
        for (char c : SEARCH_LETTERS.toCharArray()) {
            player.getPacketDispatcher().sendComponentSettings(getInterface().getId(), getComponent("Search letter " + c), -1, -1, AccessMask.CLICK_OP1);
        }
        refreshTotalUnlocked(player);
        player.getPacketDispatcher().sendUpdateItemContainer(player.getCollectionLog().getContainer());
        player.getInterfaceHandler().sendInterface(this);

        final CLCategoryType category = (CLCategoryType) player.getTemporaryAttributes().getOrDefault(CATEGORY_ATTR_KEY, CLCategoryType.BOSS);
        final int subCategory = (int) player.getTemporaryAttributes().getOrDefault(SUB_CATEGORY_ATTR_KEY, 0);
        populate(player, category, subCategory);
        final Container container = player.getCollectionLog().getContainer();
        container.setFullUpdate(true);
        container.refresh(player);
    }

    @Override
    public void close(Player player, Optional<GameInterface> replacement) {
        super.close(player, replacement);

        player.getPacketDispatcher().sendClientScript(2158);
    }

    private static void refreshTotalUnlocked(Player player) {
        player.getVarManager().sendVarInstant(COLLECTION_LOG_TOTAL_UNLOCKABLE_VARP, CollectionLog.COLLECTION_LOG_ITEMS.size());
        player.getVarManager().sendVarInstant(COLLECTION_LOG_TOTAL_UNLOCKED_VARP, player.getCollectionLog().getContainer().getSize());
    }

    @Subscribe
    public static void onLogin(@NotNull final LoginEvent event) {
        refreshTotalUnlocked(event.getPlayer());
    }

    private void populate(@NotNull final Player player, @NotNull final CLCategoryType type, final int subCategory) {
        final PacketDispatcher dispatcher = player.getPacketDispatcher();
        final StructDefinitions struct = Objects.requireNonNull(StructDefinitions.get(type.struct()));
        final Optional<?> optional = struct.getValue(STRUCT_POINTER_ENUM_CAT);
        final Object enumId = optional.orElseThrow(RuntimeException::new);
        assert enumId instanceof Integer;
        final IntEnum categoryEnum = EnumDefinitions.getIntEnum((Integer) enumId);
        final int length = categoryEnum.getSize();
        Preconditions.checkArgument(subCategory >= 0 && subCategory < length);
        final int value = categoryEnum.getValue(subCategory).orElseThrow(RuntimeException::new);
        final StructDefinitions subCatStruct = Objects.requireNonNull(StructDefinitions.get(value));
        final String name = subCatStruct.getValue(STRUCT_POINTER_SUB_ENUM_CAT_NAME).orElseThrow(RuntimeException::new).toString();
        final Function<Player, Integer>[] functions = CollectionLogCategories.getFunctions(name);
        final IntArrayList options = new IntArrayList(5);
        final int layerComponent = getStructParam(struct, ELEMENTS_LAYER_COMPONENT_PARAM);
        options.add(getStructParam(struct, SCROLL_LAYER_COMPONENT_PARAM));
        options.add(layerComponent);
        options.add(getStructParam(struct, ELEMENT_NAME_COMPONENT_PARAM));
        options.add(getStructParam(struct, ELEMENTS_SCROLLBAR_COMPONENT));
        options.add(type.struct());
        options.add(subCategory);
        if (functions != null) {
            if (functions.length > 0) player.getVarManager().sendVarInstant(2048, functions[0].apply(player));
            if (functions.length > 1) player.getVarManager().sendVarInstant(2941, functions[1].apply(player));
            if (functions.length > 2) player.getVarManager().sendVarInstant(2942, functions[2].apply(player));
        }
        dispatcher.sendComponentSettings(getInterface(), layerComponent, 0, length, AccessMask.CLICK_OP1);
        dispatcher.sendClientScript(CATEGORY_SCRIPT, type.ordinal());
        dispatcher.sendClientScript(BUILD_INTERFACE_SCRIPT, options.toArray());
        player.getTemporaryAttributes().put(CATEGORY_ATTR_KEY, type);
        player.getTemporaryAttributes().put(SUB_CATEGORY_ATTR_KEY, subCategory);
    }

    private static int getStructParam(StructDefinitions struct, int param) {
        final Optional<?> optional = struct.getValue(param);
        final Object enumId = optional.orElseThrow(RuntimeException::new);
        assert enumId instanceof Integer;
        return (int) enumId;
    }

    private static void updateSearchResults(Player player) {
        int index = 0;
        for (final CLCategoryType type : CLCategoryType.values) {
            final StructDefinitions struct = Objects.requireNonNull(StructDefinitions.get(type.struct()));
            final Optional<?> optional = struct.getValue(STRUCT_POINTER_ENUM_CAT);
            final Object enumId = optional.orElseThrow(RuntimeException::new);
            assert enumId instanceof Integer;
            final IntEnum categoryEnum = EnumDefinitions.getIntEnum((Integer) enumId);
            final ObjectSet<Int2IntMap.Entry> entrySet = categoryEnum.getValues().int2IntEntrySet();
            for (final Int2IntMap.Entry entry : entrySet) {
                final int subCategoryStructId = entry.getIntValue();
                final StructDefinitions subCategoryStruct = Objects.requireNonNull(StructDefinitions.get(subCategoryStructId));
                final int subEnumId = Integer.parseInt(subCategoryStruct.getValue(STRUCT_POINTER_SUB_ENUM_CAT).orElseThrow(RuntimeException::new).toString());
                final IntEnum subEnum = EnumDefinitions.getIntEnum(subEnumId);
                final ObjectSet<Int2IntMap.Entry> subEnumEntrySet = subEnum.getValues().int2IntEntrySet();
                for (final Int2IntMap.Entry e : subEnumEntrySet) {
                    final int item = e.getIntValue();
                    final int amount = player.getCollectionLog().getContainer().getAmountOf(item);
                    player.getPacketDispatcher().sendClientScript(4100, item, amount, index, subCategoryStructId);
                }
                index++;
            }
        }
    }

    @Override
    protected void build() {
        bind(CLCategoryType.BOSS.category(), (player, slotId, itemId, option) -> populate(player, CLCategoryType.BOSS, 0));
        bind(CLCategoryType.RAIDS.category(), (player, slotId, itemId, option) -> populate(player, CLCategoryType.RAIDS, 0));
        bind(CLCategoryType.CLUES.category(), (player, slotId, itemId, option) -> populate(player, CLCategoryType.CLUES, 0));
        bind(CLCategoryType.MINIGAMES.category(), (player, slotId, itemId, option) -> populate(player, CLCategoryType.MINIGAMES, 0));
        bind(CLCategoryType.OTHER.category(), (player, slotId, itemId, option) -> populate(player, CLCategoryType.OTHER, 0));
        bind("Combat achievements", (player, slotId, itemId, option) -> {
            final CLCategoryType currentCategory = (CLCategoryType) player.getTemporaryAttributes().getOrDefault(CATEGORY_ATTR_KEY, CLCategoryType.BOSS);
            final int currentSubCategory = (int) player.getTemporaryAttributes().getOrDefault(SUB_CATEGORY_ATTR_KEY, 0);
            final Optional<CABossType> optionalType = Arrays.stream(CABossType.values).
                    filter(b -> b.getCollLogVal() == currentSubCategory && currentCategory.equals(b.isRaid() ? CLCategoryType.RAIDS : CLCategoryType.BOSS)).findFirst();
            if (optionalType.isPresent()) {
                player.getVarManager().sendBitInstant(CALogBossOverviewInterface.BOSS_SELECT_VARBIT, optionalType.get().ordinal() + 1);
                GameInterface.CA_BOSS.open(player);
            }
        });
        bind("Close", (player) -> player.getInterfaceHandler().closeInterfaces());
        for (CLCategoryType type : CLCategoryType.values) {
            bind(type + " layer", (player, slotId, itemId, option) -> populate(player, getCurrentCategory(player), slotId));
        }

        for (char c : SEARCH_LETTERS.toCharArray()) {
            bind("Search letter " + c, (player) -> {
                /* Avoid updating the results more than once a tick - it's very expensive. */
                long lastTick = player.getNumericTemporaryAttribute("collection log last search tick").longValue();
                if (lastTick < WorldThread.WORLD_CYCLE) {
                    player.addTemporaryAttribute("collection log last search tick", WorldThread.WORLD_CYCLE);
                    updateSearchResults(player);
                }
            });
        }
    }

    @NotNull
    private CLCategoryType getCurrentCategory(@NotNull final Player player) {
        final Object categoryAttr = player.getTemporaryAttributes().get(CATEGORY_ATTR_KEY);
        Preconditions.checkArgument(categoryAttr instanceof CLCategoryType);
        return (CLCategoryType) categoryAttr;
    }

    @Override
    public GameInterface getInterface() {
        return GameInterface.COLLECTION_LOG;
    }
}
