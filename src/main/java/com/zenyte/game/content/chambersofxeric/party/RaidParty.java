package com.zenyte.game.content.chambersofxeric.party;

import com.google.common.eventbus.Subscribe;
import com.zenyte.game.content.chambersofxeric.MountQuidamortemArea;
import com.zenyte.game.content.chambersofxeric.Raid;
import com.zenyte.game.content.clans.ClanChannel;
import com.zenyte.game.ui.InterfacePosition;
import com.zenyte.game.util.AccessMask;
import com.zenyte.game.util.Colour;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.plugins.events.ClanLeaveEvent;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.zenyte.game.world.entity.player.Skills.*;

/**
 * @author Kris | 15. nov 2017 : 18:12.32
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public final class RaidParty {
    /**
     * The list of all the advertised raids parties.
     */
    public static final List<RaidParty> advertisedParties = new ObjectArrayList<>((int) (40 / 0.75F));
    /**
     * The skill ids ordered in the same exact order that the CS2 accepts.
     */
    private static final int[] orderedSkills = new int[]{ATTACK, STRENGTH, RANGED, MAGIC, DEFENCE, HITPOINTS, PRAYER, AGILITY, HERBLORE, THIEVING, CRAFTING, RUNECRAFTING, MINING, SMITHING, FISHING, COOKING, FIREMAKING, WOODCUTTING, FLETCHING, SLAYER, FARMING, CONSTRUCTION, HUNTER};
    /**
     * The clan channel which is tied to this raid party.
     */
    private final ClanChannel channel;
    /**
     * The time in epoch milliseconds when the party was initially created.
     */
    private final long milliseconds;
    /**
     * The raid attached to this party.
     */
    private final Raid raid;
    /**
     * The username of the player who created the party.
     */
    private String player;
    /**
     * The preferred party size, combat level and skill total. These variables do not set any actual restrictions, it is just for visibilitiy purposes so the party owner can see if
     * the caps are reached, and so that the players who join the party can see what the party owner is expecting of them.
     */
    private int preferredPartySize;
    private int preferredCombatLevel;
    private int preferredSkillTotal;
    /**
     * Whether or not the challenge mode version of the raid is enabled.
     */
    private boolean challengeMode;

    /**
     * The constructor for the raid party, requiring a host and a clan channel to be constructed.
     *
     * @param host    the player constructing the raid party.
     * @param channel the clan channel in which the player builds the raid party.
     */
    RaidParty(@NotNull final Player host, @NotNull final ClanChannel channel) {
        player = host.getUsername();
        raid = new Raid(this);
        this.channel = channel;
        milliseconds = Utils.currentTimeMillis();
    }

    /**
     * De-list the raid party from the advertised parties list when the player who constructed the party leaves the clan chat.
     *
     * @param event the clan channel leave event.
     */
    @Subscribe
    public static void onClanLeave(final ClanLeaveEvent event) {
        final Player player = event.getPlayer();
        final ClanChannel channel = player.getSettings().getChannel();
        if (channel == null) {
            return;
        }
        if (channel.getRaidParty() == null || !channel.getRaidParty().getPlayer().equals(player.getUsername())) {
            return;
        }
        if (MountQuidamortemArea.appiontAnotherPartyLeader(player)) {
            return;
        }
        advertisedParties.remove(channel.getRaidParty());
    }

    /**
     * Refreshes the raid party interface which shows the contents of the raid party.
     *
     * @param player the player for whom to refresh the interface.
     */
    public void refresh(final Player player) {
        if (!player.getInterfaceHandler().containsInterface(InterfacePosition.CENTRAL)) {
            return;
        }
        final IntArrayList levels = new IntArrayList();
        player.getVarManager().sendBit(5428, this.player.equalsIgnoreCase(player.getUsername()) ? 1 : 0);
        player.getInterfaceHandler().sendInterface(InterfacePosition.CENTRAL, 507);
        player.getVarManager().sendBit(6385, challengeMode ? 1 : 0);
        int size = 0;
        for (final Player p : channel.getMembers()) {
            if (p.isNulled() || !p.inArea(MountQuidamortemArea.class)) {
                continue;
            }
            size++;
            levels.add(p.getSkills().getCombatLevel());
            levels.add(p.getSkills().getTotalLevel());
            levels.add(p.getNumericAttribute(challengeMode ? "challengechambersofxeric" : "chambersofxeric").intValue());
            for (int i : orderedSkills) {
                levels.add(p.getSkills().getLevelForXp(i));
            }
            final StringBuilder arguments = new StringBuilder();
            arguments.append(Colour.WHITE.wrap(p.getName()));
            arguments.append('|');
            for (final Integer level : levels) {
                arguments.append(level).append('|');
            }
            player.getPacketDispatcher().sendClientScript(1517, 0, arguments.toString());
            levels.clear();
        }
        player.getPacketDispatcher().sendClientScript(1524, "Raiding Party of " + Utils.formatString(this.player) + " (" + size + ")", !channel.getMembers().contains(player) ? 0 : player.getUsername().equals(channel.getOwner()) ? 2 : 1, advertisedParties.contains(this) ? 1 : 0, player.getVariables().getRaidAdvertsQuota());
        player.getPacketDispatcher().sendComponentSettings(507, 3, 0, 7, AccessMask.CONTINUE);
        player.getVarManager().sendBit(5433, preferredPartySize);
        player.getVarManager().sendBit(5426, preferredCombatLevel);
        player.getVarManager().sendBit(5427, preferredSkillTotal);
    }

    /**
     * Refreshes the raid party tab which is visible when the player enters the chambers itself.
     *
     * @param player the player for whom to refresh the interface.
     */
    public void refreshTab(@NotNull final Player player) {
        RaidPartyInterface.refresh(player, raid);
    }

    /**
     * The username of the player who created the party.
     */
    public String getPlayer() {
        return this.player;
    }

    /**
     * The username of the player who created the party.
     */
    public void setPlayer(final String player) {
        this.player = player;
    }

    /**
     * The clan channel which is tied to this raid party.
     */
    public ClanChannel getChannel() {
        return this.channel;
    }

    /**
     * The time in epoch milliseconds when the party was initially created.
     */
    public long getMilliseconds() {
        return this.milliseconds;
    }

    /**
     * The raid attached to this party.
     */
    public Raid getRaid() {
        return this.raid;
    }

    /**
     * The preferred party size, combat level and skill total. These variables do not set any actual restrictions, it is just for visibilitiy purposes so the party owner can see if
     * the caps are reached, and so that the players who join the party can see what the party owner is expecting of them.
     */
    public int getPreferredPartySize() {
        return this.preferredPartySize;
    }

    /**
     * The preferred party size, combat level and skill total. These variables do not set any actual restrictions, it is just for visibilitiy purposes so the party owner can see if
     * the caps are reached, and so that the players who join the party can see what the party owner is expecting of them.
     */
    public void setPreferredPartySize(final int preferredPartySize) {
        this.preferredPartySize = preferredPartySize;
    }

    public int getPreferredCombatLevel() {
        return this.preferredCombatLevel;
    }

    public void setPreferredCombatLevel(final int preferredCombatLevel) {
        this.preferredCombatLevel = preferredCombatLevel;
    }

    public int getPreferredSkillTotal() {
        return this.preferredSkillTotal;
    }

    public void setPreferredSkillTotal(final int preferredSkillTotal) {
        this.preferredSkillTotal = preferredSkillTotal;
    }

    /**
     * Whether or not the challenge mode version of the raid is enabled.
     */
    public boolean isChallengeMode() {
        return this.challengeMode;
    }

    /**
     * Whether or not the challenge mode version of the raid is enabled.
     */
    public void setChallengeMode(final boolean challengeMode) {
        this.challengeMode = challengeMode;
    }
}
