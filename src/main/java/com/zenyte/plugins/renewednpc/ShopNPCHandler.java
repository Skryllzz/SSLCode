package com.zenyte.plugins.renewednpc;

import com.zenyte.game.world.entity.npc.NpcId;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

/**
 * @author Kris | 25/11/2018 09:49
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public enum ShopNPCHandler {

    AARONS_ARCHERY_APPENDAGES("Aaron's Archery Appendages", NpcId.ARMOUR_SALESMAN),
    AEMADS_ADVENTURING_SUPPLIES("Aemad's Adventuring Supplies", NpcId.AEMAD),
    AGMUNDI_QUALITY_CLOTHES("Agmundi Quality Clothes", NpcId.AGMUNDI),
    //TODO: Npc only visible after quest completion.
    AK_HARANUS_EXOTIC_SHOP("Ak-Haranu's Exotic Shop", NpcId.AKHARANU),
    AL_KHARID_GENERAL_STORE("Al Kharid General Store", NpcId.SHOP_KEEPER_2817),
    ALECKS_HUNTER_EMPORIUM("Aleck's Hunter Emporium", NpcId.ALECK),
    ALICES_FARMING_SHOP("Alice's Farming shop", NpcId.ALICE),
    ALLANNAS_FARMING_SHOP("Allanna's Farming Shop", NpcId.ALLANNA),
    //TODO: Ali's Discount Wares,
    AMELIAS_SEED_SHOP("Amelia's Seed Shop", NpcId.AMELIA_8530),
    //Potentially TODO: Western side sells more items.
    ARDOUGNE_BAKERS_STALL("Ardougne Baker's Stall", NpcId.BAKER),
    ARDOUGNE_FUR_STALL("Ardougne Fur Stall", NpcId.FUR_TRADER),
    ARDOUGNE_GEM_STALL("Ardougne Gem Stall", NpcId.GEM_MERCHANT),
    ARDOUGNE_SILVER_STALL("Ardougne Silver Stall", NpcId.SILVER_MERCHANT_8722),
    ARDOUGNE_SPICE_STALL("Ardougne Spice Stall", NpcId.SPICE_SELLER),
    NEDS_HANDMADE_ROPE("Ned's Handmade Rope", NpcId.NED),
    ARHEIN_STORE("Arhein Store", NpcId.ARHEIN),
    RAUM_URDA_STEIN_ARMOUR_SHOP("Armour Shop", NpcId.RAUM_URDASTEIN),
    //TODO: Verify that npc exists + add access to it.
    JORZIK_ARMOUR_STORE("Armour store", NpcId.JORZIK),
    ARMOURY("Armoury", NpcId.SHOP_KEEPER_2894),
    //TODO: Add npc
    ARNOLDS_ECLECTIC_SUPPLIES("Arnold's Eclectic Supplies", NpcId.ARNOLD_LYDSPOR),
    AUBURY_RUNES_SHOP("Aubury's Rune Shop", NpcId.AUBURY_11435),
    //TODO: Requires quest completion.
    AURELS_SUPPLIES("Aurel's Supplies", NpcId.AUREL),
    AUTHENTIC_THROWING_WEAPONS("Authentic Throwing Weapons", NpcId.TRIBAL_WEAPON_SALESMAN),
    //TODO: Requires quest completion.
    AVAS_ODDS_AND_ENDS("Ava's Odds and Ends", NpcId.AVA),
    //TODO: Add access + spawns.
    BABA_YAGAS_MAGIC_SHOP("Baba Yaga's Magic Shop", NpcId.BABA_YAGA),
    BANDIT_BARGAINS("Bandit Bargains", NpcId.BANDIT_SHOPKEEPER),
    BANDIT_DUTY_FREE("Bandit Duty Free", NpcId.NOTERAZZO),
    BARKER_HABERDASHERY("Barker's Haberdashery", NpcId.BARKER),
    BATTLE_RUNES("Battle Runes", NpcId.MAGE_OF_ZAMORAK_2581),
    BEDABIN_VILLAGE_BARTERING("Bedabin Village Bartering", NpcId.BEDABIN_NOMAD),
    BETTYS_MAGIC_EMPORIUM("Betty's Magic Emporium", NpcId.BETTY),
    //TODO: Requires quest completion.
    BLADES_BY_URBI("Blades by Urbi", NpcId.URBI),
    //TODO: Daero npc also allows opening shop.
    BLURBERRY_BAR("Blurberry Bar", NpcId.BARMAN_6532),
    BOBS_BRILLIANT_AXES("Bob's Brilliant Axes", NpcId.BOB_10619),
    BOLKOYS_VILLAGE_SHOP("Bolkoy's Village Shop", NpcId.BOLKOY),
    BRIANS_ARCHERY_SUPPLIES("Brian's Archery Supplies", NpcId.BRIAN_8694),
    BRIANS_BATTLEAXE_BAZAAR("Brian's Battleaxe Bazaar", NpcId.BRIAN),
    //TODO: On option "Armour"
    BRIDGETS_ARMOUR("Briget's Armour", 7201),
    //TODO: On option "Weapons"
    BRIDGETS_WEAPONS("Briget's Weapons", 7201),
    BURTHORPE_SUPPLIES("Burthorpe Supplies", NpcId.WISTAN),
    CANDLE_SHOP("Candle Shop", NpcId.CANDLE_MAKER),
    //TODO: Ensure npc exists
    CAREFREE_CRAFTING_STALL("Carefree Crafting Stall", NpcId.NOLAR),
    CASSIES_SHIELD_SHOP("Cassie's Shield Shop", NpcId.CASSIE),
    //TODO: No trade option.
    CONTRABAND_YAK_PRODUCE("Contraband yak produce", NpcId.VANLIGGA_GASTFRIHET),
    //TODO: Culinaromancer's chest.
    DAGAS_SCIMITAR_SMITHY("Daga's Scimitar Smithy", NpcId.DAGA),
    DALS_GENERAL_OGRE_SUPPLIES("Dal's General Ogre Supplies", NpcId.OGRE_TRADER_4404),
    DARGAUDS_BOW_AND_ARROWS("Dargaud's Bow and Arrows", NpcId.BOW_AND_ARROW_SALESMAN),
    DARRENS_WILDERNESS_CAPE_SHOP("Darren's Wilderness Cape Shop", NpcId.DARREN),
    DAVONS_AMULET_STORE("Davon's Amulet Store", NpcId.DAVON),
    //TODO: No trade option.
    DEAD_MANS_CHEST("Dead Man's Chest", NpcId.BARTENDER_1314),
    DIANGOS_TOY_STORE("Diango's Toy Store", NpcId.DIANGO),
    //DODGY_MIKES_SECOND_HAND_CLOTHING("Dodgy Mike's Second-hand Clothing", 4022),
    DOMMIKS_CRAFTING_STORE("Dommik's Crafting Store", NpcId.DOMMIK),
    //TODO: Verify npc's existence.
    DORGESH_KAAN_GENERAL_SUPPLIES("Dorgesh-Kaan General Supplies", NpcId.LURGON),
    DRAYNOR_SEED_MARKET("Draynor Seed Market", NpcId.OLIVIA),
    DROGOS_MINING_EMPORIUM("Drogo's Mining Emporium", NpcId.DROGO_DWARF),
    DWARVEN_SHOPPING_STORE("Dwarven Shopping Store", NpcId.DWARF_5904),
    EDGEVILLE_GENERAL_STORE_ASSISTANCE("Edgeville General Store", NpcId.SHOP_ASSISTANT_2822),
    EDGEVILLE_GENERAL_STORE_KEEPER("Edgeville General Store", NpcId.SHOP_KEEPER_2821),
    EDMONDS_WILDERNESS_CAPE_SHOP("Edmond's Wilderness Cape Shop", NpcId.EDMOND),
    EDWARDS_WILDERNESS_CAPE_SHOP("Edward's Wilderness Cape Shop", NpcId.EDWARD),
    ETCETERIA_FISH("Etceteria Fish", NpcId.FISHMONGER),
    //TODO: Quest requirement
    FAIRY_FIXITS_FAIRY_ENCHANTMENT("Fairy Fixit's Fairy Enchantment", NpcId.FAIRY_FIXIT_7333),
    FALADOR_GENERAL_STORE("Falador General Store", NpcId.SHOP_KEEPER_2817, NpcId.SHOP_ASSISTANT_2818),
    FANCY_CLOTHES_STORE("Fancy Clothes Store", 1023),
    FERNAHEIS_FISHING_HUT("Fernahei's Fishing Hut", NpcId.FERNAHEI),
    //TODO: Add npc spawn
    FILAMINAS_WARES("Filamina's Wares", NpcId.FILAMINA),
    FINE_FASHIONS("Fine Fashions", NpcId.ROMETTI),
    FISHING_GUILD_SHOP("Fishing Guild Shop", NpcId.ROACHEY),
    FLOSIS_FISHMONGERS("Flosi's Fishmongers", NpcId.FLOSI_DALKSSON),
    FLYNNS_MACE_MARKET("Flynn's Mace Market", NpcId.FLYNN),
    FORTUNADOS_FINE_WINE("Fortunato's Fine Wine", NpcId.FORTUNATO),
    FOSSIL_ISLAND_GENERAL_STORE("Fossil Island General Store", NpcId.SHOP_KEEPER_7769),
    FRANKIES_FISHING_EMPORIUM("Frankie's Fishing Emporium", NpcId.FRANKIE),
    FREMENNIK_FISHMONGER("Fremennik Fishmonger", NpcId.FISH_MONGER),
    FREMENNIK_FUR_TRADER("Fremennik Fur Trader", NpcId.FUR_TRADER_3948),
    FRENITAS_COOKERY_SHOP("Frenita's Cookery Shop", NpcId.FRENITA),
    FRINCOS_FABULOUS_HERB_STORE("Frincos' Fabulous Herb Store", NpcId.FRINCOS),
    FUNCHS_FINE_GROCERIES("Funch's Fine Groceries", NpcId.HECKEL_FUNCH),
    //TODO: Co-op option + tradingsticks used.
    GABOOTYS_TAI_BWO_WANNAI_COOPERATIVE("Gabooty's Tai Bwo Wannai Cooperative", NpcId.GABOOTY),
    //TODO: Drinks option +trading sticks
    GABOOTYS_TAI_BWO_WANNAI_DRINKY_STORE("Gabooty's Tai Bwo Wannai Drinky Store", NpcId.GABOOTY),
    GAIUS_TWO_HANDED_STORE("Gaius' Two-Handed Shop", NpcId.GAIUS),
    GARDEN_CENTRE("Garden Centre", NpcId.GARDEN_SUPPLIER),
    GEM_TRADER("Gem Trader", NpcId.GEM_TRADER),
    GENERAL_STORE_CANIFIS("General Store (Canifis)", NpcId.FIDELIO),
    GERRANTS_FISHY_BUSINESS("Gerrant's Fishy Business", NpcId.GERRANT),
    GIANNES_RESTAURANT("Gianne's Restaurant", NpcId.GNOME_WAITER),
    GORMANS_GOURMET_FOODS("Gorman’s Gourmet Foods", NpcId.GORMAN),
    //TODO: Verify existence + use marks of grace currency
    GRACES_GRACEFUL_CLOTHING("Grace's Graceful Clothing", 5919),
    GRAND_TREE_GROCERIES("Grand Tree Groceries", NpcId.HUDO),
    //TODO: Verify existence
    GREEN_GEMSTONE_GEMS("Green Gemstone Gems", NpcId.HERVI),
    GREENGROCER_OF_MISCELLANIA("Greengrocer of Miscellania", NpcId.GREENGROCER_3689),
    GRUDS_HERBLORE_STALL("Grud's Herblore Stall", NpcId.OGRE_MERCHANT),
    GRUMS_GOLD_EXCHANGE("Grum's Gold Exchange", NpcId.GRUM),
    GULLUCK_AND_SONS("Gulluck and Sons", NpcId.GULLUCK),
    GUNSLIK_ASSORTED_ITEMS("Gunslik's Assorted Items", NpcId.GUNSLIK),
    HABABS_CRAFTING_EMPORIUM("Hamab's Crafting Emporium", NpcId.HAMAB),
    HAPPY_HEROES_HEMPORIUM("Happy Heroes' H'Emporium", NpcId.HELEMOS),
    HARPOON_JOES_HOUSE_OF_RUM("Harpoon Joe's House of 'Rum'", NpcId.JOE_4019),
    HARRYS_FISHING_SHOP("Harry's Fishing Shop", NpcId.HARRY),
    HELMET_SHOP("Helmet Shop", NpcId.PEKSA),
    HENDORS_AWESOME_ORES("Hendor's Awesome Ores", NpcId.HENDOR),
    HERQUINS_GEMS("Herquin's Gems", NpcId.HERQUIN),
    HICKTONS_ARCHERY_EMPORIUM("Hickton's Archery Emporium", NpcId.HICKTON),
    IANS_WILDERNESS_CAPE_SHOP("Ian's Wilderness Cape Shop", NpcId.IAN),
    IFABAS_GENERAL_STORE("Ifaba's General Store", NpcId.IFABA),
    IRONCLAD_EMPORIUM("Ironclad Emporium", NpcId.TIMOTHY),
    ISLAND_FISHMONGER("Island Fishmonger", NpcId.FISHMONGER_3688),
    ISLAND_GREENGROCER("Island Greengrocer", NpcId.GREENGROCER),
    //TODO: Quest requirement
    JAMILAS_CRAFT_STALL("Jamila's Craft Stall", NpcId.JAMILA),
    JATIXS_HERBLORE_SHOP("Jatix's Herblore Shop", NpcId.JATIX),
    JENNIFERS_GENERAL_FIELD_SUPPLIES("Jennifer's General Field Supplies", 305),
    JIMINUAS_JUNGLE_STORE("Jiminua's Jungle Store", NpcId.JIMINUA),
    KARAMJA_GENERAL_STORE("Karamja General Store", NpcId.SHOP_KEEPER_2825, NpcId.SHOP_ASSISTANT_2826),
    KARAMJA_WINES_SPIRITS_BEERS("Karamja Wines, Spirits, and Beers", NpcId.ZAMBO),
    KEEPA_KETTILONS_STORE("Keepa Kettilon's Store", NpcId.KEEPA_KETTILON),
    KELDAGRIM_STONEMASON("Keldagrim Stonemason", NpcId.STONEMASON),
    //TODO: Verify existence.
    KELDAGRIMS_BEST_BREAD("Keldagrim's Best Bread", NpcId.RANDIVOR),
    KENELMES_WARES("Kenelme's Wares", NpcId.KENELME),
    KHAZARD_GENERAL_STORE("Khazard General Store", NpcId.SHOP_KEEPER_2888),
    //KING_NARNODES_ROYAL_SEED_PODS("King Narnode's Royal Seed Pods", 8020),
    //TODO: Kjut's kebabs through dialogue
    LARRYS_WILDERNESS_CAPE_SHOP("Larry's Wilderness Cape Shop", NpcId.LARRY_2197),
    LEENZ_GENERAL_SUPPLIES("Leenz's General Supplies", NpcId.LEENZ),
    //TODO: Add npc
    LEGENDS_GUILD_GENERAL_STORE("Legends Guild General Store", NpcId.FIONELLA),
    //TODO: Add npc
    LEGENDS_GUILD_SHOP_OF_USEFUL_ITEMS("Legends Guild Shop of Useful Items", NpcId.SIEGFRIED_ERKLE),
    //TODO: Add npc
    LEPRECHAUN_LARRYS_FARMING_SUPPLIES("Leprechaun Larry's Farming Supplies", NpcId.TOOL_LEPRECHAUN_757),
    LITTLE_MUNTYS_LITTLE_SHOP("Little Munty's Little Shop", NpcId.MUNTY),
    //TODO: Add npc
    LITTLE_SHOP_OF_HORACE("Little Shop of Horace", NpcId.HORACE),
    LLETYA_ARCHERY_SHOP("Lletya Archery Shop", 1481),
    LLETYA_FOOD_STORE("Lletya Food Store", 1482),
    LLETYA_GENERAL_STORE("Lletya General Store", 1477),
    LLETYA_SEAMSTRESS("Lletya Seamstress", 1478),
    LOGAVA_GRICOLLERS_COOKING_SUPPLIES("Logava Gricoller's Cooking Supplies", NpcId.LOGAVA),
    LOUIES_ARMOURED_LEGS_BAZAAR("Louie's Armoured Legs Bazaar", NpcId.LOUIE_LEGS),
    LOVECRAFTS_TACKLE("Lovecraft's Tackle", NpcId.EZEKIAL_LOVECRAFT),
    HOSIDIUS_GENERAL_STORE("Hosidius General Store", NpcId.SHOP_KEEPER),
    LUNDAILS_ARENASIDE_RUNE_SHOP("Lundail's Arena-side Rune Shop", NpcId.LUNDAIL),
    MAGE_ARENA_STAFFS("Mage Arena Staffs", NpcId.CHAMBER_GUARDIAN),
    MAGIC_GUILD_STORE("Magic Guild Store (Mystic Robes)", NpcId.WIZARD_SININA),
    MAGIC_GUID_RUNES("Magic Guild Store (Runes and Staves)", NpcId.WIZARD_AKUTHA),
    IRKSOL("Irksol", NpcId.IRKSOL),
    JUKAT("Jukat", NpcId.JUKAT),
    //TODO: Verify existence
    MARTIN_THWAITS_LOST_AND_FOUND("Martin Thwait's Lost and Found", NpcId.MARTIN_THWAIT),
    //TODO: Verify existence
    MILTOGS_LAMPS("Miltog's Lamps", NpcId.MILTOG),
    //TODO: Add npc
    MISCELLANIAN_CLOTHES_SHOP("Miscellanian Clothes Shop", NpcId.HALLA),
    MISCELLANIAN_FOOD_SHOP("Miscellanian Food Shop", NpcId.OSVALD),
    MISCELLANIAN_GENERAL_STORE("Miscellanian General Store", NpcId.FINN),
    MOON_CLAN_FINE_CLOTHES("Moon Clan Fine Clothes", NpcId.RIMAE_SIRSALIS),
    MOON_CLAN_GENERAL_STORE("Moon Clan General Store", NpcId.MELANA_MOONLANDER),
    MULTICANNON_PARTS("Multicannon Parts", NpcId.NULODION),
    MYTHICAL_CAPE_STORE("Mythical Cape Store", NpcId.JACK_8037),
    MYTHS_GUILD_ARMOURY("Myths' Guild Armoury", NpcId.ERDAN),
    MYTHS_GUILD_HERBALIST("Myths' Guild Herbalist", NpcId.PRIMULA),
    MYTHS_GUILD_WEAPONRY("Myths' Guild Weaponry", NpcId.DIANA),
    NARDAH_GENERAL_STORE("Nardah General Store", NpcId.KAZEMDE),
    NARDAH_HUNTER_SHOP("Nardah Hunter Shop", NpcId.ARTIMEUS),
    //TODO: Verify existence
    NARDOKS_BONE_WEAPONS("Nardok's Bone Weapons", NpcId.NARDOK),
    //TODO: Requires quest completion.
    NATHIFAS_BAKE_STALL("Nathifa's Bake Stall", NpcId.NATHIFA),
    NEILS_WILDERNESS_CAPE_SHOP("Neil's Wilderness Cape Shop", NpcId.NEIL),
    NEITIZNOT_SUPPLIES("Neitiznot supplies", NpcId.JOFRIDR_MORDSTATTER),
    NURMOFS_PICKAXE_SHOP("Nurmof's Pickaxe Shop", NpcId.NURMOF),
    OBLIS_GENERAL_STORE("Obli's General Store", NpcId.OBLI),
    OOBAPOHKS_JAVELIN_STORE("Oobapohk's Javelin Store", NpcId.OOBAPOHK),
    //TODO: Verify exitence
    ORE_SELLER("Ore Seller", NpcId.ORDAN),
    ORE_STORE("Ore store", NpcId.HRING_HRING),
    OZIACHS_ARMOUR("Oziach's Armour", NpcId.OZIACH),
    PERRYS_CHOP_CHOP_SHOP("Perry's Chop-chop Shop", NpcId.PERRY),
    PICKAXE_IS_MINE("Pickaxe-Is-Mine", NpcId.TATI),
    POLLNIVNEACH_GENERAL_STORE("Pollnivneach general store", NpcId.MARKET_SELLER),
    PORT_PHASMATYS_GENERAL_STORE("Port Phasmatys General Store", NpcId.GHOST_SHOPKEEPER),
    QUALITY_ARMOUYR_SHOP("Quality Armour Shop", NpcId.SARO),
    QUALITY_WEAPONS_SHOP("Quality Weapons Shop", NpcId.SANTIRI),
    //TODO: Open through dialogue, no trade option.
    QUARTERMASTERS_STORES("Quartermaster's Stores", 3438),
    //TODO: Open through dialogue, no trade op.
    RAETUL_AND_COS_CLOTHS_STORE("Raetul and Co's Cloth Store", NpcId.RAETUL),
    RANAELS_SUPER_SKIRT_STORE("Ranael's Super Skirt Store", NpcId.RANAEL),
    RASOLO_THE_WANDERING_MERCHANT("Rasolo the Wandering Merchant", NpcId.RASOLO),
    //TODO two options, no trade. Additionally quest
    RAZMIRE_BUILDERS_MERCHANTS("Razmire Builders Merchants", NpcId.RAZMIRE_KEELGAN),
    //TODO: Two options, no trade. Additionally quest
    RAZMIRE_GENERAL_STORE("Razmire General Store", NpcId.RAZMIRE_KEELGAN),
    REGATHS_WARES("Regath's Wares", NpcId.REGATH),
    //TODO: Verify exitence
    RELDAKS_LEATHER_ARMOUR("Reldak's Leather Armour", NpcId.RELDAK),
    RICHARDS_FARMING_SHOP("Richard's Farming shop", NpcId.RICHARD),
    RICHARDS_WILDERNESS_CAPE_SHOP("Richard's Wilderness Cape Shop", NpcId.RICHARD_2200),
    RIMMINGTON_GENERAL_STORE("Rimmington General Store", NpcId.SHOP_KEEPER_2823, NpcId.SHOP_ASSISTANT_2824),
    ROKS_CHOCS_BOX("Rok's Chocs Box", NpcId.ROKUH),
    ROMMIKS_CRAFTY_SUPPLIES("Rommik's Crafty Supplies", NpcId.ROMMIK),
    RUFUS_MEAT_EMPORIUM("Rufus's Meat Emporium", NpcId.RUFUS_6478),
    SAMS_WILDERNESS_CAPE_SHOP("Sam's Wilderness Cape Shop", NpcId.SAM),
    SARAHS_FARMING_SHOP("Sarah's Farming Shop", NpcId.SARAH),
    SCAVVOS_RUNE_STORE("Scavvo's Rune Store", NpcId.SCAVVO),
    SEDDUS_ADVENTURER_STORE("Seddu's Adventurers' Store", NpcId.SEDDU),
    SHANTAY_PASS_SHOP("Shantay Pass Shop", NpcId.SHANTAY),
    //TODO: Uses Buy option instead.
    SHOP_OF_DISTASTE("Shop of Distaste", NpcId.FADLI),
    //TODO: Verify existence
    SILVER_COG_SILVER_STALL("Silver Cog Silver Stall", NpcId.GULLDAMAR),
    SIMONS_WILDERNESS_CAPE_SHOP("Simon's Wilderness Cape Shop", NpcId.SIMON),
    SKULGRIMENS_BATTLE_GEAR("Skulgrimen's Battle Gear", NpcId.SKULGRIMEN),
    SMITHING_SMITHS_SHOP("Smithing Smith's Shop", NpcId.SMITH),
    SOLIHIBS_FOOD_STAFF("Solihib's Food Stall", NpcId.SOLIHIB),
    //TODO: Add npc, trading done through dialogue.
    TAMAYUS_SPEAR_STALL("Tamayu's Spear Stall", NpcId.TAMAYU),
    //TODO: Multiple npcs open it.
    THE_ASP_AND_SNAKE_BAR("The Asp & Snake Bar", 3535),
    THE_BIG_HEIST_LODGE("The Big Heist Lodge", NpcId.BARTENDER),
    THE_DEEPER_LODE("The Deeper Lode", NpcId.FUGGY),
    THE_GOLDEN_FIELD("The Golden Field", NpcId.RICHARD_6954),
    THE_HAYMAKERS_ARMS("The Haymaker's Arms", NpcId.GOLOVA),
    THE_LIGHTHOUSE_STORE("The Lighthouse Store", NpcId.JOSSIK),
    //TODO: Another npc opens through dialogue.
    THE_OTHER_INN("The Other Inn", NpcId.MAMA),
    //TODO Another npc opens it too.
    THE_SHRIMP_AND_PARROT("The Shrimp and Parrot", NpcId.ALFONSE_THE_WAITER),
    THE_SPICE_IS_RIGHT("The Spice is Right", NpcId.EMBALMER),
    THESSELIAS_FINE_CLOTHES("Thessalia's Fine Clothes", 534),
    THIRUS_URKAR_FINE_DYNAMITE_STORE("Thirus Urkar's Fine Dynamite Store", 7208),
    THYRIAS_WARES("Thyria's Wares", NpcId.THYRIA),
    //TODO: Add npc + Quest req
    TIADECHES_KARAMBWAN_STALL("Tiadeche's Karambwan Stall", NpcId.TIADECHE_4700),
    TOAD_AND_CHICKEN("Toad and Chicken", NpcId.TOSTIG),
    TONYS_PIZZA_BASES("Tony's Pizza Bases", NpcId.FAT_TONY),
    TOOTHYS_PICKAXES("Toothy's Pickaxes", NpcId.TOOTHY),
    TRADER_STANS_TRADING_POST("Trader Stan's Trading Post", 1328, 1329, 1330, 1331, 1332, 1333, 1334),
    //TODO: Opened through dialogue.
    TRADER_SVENS_BLACK_MARKET_GOODS("Trader Sven's Black-market Goods", 3779),
    TUTABS_MAGICAL_MARKET("Tutab's Magical Market", NpcId.TUTAB),
    TWO_FEET_CHARLEYS_FISH_SHOP("Two Feet Charley's Fish Shop", NpcId.CHARLEY),
    TYNANS_FISHING_SUPPLIES("Tynan's Fishing Supplies", NpcId.TYNAN),
    TZHAAR_HUR_LEKS_ORE_AND_GEM_STORE("TzHaar-Hur-Lek's Ore and Gem Store", NpcId.TZHAARHURLEK),
    TZHAAR_HUR_RINS_ORE_AND_GEM_STORE("TzHaar-Hur-Rin's Ore and Gem Store", NpcId.TZHAARHURRIN),
    TZHAAR_HUR_TELS_EQUIPMENT_STORE("TzHaar-Hur-Tel's Equipment Store", NpcId.TZHAARHURTEL),
    TZHAAR_MEJ_ROHS_RUNE_STORE("TzHaar-Mej-Roh's Rune Store", NpcId.TZHAARMEJROH),
    TZHAAR_HUR_ZALS_EQUIPMENT_STORE("TzHaar-Hur-Zal's Equipment Store", NpcId.TZHAARHURZAL),
    UGLUGS_STUFFIES("Uglug's Stuffsies", NpcId.UGLUG_NAR),
    VALAINES_SHOP_OF_CHAMPIONS("Valaine's Shop of Champions", NpcId.VALAINE),
    VANESSAS_FARMING_SHOP("Vanessa's Farming shop", NpcId.VANESSA),
    VANNAHS_FARM_STORE("Vannah's Farm Store", NpcId.VANNAH),
    VERMUNDIS_CLOTHES_STALL("Vermundi's Clothes Stall", NpcId.VERMUNDI),
    VIGRS_WARHAMMERS("Vigr's Warhammers", NpcId.VIGR),
    VOID_KNIGHT_ARCHERY_STORE("Void Knight Archery Store", NpcId.SQUIRE_1765),
    VOID_KNIGHT_GENERAL_STORE("Void Knight General Store", NpcId.SQUIRE_1768),
    VOID_KNIGHT_MAGIC_STORE("Void Knight Magic Store", NpcId.SQUIRE_1767),
    //TODO: Add npc
    WARRENS_FISH_MONGER("Warrens Fish Monger", NpcId.FISH_MONGER_7912),
    //TODO: Add npc
    WARRENS_GENERAL_STORE("Warrens General Store", NpcId.SHOP_KEEPER_7913),
    WARRIOR_GUILD_ARMOURY("Warrior Guild Armoury", NpcId.ANTON),
    WARRIOR_GUILD_FOOD_SHOP("Warrior Guild Food Shop", NpcId.LIDIO),
    WARRIOR_GUILD_POTION_SHOP("Warrior Guild Potion Shop", NpcId.LILLY),
    WAYNES_CHAINS_CHAINMAIL_SPECIALIST("Wayne's Chains - Chainmail Specialist", NpcId.WAYNE),
    WEAPONS_GALORE("Weapons galore", NpcId.SKULI_MYRKA),
    WEST_ARDOUGNE_GENERAL_STORE("West Ardougne General Store", NpcId.CHADWELL),
    WILLIAMS_WILDERNESS_CAPE_SHOP("William's Wilderness Cape Shop", NpcId.WILLIAM),
    WYDINS_FOOD_STORE("Wydin's Food Store", NpcId.WYDIN),
    YARSUL_PRODIGIUOUS_PICKAXES("Yarsul's Prodigious Pickaxes", NpcId.YARSUL),
    YE_OLDE_TEA_SHOPPE("Ye Olde Tea Shoppe", 1302),
    YRSAS_ACCPUNTREMENTS("Yrsa's Accoutrements", NpcId.YRSA_3933),
    ZANARIS_GENERAL_STORE("Zanaris General Store", NpcId.FAIRY_SHOP_KEEPER),
    ZEKES_SUPERIOR_SCIMITARS("Zeke's Superior Scimitars", NpcId.ZEKE),
    ZENESHAS_PLATE_MAIL_BODY_SHOP("Zenesha's Plate Mail Body Shop", NpcId.ZENESHA_8681),
    LEKE_QUO_KERAN("Mount Karuulm Weapon Shop", NpcId.LEKE_QUO_KERAN),
    SLAYER_EQUIPMENT("Slayer Equipment", NpcId.TURAEL, NpcId.MAZCHNA, NpcId.VANNAKA, NpcId.CHAELDAR, NpcId.DURADEL, 490, NpcId.NIEVE, NpcId.KRYSTILIA, NpcId.KONAR_QUO_MATEN);

    ShopNPCHandler(final String shop, final int... npcIds) {
        this.npcIds = npcIds;
        this.shop = shop;
    }

    final int[] npcIds;

    final String shop;

    static final ShopNPCHandler[] values = values();

    static final Int2ObjectOpenHashMap<ShopNPCHandler> map = new Int2ObjectOpenHashMap<>();

    static {
        for (final ShopNPCHandler shop : values) {
            for (final int id : shop.npcIds) {
                map.put(id, shop);
            }
        }
    }
}
