package com.zenyte.plugins.object;

import com.google.common.collect.ImmutableList;
import com.zenyte.game.content.achievementdiary.Diary;
import com.zenyte.game.util.Colour;
import com.zenyte.game.util.Direction;
import com.zenyte.game.world.entity.pathfinding.events.player.TileEvent;
import com.zenyte.game.world.entity.pathfinding.strategy.TileStrategy;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.entity.player.variables.PlayerVariables;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;

import java.util.ArrayList;
import java.util.Calendar;

import static com.zenyte.game.content.achievementdiary.DiaryReward.*;

/**
 * @author Kris | 27/08/2020
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class DailyBoard implements ObjectAction {
    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        player.setFaceLocation(player.getLocation().transform(Direction.SOUTH));
        final PlayerVariables vars = player.getVariables();
        final Colour red = Colour.RS_RED;
        final Colour green = Colour.RS_GREEN;
        final boolean resurrectable = vars.getZulrahResurrections() == 0 && WESTERN_BANNER4.eligibleFor(player);
        final boolean zaffsBattlestaves = !vars.isClaimedBattlestaves() && (VARROCK_ARMOUR1.eligibleFor(player) || VARROCK_ARMOUR2.eligibleFor(player) || VARROCK_ARMOUR3.eligibleFor(player) || VARROCK_ARMOUR4.eligibleFor(player));
        final int teletabPurchases = vars.getTeletabPurchases();
        final int spellbookSwaps = vars.getSpellbookSwaps();
        final int spellbookSwapLimit = player.getSkills().getLevelForXp(Skills.MAGIC) < 99 ? 0 : 5;
        final int partyAdvertisements = vars.getRaidAdvertsQuota();
        final int fountainTeleports = vars.getFountainOfRuneTeleports();
        final int fountainTeleportsLimit = WILDERNESS_SWORD4.eligibleFor(player) ? Integer.MAX_VALUE : WILDERNESS_SWORD3.eligibleFor(player) ? 1 : 0;
        final int ardougneTeleports = vars.getArdougneFarmTeleports();
        final int ardougneTeleportsLimit = ARDOUGNE_CLOAK4.eligibleFor(player) ? Integer.MAX_VALUE : ARDOUGNE_CLOAK3.eligibleFor(player) ? 5 : ARDOUGNE_CLOAK2.eligibleFor(player) ? 3 : 0;
        final int fishingColonyTeleports = vars.getFishingColonyTeleports();
        final int fishingColonyTeleportsLimit = WESTERN_BANNER4.eligibleFor(player) ? Integer.MAX_VALUE : WESTERN_BANNER3.eligibleFor(player) ? 3 : WESTERN_BANNER2.eligibleFor(player) ? 2 : WESTERN_BANNER1.eligibleFor(player) ? 1 : 0;
        final int sherlockTeleports = vars.getSherlockTeleports();
        final int sherlockTeleportsLimit = KANDARIN_HEADGEAR4.eligibleFor(player) ? Integer.MAX_VALUE : KANDARIN_HEADGEAR3.eligibleFor(player) ? 1 : 0;
        final int rellekkaTeleports = vars.getRellekkaTeleports();
        final int rellekkaTeleportsLimit = FREMENNIK_SEA_BOOTS4.eligibleFor(player) ? Integer.MAX_VALUE : (FREMENNIK_SEA_BOOTS3.eligibleFor(player) || FREMENNIK_SEA_BOOTS2.eligibleFor(player) || FREMENNIK_SEA_BOOTS1.eligibleFor(player)) ? 1 : 0;
        final int faladorShieldRecharge = vars.getFaladorPrayerRecharges();
        final int faladorShieldRechargeLimit = FALADOR_SHIELD4.eligibleFor(player) ? Integer.MAX_VALUE : (FALADOR_SHIELD3.eligibleFor(player) || FALADOR_SHIELD2.eligibleFor(player) || FALADOR_SHIELD1.eligibleFor(player)) ? 1 : 0;
        final int explorersRingLimit = EXPLORERS_RING3.eligibleFor(player) ? 4 : EXPLORERS_RING4.eligibleFor(player) || EXPLORERS_RING2.eligibleFor(player) ? 3 : EXPLORERS_RING1.eligibleFor(player) ? 2 : 0;
        final int explorersRingCharges = Math.min(explorersRingLimit, vars.getRunReplenishments());
        final int explorersRingAlchemy = vars.getFreeAlchemyCasts();
        final int explorersRingAlchemyLimit = EXPLORERS_RING1.eligibleFor(player) || EXPLORERS_RING2.eligibleFor(player) || EXPLORERS_RING3.eligibleFor(player) || EXPLORERS_RING4.eligibleFor(player) ? 30 : 0;
        final int cabbageTeleports = vars.getCabbageFieldTeleports();
        final int cabbageTeleportsLimit = EXPLORERS_RING3.eligibleFor(player) || EXPLORERS_RING4.eligibleFor(player) ? Integer.MAX_VALUE : EXPLORERS_RING2.eligibleFor(player) ? 3 : 0;
        final int nardahTeleports = vars.getNardahTeleports();
        final int nardahTeleportsLimit = DESERT_AMULET4.eligibleFor(player) ? Integer.MAX_VALUE : DESERT_AMULET3.eligibleFor(player) || DESERT_AMULET2.eligibleFor(player) ? 1 : 0;
        final int kourendWoodlandTeleports = vars.getKourendWoodlandTeleports();
        final int kourendWoodlandsTeleportsLimit = RADAS_BLESSING4.eligibleFor(player) || RADAS_BLESSING3.eligibleFor(player) ? Integer.MAX_VALUE : RADAS_BLESSING2.eligibleFor(player) ? 5 : RADAS_BLESSING1.eligibleFor(player) ? 3 : 0;
        final int mountKaruulmTeleports = vars.getMountKaruulmTeleports();
        final int mountKaruulmTeleportsLimit = RADAS_BLESSING4.eligibleFor(player) ? Integer.MAX_VALUE : RADAS_BLESSING3.eligibleFor(player) ? 3 : 0;
        final int grappleSearches = vars.getGrappleAndCrossbowSearches();
        final int grappleSearchesLimit = player.getSkills().getLevelForXp(Skills.FLETCHING) < 99 ? 0 : 3;
        final ImmutableList<String> entries = ImmutableList.<String>builder().add("Zulrah resurrection: " + (resurrectable ? green.wrap("Available") : red.wrap("Unavailable"))).add("Zaff's battlestaves: " + (zaffsBattlestaves ? green.wrap("Available") : red.wrap("Unavailable"))).add("Fountain of Rune teleport: " + (fountainTeleports == fountainTeleportsLimit ? red.wrap("Unavailable") : green.wrap("Available"))).add("Teletab purchases: " + (teletabPurchases >= 1000 ? red.wrap("Unavailable") : green.wrap(teletabPurchases + "/" + 1000))).add("Spellbook swaps: " + (spellbookSwapLimit == 0 ? red.wrap("Unavailable") : (spellbookSwaps == spellbookSwapLimit ? red : green).wrap(spellbookSwaps + "/" + spellbookSwapLimit))).add("CoX Party advertisements: " + (partyAdvertisements == 0 ? red.wrap("Unavailable") : green.wrap((15 - partyAdvertisements) + "/" + 15))).add("Ardougne Farm teleports: " + (ardougneTeleportsLimit == 0 ? red.wrap("Unavailable") : ardougneTeleportsLimit == Integer.MAX_VALUE ? green.wrap("Available") : (ardougneTeleports == ardougneTeleportsLimit ? red : green).wrap(ardougneTeleports + "/" + ardougneTeleportsLimit))).add("Fishing Colony teleports: " + (fishingColonyTeleportsLimit == 0 ? red.wrap("Unavailable") : fishingColonyTeleportsLimit == Integer.MAX_VALUE ? green.wrap("Available") : (fishingColonyTeleports == fishingColonyTeleportsLimit ? red : green).wrap(fishingColonyTeleports + "/" + fishingColonyTeleportsLimit))).add("Sherlock teleports: " + (sherlockTeleportsLimit == 0 ? red.wrap("Unavailable") : sherlockTeleportsLimit == Integer.MAX_VALUE ? green.wrap("Available") : (sherlockTeleports == sherlockTeleportsLimit ? red : green).wrap(sherlockTeleports + "/" + sherlockTeleportsLimit))).add("Rellekka teleports: " + (rellekkaTeleportsLimit == 0 ? red.wrap("Unavailable") : rellekkaTeleportsLimit == Integer.MAX_VALUE ? green.wrap("Available") : (rellekkaTeleports == rellekkaTeleportsLimit ? red : green).wrap(rellekkaTeleports + "/" + rellekkaTeleportsLimit))).add("Cabbage field teleports: " + (cabbageTeleportsLimit == 0 ? red.wrap("Unavailable") : cabbageTeleportsLimit == Integer.MAX_VALUE ? green.wrap("Available") : (cabbageTeleports == cabbageTeleportsLimit ? red : green).wrap(cabbageTeleports + "/" + cabbageTeleportsLimit))).add("Nardah teleports: " + (nardahTeleportsLimit == 0 ? red.wrap("Unavailable") : nardahTeleportsLimit == Integer.MAX_VALUE ? green.wrap("Available") : (nardahTeleports == nardahTeleportsLimit ? red : green).wrap(nardahTeleports + "/" + nardahTeleportsLimit))).add("Kourend Woodlands teleports: " + (kourendWoodlandsTeleportsLimit == 0 ? red.wrap("Unavailable") : kourendWoodlandsTeleportsLimit == Integer.MAX_VALUE ? green.wrap("Available") : (kourendWoodlandTeleports == kourendWoodlandsTeleportsLimit ? red : green).wrap(kourendWoodlandTeleports + "/" + kourendWoodlandsTeleportsLimit))).add("Mount Karuulm teleports: " + (mountKaruulmTeleportsLimit == 0 ? red.wrap("Unavailable") : mountKaruulmTeleportsLimit == Integer.MAX_VALUE ? green.wrap("Available") : (mountKaruulmTeleports == mountKaruulmTeleportsLimit ? red : green).wrap(mountKaruulmTeleports + "/" + mountKaruulmTeleportsLimit))).add("Falador shield restores: " + (faladorShieldRechargeLimit == 0 ? red.wrap("Unavailable") : faladorShieldRechargeLimit == Integer.MAX_VALUE ? green.wrap("Available") : (faladorShieldRecharge == faladorShieldRechargeLimit ? red : green).wrap(faladorShieldRecharge + "/" + faladorShieldRechargeLimit))).add("Explorer's ring energy replenish: " + (explorersRingCharges == explorersRingLimit ? red.wrap("Unavailable") : green.wrap(explorersRingCharges + "/" + explorersRingLimit))).add("Explorer's ring alchemy: " + (explorersRingAlchemy == explorersRingAlchemyLimit ? red.wrap("Unavailable") : green.wrap(explorersRingAlchemy + "/" + explorersRingAlchemyLimit))).add("Grapple and crossbow searches: " + (grappleSearches == grappleSearchesLimit ? red.wrap("Unavailable") : green.wrap(grappleSearches + "/" + grappleSearchesLimit))).build();
        final Calendar currentCalendar = Calendar.getInstance();
        final int hoursRemaining = 24 - Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        Diary.sendJournal(player, "Daily board | Approx. hours until reset: " + hoursRemaining, new ArrayList<>(entries));
    }

    public void handle(final Player player, final WorldObject object, final String name, final int optionId, final String option) {
        player.setRouteEvent(new TileEvent(player, new TileStrategy(object), getRunnable(player, object, name, optionId, option), getDelay()));
    }

    @Override
    public Object[] getObjects() {
        return new Object[]{35023};
    }
}
