package com.zenyte.game.item.enums;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;

/**
 * @author Tommeh | 4-2-2019 | 22:24
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public enum RareDrop {
    STAFF_OF_THE_DEAD(11791),
    ZAMORAKIAN_SPEAR(11824),
    ZAMORAK_HILT(11816),
    //SARADOMIN_SWORD(11838),
    ARMADYL_CROSSBOW(11785),
    SARADOMIN_HILT(11814),
    BANDOS_BOOTS(11836),
    BANDOS_CHESTPLATE(11832),
    BANDOS_TASSETS(11834),
    BANDOS_HILT(11812),
    ARMADYL_HELMET(11826),
    ARMADYL_CHESTPLATE(11828),
    ARMADYL_CHAINSKIRT(11830),
    ARMADYL_HILT(11810),
    //BLACK_MASK(8901),
    /*SEERS_RING(6731),
    ARCHERS_RING(6733),
    WARRIOR_RING(6735),
    BERSERKER_RING(6737),
    DRAGON_AXE(6739),*/
    TANZANITE_FANG(12922),
    SERPENTINE_VISAGE(12927),
    MAGIC_FANG(12932),
    TANZANITE_MUTAGEN(13200),
    MAGMA_MUTAGEN(13201),
    RING_OF_THE_GODS(12601),
    TYRANNICAL_RING(12603),
    TREASONOUS_RING(12605),
    DRAGON_PICKAXE(11920),
    //DRAGON_2H_SWORD(7158),
    //UNCHARGED_TRIDENT(11908),
//    TRIDENT_OF_THE_SEAS_FULL(11905),
//    KRAKEN_TENTACLE(12004),
    ETERNAL_CRYSTAL(13227),
    PEGASIAN_CRYSTAL(13229),
    PRIMORDIAL_CRYSTAL(13231),
    SMOULDERING_STONE(13233),
    //HOLY_ELIXIR(12833),
    SPECTRAL_SIGIL(12823),
    ARCANE_SIGIL(12827),
    ELYSIAN_SIGIL(12819),
    WYVERN_VISAGE(ItemId.WYVERN_VISAGE),
    DRACONIC_VISAGE(11286),
    SKELETAL_VISAGE(22006),
    DRAGONBONE_NECKLACE(22111),
    THAMMARON_SCEPTRE(ItemId.THAMMARONS_SCEPTRE_U),
    CRAWS_BOW(ItemId.CRAWS_BOW_U),
    VIGGORA_MACE(ItemId.VIGGORAS_CHAINMACE_U),
    AMULET_OF_AVARICE(ItemId.AMULET_OF_AVARICE),
    UNSIRED(13273),
    ABYSSAL_DAGGER(13265),
    BLACK_TOURMALINE_CORE(21730),
    //GRANITE_HAMMER(21742),
    DRAGON_CHAINBODY(2513),
    //OCCULT_NECKLACE(12002),
    //ABYSSAL_WHIP(4151),
//    DARK_BOW(11235),
    ZENYTE_SHARD(19529),
    DRAGON_WARHAMMER(13576),
    DRAGON_LUMP(22103),
    IMBUED_HEART(20724),
    ETERNAL_GEM(ItemId.ETERNAL_GEM),
    DRAGON_LIMBS(21918),
    //Chambers of Xeric
    DEXTEROUS_PRAYER_SCROLL(ItemId.DEXTEROUS_PRAYER_SCROLL),
    ARCANE_PRAYER_SCROLL(ItemId.ARCANE_PRAYER_SCROLL),
    TWISTED_BUCKLET(ItemId.TWISTED_BUCKLER),
    DRAGON_HUNTER_CROSSBOW(ItemId.DRAGON_HUNTER_CROSSBOW),
    DINHS_BULWARK(ItemId.DINHS_BULWARK),
    ANCESTRAL_HAT(ItemId.ANCESTRAL_HAT),
    ANCESTRAL_ROBE_TOP(ItemId.ANCESTRAL_ROBE_TOP),
    ANCESTRAL_ROBE_BOTTOM(ItemId.ANCESTRAL_ROBE_BOTTOM),
    DRAGON_CLAWS(ItemId.DRAGON_CLAWS),
    ELDER_MAUL(ItemId.ELDER_MAUL),
    KODAI_INSIGNIA(ItemId.KODAI_INSIGNIA),
    TWISTED_BOW(ItemId.TWISTED_BOW),
    METAMORPHIC_DUST(ItemId.METAMORPHIC_DUST),
    //(Alchemical) Hydra
    HYDRAS_EYE(ItemId.HYDRAS_EYE),
    HYDRAS_HEART(ItemId.HYDRAS_HEART),
    HYDRAS_FANG(ItemId.HYDRAS_FANG),
    HYDRAS_TAIL(ItemId.HYDRA_TAIL),
    HYDRAS_CLAW(ItemId.HYDRAS_CLAW),
    HYDRA_LEATHER(ItemId.HYDRA_LEATHER),
    HYDRA_HEAD(ItemId.ALCHEMICAL_HYDRA_HEADS),
    PHARAOH_SCEPTRE(ItemId.PHARAOHS_SCEPTRE),
    JAR_OF_CHEMICALS(ItemId.JAR_OF_CHEMICALS),
    JAR_OF_DARKNESS(ItemId.JAR_OF_DARKNESS),
    JAR_OF_DECAY(ItemId.JAR_OF_DECAY),
    JAR_OF_DIRT(ItemId.JAR_OF_DIRT),
    JAR_OF_MIASMA(ItemId.JAR_OF_MIASMA),
    JAR_OF_SAND(ItemId.JAR_OF_SAND),
    JAR_OF_SOULS(ItemId.JAR_OF_SOULS),
    JAR_OF_STONE(ItemId.JAR_OF_STONE),
    JAR_OF_SWAMP(ItemId.JAR_OF_SWAMP);
    private static final RareDrop[] VALUES = values();
    private static final Int2ObjectOpenHashMap<RareDrop> DROPS = new Int2ObjectOpenHashMap<>(VALUES.length);

    static {
        for (final RareDrop drop : VALUES) {
            DROPS.put(drop.getId(), drop);
        }
    }

    private final int id;

    RareDrop(final int id) {
        this.id = id;
    }

    public static boolean contains(final Item item) {
        return DROPS.containsKey(item.getId());
    }

    public static void add(final int id) {
        DROPS.put(id, null);
    }

    public static void remove(final int id) {
        DROPS.remove(id);
    }

    public static IntArrayList getDynamicIds() {
        final IntArrayList list = new IntArrayList();
        for (final Int2ObjectMap.Entry<RareDrop> entry : DROPS.int2ObjectEntrySet()) {
            if (entry.getValue() == null) {
                list.add(entry.getIntKey());
            }
        }
        return list;
    }

    public int getId() {
        return this.id;
    }
}
