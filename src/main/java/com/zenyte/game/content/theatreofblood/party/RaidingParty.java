package com.zenyte.game.content.theatreofblood.party;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.content.theatreofblood.TheatreOfBloodRaid;
import com.zenyte.game.content.theatreofblood.area.VerSinhazaArea;
import com.zenyte.game.content.theatreofblood.interfaces.PartiesOverviewInterface;
import com.zenyte.game.content.theatreofblood.interfaces.PartyOverlayInterface;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;

import java.util.*;

/**
 * @author Tommeh | 5/21/2020 | 7:32 PM
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class RaidingParty {
    private final int id;
    private final List<String> originalMembers;
    private final List<String> members;
    private final List<String> applicants;
    private final Set<String> blocked;
    private final Map<Player, Integer> statuses;
    private final Map<String, String> lifeStates;
    private boolean wiped = false;
    private boolean practice = false;
    private final Date creationTimeStamp = new Date();
    private TheatreOfBloodRaid raid;
    private int preferredSize;
    private int preferredCombatLevel = 3;
    private boolean hardMode = false;

    public RaidingParty(final int id, final Player leader) {
        this.id = id;
        originalMembers = new ArrayList<>(5);
        members = new ArrayList<>(5);
        originalMembers.add(leader.getUsername());
        members.add(leader.getUsername());
        applicants = new ArrayList<>(10);
        blocked = new HashSet<>(2047);
        statuses = new HashMap<>(5);
        lifeStates = new HashMap<>(5);
    }

    public Player getMember(final int index) {
        return getPlayer(members.get(index));
    }

    public List<Player> getPlayers() {
        final var players = new ArrayList<Player>();
        for (String memberName : members) {
            final var member = getPlayer(memberName);
            if (member == null) {
                continue;
            }
            players.add(member);
        }
        return players;
    }

    public Player getLeader() {
        if (members.isEmpty()) {
            return null;
        }
        return getPlayer(members.get(0));
    }

    public static Player getPlayer(final String username) {
        final var optionalPlayer = World.getPlayer(username);
        return optionalPlayer.orElse(null);
    }

    public void removeMember(final Player player) {
        members.remove(player.getUsername());
        if (members.isEmpty()) {
            VerSinhazaArea.removeParty(this);
            for (final var a : applicants) {
                final var applicant = getPlayer(a);
                if (applicant == null) {
                    continue;
                }
                if (applicant.getInterfaceHandler().isPresent(GameInterface.TOB_PARTY_INFORMATION)) {
                    PartiesOverviewInterface.refresh(applicant);
                }
            }
        } else {
            if (raid != null) {
                members.forEach(m -> {
                    final var member = getPlayer(m);
                    if (member == null) {
                        return;
                    }
                    initializeStatusHUD(member);
                    updateStatusHUD(member);
                });
                raid.getSpectators().forEach(s -> {
                    final var spectator = getPlayer(s);
                    if (spectator == null) {
                        return;
                    }
                    initializeStatusHUD(spectator);
                    updateStatusHUD(spectator);
                });
            } else {
                for (final var m : members) {
                    final var member = getPlayer(m);
                    if (member == null) {
                        continue;
                    }
                    if (member.getInterfaceHandler().isPresent(GameInterface.TOB_PARTY_INFORMATION)) {
                        updateInformation(member);
                    }
                    PartyOverlayInterface.refresh(member, this);
                }
                for (final var a : applicants) {
                    final var applicant = getPlayer(a);
                    if (applicant == null) {
                        continue;
                    }
                    if (applicant.getInterfaceHandler().isPresent(GameInterface.TOB_PARTY_INFORMATION)) {
                        updateInformation(applicant);
                    }
                }
            }
        }
        if (player.getInterfaceHandler().isPresent(GameInterface.TOB_PARTY_INFORMATION)) {
            PartiesOverviewInterface.refresh(player);
        }
        PartyOverlayInterface.refresh(player, null);
    }

    public void addApplicant(final Player player) {
        applicants.add(player.getUsername());
        for (final var entry : VerSinhazaArea.getParties().entrySet()) {
            final var p = entry.getValue();
            if (p == this) {
                continue;
            }
            p.getApplicants().remove(player.getUsername());
            for (final var m : p.getMembers()) {
                final var member = getPlayer(m);
                if (member == null) {
                    continue;
                }
                if (member.getInterfaceHandler().isPresent(GameInterface.TOB_PARTY_INFORMATION)) {
                    p.updateInformation(member);
                }
            }
        }
        if (getLeader().getInterfaceHandler().isPresent(GameInterface.TOB_PARTY_INFORMATION)) {
            updateInformation(getLeader());
        }
        updateInformation(player);
    }

    public List<String> getAliveMembers() {
        final var alive = new ArrayList<String>();
        for (String memberName : members) {
            final var member = getPlayer(memberName);
            if (member == null || !lifeStates.get(memberName).equals("alive")) {
                continue;
            }
            alive.add(member.getUsername());
        }
        //System.out.println("Alive:" + members.size());
        return alive;
    }

    public List<String> getTargetableMembers() {
        final var targetable = new ArrayList<String>();
        for (String memberName : members) {
            final var member = getPlayer(memberName);
            if (member == null || !lifeStates.get(memberName).equals("alive") || !getRaid().getActiveRoom().inCombatZone(member.getX(), member.getY())) {
                continue;
            }
            targetable.add(member.getUsername());
        }
        //System.out.println("Alive:" + members.size());
        return targetable;
    }

    public List<Player> getAlivePlayers() {
        final var targetable = new ArrayList<Player>();
        for (String memberName : members) {
            final var member = getPlayer(memberName);
            if (member == null || !lifeStates.get(memberName).equals("alive")) {
                continue;
            }
            targetable.add(member);
        }
        return targetable;
    }

    public List<Player> getTargetablePlayers() {
        final var alive = new ArrayList<Player>();
        for (String memberName : members) {
            final var member = getPlayer(memberName);
            if (member == null || !lifeStates.get(memberName).equals("alive") || !getRaid().getActiveRoom().inCombatZone(member.getX(), member.getY())) {
                continue;
            }
            alive.add(member);
        }
        return alive;
    }

    public void disband() {
        for (final var m : members) {
            final var member = getPlayer(m);
            if (member == null) {
                continue;
            }
            member.sendMessage("Your party has disbanded.");
            if (member.getInterfaceHandler().isPresent(GameInterface.TOB_PARTY_INFORMATION)) {
                PartiesOverviewInterface.refresh(member);
            }
            PartyOverlayInterface.refresh(member, null);
        }
        for (final var a : applicants) {
            final var applicant = getPlayer(a);
            if (applicant == null) {
                continue;
            }
            if (applicant.getInterfaceHandler().isPresent(GameInterface.TOB_PARTY_INFORMATION)) {
                PartiesOverviewInterface.refresh(applicant);
            }
        }
    }

    public boolean isLeader(final Player player) {
        return getLeader() == player;
    }

    public int getAge() {
        return (int) ((Utils.currentTimeMillis() - creationTimeStamp.getTime()) / 600);
    }

    public int getSize() {
        return members.size();
    }

    public PartyRights getRights(final Player player) {
        if (isLeader(player)) {
            return PartyRights.LEADER;
        } else if (members.contains(player.getUsername())) {
            return PartyRights.PARTY_MEMBER;
        } else if (applicants.contains(player.getUsername())) {
            return PartyRights.APPLICANT;
        } else if (blocked.contains(player.getUsername())) {
            return PartyRights.BLOCKED_APPLICANT;
        }
        return PartyRights.CAN_APPLY;
    }

    public void updateInformation(final Player player) {
        final var dispatcher = player.getPacketDispatcher();
        player.addTemporaryAttribute("tob_viewing_party_id", id);
        GameInterface.TOB_PARTY_INFORMATION.open(player);
        //player.getPacketDispatcher().sendClientScript(2524, -1, -1);
        for (int index = 0; index < 5; index++) {
            if (index >= members.size()) {
                dispatcher.sendClientScript(2317, 2, "");
                continue;
            }
            final var member = getMember(index);
            String builder = member.getName() + "|" +
                    member.getCombatLevel() + "|" +
                    member.getSkills().getLevelForXp(Skills.ATTACK) + "|" +
                    member.getSkills().getLevelForXp(Skills.STRENGTH) + "|" +
                    member.getSkills().getLevelForXp(Skills.RANGED) + "|" +
                    member.getSkills().getLevelForXp(Skills.MAGIC) + "|" +
                    member.getSkills().getLevelForXp(Skills.DEFENCE) + "|" +
                    member.getSkills().getLevelForXp(Skills.HITPOINTS) + "|" +
                    member.getSkills().getLevelForXp(Skills.PRAYER) + "|" +
                    "0|";
            dispatcher.sendClientScript(2317, 2, builder);
        }
        if (members.contains(player.getUsername()) || applicants.contains(player.getUsername())) {
            for (final var a : applicants) {
                final var applicant = getPlayer(a);
                if (applicant == null) {
                    continue;
                }
                String builder = applicant.getName() + "|" +
                        applicant.getCombatLevel() + "|" +
                        applicant.getSkills().getLevelForXp(Skills.ATTACK) + "|" +
                        applicant.getSkills().getLevelForXp(Skills.STRENGTH) + "|" +
                        applicant.getSkills().getLevelForXp(Skills.RANGED) + "|" +
                        applicant.getSkills().getLevelForXp(Skills.MAGIC) + "|" +
                        applicant.getSkills().getLevelForXp(Skills.DEFENCE) + "|" +
                        applicant.getSkills().getLevelForXp(Skills.HITPOINTS) + "|" +
                        applicant.getSkills().getLevelForXp(Skills.PRAYER) + "|" +
                        "0|";
                dispatcher.sendClientScript(2321, builder);
            }
        }
        dispatcher.sendClientScript(2323, getRights(player).getId(), preferredSize, preferredCombatLevel, hardMode ? 2 : 0);
    }

    public void initializeStatusHUD(final Player player) {
        final var arguments = new Object[5];
        for (int index = 0; index < 5; index++) {
            if (index >= members.size()) {
                arguments[index] = "";
                continue;
            }
            final var member = getMember(index);
            arguments[index] = member.getName();
        }
        if (!player.getInterfaceHandler().isPresent(GameInterface.TOB_PARTY)) {
            GameInterface.TOB_PARTY.open(player);
        }
        player.getVarManager().sendBit(6440, 2);
        player.getVarManager().sendBit(6441, raid.getSpectators().contains(player.getUsername()) ? 0 : members.indexOf(player.getUsername()) + 1);
        player.getPacketDispatcher().sendClientScript(2301, arguments);
        updateStatusHUD(player);
    }

    public void updateStatusHUD(final boolean force) {
        for (int index = 0; index < 5; index++) {
            if (index >= members.size()) {
                for (final var m : members) {
                    final var member = getPlayer(m);
                    if (member == null) {
                        continue;
                    }
                    if (member.getVarManager().getBitValue(6442 + index) != 0) {
                        member.getVarManager().sendBit(6442 + index, 0);
                    }
                }
                for (final var s : raid.getSpectators()) {
                    final var spectator = getPlayer(s);
                    if (spectator == null) {
                        continue;
                    }
                    if (spectator.getVarManager().getBitValue(6442 + index) != 0) {
                        spectator.getVarManager().sendBit(6442 + index, 0);
                    }
                }
                continue;
            }
            final var m = members.get(index);
            if (m == null) {
                continue;
            }
            final var member = getPlayer(m);
            if (member == null) {
                continue;
            }
            final var previousStatus = statuses.getOrDefault(member, -1);
            final var currentStatus = getStatus(member);
            if (previousStatus != currentStatus || force) {
                for (final var o : members) {
                    final var otherMember = getPlayer(o);
                    if (otherMember == null) {
                        continue;
                    }
                    otherMember.getVarManager().sendBit(6442 + index, currentStatus == 31 ? 31 : otherMember == member ? 1 : currentStatus);
                }
                for (final var s : raid.getSpectators()) {
                    final var spectator = getPlayer(s);
                    if (spectator == null) {
                        continue;
                    }
                    spectator.getVarManager().sendBit(6442 + index, currentStatus);
                }
                statuses.put(member, currentStatus);
            }
        }
    }

    private void updateStatusHUD(final Player player) {
        for (int index = 0; index < 5; index++) {
            if (index >= members.size()) {
                if (player.getVarManager().getBitValue(6442 + index) != 0) {
                    player.getVarManager().sendBit(6442 + index, 0);
                }
                continue;
            }
            final var m = members.get(index);
            if (m == null) {
                continue;
            }
            final var member = getPlayer(m);
            if (member == null) {
                continue;
            }
            final var status = statuses.getOrDefault(member, getStatus(member));
            player.getVarManager().sendBit(6442 + index, player == member ? 1 : status);
        }
    }

    public int getStatus(final Player member) {
        if (raid.getActiveRoom() != member.getArea() && !member.getBooleanTemporaryAttribute("tob_advancing_room")) {
            return 31;
        } else if (member.isDead() || getLifeStates().get(member.getUsername()).equals("dead")) {
            return 30;
        }
        final var maxHp = Math.min(99, member.getMaxHitpoints());
        final var currentHp = Math.min(99, member.getHitpoints());
        final var fraction = currentHp / (double) maxHp;
        return (int) (1 + Math.round(26 * fraction));
    }

    public Player getRandomPlayer() {
        if (members.isEmpty()) {
            return null;
        }
        return getPlayer(members.get(Utils.random(members.size() - 1)));
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof RaidingParty)) {
            return false;
        }
        return ((RaidingParty) obj).getId() == id;
    }

    @Override
    public String toString() {
        return "RaidingParty(id=" + id + ", members=" + members + ")";
    }

    public int getId() {
        return this.id;
    }

    public List<String> getOriginalMembers() {
        return this.originalMembers;
    }

    public List<String> getMembers() {
        return this.members;
    }

    public List<String> getApplicants() {
        return this.applicants;
    }

    public Set<String> getBlocked() {
        return this.blocked;
    }

    public Map<Player, Integer> getStatuses() {
        return this.statuses;
    }

    public Map<String, String> getLifeStates() {
        return this.lifeStates;
    }

    public Date getCreationTimeStamp() {
        return this.creationTimeStamp;
    }

    public TheatreOfBloodRaid getRaid() {
        return this.raid;
    }

    public int getPreferredSize() {
        return this.preferredSize;
    }

    public int getPreferredCombatLevel() {
        return this.preferredCombatLevel;
    }

    public RaidingParty(final int id, final List<String> originalMembers, final List<String> members, final List<String> applicants, final Set<String> blocked, final Map<Player, Integer> statuses, final Map<String, String> lifeStates) {
        this.id = id;
        this.originalMembers = originalMembers;
        this.members = members;
        this.applicants = applicants;
        this.blocked = blocked;
        this.statuses = statuses;
        this.lifeStates = lifeStates;
    }

    public boolean isWiped() {
        return this.wiped;
    }

    public void setWiped(final boolean wiped) {
        this.wiped = wiped;
    }

    public boolean isPractice() {
        return this.practice;
    }

    public void setPractice(final boolean practice) {
        this.practice = practice;
    }

    public void setRaid(final TheatreOfBloodRaid raid) {
        this.raid = raid;
    }

    public void setPreferredSize(final int preferredSize) {
        this.preferredSize = preferredSize;
    }

    public void setPreferredCombatLevel(final int preferredCombatLevel) {
        this.preferredCombatLevel = preferredCombatLevel;
    }

    public boolean isHardMode() {
        return hardMode;
    }

    public void setHardMode(boolean hardMode) {
        this.hardMode = hardMode;
    }
}
