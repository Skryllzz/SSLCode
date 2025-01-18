package com.zenyte.game.content.theatreofblood;

import com.google.common.eventbus.Subscribe;
import com.zenyte.game.content.ItemRetrievalService;
import com.zenyte.game.content.theatreofblood.area.VerSinhazaArea;
import com.zenyte.game.content.theatreofblood.boss.TheatreArea;
import com.zenyte.game.content.theatreofblood.interfaces.PartyOverlayInterface;
import com.zenyte.game.content.theatreofblood.party.RaidingParty;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.region.dynamicregion.AllocatedArea;
import com.zenyte.game.world.region.dynamicregion.MapBuilder;
import com.zenyte.plugins.events.PostWindowStatusEvent;
import org.apache.logging.log4j.util.Strings;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Tommeh | 5/22/2020 | 5:03 PM
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class TheatreOfBloodRaid {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TheatreOfBloodRaid.class);
    public static Location outsideLocation = new Location(3677, 3217, 0);
    public static boolean TOB_ENABLED = true;
    public static TheatreRoom theatreRoom = TheatreRoom.THE_MAIDEN_OF_SUGADINTI;
    private final RaidingParty party;
    private final Map<TheatreRoom, TheatreArea> rooms;
    private final Set<String> spectators;
    private final Map<TheatreRoom, Integer> roomDurations;
    private boolean completed;

    public TheatreOfBloodRaid(final RaidingParty party) {
        this.party = party;
        rooms = new HashMap<>(2);
        spectators = new HashSet<>(2047);
        roomDurations = new HashMap<>(5);
    }

    @Subscribe
    public static final void onLogin(final PostWindowStatusEvent event) {
        final var player = event.getPlayer();
        if (!player.getAttributes().containsKey("last_tob_party_id")) {
            return;
        }
        final var id = player.getNumericAttribute("last_tob_party_id").intValue();
        player.getAttributes().remove("last_tob_party_id");
        final var party = VerSinhazaArea.getParty(id);
        /*
        GameInterface.TOB_PARTY.open(player);
        PartyOverlayInterface.fade(player, 0, 1, "Seeking party...");
        */
        WorldTasksManager.schedule(() -> {
            if (party == null || party.getRaid() == null || party.getRaid().isCompleted()) {
                player.setLocation(outsideLocation);
                PartyOverlayInterface.fade(player, 200, 0, "Unable to rejoin party.");
                if (player.getAttributes().containsKey("last_tob_safe")) {
                    boolean safe = player.getBooleanAttribute("last_tob_safe");
                    player.getAttributes().remove("last_tob_safe");
                    if (!safe) {
                        player.setLocation(TheatreOfBloodRaid.outsideLocation);
                        player.getDeathMechanics().service(ItemRetrievalService.RetrievalServiceType.THEATRE_OF_BLOOD, party != null ? party.getRaid().getActiveRoom().getBoss().get() : null);
                        player.sendMessage("A magical chest has retrieved some of your items. You can collect them from it outside the Theatre of Blood.");
                    }
                }
            } else {
                for (final var p : party.getOriginalMembers()) {
                    final var originalPlayer = World.getPlayer(p);
                    if (originalPlayer.isPresent() && originalPlayer.get().getUsername().equals(player.getUsername())) {
                        party.getRaid().onLogin(player);
                        return;
                    }
                }
            }
        }, 2);
    }

    public void onLogin(final Player player) {
        final var room = getActiveRoom();
        if (room == null) {
            return;
        }
        if (room.isCompleted() || !room.isStarted()) {
            player.setLocation(room.getVyreOrator().getPosition());
            party.getMembers().add(player.getUsername());
            party.getLifeStates().replace(player.getUsername(), "alive");
        } else {
            player.setLocation(room.getJailLocations()[room.getJailSpotsTaken()]);
            getActiveRoom().jailSpotsTaken++;
            party.getMembers().add(player.getUsername());
            party.getLifeStates().replace(player.getUsername(), "dead");
            player.getAttributes().put("tobpoints", Math.max(0, player.getNumericAttribute("tobpoints").intValue() - 4));
        }
        for (final var m : party.getMembers()) {
            final var member = RaidingParty.getPlayer(m);
            if (member == null) {
                continue;
            }
            party.initializeStatusHUD(member);
            party.updateStatusHUD(true);
        }
        PartyOverlayInterface.fade(player, 255, 0, "You have rejoined your party.");
    }

    public void enter(final Player member) {
        member.lock(4);
        member.getAttributes().put("tobpoints", 0);
        member.getAttributes().put("tobdeaths", 0);
        member.getAttributes().put("tobrefillpoints", 0);
        member.getAttributes().put("maidenbossdamage", 0);
        member.getAttributes().put("bloatbossdamage", 0);
        member.getAttributes().put("nylobossdamage", 0);
        member.getAttributes().put("sotebossdamage", 0);
        member.getAttributes().put("xarpusbossdamage", 0);
        member.getAttributes().put("verzikp1bossdamage", 0);
        member.getAttributes().put("verzikp2bossdamage", 0);
        member.getAttributes().put("verzikp3bossdamage", 0);
        member.putBooleanAttribute("PopItTask", true);
        member.getAttributes().put("soloBombTanks", 0);
        member.putBooleanAttribute("ATimelySnack", true);
        member.getAttributes().put("bloatShutDowns", 0);
        member.putBooleanAttribute("CantDrainThis", true);
        member.putBooleanAttribute("CanYouDance", true);
        member.putBooleanAttribute("PerfectMaiden", true);
        member.putBooleanAttribute("PerfectBloat", true);
        member.putBooleanAttribute("PerfectNylocas", true);
        member.putBooleanAttribute("PerfectSotetseg", true);
        member.putBooleanAttribute("PerfectXarpus", true);
        member.putBooleanAttribute("PerfectVerzik", true);
        member.putBooleanAttribute("BackInMyDay", true);
        member.putBooleanAttribute("MorytaniaOnly", true);
        member.getVarManager().sendVar(3806, 1);
        if (party.isLeader(member)) {
            final var room = theatreRoom;
            addRoom(room);
            advance(member, room);
            constructMap();
        }
        final var originalMembers = party.getOriginalMembers();
        if (!originalMembers.contains(member.getUsername())) {
            originalMembers.add(member.getUsername());
        }
        member.sendSound(new SoundEffect(1952));
        member.getPacketDispatcher().sendClientScript(2379);
        PartyOverlayInterface.fade(member, 0, 0, "The Theatre awaits...");
        WorldTasksManager.schedule(() -> {
            PartyOverlayInterface.fade(member, 255, 0, getActiveRoom().getRoom().getName());
            party.initializeStatusHUD(member);
            member.setLocation(getActiveRoom().getEntranceLocation());
            member.addTemporaryAttribute("tob_advancing_room", 0);
        }, 2);
    }

    public void advance(final Player player, final TheatreRoom room) {
        for (final var m : party.getMembers()) {
            final var member = RaidingParty.getPlayer(m);
            if (member == player) {
                continue;
            }
            if (room.equals(TheatreRoom.THE_MAIDEN_OF_SUGADINTI)) {
                member.getVarManager().sendBit(6440, 2);
                member.sendMessage(player.getName() + " has entered the Theatre of Blood. Step inside to join " + (player.getAppearance().isMale() ? "him..." : "her..."));
            } else {
                member.sendMessage(player.getName() + " has advanced to Wave " + room.getWave() + ". Join " + (player.getAppearance().isMale() ? "him..." : "her..."));
            }
        }
        player.addTemporaryAttribute("tob_advancing_room", 1);
        getActiveRoom().onLoad();
    }

    public TheatreArea getRoom(final TheatreRoom room) {
        return rooms.get(room);
    }

    public TheatreArea getActiveRoom() {
        if (rooms.isEmpty()) {
            return null;
        }
        TheatreArea area = null;
        var wave = 0;
        for (final var entry : rooms.entrySet()) {
            final var room = entry.getKey();
            if (room.getWave() > wave) {
                wave = room.getWave();
                area = entry.getValue();
            }
        }
        return area;
    }

    public void constructMap() {
        for (final var entry : rooms.entrySet()) {
            final var area = entry.getValue();
            if (area == null || area.isCompleted()) {
                continue;
            }
            area.constructRegion();
        }
    }

    public void complete() {
        if (this.completed) {
            return;
        }
        this.completed = true;
    }

    public void addRoom(final TheatreRoom room) {
        try {
            final var allocatedArea = MapBuilder.findEmptyChunk(room.getSizeX(), room.getSizeY());
            final var area = room.getClazz().getDeclaredConstructor(TheatreOfBloodRaid.class, AllocatedArea.class, TheatreRoom.class).newInstance(this, allocatedArea, room);
            rooms.put(room, area);
        } catch (Exception e) {
            log.error(Strings.EMPTY, e);
        }
    }

    public void removeRoom(final TheatreRoom room) {
        final var area = rooms.get(room);
        if (area == null) {
            return;
        }
        area.destroyRegion();
    }

    public RaidingParty getParty() {
        return this.party;
    }

    public Map<TheatreRoom, TheatreArea> getRooms() {
        return this.rooms;
    }

    public Set<String> getSpectators() {
        return this.spectators;
    }

    public Map<TheatreRoom, Integer> getRoomDurations() {
        return this.roomDurations;
    }

    public boolean isCompleted() {
        return this.completed;
    }
}
