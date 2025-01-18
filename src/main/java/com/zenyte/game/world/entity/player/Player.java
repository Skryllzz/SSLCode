package com.zenyte.game.world.entity.player;

import com.google.gson.annotations.Expose;
import com.zenyte.Constants;
import com.zenyte.api.client.query.SubmitPlayerInformation;
import com.zenyte.api.client.query.TotalDonatedRequest;
import com.zenyte.api.client.query.adventurerslog.AdventurersLogIcon;
import com.zenyte.api.client.query.adventurerslog.ApiAdventurersLogRequest;
import com.zenyte.api.client.query.hiscores.SendPlayerHiscores;
import com.zenyte.api.model.ExpMode;
import com.zenyte.api.model.Skill;
import com.zenyte.api.model.SkillHiscore;
import com.zenyte.cores.CoresManager;
import com.zenyte.game.BonusXpManager;
import com.zenyte.game.constants.GameConstants;
import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.content.AvasDevice;
import com.zenyte.game.content.GodBooks;
import com.zenyte.game.content.ItemRetrievalService;
import com.zenyte.game.content.RespawnPoint;
import com.zenyte.game.content.achievementdiary.AchievementDiaries;
import com.zenyte.game.content.boss.grotesqueguardians.instance.GrotesqueGuardiansInstance;
import com.zenyte.game.content.bountyhunter.BountyHunter;
import com.zenyte.game.content.chambersofxeric.Raid;
import com.zenyte.game.content.chambersofxeric.party.RaidParty;
import com.zenyte.game.content.chambersofxeric.storageunit.PrivateStorage;
import com.zenyte.game.content.clans.ClanChannel;
import com.zenyte.game.content.clans.ClanManager;
import com.zenyte.game.content.follower.Follower;
import com.zenyte.game.content.follower.PetInsurance;
import com.zenyte.game.content.follower.PetWrapper;
import com.zenyte.game.content.grandexchange.GrandExchange;
import com.zenyte.game.content.minigame.barrows.Barrows;
import com.zenyte.game.content.minigame.blastfurnace.BlastFurnace;
import com.zenyte.game.content.minigame.duelarena.Duel;
import com.zenyte.game.content.minigame.inferno.instance.Inferno;
import com.zenyte.game.content.multicannon.DwarfMulticannon;
import com.zenyte.game.content.preset.PresetManager;
import com.zenyte.game.content.sailing.CharterLocation;
import com.zenyte.game.content.skills.construction.Construction;
import com.zenyte.game.content.skills.construction.RoomReference;
import com.zenyte.game.content.skills.farming.Farming;
import com.zenyte.game.content.skills.farming.seedvault.SeedVault;
import com.zenyte.game.content.skills.hunter.Hunter;
import com.zenyte.game.content.skills.magic.spells.lunar.SpellbookSwap;
import com.zenyte.game.content.skills.magic.spells.teleports.ForceTeleport;
import com.zenyte.game.content.skills.magic.spells.teleports.Teleport;
import com.zenyte.game.content.skills.magic.spells.teleports.TeleportType;
import com.zenyte.game.content.skills.prayer.Prayer;
import com.zenyte.game.content.skills.prayer.PrayerManager;
import com.zenyte.game.content.skills.slayer.Slayer;
import com.zenyte.game.content.treasuretrails.clues.LightBox;
import com.zenyte.game.content.treasuretrails.clues.PuzzleBox;
import com.zenyte.game.content.treasuretrails.stash.Stash;
import com.zenyte.game.content.wheeloffortune.WheelOfFortune;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.item.SkillcapePerk;
import com.zenyte.game.item.containers.GemBag;
import com.zenyte.game.item.containers.HerbSack;
import com.zenyte.game.item.degradableitems.ChargesManager;
import com.zenyte.game.item.degradableitems.DegradeType;
import com.zenyte.game.packet.GamePacketEncoder;
import com.zenyte.game.packet.PacketDispatcher;
import com.zenyte.game.packet.Session;
import com.zenyte.game.packet.out.*;
import com.zenyte.game.polls.PollManager;
import com.zenyte.game.shop.Shop;
import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.ui.InterfaceHandler;
import com.zenyte.game.ui.testinterfaces.advancedsettings.SettingVariables;
import com.zenyte.game.ui.testinterfaces.advancedsettings.SettingsInterface;
import com.zenyte.game.util.Colour;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.TimeUnit;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.Position;
import com.zenyte.game.world.SceneSynchronization;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.*;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.masks.*;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.pathfinding.Flags;
import com.zenyte.game.world.entity.pathfinding.events.RouteEvent;
import com.zenyte.game.world.entity.pathfinding.strategy.EntityStrategy;
import com.zenyte.game.world.entity.player.action.combat.CombatType;
import com.zenyte.game.world.entity.player.action.combat.CombatUtilities;
import com.zenyte.game.world.entity.player.action.combat.PlayerCombat;
import com.zenyte.game.world.entity.player.collectionlog.CollectionLog;
import com.zenyte.game.world.entity.player.container.Container;
import com.zenyte.game.world.entity.player.container.ContainerWrapper;
import com.zenyte.game.world.entity.player.container.impl.*;
import com.zenyte.game.world.entity.player.container.impl.bank.BankSetting;
import com.zenyte.game.world.entity.player.container.impl.bank.SinglePlayerBank;
import com.zenyte.game.world.entity.player.container.impl.death.DeathMechanics;
import com.zenyte.game.world.entity.player.container.impl.equipment.Equipment;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentUtils;
import com.zenyte.game.world.entity.player.controller.ControllerManager;
import com.zenyte.game.world.entity.player.cutscene.CutsceneManager;
import com.zenyte.game.world.entity.player.dailychallenge.DailyChallengeManager;
import com.zenyte.game.world.entity.player.dialogue.DialogueManager;
import com.zenyte.game.world.entity.player.login.Authenticator;
import com.zenyte.game.world.entity.player.loyalty.LoyaltyManager;
import com.zenyte.game.world.entity.player.perk.PerkManager;
import com.zenyte.game.world.entity.player.perk.PerkWrapper;
import com.zenyte.game.world.entity.player.punishments.PunishmentManager;
import com.zenyte.game.world.entity.player.punishments.PunishmentType;
import com.zenyte.game.world.entity.player.teleportsystem.TeleportManager;
import com.zenyte.game.world.entity.player.update.NPCInfo;
import com.zenyte.game.world.entity.player.update.PlayerInfo;
import com.zenyte.game.world.entity.player.var.EventType;
import com.zenyte.game.world.entity.player.var.VarCollection;
import com.zenyte.game.world.entity.player.variables.PlayerVariables;
import com.zenyte.game.world.entity.player.variables.TickVariable;
import com.zenyte.game.world.flooritem.FloorItem;
import com.zenyte.game.world.region.*;
import com.zenyte.game.world.region.area.TutorialIslandArea;
import com.zenyte.game.world.region.area.plugins.*;
import com.zenyte.game.world.region.area.wilderness.WildernessArea;
import com.zenyte.network.game.packet.GamePacketOut;
import com.zenyte.plugins.MethodicPluginHandler;
import com.zenyte.plugins.PluginManager;
import com.zenyte.plugins.dialogue.CountDialogue;
import com.zenyte.plugins.dialogue.ItemDialogue;
import com.zenyte.plugins.dialogue.NameDialogue;
import com.zenyte.plugins.dialogue.StringDialogue;
import com.zenyte.plugins.events.LoginEvent;
import com.zenyte.plugins.events.LogoutEvent;
import com.zenyte.plugins.events.PlayerResetEvent;
import com.zenyte.processor.Listener.ListenerType;
import com.zenyte.utils.FieldModifiersHelper;
import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import mgi.types.config.AnimationDefinitions;
import mgi.types.config.TransmogrifiableType;
import mgi.types.config.items.ItemDefinitions;
import mgi.types.config.npcs.NPCDefinitions;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Queue;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import static com.zenyte.game.ui.testinterfaces.advancedsettings.SettingVariables.*;
import static com.zenyte.game.world.entity.player.Emote.GIVE_THANKS_VARP;

/**
 * @author Kris | 29. dets 2017 : 3:52.50
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
@SuppressWarnings("FieldMayBeFinal")
public class Player extends Entity {
    public static final int SCENE_DIAMETER = 104;
    public static final int SCENE_RADIUS = SCENE_DIAMETER >> 1;
    public static final int SMALL_VIEWPORT_RADIUS = 15;
    public static final int LARGE_VIEWPORT_RADIUS = 127;
    public static final Animation DEATH_ANIMATION = new Animation(836);
    private static final Logger log = LoggerFactory.getLogger(Player.class);
    private static final ForceTalk VENGEANCE = new ForceTalk("Taste vengeance!");
    private static final HitType[] PROCESSED_HIT_TYPES = new HitType[]{HitType.MELEE, HitType.RANGED, HitType.MAGIC, HitType.DEFAULT};
    private static final Graphics ELYSIAN_EFFECT_GFX = new Graphics(321);
    private static final Graphics BULWARK_GFX = new Graphics(1336);
    private static final Animation BULWARK_ANIM = new Animation(7512);
    private static final Animation PLAIN_DEFENCE_ANIM = new Animation(424);
    private static final int[] maleDamageSounds = new int[]{518, 519, 521};
    private static final int[] femaleDamageSounds = new int[]{509, 510};
    private static final Animation candyCaneBlockAnimation = new Animation(15086);
    private static final Animation easterCarrotBlockAnimation = new Animation(15162);
    private static final Calendar thanksgivingStart;
    private static final Calendar thanksgivingEnd;
    private static String[] deathMessages = new String[]{"You have defeated %s.", "You were clearly a better fighter than %s.", "%s was no match for you.", "With a crushing blow you finish %s.", "%s didn't stand a chance against you.", "Can anyone defeat you? Certainly not %s.", "%s falls before your might.", "A humiliating defeat for %s.", "What was %s thinking challenging you...", "What an embarrassing performance by %s.", "RIP %s.", "%s probably logged out after that beating.", "Such a shame that %s can't play this game.", "How not to do it right: Written by %s.", "A certain, crouching-over-face animation would be suitable for %s right now.", "%s got rekt.", "%s was made to sit down.", "The struggle for %s is real.", "MUM! GET THE CAMERA, I JUST KILLED %s!", "%s will probably tell you %gender% wanted a free teleport after that performance.", //he/she
            "%s should take lessons from you. You're clearly too good for %gender%."};

    static {
        thanksgivingStart = Calendar.getInstance();
        thanksgivingEnd = Calendar.getInstance();
        thanksgivingStart.set(2019, Calendar.NOVEMBER, 28);
        thanksgivingEnd.set(2019, Calendar.DECEMBER, 8);
    }

    public transient Runnable closeInterfacesEvent;
    //him/her
    private AchievementDiaries achievementDiaries = new AchievementDiaries(this);
    private transient CutsceneManager cutsceneManager = new CutsceneManager(this);
    private transient PuzzleBox puzzleBox = new PuzzleBox(this);
    private transient LightBox lightBox = new LightBox(this);
    private transient ChargesManager chargesManager = new ChargesManager(this);
    private transient PollManager pollManager = new PollManager(this);
    private transient AreaManager areaManager = new AreaManager(this);
    private GodBooks godBooks = new GodBooks();
    @Expose
    private BossTimer bossTimer = new BossTimer(this);
    private CollectionLog collectionLog = new CollectionLog(this);
    private transient DialogueManager dialogueManager = new DialogueManager(this);
    @Expose
    private Map<String, Object> attributes = new ConcurrentHashMap<>();
    @Expose
    private ControllerManager controllerManager = new ControllerManager(this);
    @Expose
    private MusicHandler music = new MusicHandler(this);
    private PresetManager presetManager = new PresetManager(this);
    @Expose
    private EmotesHandler emotesHandler = new EmotesHandler(this);
    @Expose
    private InterfaceHandler interfaceHandler = new InterfaceHandler(this);
    private BountyHunter bountyHunter = new BountyHunter(this);
    private List<Integer> trackedHolidayItems = new IntArrayList();
    @Expose
    private Appearance appearance = new Appearance(this);
    private transient Set<Container> pendingContainers = new LinkedHashSet<>();
    @Expose
    private SocialManager socialManager = new SocialManager(this);
    @Expose
    private CombatDefinitions combatDefinitions = new CombatDefinitions(this);
    @Expose
    private DwarfMulticannon dwarfMulticannon = new DwarfMulticannon(this);
    @Expose
    private Equipment equipment = new Equipment(this);
    @Expose
    private Inventory inventory = new Inventory(this);
    private transient DeathMechanics deathMechanics = new DeathMechanics(this);
    @Expose
    private NotificationSettings notificationSettings = new NotificationSettings(this);
    @Expose
    private PriceChecker priceChecker = new PriceChecker(this);
    @Expose
    private transient Trade trade = new Trade(this);
    private SeedVault seedVault = new SeedVault(this);
    @Expose
    private RunePouch runePouch = new RunePouch(this);
    private RunePouch secondaryRunePouch = new RunePouch(this);
    private SeedBox seedBox = new SeedBox(this);
    private LootingBag lootingBag = new LootingBag(this);
    private HerbSack herbSack = new HerbSack(this);
    private GemBag gemBag = new GemBag(this);
    @Expose
    private Skills skills = new Skills(this);
    @Expose
    private Settings settings = new Settings(this);
    @Expose
    private Construction construction = new Construction(this);
    @Expose
    private PrayerManager prayerManager = new PrayerManager(this);
    @Expose
    private TeleportManager teleportManager = new TeleportManager(this);
    private VarManager varManager = new VarManager(this);
    private transient PlayerInfo playerViewport = new PlayerInfo(this);
    private transient NPCInfo npcViewport = new NPCInfo(this);
    @Expose
    private PlayerVariables variables = new PlayerVariables(this);
    private transient Set<Player> botObservers = new ObjectOpenHashSet<>();
    private transient WorldMap worldMap = new WorldMap(this);
    @Expose
    private GrandExchange grandExchange = new GrandExchange(this);
    private transient Bonuses bonuses = new Bonuses(this);
    private transient String[] options = new String[9];
    private transient Object2LongOpenHashMap<String> attackedByPlayers = new Object2LongOpenHashMap<>();
    private PerkManager perkManager = new PerkManager(this);
    private transient ChatMessage chatMessage = new ChatMessage();
    private transient ChatMessage clanMessage = new ChatMessage();
    private Barrows barrows = new Barrows(this);
    private ItemRetrievalService retrievalService = new ItemRetrievalService(this);
    private transient boolean needRegionUpdate;
    private transient boolean running;
    private transient List<ProjPacket> tempList = new ArrayList<>();
    private transient ActionManager actionManager = new ActionManager(this);
    @Expose
    private PrivateStorage privateStorage = new PrivateStorage(this);
    @Expose
    private PlayerInformation playerInformation;
    private transient Entity lastTarget;
    private transient DelayedActionManager delayedActionManager = new DelayedActionManager(this);
    @Expose
    private Farming farming = new Farming(this);
    private transient PacketDispatcher packetDispatcher = new PacketDispatcher(this);
    private PetInsurance petInsurance = new PetInsurance(this);
    @Expose
    private transient Follower follower;
    private int petId;
    private transient boolean canPvp;
    @Expose
    private Stash stash = new Stash(this);
    private transient boolean maximumTolerance;
    private transient Duel duel;
    @Expose
    private SinglePlayerBank bank = new SinglePlayerBank(this);
    private transient boolean forceReloadMap;
    private transient int viewDistance = 15;
    private Slayer slayer = new Slayer(this);
    private Hunter hunter = new Hunter(this);
    private BlastFurnace blastFurnace = new BlastFurnace(this);
    @Expose
    private RespawnPoint respawnPoint = RespawnPoint.EDGEVILLE;
    private DailyChallengeManager dailyChallengeManager = new DailyChallengeManager(this);
    private transient Optional<GrotesqueGuardiansInstance> grotesqueGuardiansInstance;
    private transient int pid;
    private transient boolean loadingRegion;
    private transient long movementLock;
    private transient long diceDelay;
    private transient String[] nametags;
    @Expose
    private GameMode gameMode = GameMode.REGULAR;
    private MemberRank memberRank = MemberRank.NONE;
    private ExperienceMode experienceMode = ExperienceMode.TIMES_10;
    @Expose
    private Privilege privilege = Privilege.PLAYER;
    private transient long lastDisconnectionTime;
    private transient boolean loggedOut;
    private WheelOfFortune wheelOfFortune = new WheelOfFortune(this);
    private transient int logoutCount;
    private transient boolean updatingNPCOptions = true;
    private transient boolean updateNPCOptions;
    private transient IntLinkedOpenHashSet pendingVars = new IntLinkedOpenHashSet(100);
    private transient Runnable pathfindingEvent;
    private transient RouteEvent<Player, EntityStrategy> combatEvent;
    private transient int hashcode;
    private transient Rectangle sceneRectangle;
    private transient Int2ObjectOpenHashMap<List<GamePacketEncoder>> zoneFollowPackets = new Int2ObjectOpenHashMap<>();
    private transient boolean heatmap;
    private transient IntOpenHashSet chunksInScope = new IntOpenHashSet(SceneSynchronization.CHUNK_SYNCHRONIZATION_MAX_COUNT);
    private transient int heatmapRenderDistance = SMALL_VIEWPORT_RADIUS;
    private transient boolean hidden;
    private transient int damageSound = -1;
    private IntArrayList paydirt = new IntArrayList();
    private LoyaltyManager loyaltyManager = new LoyaltyManager(this);
    private transient long lastReceivedPacket = System.currentTimeMillis();
    private Authenticator authenticator;
    private transient List<Runnable> postPacketProcessingRunnables = new LinkedList<>();
    private String lastIP;
    private String lastMAC;
    private transient int lastWalkX;
    private transient int lastWalkY;
    private transient List<MovementLock> movementLocks = new LinkedList<>();
    private transient Runnable postSaveFunction;
    private transient boolean nulled;
    private Queue<Location> tolerancePositionQueue = new LinkedList<>();
    private transient PlayerLogger logger = new PlayerLogger(this);

    public Player(final PlayerInformation information, final Authenticator authenticator) {
        this.authenticator = authenticator == null ? new Authenticator() : authenticator;
        forceLocation(new Location(GameConstants.REGISTRATION_LOCATION));
        setLastLocation(new Location(getLocation()));
        playerInformation = information;
        getUpdateFlags().flag(UpdateFlag.APPEARANCE);
        getUpdateFlags().flag(UpdateFlag.TEMPORARY_MOVEMENT_TYPE);
        setTeleported(true);
        this.hashcode = information.getUsername().hashCode();
    }

    public void addPostProcessRunnable(@NotNull final Runnable runnable) {
        postPacketProcessingRunnables.add(runnable);
    }

    public void addMovementLock(final MovementLock lock) {
        movementLocks.add(lock);
    }

    public void removeAllMovementLocks() {
        movementLocks.clear();
    }
    public boolean isFullMovementLocked() {
        if (movementLocks.isEmpty()) {
            return false;
        }
        for (MovementLock next : movementLocks) {
            if (!next.isFullLock()) {
                continue;
            }
            if (!next.canWalk(this, false)) {
                return true;
            }
        }
        return false;
    }

    public boolean isMovementLocked(final boolean executeAttachments) {
        if (movementLocks.isEmpty()) {
            return false;
        }
        final Iterator<MovementLock> iterator = movementLocks.iterator();
        while (iterator.hasNext()) {
            final MovementLock next = iterator.next();
            if (!next.canWalk(this, executeAttachments)) {
                return true;
            }
            iterator.remove();
        }
        return false;
    }

    public boolean canHit(final Player other) {
        if (!other.isCanPvp() || !isCanPvp()) {
            return false;
        }
        final OptionalInt level = WildernessArea.getWildernessLevel(getLocation());
        if (level.isPresent()) {
            return Math.abs(getSkills().getCombatLevel() - other.getSkills().getCombatLevel()) <= level.getAsInt();
        }
        return true;
    }

    public void resetViewDistance() {
        this.viewDistance = SMALL_VIEWPORT_RADIUS;
    }

    public final Session getSession() {
        return getPlayerInformation().getSession();
    }

    public Area getArea() {
        return areaManager.getArea();
    }

    public boolean isOnMobile() {
        return playerInformation.isOnMobile();
    }

    public boolean updateNPCOptions(final NPC npc) {
        final NPCDefinitions definitions = NPCDefinitions.get(getTransmogrifiedId(npc.getDefinitions(), npc.getId()));
        if (definitions == null) return false;
        return definitions.getFilterFlag() > 0;
    }

    public void teleport(@NotNull final Location location) {
        new ForceTeleport(location).teleport(this);
    }

    /**
     * Checks whether the player is tolerant to the entities around.
     *
     * @return whether it's tolerant or not.
     */
    public final boolean isTolerant(@NotNull final Location tile) {
        return this.variables.getToleranceTimer() > TimeUnit.MINUTES.toTicks(10) && inTolerantPosition(tile);
    }

    /**
     * Sets the force movement for the player for the teleportation-type. This means that the player will be teleported to the end destination right as the force movement starts, and the
     * force-movement rewinds the player to the start location client-sided and rolls from the start. To the user, this is seamless and not visible.
     *
     * @param tile                  the tile to which the player is force-moved.
     * @param direction             the direction value which the player will be facing throughout the force movement; If absent, default face direction of where the player moves to is calculated.
     * @param delayInTicks          the delay in ticks until the player starts the force movement action - a value of 0 would mean instant start.
     * @param speedInTicks          the delay in ticks for how long the player will be moved through the force movement. Minimum value is 1!
     * @param startConsumer         the optional consumer that is executed instantly as the method is executed, this will not wait for the force movement to begin.
     * @param movementStartConsumer the optional consumer that is executed as soon as the force movement itself begins.
     * @param endConsumer           the consumer that is executed when the player finishes the force movement.
     */
    public void setTeleportForceMovement(@NotNull final Location tile, @NotNull final OptionalInt direction, final int delayInTicks, final int speedInTicks, @NotNull final Optional<Consumer<Location>> startConsumer, @NotNull final Optional<Consumer<Location>> movementStartConsumer, @NotNull final Optional<Consumer<Location>> endConsumer) {
        if (speedInTicks < 1) {
            throw new IllegalStateException("Speed must always be positive.");
        }
        startConsumer.ifPresent(consumer -> consumer.accept(tile));
        WorldTasksManager.scheduleOrExecute(() -> {
            movementStartConsumer.ifPresent(consumer -> consumer.accept(tile));
            final Location currentTile = new Location(getLocation());
            setForceMovement(new ForceMovement(new Location(getLocation()), 0, tile, speedInTicks * 30, direction.orElse(Utils.getFaceDirection(tile.getX() - currentTile.getX(), tile.getY() - currentTile.getY()))));
            setLocation(tile);
            endConsumer.ifPresent(consumer -> WorldTasksManager.scheduleOrExecute(() -> consumer.accept(tile), speedInTicks - 1));
        }, delayInTicks - 1);
    }

    public void autoForceMovement(final Location tile, final int speed) {
        final Location currentTile = new Location(getLocation());
        setLocation(tile);
        final ForceMovement fm = new ForceMovement(currentTile, 1, tile, speed, Utils.getFaceDirection(tile.getX() - currentTile.getX(), tile.getY() - currentTile.getY()));
        setForceMovement(fm);
    }

    public void autoForceMovement(final Location tile, final int delay, final int totalDuration, final int direction) {
        /*if ((totalDuration) % 30 != 0) {
            throw new RuntimeException("Unable to synchronize players location with forcemovement due to" + " delay and duration not being in synchronization with game ticks.");
        }
        if (delay == totalDuration) {
            throw new RuntimeException("Delay cannot be equal to speed.");
        }*/
        final Location currentTile = new Location(getLocation());
        final ForceMovement fm = new ForceMovement(currentTile, delay, tile, totalDuration, direction);
        setForceMovement(fm);
        WorldTasksManager.schedule(() -> setLocation(tile), (int) Math.ceil(totalDuration / 30.0F) - 1);
    }

    public void autoForceMovement(final Location tile, final int delay, final int totalDuration) {
        final Location currentTile = new Location(getLocation());
        final int direction = Utils.getFaceDirection(tile.getX() - currentTile.getX(), tile.getY() - currentTile.getY());
        autoForceMovement(tile, delay, totalDuration, direction);
    }

    public boolean eligibleForShiftTeleportation() {
        return privilege.eligibleTo(Privilege.SPAWN_ADMINISTRATOR) || (privilege.eligibleTo(Privilege.ADMINISTRATOR) && !(getArea() instanceof Inferno));
    }

    public void setNametag(final int index, final String string) {
        if (index < 0 || index >= 3) {
            return;
        }
        if (nametags == null) {
            nametags = new String[3];
        }
        nametags[index] = string;
        updateFlags.flag(UpdateFlag.NAMETAG);
    }

    public void resetNametags() {
        nametags = null;
        updateFlags.flag(UpdateFlag.NAMETAG);
    }

    /**
     * Gets the player's current display name.
     *
     * @return current display name.
     */
    public String getName() {
        return playerInformation.getDisplayname();
    }

    public boolean inArea(@NotNull final Class<? extends Area> clazz) {
        return inArea(GlobalAreaManager.getArea(clazz).name());
    }

    public boolean inArea(final String areaName) {
        final Area area = areaManager.getArea();
        if (area == null) {
            return false;
        }
        Area superArea = area;
        final String name = areaName.toLowerCase();
        final Location location = getLocation();
        while (true) {
            if (superArea.inside(location) && name.equals(superArea.name().toLowerCase())) {
                return true;
            }
            superArea = superArea.getSuperArea();
            if (superArea == null) {
                return false;
            }
        }
    }

    @Override
    public boolean isFrozen() {
        return super.isFrozen() && getTemporaryAttributes().get("ignoreWalkingRestrictions") == null;
    }

    public boolean isUnderCombat() {
        return (getAttackedByDelay() + 4200) > Utils.currentTimeMillis();
    }

    public boolean isUnderCombat(final int ticksAfterLastAttack) {
        return (getAttackedByDelay() + (ticksAfterLastAttack * 600L)) > Utils.currentTimeMillis();
    }

    public void setDefaultSettings() {
        settings.setSettingNoRefresh(Setting.AUTO_MUSIC, 1);
        setQuestPoints(250);
        attributes.put("LEVEL_99_DIALOGUES", 75);
        attributes.put("ALCHEMY_WARNING_VALUE", 30000);
        attributes.put("RING_OF_RECOIL", 40);
        attributes.put("RING_OF_FORGING", 140);
        attributes.put("checking combat in slayer", 1);
        attributes.put("recoil effect", 1);
        attributes.put("looting_bag_amount_prompt", 1);
        attributes.put("first_99_skill", -1);
        attributes.put("quest_points", 250);//To unlock slayer rewards.
        for (final GameSetting setting : GameSetting.ALL) {
            if (setting == GameSetting.YELL_FILTER || setting == GameSetting.ALWAYS_SHOW_LATEST_UPDATE || setting == GameSetting.EXAMINE_NPCS_DROP_VIEWER) {
                continue;
            }
            attributes.put(setting.toString(), 1);
        }
        SettingsInterface.setDefaultKeybinds(this);

        bank.toggleSetting(BankSetting.DEPOSIT_INVENTOY_ITEMS, true);
        varManager.sendVar(SettingVariables.PLAYER_ATTACK_OPTIONS_VARP_ID, 2);
        varManager.sendVar(SettingVariables.NPC_ATTACK_OPTIONS_VARP_ID, 2);
        varManager.sendBit(SettingsInterface.COLLECTION_LOG_NEW_ADDITIONS_VARBIT_ID, 3);
        varManager.sendBit(ESC_CLOSES_THE_CURRENT_INTERFACE_VARBIT_ID, 1);

        varManager.sendBit(SHOW_WARNING_WHEN_CASTING_TELEPORT_TO_TARGET_VARBIT_ID, 1);
        varManager.sendBit(SHOW_WARNING_WHEN_CASTING_DAREEYAK_TELEPORT_VARBIT_ID, 1);
        varManager.sendBit(SHOW_WARNING_WHEN_CASTING_CARRALLANGAR_TELEPORT_VARBIT_ID, 1);
        varManager.sendBit(SHOW_WARNING_WHEN_CASTING_ANNAKARL_TELEPORT_VARBIT_ID, 1);
        varManager.sendBit(SHOW_WARNING_WHEN_CASTING_GHORROCK_TELEPORT_VARBIT_ID, 1);
    }

    @Override
    protected void processHit(final Hit hit) {
        if (hit.getScheduleTime() < protectionDelay) {
            return;
        }
        if (isImmune(hit.getHitType())) {
            hit.setDamage(0);
        }
        final Action action = actionManager.getAction();
        if (action != null && action.interruptedByCombat()) {
            actionManager.forceStop();
        }
        if (hit.getDamage() > Short.MAX_VALUE) {
            hit.setDamage(Short.MAX_VALUE);
        }
        getUpdateFlags().flag(UpdateFlag.HIT);
        nextHits.add(hit);
        if (hitBars.isEmpty()) {
            hitBars.add(hitBar);
        }
        final HitType type = hit.getHitType();
        if (type == HitType.DISEASED) {
            return;
        }
        if (type == HitType.HEALED) {
            heal(hit.getDamage());
        } else {
            removeHitpoints(hit);
        }
    }

    public void sendAdventurersEntry(final AdventurersLogIcon icon, final String message) {
        sendAdventurersEntry(icon.getLink(), message, false);
    }

    public void sendAdventurersEntry(final String icon, final String message, final boolean pvp) {
        if (!Constants.WORLD_PROFILE.getApi().isEnabled() || Constants.WORLD_PROFILE.isBeta() || Constants.WORLD_PROFILE.isPrivate() || Constants.WORLD_PROFILE.isDevelopment()) {
            return;
        }
        CoresManager.getServiceProvider().submit(() -> new ApiAdventurersLogRequest(Player.this, icon, message).execute());
    }

    public void refreshDirection() {
        if (faceEntity >= 0) {
            final Entity target = faceEntity >= 32768 ? World.getPlayers().get(faceEntity - 32768) : World.getNPCs().get(faceEntity);
            if (target != null) {
                direction = Utils.getFaceDirection(target.getLocation().getCoordFaceX(target.getSize()) - getX(), target.getLocation().getCoordFaceY(target.getSize()) - getY());
            }
        }
    }

    @Override
    public void processMovement() {
        refreshDirection();
        walkDirection = runDirection = crawlDirection = -1;
        final Area area = getArea();
        if (nextLocation != null) {
            final Area nextArea = GlobalAreaManager.getArea(nextLocation);
            if (nextArea instanceof TeleportMovementPlugin plugin) {
                if (!plugin.canTeleport(this, nextLocation)) {
                    teleported = false;
                    nextLocation = null;
                    return;
                }
            }
            if (lastLocation == null) {
                lastLocation = new Location(location);
            } else {
                lastLocation.setLocation(location);
            }
            unclip();
            LocationMap.remove(this);
            if (area instanceof TeleportMovementPlugin) {
                ((TeleportMovementPlugin) area).processMovement(this, nextLocation);
            }
            forceLocation(nextLocation);
            clip();
            LocationMap.add(this);
            nextLocation = null;
            updateFlags.flag(UpdateFlag.TEMPORARY_MOVEMENT_TYPE);
            teleported = true;
            refreshToleranceRectangle();
            World.updateEntityChunk(this, false);
            controllerManager.teleport(location);
            farming.refresh();
            if (interfaceHandler.isVisible(GameInterface.WORLD_MAP.getId())) {
                worldMap.updateLocation();
            }
            if (needMapUpdate()) {
                setNeedRegionUpdate(true);
                setLoadingRegion(true);
            }
            resetWalkSteps();
            return;
        }
        teleported = false;
        if (walkSteps.isEmpty()) {
            return;
        }
        if (isDead()) {
            return;
        }
        if (lastLocation == null) {
            lastLocation = new Location(location);
        } else {
            lastLocation.setLocation(location);
        }
        lastWalkX = 0;
        lastWalkY = 0;
        if (isRun() && !isSilentRun()) {
            int runStep = walkSteps.size() > 2 ? walkSteps.nthPeek(2) : 0;
            if (runStep != 0 && WalkStep.getDirection(runStep) != -1) {
                double energyLost = ((Math.min(inventory.getWeight() + equipment.getWeight(), 64) / 100) + 0.64);
                if (Constants.PRIVATE_BETA) {
                    //Run energy depletion lowered by 5x during private beta.
                    energyLost /= 5.0F;
                }
                if (variables.getTime(TickVariable.STAMINA_ENHANCEMENT) > 0) {
                    energyLost *= 0.3;
                }
                if (variables.getTime(TickVariable.HAMSTRUNG) > 0) {
                    energyLost *= 6;
                }
                if (!inArea("Wilderness")) {
                    if (memberRank.eligibleTo(MemberRank.ONYX_MEMBER)) {
                        energyLost *= 0.8F;
                    } else if (memberRank.eligibleTo(MemberRank.DRAGONSTONE_MEMBER)) {
                        energyLost *= 0.85F;
                    } else if (memberRank.eligibleTo(MemberRank.RUBY_MEMBER)) {
                        energyLost *= 0.9F;
                    } else if (memberRank.eligibleTo(MemberRank.SAPPHIRE_MEMBER)) {
                        energyLost *= 0.95F;
                    }
                }
                if (variables.getRunEnergy() >= 0) {
                    variables.forceRunEnergy(Math.max(0, variables.getRunEnergy() - energyLost));
                    if (variables.getRunEnergy() == 0) {
                        setRun(false);
                        varManager.sendVar(173, 0);
                    }
                }
            }
        }
        int steps = Math.min(silentRun ? 1 : run ? 2 : 1, walkSteps.size());
        int stepCount;
        for (stepCount = 0; stepCount < steps; stepCount++) {
            final int nextStep = getNextWalkStep();
            if (nextStep == 0) {
                break;
            }
            final int dir = WalkStep.getDirection(nextStep);
            if ((WalkStep.check(nextStep) && !World.checkWalkStep(getPlane(), getX(), getY(), dir, getSize(), false, true))) {
                resetWalkSteps();
                break;
            }
            final int x = Utils.DIRECTION_DELTA_X[dir];
            final int y = Utils.DIRECTION_DELTA_Y[dir];
            if (area instanceof FullMovementPlugin) {
                if (!((FullMovementPlugin) area).processMovement(this, getX() + x, getY() + y)) {
                    break;
                }
            }
            if (stepCount == 0) {
                walkDirection = dir;
                lastWalkX = -x;
                lastWalkY = -y;
            } else {
                runDirection = dir;
            }
            controllerManager.move((getWalkSteps().size() > 0 && steps == 2) ? stepCount == 1 : stepCount == 0, getX() + x, getY() + y);
            unclip();
            LocationMap.remove(this);
            location.moveLocation(x, y, 0);
            clip();
            LocationMap.add(this);
            if (interfaceHandler.isVisible(GameInterface.WORLD_MAP.getId())) {
                worldMap.updateLocation();
            }
        }
        if (area instanceof PartialMovementPlugin) {
            if (!(area instanceof FullMovementPlugin)) {
                ((PartialMovementPlugin) area).processMovement(this, getX(), getY());
            }
        }
        final int type = runDirection == -1 ? 1 : 2;
        if (type != lastMovementType) {
            if (stepCount == 1 && run) {
                updateFlags.flag(UpdateFlag.TEMPORARY_MOVEMENT_TYPE);
            } else {
                lastMovementType = type;
                updateFlags.flag(UpdateFlag.MOVEMENT_TYPE);
            }
        }
        if (faceEntity < 0) {
            direction = Utils.getFaceDirection(location.getX() - lastLocation.getX(), location.getY() - lastLocation.getY());
        }
        refreshToleranceRectangle();
        World.updateEntityChunk(this, false);//TODO check why double.
        farming.refresh();
        if (needMapUpdate()) {
            setNeedRegionUpdate(true);
            setLoadingRegion(true);
        }
    }

    private boolean inTolerantPosition(final Location t) {
        for (final Location tile : tolerancePositionQueue) {
            if (tile.withinDistance(t, 10)) {
                return true;
            }
        }
        return false;
    }

    private void refreshToleranceRectangle() {
        if (inTolerantPosition(getLocation())) {
            return;
        }
        if (tolerancePositionQueue.size() >= 2) {
            //Remove the earliest tolerance position.
            tolerancePositionQueue.poll();
        }
        //Add a new position to the tolerance queue.
        tolerancePositionQueue.add(new Location(getLocation()));
        //And every time the player's tolerance position(s) change, we reset the timer again.
        variables.setToleranceTimer(0);
    }

    public void logout(final boolean force) {
        if (!force) {
            if (!isRunning()) {
                return;
            }
            if (isLocked()) {
                this.sendMessage("You can't log out while performing an action.");
                return;
            } else if (isUnderCombat()) {
                this.sendMessage("You can't log out until 10 seconds after the end of combat.");
                return;
            }
        }
        packetDispatcher.sendLogout();
    }

    public void sendInputString(final String question, final StringDialogue dialogue) {
        packetDispatcher.sendClientScript(110, question);
        temporaryAttributes.put("interfaceInput", dialogue);
    }

    public void sendInputName(final String question, final NameDialogue dialogue) {
        packetDispatcher.sendClientScript(109, question);
        temporaryAttributes.put("interfaceInput", dialogue);
    }

    public void sendInputInt(final String question, final CountDialogue dialogue) {
        packetDispatcher.sendClientScript(108, question);
        temporaryAttributes.put("interfaceInput", dialogue);
    }

    public void sendInputItem(final String question, final ItemDialogue dialogue) {
        packetDispatcher.sendClientScript(750, question, 1, -1);
        temporaryAttributes.put("interfaceInput", dialogue);
    }

    public Construction getCurrentHouse() {
        final Object object = getTemporaryAttributes().get("VisitingHouse");
        if (!(object instanceof Construction)) {
            return null;
        }
        return (Construction) object;
    }

    @Override
    public void reset() {
        try {
            try {
                super.reset();
            } catch (Exception e) {
                log.error("", e);
            }
            try {
                for (int i = 0; i < 23; i++) {
                    skills.setLevel(i, skills.getLevelForXp(i));
                }
            } catch (Exception e) {
                log.error("", e);
            }
            try {
                attackedByPlayers.clear();
            } catch (Exception e) {
                log.error("", e);
            }
            try {
                PluginManager.post(new PlayerResetEvent(this));
            } catch (Exception e) {
                log.error("", e);
            }
            try {
                toxins.reset();
            } catch (Exception e) {
                log.error("", e);
            }
            try {
                attributes.remove("vengeance");
            } catch (Exception e) {
                log.error("", e);
            }
            try {
                variables.resetScheduled();
            } catch (Exception e) {
                log.error("", e);
            }
            try {
                prayerManager.deactivateActivePrayers();
            } catch (Exception e) {
                log.error("", e);
            }
            try {
                variables.setRunEnergy(100);
            } catch (Exception e) {
                log.error("", e);
            }
            try {
                combatDefinitions.setSpecial(false, true);
            } catch (Exception e) {
                log.error("", e);
            }
            try {
                setAttackedByDelay(0);
                setAttackingDelay(0);
            } catch (Exception e) {
                log.error("", e);
            }
            try {
                resetFreeze();
            } catch (Exception e) {
                log.error("", e);
            }
        } catch (Exception e) {
            log.error("", e);
        }
    }

    public void setLunarDelay(final long delay) {
        getTemporaryAttributes().put("spellDelay", Utils.currentTimeMillis() + delay);
    }

    @Override
    public List<Entity> getPossibleTargets(final EntityType type) {
        if (!possibleTargets.isEmpty()) {
            possibleTargets.clear();
        }
        CharacterLoop.populateEntityList(possibleTargets, this.getLocation(), 15, type.getClazz(), this::isPotentialTarget);
        return possibleTargets;
    }

    @Override
    protected boolean isAcceptableTarget(final Entity entity) {
        return true;
    }

    @Override
    protected boolean isPotentialTarget(final Entity entity) {
        final int entityX = entity.getX();
        final int entityY = entity.getY();
        final int entitySize = entity.getSize();
        final int x = getX();
        final int y = getY();
        final int size = getSize();
        return entity != this && !entity.isDead() && !entity.isMaximumTolerance() && (entity.isMultiArea() || entity.getAttackedBy() == this) && (!isProjectileClipped(entity, false) || Utils.collides(x, y, size, entityX, entityY, entitySize)) && (!(entity instanceof NPC) || ((NPC) entity).isAttackableNPC()) && (!(entity instanceof Player) || ((Player) entity).isCanPvp());
    }

    public long generateSnowflake() {
        return Utils.generateSnowflake(playerInformation.getUserIdentifier());
    }

    public final Number getNumericTemporaryAttribute(final String key) {
        final Object object = getTemporaryAttributes().get(key);
        if (!(object instanceof Number)) {
            return 0;
        }
        return (Number) object;
    }

    public final Number getNumericTemporaryAttributeOrDefault(final String key, final int defaultValue) {
        final Object object = getTemporaryAttributes().get(key);
        if (!(object instanceof Number)) {
            return defaultValue;
        }
        return (Number) object;
    }

    public final Number getNumericAttribute(final String key) {
        final Object object = attributes.get(key);
        if (object == null || !(object instanceof Number)) {
            return 0;
        }
        return (Number) object;
    }

    /**
     * Adds or subtracts a numeric attribute by specified amount.
     *
     * @param key    the key of the attribute to apply the arithmetic operation to.
     * @param amount the amount to add or subtract from current value of attribute. If no value is found then operation is applied to 0.
     * @return the new value for the numeric attribute.
     */
    public final Number incrementNumericAttribute(@NotNull final String key, final int amount) {
        final Object object = attributes.get(key);
        if (object != null && !(object instanceof Number)) {
            throw new IllegalArgumentException("Attribute with key [" + key + "] is not numeric.");
        }
        final int newAmount = object == null ? amount : ((Number) object).intValue() + amount;
        attributes.put(key, newAmount);
        return newAmount;
    }

    @SuppressWarnings("unchecked")
    public final <T> T getAttributeOrDefault(final String key, final T defaultValue) {
        final Object object = attributes.get(key);
        if (object == null) {
            return defaultValue;
        }
        try {
            return (T) object;
        } catch (final Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    public int getIntSetting(final Setting setting) {
        return getNumericAttribute(setting.toString()).intValue();
    }

    public void addAttribute(final String key, final Object value) {
        if (value == null || value instanceof Number && ((Number) value).longValue() == 0) {
            attributes.remove(key);
        } else {
            attributes.put(key, value);
        }
    }

    public void addTemporaryAttribute(final String key, final Object value) {
        if (value instanceof Number && ((Number) value).longValue() == 0) {
            temporaryAttributes.remove(key);
        } else {
            temporaryAttributes.put(key, value);
        }
    }

    public int getTransmogrifiedId(@NotNull final TransmogrifiableType type, final int defaultValue) {
        if (type == null) {
            throw new NullPointerException("type is marked non-null but is null");
        }
        final int[] array = type.getTransmogrifiedIds();
        if (array == null) return defaultValue;
        final int varbit = type.getVarbitId();
        final int varp = type.getVarpId();
        final int index = varbit == -1 ? varManager.getValue(varp) : varManager.getBitValue(varbit);
        if (index < 0) return defaultValue;
        if (index >= array.length) {
            return type.defaultId();
        }
        return array[index];
    }

    public int getKillcount(final NPC npc) {
        return this.notificationSettings.getKillcount(npc.getName(this));
    }

    public void toggleBooleanAttribute(final String key) {
        if (key == null) {
            return;
        }
        final int value = getNumericAttribute(key).intValue();
        if (value == 0) {
            addAttribute(key, 1);
            return;
        }
        addAttribute(key, 0);
    }

    public boolean getBooleanAttribute(final String key) {
        if (key == null) {
            return false;
        }
        final int value = getNumericAttribute(key).intValue();
        return value == 1;
    }

    public boolean getBooleanTemporaryAttribute(final String key) {
        if (key == null) {
            return false;
        }
        final int value = getNumericTemporaryAttribute(key).intValue();
        return value == 1;
    }

    public void putBooleanTemporaryAttribute(final String key, final boolean bool) {
        if (key == null) {
            return;
        }
        addTemporaryAttribute(key, bool ? 1 : 0);
    }

    public void putBooleanAttribute(final String key, final boolean bool) {
        if (key == null) {
            return;
        }
        addAttribute(key, bool ? 1 : 0);
    }

    public boolean getBooleanSetting(final Setting key) {
        if (key == null) {
            return false;
        }
        final int value = getNumericAttribute(key.toString()).intValue();
        return value == 1;
    }

    @Override
    public boolean addWalkStep(final int nextX, final int nextY, final int lastX, final int lastY, final boolean check) {
        final int dir = Utils.getMoveDirection(nextX - lastX, nextY - lastY);
        if (dir == -1) {
            return false;
        }
        if (check && !World.checkWalkStep(getPlane(), lastX, lastY, dir, getSize(), false, true)) {
            return false;
        }
        if (!controllerManager.canMove(dir, nextX, nextY)) {
            return false;
        }
        getWalkSteps().enqueue(WalkStep.getHash(dir, nextX, nextY, check));
        return true;
    }

    public void openShop(final String name) {
        //Different shop across the world, same npc.
        if (name.equals("Trader Stan's Trading Post")) {
            final CharterLocation charterLocation = Utils.getOrDefault(CharterLocation.getLocation(getLocation()), CharterLocation.BRIMHAVEN);
            Shop.get(name + "<" + charterLocation.getShopPrefix() + ">", isIronman(), this).open(this);
            return;
        }
        Shop.get(name, isIronman(), this).open(this);
    }

    @Override
    public double getMagicPrayerMultiplier() {
        return 0.6;
    }

    @Override
    public double getRangedPrayerMultiplier() {
        return 0.6;
    }

    @Override
    public double getMeleePrayerMultiplier() {
        return 0.6;
    }

    @Override
    public void heal(final int amount) {
        final int hitpoints = getHitpoints();
        if (hitpoints >= getMaxHitpoints()) {
            return;
        }
        setHitpoints((hitpoints + amount) >= (getMaxHitpoints()) ? (getMaxHitpoints()) : (hitpoints + amount));
    }

    @Override
    public void unclip() {
        final int size = getSize();
        final int x = getX();
        final int y = getY();
        final int z = getPlane();
        int hash;
        int lastHash = -1;
        Chunk chunk = null;
        for (int x1 = x; x1 < (x + size); x1++) {
            for (int y1 = y; y1 < (y + size); y1++) {
                if ((hash = Chunk.getChunkHash(x1 >> 3, y1 >> 3, z)) != lastHash) {
                    chunk = World.getChunk(lastHash = hash);
                }
                assert chunk != null;
                //if (collides(chunk.getPlayers(), x1, y1) || collides(chunk.getNPCs(), x1, y1)) continue;
                World.getRegion(Location.getRegionId(x1, y1), true).removeFlag(z, x1 & 63, y1 & 63, Flags.OCCUPIED_BLOCK_NPC);
            }
        }
    }

    @Override
    public void clip() {
        if (isFinished()) {
            return;
        }
        final int size = getSize();
        final int x = getX();
        final int y = getY();
        final int z = getPlane();
        for (int x1 = x; x1 < (x + size); x1++) {
            for (int y1 = y; y1 < (y + size); y1++) {
                World.getRegion(Location.getRegionId(x1, y1), true).addFlag(z, x1 & 63, y1 & 63, Flags.OCCUPIED_BLOCK_NPC);
            }
        }
    }

    @Override
    public void processEntity() {
        getSession().processEvents();
        try {
            final RouteEvent<?, ?> event = routeEvent;
            if (event != null) {
                if (event.process()) {
                    if (routeEvent == event) {
                        routeEvent = null;
                    }
                }
            }
        } catch (Exception e) {
            log.error("", e);
        }
        try {
            if (!postPacketProcessingRunnables.isEmpty()) {
                postPacketProcessingRunnables.forEach(runnable -> {
                    try {
                        runnable.run();
                    } catch (Exception e) {
                        log.error("", e);
                    }
                });
                postPacketProcessingRunnables.clear();
            }
        } catch (Exception e) {
            log.error("", e);
        }
        try {
            try {
                actionManager.process();
            } catch (final Exception e) {
                log.error("", e);
            }
            variables.process();
            if (getCape() != null) {
                AvasDevice.collectMetal(this);
            }
            try {
                controllerManager.process();
            } catch (final Exception e) {
                log.error("", e);
            }
            cutsceneManager.process();
            music.processMusicPlayer();
            if (getAttackedByDelay() > Utils.currentTimeMillis() && getAttackedBy() != null || actionManager.getAction() instanceof PlayerCombat && getAttacking() != null) {
                chargesManager.removeCharges(DegradeType.TICKS);
            }
            farming.processAll();
            hunter.process();
            prayerManager.process();
            final double energy = variables.getRunEnergy();
            if (energy < 100 && getRunDirection() == -1) {
                float restore = ((8.0F + (skills.getLevel(Skills.AGILITY) / 6.0F)) / 0.6F / 100.0F) * 0.6F;
                double boost = 1;
                if (EquipmentUtils.containsFullGraceful(this)) {
                    boost += 0.3F;
                }
                if (perkManager.ifValidConsume(PerkWrapper.ATHLETIC_RUNNER) && !inArea("Wilderness")) {
                    boost += 0.25F;
                }
                if (getSkillingXPRate() == 10) {
                    boost += 0.02F;
                } else if (getSkillingXPRate() == 5) {
                    boost += 0.03F;
                }
                variables.forceRunEnergy(energy + (restore * boost));
            }
        } catch (final Exception e) {
            log.error("", e);
        }
        super.processEntity();
        appendNearbyNPCs();
        if (damageSound != -1) {
            sendSound(new SoundEffect(damageSound));
            damageSound = -1;
        }
    }

    public void postProcess() {
        try {
            delayedActionManager.process();
        } catch (final Exception e) {
            log.error("", e);
        }
    }

    @Override
    public void appendHitEntry(final HitEntry entry) {
        if (!entry.isFreshEntry()) {
            return;
        }
        entry.setFreshEntry(false);
        final Entity source = entry.getSource();
        if (source != null) {
            final Hit hit = entry.getHit();
            final HitType type = hit.getHitType();
            if (type == HitType.MELEE) {
                if (prayerManager.isActive(Prayer.PROTECT_FROM_MELEE)) {
                    hit.setDamage((int) Math.ceil(hit.getDamage() * source.getMeleePrayerMultiplier()));
                }
            } else if (type == HitType.RANGED) {
                if (prayerManager.isActive(Prayer.PROTECT_FROM_MISSILES)) {
                    hit.setDamage((int) Math.ceil(hit.getDamage() * source.getRangedPrayerMultiplier()));
                }
            } else if (type == HitType.MAGIC) {
                if (prayerManager.isActive(Prayer.PROTECT_FROM_MAGIC)) {
                    hit.setDamage((int) Math.ceil(hit.getDamage() * source.getMagicPrayerMultiplier()));
                }
            }
        }
    }

    private void appendNearbyNPCs() {
        CharacterLoop.forEach(getLocation(), 25, NPC.class, npc -> {
            if (npc.getTargetType() != EntityType.PLAYER || npc.isDead()) return;
            NPC.pendingAggressionCheckNPCs.add(npc.getIndex());
        });
    }

    public void finish() {
        if (isFinished()) {
            return;
        }
        try {
            log.info("'" + getName() + "' has logged out.");
            SpellbookSwap.checkSpellbook(this);
            final Object loc = getTemporaryAttributes().get("oculusStart");
            if (loc instanceof Location) {
                setLocation((Location) loc);
            }
            controllerManager.logout();
            final Area area = getArea();
            if (area instanceof LogoutPlugin) {
                ((LogoutPlugin) area).onLogout(this);
            }
            construction.getTipJar().onLogout();
            setFinished(true);
            World.updateEntityChunk(this, true);
            LocationMap.remove(this);
            getInterfaceHandler().closeInterface(GameInterface.TOURNAMENT_SPECTATING);
            GlobalAreaManager.update(this, false, true);
            if (getTemporaryAttributes().get("cameraShake") != null) {
                packetDispatcher.resetCamera();
            }
            if (follower != null) {
                follower.finish();
            }
            ClanManager.leave(this, false);
            socialManager.updateStatus();
            interfaceHandler.closeInterfaces();
            final String address = getSession().getChannel().remoteAddress().toString();
            playerInformation.setIp(address.substring(1, address.indexOf(":")));
            MethodicPluginHandler.invokePlugins(ListenerType.LOGOUT, this);
            PluginManager.post(new LogoutEvent(this));
            CoresManager.getServiceProvider().submit(logger::shutdown);
            /*appender.getManager().flush();
            appender.stop();
            LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
            loggerConfig.removeAppender(appender.getName());
            ctx.updateLoggers();*/
            this.getSession().getChannel().flush().closeFuture();
            sendPlayerInformationToApi();
            postFinish();
        } catch (final Exception e) {
            log.error("", e);
        }
    }

    private void sendPlayerInformationToApi() {
        if (Constants.WORLD_PROFILE.isPrivate() || Constants.WORLD_PROFILE.isBeta() || Constants.WORLD_PROFILE.isDevelopment() || !Constants.WORLD_PROFILE.getApi().isEnabled()) {
            return;
        }
        //Avoids a dumb exception.
        if (getCombatXPRate() == 1) {
            return;
        }
        final com.zenyte.api.model.PlayerInformation info = new com.zenyte.api.model.PlayerInformation(playerInformation.getUserIdentifier(), getUsername().replaceAll("_", " "), skills.getTotalLevel(), memberRank.getApiRole(), gameMode.getApiRole(), getApiExperienceMode());
        final boolean skipHighscores = this.playerInformation.getUserIdentifier() == -1 || this.privilege.eligibleTo(Privilege.ADMINISTRATOR) || !isApiExperienceModePresent();
        final Location tile = getLocation();
        final java.util.List<SkillHiscore> hiscores = skipHighscores ? null : getHiscores();
        final String username = getUsername();
        CoresManager.getServiceProvider().submit(() -> {
            if (!skipHighscores) {
                refreshHighscores(username, tile, hiscores);
            }
            sendPlayerInfo(info);
        });
    }

    private void sendPlayerInfo(final com.zenyte.api.model.PlayerInformation info) {
        new SubmitPlayerInformation(info).execute();
    }

    private void refreshHighscores(final String username, final Location location, final List<SkillHiscore> hiscores) {
        if (isNulled() || this.playerInformation.getUserIdentifier() == -1 || this.privilege.eligibleTo(Privilege.ADMINISTRATOR) || !isApiExperienceModePresent()) {
            return;
        }
        if (TutorialIslandArea.polygon.contains(location)) {
            log.info("User '" + getName() + "' in tutorial island, holding off sending hiscores data");
            return;
        }
        new SendPlayerHiscores(username, hiscores).execute();
    }

    private List<SkillHiscore> getHiscores() {
        final ArrayList<SkillHiscore> hiscores = new ArrayList<>(Skills.SKILLS.length);
        for (Skill skill : Skill.VALUES_NO_TOTAL) {
            hiscores.add(new SkillHiscore(getPlayerInformation().getUserIdentifier(), getUsername(), getGameMode().getApiRole(), getApiExperienceMode(), skill.getId(), skill.getFormattedName(), getSkills().getLevelForXp(skill.getId()), (long) getSkills().getExperience(skill.getId())));
        }
        return hiscores;
    }

    private void postFinish() {
        temporaryAttributes.clear();
        pendingContainers.clear();
        attackedByPlayers.clear();
        pendingVars.clear();
        zoneFollowPackets.clear();
        tempList.clear();
        chunksInScope.clear();
        receivedHits.clear();
        nextHits.clear();
        hitBars.clear();
        npcViewport.reset();
        playerViewport.reset();
        postSaveFunction = this::postSave;
    }

    private void postSave() {
        try {
            if (isNulled()) {
                return;
            }
            setNulled(true);
            unlink();
            final Field[] fields = getClass().getDeclaredFields();
            for (final Field field : fields) {
                final int modifier = field.getModifiers();
                if (Modifier.isStatic(modifier) || field.getType().isPrimitive()) {
                    continue;
                }
                field.setAccessible(true);
                try {
                    FieldModifiersHelper.definalize(field);
                } catch (Exception e) {
                    log.error("", e);
                }
                try {
                    field.set(this, null);
                } catch (IllegalAccessException e) {
                    log.error("", e);
                }
            }
            this.postSaveFunction = null;
        } catch (Exception e) {
            log.error("", e);
        }
    }

    public void processEntityUpdate() {
        if (!pendingContainers.isEmpty()) {
            if (pendingContainers.contains(inventory.getContainer()) || pendingContainers.contains(equipment.getContainer())) {
                packetDispatcher.sendWeight();
            }
            for (final Container container : pendingContainers) {
                if (container.isFullUpdate() || container.getModifiedSlots().size() >= (container.getContainerSize() * 0.67F)) {
                    packetDispatcher.sendUpdateItemContainer(container);
                } else {
                    packetDispatcher.sendUpdateItemsPartial(container);
                }
            }
            pendingContainers.clear();
        }
        skills.sendQueuedFakeExperienceDrops();
        final boolean regionUpdate = isNeedRegionUpdate();
        if (regionUpdate) {
            loadMapRegions();
        }
        send(playerViewport.cache());
        //from here.
        if (!pendingVars.isEmpty()) {
            for (final Integer var : pendingVars) {
                packetDispatcher.sendConfig(var, varManager.getValue(var));
            }
            pendingVars.clear();
        }
        if (teleported) {
            if (lastLocation != null && getPlane() != lastLocation.getPlane()) {
                zoneFollowPackets.clear();
            }
            updateScopeInScene();
        }
        if (!tempList.isEmpty()) {
            for (final Player.ProjPacket proj : tempList) {
                sendZoneUpdate(proj.sender.getX(), proj.sender.getY(), proj.packet);
            }
            tempList.clear();
        }
        if (!zoneFollowPackets.isEmpty()) {
            for (Int2ObjectMap.Entry<List<GamePacketEncoder>> entry : zoneFollowPackets.int2ObjectEntrySet()) {
                final int key = entry.getIntKey();
                final java.util.List<GamePacketEncoder> packets = entry.getValue();
                if (packets.size() == 1) {
                    send(new UpdateZonePartialFollows((key & 2047) << 3, (key >> 11 & 2047) << 3, this));
                    send(packets.get(0));
                } else {
                    final UpdateZonePartialEnclosed zonePacket = new UpdateZonePartialEnclosed((key & 2047) << 3, (key >> 11 & 2047) << 3, this);
                    for (int i = packets.size() - 1; i >= 0; i--) {
                        zonePacket.append(packets.get(i));
                    }
                    send(zonePacket);
                }
            }
            zoneFollowPackets.clear();
        }
        send(npcViewport.cache());//to here
        if (regionUpdate) {
            setNeedRegionUpdate(false);
        }
        flush();
    }

    public void sendZoneUpdate(final int tileX, final int tileY, final GamePacketEncoder packet) {
        final int chunkX = tileX >> 3;
        final int chunkY = tileY >> 3;
        final int hash = chunkX | chunkY << 11;
        List<GamePacketEncoder> list = zoneFollowPackets.get(hash);
        if (list == null) {
            list = new ArrayList<>();
            zoneFollowPackets.put(hash, list);
        }
        list.add(packet);
    }

    /**
     * TODO: Temporary; testing to see if this fixes the issue!
     */
    public void addProj(final Location sender, final GamePacketEncoder packet) {
        tempList.add(new ProjPacket(sender, packet));
    }

    public void sendSound(final int id) {
        if (id <= -1) {
            return;
        }
        this.packetDispatcher.sendSoundEffect(SoundEffect.get(id));
    }

    public void sendSound(final SoundEffect sound) {
        this.packetDispatcher.sendSoundEffect(sound);
    }

    @Override
    public void setAnimation(final Animation animation) {
        this.animation = animation;
        if (animation == null) {
            updateFlags.set(UpdateFlag.ANIMATION, false);
            lastAnimation = 0;
        } else {
            if (!AnimationMap.isValidAnimation(appearance.getNpcId(), animation.getId())) {
                return;
            }
            updateFlags.flag(UpdateFlag.ANIMATION);
            final AnimationDefinitions defs = AnimationDefinitions.get(animation.getId());
            if (defs != null) {
                lastAnimation = Utils.currentTimeMillis() + defs.getDuration();
            } else {
                lastAnimation = Utils.currentTimeMillis();
            }
        }
    }

    @Override
    public void setInvalidAnimation(final Animation animation) {
        this.animation = animation;
        if (animation == null) {
            updateFlags.set(UpdateFlag.ANIMATION, false);
            lastAnimation = 0;
        } else {
            updateFlags.flag(UpdateFlag.ANIMATION);
            final AnimationDefinitions defs = AnimationDefinitions.get(animation.getId());
            if (defs != null) {
                lastAnimation = Utils.currentTimeMillis() + defs.getDuration();
            } else {
                lastAnimation = Utils.currentTimeMillis();
            }
        }
    }

    public ExpMode getApiExperienceMode() {
        return getApiExperienceMode(getCombatXPRate());
    }

    public ExpMode getApiExperienceMode(int rate) {
        if (rate == 50) {
            return ExpMode.FIFTY;
        } else if (rate == 10) {
            return ExpMode.TEN;
        } else if (rate == 5) {
            return ExpMode.FIVE;
        }
        throw new RuntimeException("Invalid combat xp rate: " + rate);
    }

    public boolean isApiExperienceModePresent() {
        final int rate = getCombatXPRate();
        return rate == 50 || rate == 10 || rate == 5;
    }

    public int getExperienceRate(final int skill) {
        return Skills.isCombatSkill(skill) ? getCombatXPRate() : getSkillingXPRate();
    }

    public int getSkillingXPRate() {
        return Math.max(1, getNumericAttribute("skilling_xp_rate").intValue());
    }

    public int getCombatXPRate() {
        return Math.max(1, getNumericAttribute("combat_xp_rate").intValue());
    }

    public void setExperienceMultiplier(final int combat, final int skilling) {
        addAttribute("skilling_xp_rate", Math.max(1, skilling));
        addAttribute("combat_xp_rate", Math.max(1, combat));
        if (getNumericAttribute("Xp Drops Multiplied").intValue() == 1) {
            if (getNumericAttribute("Xp Drops Wildy Only").intValue() == 0 || WildernessArea.isWithinWilderness(getX(), getY())) {
                getVarManager().sendVar(3504, 1);
            }
        }
        final Optional<Interface> optionalPlugin = GameInterface.GAME_NOTICEBOARD.getPlugin();
        if (optionalPlugin.isPresent()) {
            final Interface plugin = optionalPlugin.get();
            packetDispatcher.sendComponentText(plugin.getInterface(), plugin.getComponent("XP rate"), "XP: <col=ffffff>" + getCombatXPRate() + "x Combat & " + getSkillingXPRate() + "x Skilling</col>");
        }
    }

    public boolean isFloorItemDisplayed(final FloorItem item) {
        if (getNumericAttribute(GameSetting.HIDE_ITEMS_YOU_CANT_PICK.toString()).intValue() == 0) {
            return true;
        }
        return !isIronman() || !item.hasOwner() || item.isOwner(this);
    }

    public boolean isXPDropsMultiplied() {
        return getNumericAttribute("Xp Drops Multiplied").intValue() == 1;
    }

    public boolean isXPDropsWildyOnly() {
        return getNumericAttribute("Xp Drops Wildy Only").intValue() == 1;
    }

    public void setXPDropsWildyOnly(final boolean value) {
        addAttribute("Xp Drops Wildy Only", value ? 1 : 0);
    }

    public void setXpDropsMultiplied(final boolean value) {
        addAttribute("Xp Drops Multiplied", value ? 1 : 0);
    }

    @Override
    public void setUnprioritizedAnimation(final Animation animation) {
        if (lastAnimation > Utils.currentTimeMillis() || updateFlags.get(UpdateFlag.ANIMATION)) {
            return;
        }
        if (animation != null && !AnimationMap.isValidAnimation(appearance.getNpcId(), animation.getId())) {
            return;
        }
        this.animation = animation;
        updateFlags.set(UpdateFlag.ANIMATION, animation != null);
    }

    public void forceAnimation(final Animation animation) {
        this.animation = animation;
        if (animation == null) {
            updateFlags.set(UpdateFlag.ANIMATION, false);
            lastAnimation = 0;
        } else {
            updateFlags.flag(UpdateFlag.ANIMATION);
            final AnimationDefinitions defs = AnimationDefinitions.get(animation.getId());
            if (defs != null) {
                lastAnimation = Utils.currentTimeMillis() + defs.getDuration();
            } else {
                lastAnimation = Utils.currentTimeMillis();
            }
        }
    }

    private synchronized void flush() {
        final Session session = getSession();
        final java.util.Queue<GamePacketOut> prioritizedQueue = session.getGamePacketOutPrioritizedQueue();
        while (!prioritizedQueue.isEmpty()) {
            if (!session.write(prioritizedQueue.poll())) break;
        }
        final java.util.Queue<GamePacketOut> queue = session.getGamePacketOutQueue();
        while (!queue.isEmpty()) {
            if (!session.write(queue.poll())) break;
        }
        session.flush();
    }

    public synchronized void send(final GamePacketEncoder encoder) {
        try {
            final Session session = getSession();
            if (session == null) {
                return;
            }
            final GamePacketOut packet = encoder.encode();
            if (encoder.prioritized()) {
                session.getGamePacketOutPrioritizedQueue().add(packet);
            } else {
                session.getGamePacketOutQueue().add(packet);
            }
            if (encoder.level().getPriority() >= PlayerLogger.WRITE_LEVEL.getPriority()) {
                encoder.log(this);
            }
        } catch (Exception e) {
            log.error("", e);
        }
    }

    public void init(final Player player) {
        run = player.run;
        gameMode = player.gameMode;
        memberRank = Utils.getOrDefault(player.memberRank, MemberRank.NONE);
        privilege = player.privilege;
        experienceMode = player.experienceMode;
        respawnPoint = player.respawnPoint;
        if (player.paydirt != null) {
            paydirt.addAll(player.paydirt);
        }
        if (player.trackedHolidayItems != null) {
            trackedHolidayItems.addAll(player.trackedHolidayItems);
        }
    }

    @Override
    public void loadMapRegions() {
        super.loadMapRegions();
        this.setNeedRegionUpdate(false);
        if (isRunning() && isAtDynamicRegion()) {
            packetDispatcher.sendDynamicMapRegion();
        } else {
            packetDispatcher.sendStaticMapRegion();
        }
        forceReloadMap = false;
        final Location tile = getLastLoadedMapRegionTile();
        final int swx = ((tile.getChunkX() - 6) << 3) + 1;
        final int swy = ((tile.getChunkY() - 6) << 3) + 1;
        this.sceneRectangle = World.getRectangle(swx, swx + 102, swy, swy + 102);
    }

    @Override
    public int getSize() {
        try {
            final int npcId = appearance.getNpcId();
            if (npcId != -1) {
                return NPCDefinitions.get(npcId).getSize();
            }
        } catch (final Exception e) {
            log.error("", e);
        }
        return 1;
    }

    @Override
    public int getHitpoints() {
        return isNulled() ? 0 : skills.getLevel(Skills.HITPOINTS);
    }

    @Override
    public boolean setHitpoints(final int hitpoints) {
        final boolean dead = isDead();
        skills.setLevel(Skills.HITPOINTS, hitpoints);
        this.hitpoints = hitpoints;
        if (!dead && this.hitpoints <= 0) {
            sendDeath();
            return true;
        }
        return false;
    }

    @Override
    public void unlink() {
    }

    @Override
    public int getMaxHitpoints() {
        return skills.getLevelForXp(Skills.HITPOINTS);
    }

    @Override
    public int getClientIndex() {
        return getIndex() + 32768;
    }

    @Override
    public boolean isDead() {
        return getHitpoints() == 0;
    }

    @Override
    public void cancelCombat() {
        if (actionManager.getAction() instanceof PlayerCombat) {
            actionManager.forceStop();
        }
    }

    public void stopAll() {
        this.stopAll(true);
    }

    public void stopAll(final boolean stopWalk) {
        this.stopAll(stopWalk, true);
    }

    public void stopAll(final boolean stopWalk, final boolean stopInterface) {
        this.stopAll(stopWalk, stopInterface, true);
    }

    public void useStairs(final int emoteId, final Location dest, final int useDelay, final int totalDelay) {
        this.useStairs(emoteId, dest, useDelay, totalDelay, null);
    }

    public void useStairs(final int emoteId, final Location dest, final int useDelay, final int totalDelay, final String message) {
        this.useStairs(emoteId, dest, useDelay, totalDelay, message, false);
    }

    public void useStairs(final int emoteId, final Location dest, final int useDelay, final int totalDelay, final String message, final boolean resetAnimation) {
        this.stopAll();
        this.lock(totalDelay);
        if (emoteId != -1) {
            setAnimation(new Animation(emoteId));
        }
        if (useDelay == 0) {
            teleport(dest);
        } else {
            WorldTasksManager.schedule(() -> {
                if (Player.this.isDead()) {
                    return;
                }
                if (resetAnimation) {
                    Player.this.setAnimation(Animation.STOP);
                }
                teleport(dest);
                if (message != null) {
                    Player.this.sendMessage(message);
                }
            }, useDelay - 1);
        }
    }

    public void stopAll(final boolean stopWalk, final boolean stopInterfaces, final boolean stopActions) {
        setRouteEvent(null);
        if (getFaceEntity() >= 0) {
            setFaceEntity(null);
        }
        varManager.sendBit(5983, 0);
        if (getTemporaryAttributes().get("CreatingRoom") != null) {
            construction.roomPreview((RoomReference) getTemporaryAttributes().get("CreatingRoom"), true);
            getTemporaryAttributes().remove("CreatingRoom");
        }
        interfaceHandler.closeInput();
        if (stopInterfaces) {
            getTemporaryAttributes().remove("skillDialogue");
            interfaceHandler.closeInterfaces();
            if (worldMap.isVisible() && worldMap.isFullScreen()) {
                worldMap.close();
            }
        }
        if (stopWalk) {
            getPacketDispatcher().resetMapFlag();
            resetWalkSteps();
        }
        if (stopActions) {
            actionManager.forceStop();
            delayedActionManager.forceStop();
        }
    }

    @Override
    public void resetMasks() {
        if (updateFlags.isUpdateRequired()) {
            updateFlags.reset();
        }
        if (!hitBars.isEmpty()) {
            hitBars.clear();
        }
        if (!nextHits.isEmpty()) {
            nextHits.clear();
        }
        this.updateNPCOptions = false;
        if (appearance.getBuffer().isReadable()) {
            appearance.getBuffer().clear();
        }
        this.walkDirection = this.runDirection = this.crawlDirection = -1;
    }

    @Override
    public void resetWalkSteps() {
        super.resetWalkSteps();
        pathfindingEvent = null;
    }

    public void stop(final StopType... types) {
        for (final Player.StopType type : types) {
            type.consumer.accept(this);
        }
    }

    public void stopAllExclWorldMap() {
        setRouteEvent(null);
        if (varManager.getBitValue(5983) != 0) {
            varManager.sendBit(5983, 0);
        }
        if (getTemporaryAttributes().get("CreatingRoom") != null) {
            construction.roomPreview((RoomReference) getTemporaryAttributes().get("CreatingRoom"), true);
            getTemporaryAttributes().remove("CreatingRoom");
        }
        getTemporaryAttributes().remove("skillDialogue");
        interfaceHandler.closeInterfaces();
        resetWalkSteps();
        actionManager.forceStop();
        delayedActionManager.forceStop();
        setAnimation(Animation.STOP);
    }

    @Override
    public void processReceivedHits() {
        super.processReceivedHits();
    }

    @Override
    public void applyHit(final Hit hit) {
        if (isNulled()) {
            return;
        }
        super.applyHit(hit);
        interfaceHandler.closeInterfaces();
        if (worldMap.isVisible() && worldMap.isFullScreen()) {
            worldMap.close();
        }
    }

    private final void reflectDamage(final Hit hit) {
        final Entity source = hit.getSource();
        if (source == null || hit.getHitType() == HitType.REGULAR) {
            return;
        }
        final int amuletId = equipment.getId(EquipmentSlot.AMULET);
        final int damage = hit.getDamage();
        if (damage <= 0) {
            return;
        }
        if ((amuletId == 12851 || amuletId == 12853) && Utils.random(3) == 0 && CombatUtilities.hasFullBarrowsSet(this, "Dharok's")) {
            WorldTasksManager.schedule(() -> source.applyHit(new Hit(this, (int) (damage * 0.15F), HitType.REGULAR)));
        }
        if (hit.getDamage() > 3) {
            final boolean hasVengeance = getAttributes().remove("vengeance") != null;
            if (hasVengeance) {
                setForceTalk(VENGEANCE);
                source.applyHit(new Hit(this, (int) (damage * 0.75F), HitType.REGULAR));
            }
        }
        final int ring = equipment.getId(EquipmentSlot.RING);
        if (ring == 2550 || ((ring == 19710 || ring == 20655 || ring == 20657))) {
            if (ring == 2550 || getBooleanAttribute("recoil effect")) {
                final Item ringItem = getRing();
                int charges = ring == 2550 ? getNumericAttribute("RING_OF_RECOIL").intValue() : ringItem.getCharges();
                if (ring == 2550 && charges == 0) {
                    charges = 40;
                }
                final int reflected = Math.min((int) Math.floor(damage / 10.0F) + 1, charges);
                chargesManager.removeCharges(ringItem, reflected, equipment.getContainer(), EquipmentSlot.RING.getSlot());
                WorldTasksManager.schedule(() -> source.applyHit(new Hit(this, reflected, HitType.REGULAR)));
            }
        }
    }

    private void applySmite(final Hit hit) {
        final Entity source = hit.getSource();
        if (!(source instanceof Player)) {
            return;
        }
        final int damage = Math.min(hit.getDamage(), getHitpoints());
        if (((Player) source).getPrayerManager().isActive(Prayer.SMITE)) {
            final int drain = damage / 4;
            if (drain > 0) {
                prayerManager.drainPrayerPoints(drain);
            }
        }
    }

    private void applyDamageReducers(final Hit hit, final Entity source) {
        int damage = hit.getDamage();
        final int weaponId = equipment.getId(EquipmentSlot.WEAPON);
        final int shieldId = equipment.getId(EquipmentSlot.SHIELD);
        final HitType type = hit.getHitType();
        if (source.getEntityType() == EntityType.NPC) {
            if (weaponId == 21015) {
                final long delay = getNumericTemporaryAttribute("dinhsbulwarkdelay").longValue();
                if (Utils.currentTimeMillis() > delay) {
                    damage = (int) (damage * 0.8F);
                }
            }
            if (CombatUtilities.hasFullJusticiarSet(this) && type != HitType.DEFAULT) {
                final int bonus = bonuses.getBonus(type == HitType.MELEE ? 7 : type == HitType.MAGIC ? 8 : 9);
                if (bonus > 0) {
                    final double percentage = bonus / 3000.0;
                    final int reduced = (int) (damage * percentage);
                    damage -= reduced;
                }
            }
        }
        if (type != HitType.DEFAULT && shieldId == 12817 || shieldId == 19559) {
            if (Utils.randomDouble() < 0.7F) {
                final int reduced = (int) (damage * 0.25F);
                setGraphics(ELYSIAN_EFFECT_GFX);
                damage -= reduced;
            }
        }
        if (hit.getDamage() != damage) {
            hit.setDamage(Math.max(0, damage));
        }
    }

    @Override
    public void postProcessHit(final Hit hit) {
        applySmite(hit);
        reflectDamage(hit);
        if (damageSound == -1 || damageSound == 511) {
            if (hit.getDamage() > 0) {
                final int[] array = appearance.isMale() ? maleDamageSounds : femaleDamageSounds;
                damageSound = array[Utils.random(array.length - 1)];
            } else {
                damageSound = 511;
            }
        }
    }

    @Override
    public void handleIngoingHit(final Hit hit) {
        final Entity source = hit.getSource();
        if (source == null) {
            return;
        }
        if (hit.getDamage() > 0) {
            chargesManager.removeCharges(DegradeType.INCOMING_HIT);
        }
        final HitType type = hit.getHitType();
        if (!ArrayUtils.contains(PROCESSED_HIT_TYPES, type)) {
            return;
        }
        applyDamageReducers(hit, source);
    }

    public final void sendMessage(final String message) {
        packetDispatcher.sendGameMessage(message, false);
    }

    public final void sendFilteredMessage(final String message) {
        packetDispatcher.sendGameMessage(message, true);
    }

    public final void sendMessage(final String message, final MessageType type) {
        packetDispatcher.sendGameMessage(message, type);
    }

    public final void sendMessage(final String message, final MessageType type, final String extension) {
        packetDispatcher.sendMessage(message, type, extension);
    }

    /*private transient LoggerConfig loggerConfig;
    private transient FileAppender appender;
    @Getter
    private transient Logger logger;*/
    public final void createLogger() {
        /*val name = getUsername();
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        final Configuration config = ctx.getConfiguration();
        Layout<? extends Serializable> layout = PatternLayout.newBuilder().withAlwaysWriteExceptions(true).withPattern("%d{yyyy-MM-dd HH:mm:ss.SSS} " + "[%thread] %-5level - %msg%n").withConfiguration(config).build();

        appender =
                FileAppender.newBuilder().withFileName("data/logs/player/" + name + ".log").withName("File").withImmediateFlush(false).withBufferedIo(true).withBufferSize(4096).withLayout(layout).setConfiguration(config).build();

        appender.start();
        config.addAppender(appender);
        AppenderRef ref = AppenderRef.createAppenderRef("File", null, null);
        AppenderRef[] refs = new AppenderRef[] { ref };
        loggerConfig = LoggerConfig.createLogger(false, Level.INFO, "org.apache.logging.log4j", "true", refs, null, config, null);
        loggerConfig.addAppender(appender, null, null);
        config.addLogger("Player logger: " + name, loggerConfig);
        ctx.updateLoggers();
        this.logger = ctx.getLogger("Player logger: " + name);*/
        logger.build();
    }

    public void log(final LogLevel level, final String info) {
        logger.log(level, info);
    }

    public void errorlog(final String info) {
        log(LogLevel.ERROR, info);
    }

    @Override
    public void checkMultiArea() {
        final boolean isAtMultiArea = isForceMultiArea() || World.isMultiArea(getLocation());
        if (isAtMultiArea && !Player.this.isMultiArea()) {
            Player.this.setMultiArea(isAtMultiArea);
            WorldTasksManager.schedule(() -> varManager.sendBit(4605, 1));
        } else if (!isAtMultiArea && Player.this.isMultiArea()) {
            Player.this.setMultiArea(isAtMultiArea);
            WorldTasksManager.schedule(() -> varManager.sendBit(4605, 0));
        }
    }

    /**
     * Multi icon is updated in synchronization with the client.
     */
    @Override
    public void removeHitpoints(final Hit hit) {
        if (isDead()) {
            return;
        }
        final int hitpoints = getHitpoints();
        int damage = hit.getDamage();
        if (damage > hitpoints) {
            damage = hitpoints;
        }
        addReceivedDamage(hit.getSource(), damage);
        final boolean dead = setHitpoints(hitpoints - damage);
        if (dead) {
            temporaryAttributes.put("killing blow hit", hit.getSource());
        }
        if (!isDead() && (getHitpoints() < getMaxHitpoints() * 0.1F) && prayerManager.isActive(Prayer.REDEMPTION)) {
            prayerManager.applyRedemptionEffect();
        }
        if (!isDead() && (getHitpoints() < getMaxHitpoints() * 0.2F)) {
            if (equipment.getId(EquipmentSlot.AMULET) == 21157) {
                equipment.set(EquipmentSlot.AMULET, null);
                prayerManager.restorePrayerPoints((int) (skills.getLevelForXp(Skills.PRAYER) * 0.1F));
                sendFilteredMessage("Your necklace of faith degrades to dust.");
            }
        }
        if (!isDead()) {
            final Item necklace = this.getAmulet();
            if (necklace != null && necklace.getId() == 11090 && getHitpoints() < (getMaxHitpoints() * 0.2F) && getDuel() == null) {
                this.heal((int) (this.getMaxHitpoints() * 0.3F));
                sendMessage("Your phoenix necklace heals you, but is destroyed in the process.");
                equipment.set(EquipmentSlot.AMULET, null);
                equipment.refresh();
            }
            if (getHitpoints() <= (getMaxHitpoints() * 0.1F)) {
                final int ring = equipment.getId(EquipmentSlot.RING);
                final Area area = getArea();
                if (area instanceof DeathPlugin && !((DeathPlugin) area).isRingOfLifeEffective()) {
                    return;
                }
                if (!(ring == 2570 || (SkillcapePerk.DEFENCE.isEffective(this) && getBooleanAttribute("Skillcape ring of life teleport")))) {
                    return;
                }
                if (variables.getTime(TickVariable.TELEBLOCK) > 0) {
                    return;
                }
                final OptionalInt level = WildernessArea.getWildernessLevel(getLocation());
                if (level.isPresent() && level.getAsInt() > 30) {
                    return;
                }
                stopAll();
                if (SkillcapePerk.DEFENCE.isEffective(this) && getBooleanAttribute("Skillcape ring of life teleport")) {
                    sendMessage("Your cape saves you.");
                } else {
                    equipment.set(EquipmentSlot.RING, null);
                    sendMessage("Your Ring of Life saves you and is destroyed in the process.");
                    updateFlags.flag(UpdateFlag.APPEARANCE);
                }
                final Teleport teleport = new Teleport() {
                    @Override
                    public TeleportType getType() {
                        return TeleportType.REGULAR_TELEPORT;
                    }

                    @Override
                    public Location getDestination() {
                        return respawnPoint.getLocation();
                    }

                    @Override
                    public int getLevel() {
                        return 0;
                    }

                    @Override
                    public double getExperience() {
                        return 0;
                    }

                    @Override
                    public int getRandomizationDistance() {
                        return 0;
                    }

                    @Override
                    public Item[] getRunes() {
                        return null;
                    }

                    @Override
                    public int getWildernessLevel() {
                        return 30;
                    }

                    @Override
                    public boolean isCombatRestricted() {
                        return false;
                    }
                };
                teleport.teleport(this);
            }
        }
    }

    @Override
    public void sendDeath() {
        final Player source = getMostDamagePlayer();
        if (!controllerManager.sendDeath(source) || areaManager.sendDeath(this, source)) {
            return;
        }
        if (animation != null) {
            animation = null;
            updateFlags.set(UpdateFlag.ANIMATION, false);
        }
        lock();
        stopAll();
        if (prayerManager.isActive(Prayer.RETRIBUTION)) {
            prayerManager.applyRetributionEffect(source);
        }
        WorldTasksManager.schedule(new WorldTask() {
            int ticks;

            @Override
            public void run() {
                if (isFinished() || isNulled()) {
                    stop();
                    return;
                }
                if (ticks == 1) {
                    setAnimation(DEATH_ANIMATION);
                } else if (ticks == 4) {
                    deathMechanics.death(source, null);
                    if (source != null) {
                        final int index = Utils.random(deathMessages.length - 1);
                        String message = deathMessages[index];
                        if (index >= deathMessages.length - 2) {
                            if (index == deathMessages.length - 1) {
                                message = message.replace("%gender%", getAppearance().isMale() ? "him" : "her");
                            } else {
                                message = message.replace("%gender%", getAppearance().isMale() ? "he" : "she");
                            }
                        }
                        source.sendMessage(String.format(message, getName()));
                    }
                    sendMessage("Oh dear, you have died.");
                    getMusic().playJingle(90);
                    reset();
                    setAnimation(Animation.STOP);
                    variables.setSkull(false);
                    final Area area = getArea();
                    final DeathPlugin plugin = area instanceof DeathPlugin ? (DeathPlugin) area : null;
                    final Location respawnLocation = plugin == null ? null : plugin.getRespawnLocation();
                    Player.this.setLocation(respawnLocation != null ? respawnLocation : respawnPoint.getLocation());
                } else if (ticks == 5) {
                    unlock();
                    setAnimation(Animation.STOP);
                    stop();
                }
                ticks++;
            }
        }, 0, 0);
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.PLAYER;
    }

    public int getQuestPoints() {
        return getNumericAttribute("quest_points").intValue();
    }

    public void setQuestPoints(final int amount) {
        addAttribute("quest_points", amount);
    }

    public void refreshQuestPoints() {
        getVarManager().sendVar(101, getQuestPoints());
    }

    public boolean isVisibleInViewport(final Position position) {
        return isVisibleInScene(position) && location.withinDistance(position, getViewDistance());
    }

    public boolean isVisibleInScene(final Position position) {
        final Location pos = position.getPosition();
        return sceneRectangle.contains(pos.getX(), pos.getY());
    }

    void refreshScopedGroundItems(final boolean add) {
        SceneSynchronization.refreshScopedGroundItems(this, add);
    }

    public void syncTotalDonated() {
        if (Constants.WORLD_PROFILE.getApi().isEnabled()) {
            CoresManager.getServiceProvider().submit(() -> {
                final int amount = new TotalDonatedRequest(getUsername()).execute();
                final int actualAmount = Math.max(0, amount);
                WorldTasksManager.schedule(() -> {
                    addAttribute("total donated online", actualAmount);
                    refreshTotalDonated();
                });
            });
        }
    }

    public void refreshTotalDonated() {
        final int totalDonated = getNumericAttribute("total donated online").intValue();
        if (totalDonated >= 5000) {
            setMemberRank(MemberRank.ZENYTE_MEMBER);
        } else if (totalDonated >= 2500) {
            setMemberRank(MemberRank.ONYX_MEMBER);
        } else if (totalDonated >= 1000) {
            setMemberRank(MemberRank.DRAGONSTONE_MEMBER);
        } else if (totalDonated >= 500) {
            setMemberRank(MemberRank.DIAMOND_MEMBER);
        } else if (totalDonated >= 200) {
            setMemberRank(MemberRank.RUBY_MEMBER);
        } else if (totalDonated >= 50) {
            setMemberRank(MemberRank.EMERALD_MEMBER);
        } else if (totalDonated >= 10) {
            setMemberRank(MemberRank.SAPPHIRE_MEMBER);
        } else {
            setMemberRank(MemberRank.NONE);
        }
        GameInterface.GAME_NOTICEBOARD.getPlugin().ifPresent(plugin -> {
            getPacketDispatcher().sendComponentText(GameInterface.GAME_NOTICEBOARD, plugin.getComponent("Member Rank"), "Member: <col=ffffff>" + getMemberRank().getCrown() + getMemberRank().toString().replace(" Member", "") + "</col>");
            getPacketDispatcher().sendComponentText(GameInterface.GAME_NOTICEBOARD, plugin.getComponent("Total donated"), "Total donated: <col=ffffff>$" + (totalDonated) + "</col>");
        });
    }

    public void updateScopeInScene() {
        SceneSynchronization.updateScopeInScene(this);
    }

    @Override
    public void setLocation(final Location tile) {
        if (tile == null) {
            return;
        }
        nextLocation = new Location(tile);
    }

    /**
     * Equipment getters - a better form of this should be made (kotlins extension / proxy functions would be nice)
     */
    public Item getHelmet() {
        return equipment.getItem(0);
    }

    public Item getCape() {
        return equipment.getItem(1);
    }

    public Item getAmulet() {
        return equipment.getItem(2);
    }

    public Item getWeapon() {
        return equipment.getItem(3);
    }

    public Item getChest() {
        return equipment.getItem(4);
    }

    public Item getShield() {
        return equipment.getItem(5);
    }

    public Item getLegs() {
        return equipment.getItem(7);
    }

    public Item getGloves() {
        return equipment.getItem(9);
    }

    public Item getBoots() {
        return equipment.getItem(10);
    }

    public Item getRing() {
        return equipment.getItem(12);
    }

    public Item getAmmo() {
        return equipment.getItem(13);
    }

    @Override
    public Location getMiddleLocation() {
        if (middleTile == null) {
            middleTile = new Location(getLocation());
        } else {
            middleTile.setLocation(getLocation());
        }
        return middleTile;
    }

    private Animation getDefenceAnimation() {
        final int weaponId = equipment.getId(EquipmentSlot.WEAPON);
        if (weaponId == 21015) {
            return BULWARK_ANIM;
        }
        final ItemDefinitions weaponDefinitions = ItemDefinitions.get(weaponId);
        if (weaponDefinitions == null) {
            return PLAIN_DEFENCE_ANIM;
        }
        final int shieldId = equipment.getId(EquipmentSlot.SHIELD);
        final ItemDefinitions shieldDefinitions = ItemDefinitions.get(shieldId);
        if (weaponId == 4084) {
            return new Animation(1466);
        }
        if (shieldId != -1) {
            if ((shieldId >= 8844 && shieldId <= 8850) || shieldId == 12954 || shieldId == 19722 || shieldId == 22322 || shieldId == ItemId.RUNE_DEFENDER_T) {
                return new Animation(4177);
            }
            if (shieldDefinitions != null && shieldDefinitions.getName().toLowerCase().contains("book")) {
                return new Animation(420);
            }
            return new Animation(1156);
        }
        final int blockAnimation = weaponDefinitions.getBlockAnimation();
        if (!AnimationMap.isValidAnimation(appearance.getNpcId(), blockAnimation)) {
            return PLAIN_DEFENCE_ANIM;
        }
        return new Animation(blockAnimation);
    }

    public void setCanPvp(final boolean canPvp, final boolean duel) {
        if (this.canPvp == canPvp) {
            return;
        }
        this.canPvp = canPvp;
        this.setPlayerOption(1, canPvp ? duel ? "Fight" : "Attack" : "null", true);
    }

    @Override
    public void performDefenceAnimation(Entity attacker) {
        if (getWeapon() != null && getWeapon().getId() == 21015) {
            setGraphics(BULWARK_GFX);
        }
        setUnprioritizedAnimation(getDefenceAnimation());
    }

    @Override
    public int drainSkill(final int skill, final double percentage) {
        if (skill == Skills.PRAYER) {
            return prayerManager.drainPrayerPoints(percentage, 0);
        }
        return skills.drainSkill(skill, percentage, 0);
    }

    @Override
    public int drainSkill(final int skill, final double percentage, final int minimumDrain) {
        if (skill == Skills.PRAYER) {
            return prayerManager.drainPrayerPoints(percentage, minimumDrain);
        }
        return skills.drainSkill(skill, percentage, minimumDrain);
    }

    @Override
    public int drainSkill(final int skill, final int amount) {
        if (skill == Skills.PRAYER) {
            return prayerManager.drainPrayerPoints(amount);
        }
        return skills.drainSkill(skill, amount);
    }

    @Override
    public boolean startAttacking(final Player source, final CombatType type) {
        return true;
    }

    @Override
    public boolean canAttack(final Player source) {
        return true;
    }

    @Override
    public void autoRetaliate(final Entity source) {
        if (!combatDefinitions.isAutoRetaliate() || !source.triggersAutoRetaliate() || actionManager.hasSkillWorking() || hasWalkSteps() || isLocked()) {
            return;
        }
        PlayerCombat.attackEntity(this, source, null);
    }

    /**
     * Gets the players current username.
     *
     * @return current username.
     */
    public String getUsername() {
        return playerInformation.getUsername();
    }

    /**
     * Returns the player's current username.
     */
    @Override
    public String toString() {
        return playerInformation.getUsername();
    }

    @Override
    public void handleOutgoingHit(final Entity target, final Hit hit) {
        if (target == null || hit == null) {
            return;
        }
        if (target.getHitpoints() - hit.getDamage() <= 0) {
            if (target instanceof NPC) {
                handleNpcKill((NPC) target, hit);
            } else {
                handlePlayerKill((Player) target, hit);
            }
        }
        //controllerManager.processOutgoingHit(target, hit); UNUSED
    }

    private void handleNpcKill(final NPC target, final Hit hit) {
        if (getNumericAttribute("demon_kills").intValue() < 100 && CombatUtilities.isDemon(target)) {
            final int weapon = getEquipment().getId(EquipmentSlot.WEAPON);
            if (weapon != 2402) {
                //silverlight
                return;
            }
            if (!hit.getHitType().equals(HitType.MELEE)) {
                return;
            }
            addAttribute("demon_kills", getNumericAttribute("demon_kills").intValue() + 1);
            final int kills = getNumericAttribute("demon_kills").intValue();
            if (kills % 25 == 0 && kills < 100) {
                final int remaining = 100 - kills;
                sendMessage("You've reached a demon kill checkpoint! You need to kill " + remaining + " more demon" + (kills == 1 ? "" : "s") + " to upgrade your Silverlight.");
            } else if (kills == 100) {
                getEquipment().set(EquipmentSlot.WEAPON, new Item(6746));
                getUpdateFlags().flag(UpdateFlag.APPEARANCE);
                sendMessage("You've reached 100 demon kills, your Silverlight has been upgraded into a Darklight!");
            }
        }
    }

    private void handlePlayerKill(final Player target, final Hit hit) {
    }

    public void refreshTitles() {
        //setNametag(0,
        //       (!privilege.equals(Privilege.PLAYER) ? privilege.getCrown() + " " : "") + (!gameMode.equals
        //       (GameMode.REGULAR) ? gameMode.getCrown() + GameMode.getTitle(this) + " " : ""));
    }

    public Optional<Raid> getRaid() {
        if (isNulled()) {
            return Optional.empty();
        }
        final ClanChannel channel = settings.getChannel();
        if (channel == null) {
            return Optional.empty();
        }
        final RaidParty party = channel.getRaidParty();
        if (party == null) {
            return Optional.empty();
        }
        final Raid raid = party.getRaid();
        if (raid == null) {
            return Optional.empty();
        }
        if (!raid.getPlayers().contains(this)) {
            return Optional.empty();
        }
        return Optional.of(raid);
    }

    public boolean isIronman() {
        return !gameMode.equals(GameMode.REGULAR);
    }

    public boolean containsItem(final int id) {
        return containsItem(new Item(id));
    }

    public boolean containsAnyItem(final int... ids) {
        for (final int id : ids) {
            return containsItem(id);
        }
        return false;
    }

    public boolean containsAny(final int... ids) {
        boolean contains = false;
        for (final int id : ids) {
            if (containsItem(id)) {
                contains = true;
            }
        }
        return contains;
    }

    public boolean containsItem(final Item item) {
        for (final Item i : inventory.getContainer().getItems().values()) {
            if (i.getId() == item.getId()) {
                return true;
            }
        }
        for (final Item i : equipment.getContainer().getItems().values()) {
            if (i.getId() == item.getId()) {
                return true;
            }
        }
        for (final Item i : bank.getContainer().getItems().values()) {
            if (i.getId() == item.getId()) {
                return true;
            }
        }
        for (final Item i : retrievalService.getContainer().getItems().values()) {
            if (i.getId() == item.getId()) {
                return true;
            }
        }
        for (final Item i : runePouch.getContainer().getItems().values()) {
            if (i != null && i.getId() == item.getId()) {
                return true;
            }
        }
        for (final Item i : privateStorage.getContainer().getItems().values()) {
            if (i != null && i.getId() == item.getId()) {
                return true;
            }
        }
        return false;
    }

    public boolean carryingAny(final Collection<Integer> ids) {
        for (final Integer id : ids) {
            if (carryingItem(id)) {
                return true;
            }
        }
        return false;
    }

    public boolean carryingAny(final int... ids) {
        for (final int id : ids) {
            if (carryingItem(id)) {
                return true;
            }
        }
        return false;
    }

    public boolean carryingItem(final int id) {
        return carryingItem(new Item(id));
    }

    public boolean carryingItem(final Item item) {
        for (final Item i : inventory.getContainer().getItems().values()) {
            if (i.getId() == item.getId()) {
                return true;
            }
        }
        for (final Item i : equipment.getContainer().getItems().values()) {
            if (i.getId() == item.getId()) {
                return true;
            }
        }
        return false;
    }

    public int getAmountOf(final int id) {
        int count = 0;
        for (final Item i : inventory.getContainer().getItems().values()) {
            if (i.getId() == id) {
                count += i.getAmount();
            }
        }
        for (final Item i : equipment.getContainer().getItems().values()) {
            if (i.getId() == id) {
                count += i.getAmount();
            }
        }
        for (final Item i : bank.getContainer().getItems().values()) {
            if (i.getId() == id) {
                count += i.getAmount();
            }
        }
        for (final Item i : retrievalService.getContainer().getItems().values()) {
            if (i.getId() == id) {
                count += i.getAmount();
            }
        }
        for (final Item i : runePouch.getContainer().getItems().values()) {
            if (i != null && i.getId() == id) {
                count += i.getAmount();
            }
        }
        for (final Item i : privateStorage.getContainer().getItems().values()) {
            if (i != null && i.getId() == id) {
                count += i.getAmount();
            }
        }
        return count;
    }

    public void removeItem(final Item item) {
        final ContainerWrapper[] wrappers = new ContainerWrapper[]{inventory, equipment};
        for (final ContainerWrapper wrapper : wrappers) {
            for (int slot = 0; slot < wrapper.getContainer().getSize(); slot++) {
                final Item i = wrapper.getItem(slot);
                if (i == null || i.getId() != item.getId()) {
                    continue;
                }
                wrapper.deleteItem(i);
                if (wrapper instanceof Equipment) {
                    getUpdateFlags().flag(UpdateFlag.APPEARANCE);
                }
            }
        }
        for (int slot = 0; slot < bank.getContainer().getSize(); slot++) {
            final Item i = bank.get(slot);
            if (i == null || i.getId() != item.getId()) {
                continue;
            }
            bank.remove(i);
        }
    }

    public boolean addWalkSteps(final Direction direction, final int distance, final int maxStepsCount, final boolean check) {
        final Location dest = getLocation().transform(direction, distance);
        return addWalkSteps(dest.getX(), dest.getY(), maxStepsCount, check);
    }

    @Override
    public boolean addWalkSteps(final int destX, final int destY, final int maxStepsCount, final boolean check) {
        final int[] lastTile = getLastWalkTile();
        int myX = lastTile[0];
        int myY = lastTile[1];
        int stepCount = 0;
        while (true) {
            stepCount++;
            if (myX < destX) {
                myX++;
            } else if (myX > destX) {
                myX--;
            }
            if (myY < destY) {
                myY++;
            } else if (myY > destY) {
                myY--;
            }
            if (!addWalkStep(myX, myY, lastTile[0], lastTile[1], check)) {
                return false;
            }
            if (stepCount == maxStepsCount) {
                return true;
            }
            lastTile[0] = myX;
            lastTile[1] = myY;
            if (lastTile[0] == destX && lastTile[1] == destY) {
                return true;
            }
        }
    }

    @Override
    public int getCombatLevel() {
        return skills.getCombatLevel();
    }

    public void sendPlayerOptions() {
        setPlayerOption(3, "Follow", false);
        setPlayerOption(4, "Trade with", false);
    }

    public void setPlayerOption(final int index, final String option, final boolean top) {
        options[index] = option;
        packetDispatcher.sendPlayerOption(index, option, top);
        if (options[index] != null) {
            if (options[index].equals("Attack") && (option == null || !option.equals("Attack"))) {
                setCanPvp(false);
            } else if (options[index].equals("Fight") && (option == null || !option.equals("Fight"))) {
                setCanPvp(false);
            }
        }
        if (Objects.equals(option, "Attack") || Objects.equals(option, "Fight")) {
            setCanPvp(true, option.equals("Fight"));
        }
    }

    public final OptionalInt findPlayerOption(@NotNull final String query) {
        for (int i = 0; i < options.length; i++) {
            if (Objects.equals(options[i], query)) {
                return OptionalInt.of(i);
            }
        }
        return OptionalInt.empty();
    }

    public String getIP() {
        return lastIP;
    }

    public String getMACAddress() {
        return lastMAC;
    }

    public void onLogin() {
        log.info("'" + getName() + "' has logged in.");
        log(LogLevel.INFO, "Logged in with IP: " + getSession().getChannel().remoteAddress());
        sendMessage("Welcome to " + GameConstants.SERVER_NAME + ".");
        varManager.refreshDefaults();
        final String updateMessage = "Latest Update: " + Constants.UPDATE_LOG_BROADCAST + "|" + Constants.UPDATE_LOG_URL;
        if (getNumericAttribute(GameSetting.ALWAYS_SHOW_LATEST_UPDATE.toString()).intValue() == 1 || !Objects.equals(attributes.get("latest update message"), updateMessage)) {
            sendMessage(updateMessage, MessageType.GLOBAL_BROADCAST);
            addAttribute("latest update message", updateMessage);
        }
        if (Constants.WORLD_PROFILE.isBeta()) {
            sendMessage("This is a " + Colour.TURQOISE.wrap("Beta World") + "; your progress will not affect the main game.");
        }
        if (Constants.BOOSTED_XP) {
            sendMessage("<col=00FF00><shad=000000>Experience is boosted by 50% until " + new Date(BonusXpManager.expirationDate) + "!</col></shad>");
        }
        if (!attributes.containsKey("death timers info")) {
            attributes.put("death timers info", true);
            sendMessage(Colour.RS_GREEN.wrap("Info: Items lost on death will remain invisible on the ground for 3 minutes(boosted to 60 for UIM), followed by 5 minutes of visibility to everyone."));
        }
        if (!attributes.containsKey("Thanksgiving 2019 event")) {
            final Calendar date = Calendar.getInstance();
            if (thanksgivingStart.before(date) && thanksgivingEnd.after(date)) {
                attributes.put("Thanksgiving 2019 event", true);
                sendMessage(Colour.RS_PURPLE.wrap("Congratulations! You have unlocked the 'Give Thanks' emote."));
            }
        }
        if (!attributes.containsKey("treasure trails broadcasting")) {
            attributes.put("treasure trails broadcasting", 1);
            if (getNumericAttribute(GameSetting.TREASURE_TRAILS_BROADCASTS.toString()).intValue() == 0) {
                GameSetting.TREASURE_TRAILS_BROADCASTS.handleSetting(this);
            }
        }
        varManager.sendVar(GIVE_THANKS_VARP, attributes.containsKey("Thanksgiving 2019 event") ? 1 : 0);
        emotesHandler.unlock(Emote.AROUND_THE_WORLD_IN_EGGTY_DAYS);
        emotesHandler.unlock(Emote.RABBIT_HOP);
        varManager.sendBit(15026, attributes.containsKey("Christmas 2019 event") ? 1 : 0);
        final boolean christmasEventCompleted = true;
        varManager.sendBit(15024, christmasEventCompleted ? 1 : 0);
        varManager.sendBit(15025, christmasEventCompleted ? 1 : 0);
        varManager.sendBit(16000, memberRank.eligibleTo(MemberRank.SAPPHIRE_MEMBER) ? 1 : 0);
        if (!getAuthenticator().isEnabled()) {
            sendMessage(Colour.RED.wrap("You do not have 2FA enabled. Please enable it for extra account security and extra vote rewards!"));
        }
        PunishmentManager.isPunishmentActive(getUsername(), getIP(), PunishmentType.MUTE).ifPresent(value -> sendMessage("You are currently " + value.toLoginString() + "."));
        syncTotalDonated();
        updateScopeInScene();
        setRun(isRun());
        //Invokes the xp multiplier refresh.
        setExperienceMultiplier(getCombatXPRate(), getSkillingXPRate());
        lastIP = this.playerInformation.getIpFromChannel();
        refreshQuestPoints();
        inventory.refreshAll();
        equipment.refreshAll();
        skills.refresh();
        toxins.refresh();
        settings.refresh();
        bonuses.update();
        appearance.resetRenderAnimation();
        packetDispatcher.sendRunEnergy();
        sendPlayerOptions();
        MethodicPluginHandler.invokePlugins(ListenerType.LOGIN, this);
        PluginManager.post(new LoginEvent(this));
        controllerManager.login();
        GlobalAreaManager.update(this, true, false);
        World.updateEntityChunk(this, false);
        clip();
        LocationMap.add(this);
        final Calendar calendar = Calendar.getInstance();
        refreshGameClock();
        final long ticksUntilNextMinute = TimeUnit.MILLISECONDS.toTicks(60000 - (((calendar.get(Calendar.SECOND) * 1000) + calendar.get(Calendar.MILLISECOND)) % 60000)) + 1;
        if (ticksUntilNextMinute > 1) {
            WorldTasksManager.schedule(this::refreshGameClock, (int) ticksUntilNextMinute);
        }
        if (isOnMobile()) {
            packetDispatcher.sendClientScript(2644);
        }
        if (attributes.get("fixed respawn point teleport") == null) {
            attributes.put("fixed respawn point teleport", true);
            respawnPoint = RespawnPoint.EDGEVILLE;
        }
        if (isXPDropsWildyOnly()) {
            varManager.sendVar(3504, WildernessArea.isWithinWilderness(getX(), getY()) ? getSkillingXPRate() : 1);
        } else if (isXPDropsMultiplied()) {
            varManager.sendVar(3504, getSkillingXPRate());
        } else {
            varManager.sendVar(3504, 1);
        }
        packetDispatcher.resetCamera();
        // script for clearing pm history, synching all vars, send it tick later so server finishes transmitting varps and it synches, fixes roof and stuff
        WorldTasksManager.schedule(() -> packetDispatcher.sendClientScript(876, 0, 0, "", ""));
        //Blades by Urbi shop in Sophanem; Quest.
        varManager.sendBit(3275, 1);
        varManager.sendBit(8119, 1);
        variables.onLogin();
        music.refreshListConfigs();
        if (World.isUpdating()) {
            send(new UpdateRebootTimer(World.getUpdateTimer()));
        }
        if (!getBooleanAttribute("registered")) {
            setLocation(GameConstants.REGISTRATION_LOCATION);
            if (getSettings().getChannelOwner() == null) {
                ClanManager.join(this, GameConstants.SERVER_NAME);
            }
        }
        int unreadMessageCount = getNumericAttribute("unread message count").intValue();
        if (unreadMessageCount > 0) {
            sendMessage("You currently have <col=ff0000>" + unreadMessageCount + "</col> unread message" + (unreadMessageCount == 1 ? "" : "s") + "; visit the forums to check your inbox.");
        }
    }

    public void refreshGameClock() {
        if (this.isLoggedOut()) {
            return;
        }
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        final int hours = calendar.get(Calendar.HOUR_OF_DAY);
        final int minutes = calendar.get(Calendar.MINUTE);
        varManager.sendBit(8354, (hours * 60) + minutes);
    }

    public void onLobbyClose() {
        pollManager.loadAnsweredPolls();
        varManager.sendVar(1050, 90);// chivalry/piety
        varManager.sendBit(598, 2);
        prayerManager.refreshQuickPrayers();
        if (petId != -1 && PetWrapper.getByPet(petId) != null) {
            if (follower == null) {
                setFollower(new Follower(petId, this));
            }
        }
        /*
         * if (player.getHelmet() != null && player.getHelmet().getId() >= 5525 && player.getHelmet().getId() <= 5547) { final int bitId =
         * 599 + (player.getHelmet().getId() - 5525); player.getVarManager().sendBit(bitId, 1); }
         */
        combatDefinitions.refresh();
        socialManager.loadFriends();
        socialManager.loadIgnores();
        /*
         * final ClanChannel channel = player.getSettings().getChannel(); if (channel != null) { ClanManager.join(player,
         * channel.getOwner()); } else { ClanManager.join(player, "kris"); }
         */
        socialManager.updateStatus();
        farming.refresh();
        runePouch.getContainer().refresh(this);
        grandExchange.updateOffers();
        VarCollection.updateType(this, EventType.POST_LOGIN);
        send(new ChatFilterSettings(this));
        send(new SetPrivateChatFilter(this));
        dailyChallengeManager.notifyUnclaimedChallenges();
        MethodicPluginHandler.invokePlugins(ListenerType.LOBBY_CLOSE, this);
        if (isDead()) {
            sendDeath();
        }
    }

    public Duel getDuel() {
        if (duel != null && duel.getPlayer() != this) {
            final Player opponent = duel.getPlayer();
            duel.setPlayer(this);
            duel.setOpponent(opponent);
        }
        return duel;
    }

    public void setDuel(final Duel duel) {
        this.duel = duel;
    }

    public int getPrimaryIcon() {
        return privilege.getIcon();
    }

    public int getSecondaryIcon() {
        return gameMode.getIcon();
    }

    public int getTertiaryIcon() {
        return memberRank.getIcon();
    }

    public boolean isMember() {
        return !memberRank.equals(MemberRank.NONE);
    }

    public boolean isStaff() {
        return privilege.ordinal() >= Privilege.SUPPORT.ordinal();
    }

    public AchievementDiaries getAchievementDiaries() {
        return this.achievementDiaries;
    }

    public CutsceneManager getCutsceneManager() {
        return this.cutsceneManager;
    }

    public PuzzleBox getPuzzleBox() {
        return this.puzzleBox;
    }

    public LightBox getLightBox() {
        return this.lightBox;
    }

    public ChargesManager getChargesManager() {
        return this.chargesManager;
    }

    public PollManager getPollManager() {
        return this.pollManager;
    }

    public AreaManager getAreaManager() {
        return this.areaManager;
    }

    public GodBooks getGodBooks() {
        return this.godBooks;
    }

    public BossTimer getBossTimer() {
        return this.bossTimer;
    }

    public CollectionLog getCollectionLog() {
        return this.collectionLog;
    }

    public DialogueManager getDialogueManager() {
        return this.dialogueManager;
    }

    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    public ControllerManager getControllerManager() {
        return this.controllerManager;
    }

    public MusicHandler getMusic() {
        return this.music;
    }

    public PresetManager getPresetManager() {
        return this.presetManager;
    }

    public EmotesHandler getEmotesHandler() {
        return this.emotesHandler;
    }

    public InterfaceHandler getInterfaceHandler() {
        return this.interfaceHandler;
    }

    public BountyHunter getBountyHunter() {
        return this.bountyHunter;
    }

    public List<Integer> getTrackedHolidayItems() {
        return this.trackedHolidayItems;
    }

    public Appearance getAppearance() {
        return this.appearance;
    }

    public Set<Container> getPendingContainers() {
        return this.pendingContainers;
    }

    public SocialManager getSocialManager() {
        return this.socialManager;
    }

    public CombatDefinitions getCombatDefinitions() {
        return this.combatDefinitions;
    }

    public DwarfMulticannon getDwarfMulticannon() {
        return this.dwarfMulticannon;
    }

    public Equipment getEquipment() {
        return this.equipment;
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public DeathMechanics getDeathMechanics() {
        return this.deathMechanics;
    }

    public NotificationSettings getNotificationSettings() {
        return this.notificationSettings;
    }

    public PriceChecker getPriceChecker() {
        return this.priceChecker;
    }

    public Trade getTrade() {
        return this.trade;
    }

    public SeedVault getSeedVault() {
        return this.seedVault;
    }

    public RunePouch getRunePouch() {
        return this.runePouch;
    }

    public RunePouch getSecondaryRunePouch() {
        return this.secondaryRunePouch;
    }

    public SeedBox getSeedBox() {
        return this.seedBox;
    }

    public LootingBag getLootingBag() {
        return this.lootingBag;
    }

    public HerbSack getHerbSack() {
        return this.herbSack;
    }

    public GemBag getGemBag() {
        return this.gemBag;
    }

    public Skills getSkills() {
        return this.skills;
    }

    public Settings getSettings() {
        return this.settings;
    }

    public Construction getConstruction() {
        return this.construction;
    }

    public PrayerManager getPrayerManager() {
        return this.prayerManager;
    }

    public TeleportManager getTeleportManager() {
        return this.teleportManager;
    }

    public VarManager getVarManager() {
        return this.varManager;
    }

    public PlayerInfo getPlayerViewport() {
        return this.playerViewport;
    }

    public NPCInfo getNpcViewport() {
        return this.npcViewport;
    }

    public PlayerVariables getVariables() {
        return this.variables;
    }

    public Set<Player> getBotObservers() {
        return this.botObservers;
    }

    public WorldMap getWorldMap() {
        return this.worldMap;
    }

    public GrandExchange getGrandExchange() {
        return this.grandExchange;
    }

    public Bonuses getBonuses() {
        return this.bonuses;
    }

    public String[] getOptions() {
        return this.options;
    }

    public Object2LongOpenHashMap<String> getAttackedByPlayers() {
        return this.attackedByPlayers;
    }

    public PerkManager getPerkManager() {
        return this.perkManager;
    }

    public ChatMessage getChatMessage() {
        return this.chatMessage;
    }

    public ChatMessage getClanMessage() {
        return this.clanMessage;
    }

    public Barrows getBarrows() {
        return this.barrows;
    }

    public ItemRetrievalService getRetrievalService() {
        return this.retrievalService;
    }

    public Runnable getCloseInterfacesEvent() {
        return this.closeInterfacesEvent;
    }

    public void setCloseInterfacesEvent(final Runnable closeInterfacesEvent) {
        this.closeInterfacesEvent = closeInterfacesEvent;
    }

    public boolean isNeedRegionUpdate() {
        return this.needRegionUpdate;
    }

    public void setNeedRegionUpdate(final boolean needRegionUpdate) {
        this.needRegionUpdate = needRegionUpdate;
    }

    public boolean isRunning() {
        return this.running;
    }

    public void setRunning(final boolean running) {
        this.running = running;
    }

    public ActionManager getActionManager() {
        return this.actionManager;
    }

    public PrivateStorage getPrivateStorage() {
        return this.privateStorage;
    }

    public void setPrivateStorage(final PrivateStorage privateStorage) {
        this.privateStorage = privateStorage;
    }

    public PlayerInformation getPlayerInformation() {
        return this.playerInformation;
    }

    public void setPlayerInformation(final PlayerInformation playerInformation) {
        this.playerInformation = playerInformation;
    }

    public Entity getLastTarget() {
        return this.lastTarget;
    }

    public void setLastTarget(final Entity lastTarget) {
        this.lastTarget = lastTarget;
    }

    public DelayedActionManager getDelayedActionManager() {
        return this.delayedActionManager;
    }

    public Farming getFarming() {
        return this.farming;
    }

    public void setFarming(final Farming farming) {
        this.farming = new Farming(this, farming);
    }

    public PacketDispatcher getPacketDispatcher() {
        return this.packetDispatcher;
    }

    public PetInsurance getPetInsurance() {
        return this.petInsurance;
    }

    public void setPetInsurance(final PetInsurance petInsurance) {
        this.petInsurance = petInsurance;
    }

    public Follower getFollower() {
        return this.follower;
    }

    public void setFollower(final Follower follower) {
        if (this.follower != null && follower == null) {
            this.follower.finish();
            petId = -1;
            this.follower = null;
            return;
        }
        this.follower = follower;
        petId = follower == null ? -1 : follower.getId();
        if (follower != null) {
            follower.spawn();
        }
        varManager.sendVar(447, follower == null ? -1 : follower.getIndex());
    }

    public int getPetId() {
        return this.petId;
    }

    public void setPetId(final int petId) {
        this.petId = petId;
    }

    public boolean isCanPvp() {
        return this.canPvp;
    }

    public void setCanPvp(final boolean canPvp) {
        setCanPvp(canPvp, false);
    }

    public Stash getStash() {
        return this.stash;
    }

    public void setStash(final Stash stash) {
        this.stash = stash;
    }

    public boolean isMaximumTolerance() {
        return this.maximumTolerance;
    }

    public void setMaximumTolerance(final boolean maximumTolerance) {
        this.maximumTolerance = maximumTolerance;
    }

    public SinglePlayerBank getBank() {
        return this.bank;
    }

    public void setBank(final SinglePlayerBank bank) {
        this.bank = bank;
    }

    public boolean isForceReloadMap() {
        return this.forceReloadMap;
    }

    public void setForceReloadMap(final boolean forceReloadMap) {
        this.forceReloadMap = forceReloadMap;
    }

    public int getViewDistance() {
        return this.viewDistance;
    }

    public void setViewDistance(int viewDistance) {
        this.viewDistance = viewDistance < 1 ? 1 : viewDistance > 104 ? 104 : viewDistance;
    }

    public Slayer getSlayer() {
        return this.slayer;
    }

    public void setSlayer(final Slayer slayer) {
        this.slayer = slayer;
    }

    public Hunter getHunter() {
        return this.hunter;
    }

    public void setHunter(final Hunter hunter) {
        this.hunter = hunter;
    }

    public BlastFurnace getBlastFurnace() {
        return this.blastFurnace;
    }

    public RespawnPoint getRespawnPoint() {
        return this.respawnPoint;
    }

    public void setRespawnPoint(final RespawnPoint point) {
        this.respawnPoint = point;
    }

    public DailyChallengeManager getDailyChallengeManager() {
        return this.dailyChallengeManager;
    }

    public Optional<GrotesqueGuardiansInstance> getGrotesqueGuardiansInstance() {
        return this.grotesqueGuardiansInstance;
    }

    public void setGrotesqueGuardiansInstance(final Optional<GrotesqueGuardiansInstance> grotesqueGuardiansInstance) {
        this.grotesqueGuardiansInstance = grotesqueGuardiansInstance;
    }

    public int getPid() {
        return this.pid;
    }

    public void setPid(final int pid) {
        this.pid = pid;
    }

    public boolean isLoadingRegion() {
        return this.loadingRegion;
    }

    public void setLoadingRegion(final boolean loadingRegion) {
        this.loadingRegion = loadingRegion;
    }

    public long getMovementLock() {
        return this.movementLock;
    }

    public void setMovementLock(final long movementLock) {
        this.movementLock = movementLock;
    }

    public long getDiceDelay() {
        return this.diceDelay;
    }

    public void setDiceDelay(final long diceDelay) {
        this.diceDelay = diceDelay;
    }

    public String[] getNametags() {
        return this.nametags;
    }

    public GameMode getGameMode() {
        return this.gameMode;
    }

    public void setGameMode(final GameMode mode) {
        if (gameMode != GameMode.REGULAR && mode == GameMode.REGULAR) {
            if (getNumericAttribute(GameSetting.HIDE_ITEMS_YOU_CANT_PICK.toString()).intValue() == 1) {
                GameSetting.HIDE_ITEMS_YOU_CANT_PICK.handleSetting(this);
            }
        }
        gameMode = mode;
        varManager.sendBit(1777, gameMode.ordinal());
        System.out.println("sending bit " + gameMode.ordinal());
        updateFlags.flag(UpdateFlag.APPEARANCE);
        final Optional<Interface> optionalPlugin = GameInterface.GAME_NOTICEBOARD.getPlugin();
        if (optionalPlugin.isPresent()) {
            final Interface plugin = optionalPlugin.get();
            packetDispatcher.sendComponentText(plugin.getInterface(), plugin.getComponent("Game Mode"), "Mode: <col=ffffff>" + gameMode.getCrown() + gameMode.toString() + "</col>");
        }
    }

    public MemberRank getMemberRank() {
        return this.memberRank;
    }

    public void setMemberRank(final MemberRank rank) {
        memberRank = rank;
        final Optional<Interface> optionalPlugin = GameInterface.GAME_NOTICEBOARD.getPlugin();
        if (optionalPlugin.isPresent()) {
            final Interface plugin = optionalPlugin.get();
            packetDispatcher.sendComponentText(plugin.getInterface(), plugin.getComponent("Member Rank"), "Member: <col=ffffff>" + memberRank.getCrown() + memberRank.toString().replace(" Member", "") + "</col>");
        }
        varManager.sendBit(16000, memberRank.eligibleTo(MemberRank.SAPPHIRE_MEMBER) ? 1 : 0);
    }

    public ExperienceMode getExperienceMode() {
        return this.experienceMode;
    }

    public void setExperienceMode(final ExperienceMode mode) {
        experienceMode = mode;
        final Optional<Interface> optionalPlugin = GameInterface.GAME_NOTICEBOARD.getPlugin();
        if (optionalPlugin.isPresent()) {
            final Interface plugin = optionalPlugin.get();
            packetDispatcher.sendComponentText(plugin.getInterface(), plugin.getComponent("XP rate"), "XP rate: <col=ffffff>" + experienceMode.getRate() + "x</col>");
        }
    }

    public Privilege getPrivilege() {
        return this.privilege;
    }

    public void setPrivilege(final Privilege privilege) {
        this.privilege = privilege;
        final Optional<Interface> optionalPlugin = GameInterface.GAME_NOTICEBOARD.getPlugin();
        if (optionalPlugin.isPresent()) {
            final Interface plugin = optionalPlugin.get();
            packetDispatcher.sendComponentText(plugin.getInterface(), plugin.getComponent("Privilege"), "Privilege: <col=ffffff>" + privilege.getCrown() + privilege + "</col>");
        }
    }

    public long getLastDisconnectionTime() {
        return this.lastDisconnectionTime;
    }

    public void setLastDisconnectionTime(final long lastDisconnectionTime) {
        this.lastDisconnectionTime = lastDisconnectionTime;
    }

    public boolean isLoggedOut() {
        return this.loggedOut;
    }

    public void setLoggedOut(final boolean loggedOut) {
        this.loggedOut = loggedOut;
    }

    public WheelOfFortune getWheelOfFortune() {
        return this.wheelOfFortune;
    }

    public int getLogoutCount() {
        return this.logoutCount;
    }

    public void setLogoutCount(final int logoutCount) {
        this.logoutCount = logoutCount;
    }

    public boolean isUpdatingNPCOptions() {
        return this.updatingNPCOptions;
    }

    public void setUpdatingNPCOptions(final boolean updatingNPCOptions) {
        this.updatingNPCOptions = updatingNPCOptions;
    }

    public boolean isUpdateNPCOptions() {
        return this.updateNPCOptions;
    }

    public void setUpdateNPCOptions(final boolean updateNPCOptions) {
        this.updateNPCOptions = updateNPCOptions;
    }

    public IntLinkedOpenHashSet getPendingVars() {
        return this.pendingVars;
    }

    public Runnable getPathfindingEvent() {
        return this.pathfindingEvent;
    }

    public void setPathfindingEvent(final Runnable pathfindingEvent) {
        this.pathfindingEvent = pathfindingEvent;
    }

    public RouteEvent<Player, EntityStrategy> getCombatEvent() {
        return this.combatEvent;
    }

    public void setCombatEvent(final RouteEvent<Player, EntityStrategy> combatEvent) {
        this.combatEvent = combatEvent;
    }

    public Int2ObjectOpenHashMap<List<GamePacketEncoder>> getZoneFollowPackets() {
        return this.zoneFollowPackets;
    }

    public boolean isHeatmap() {
        return this.heatmap;
    }

    public void setHeatmap(final boolean heatmap) {
        this.heatmap = heatmap;
    }

    public IntOpenHashSet getChunksInScope() {
        return this.chunksInScope;
    }

    public int getHeatmapRenderDistance() {
        return this.heatmapRenderDistance;
    }

    public void setHeatmapRenderDistance(final int heatmapRenderDistance) {
        this.heatmapRenderDistance = heatmapRenderDistance;
    }

    public boolean isHidden() {
        return this.hidden;
    }

    public void setHidden(final boolean hidden) {
        this.hidden = hidden;
    }

    public int getDamageSound() {
        return this.damageSound;
    }

    public void setDamageSound(final int damageSound) {
        this.damageSound = damageSound;
    }

    public IntArrayList getPaydirt() {
        return this.paydirt;
    }

    public LoyaltyManager getLoyaltyManager() {
        return this.loyaltyManager;
    }

    public long getLastReceivedPacket() {
        return this.lastReceivedPacket;
    }

    public void setLastReceivedPacket(final long lastReceivedPacket) {
        this.lastReceivedPacket = lastReceivedPacket;
    }

    public Authenticator getAuthenticator() {
        return this.authenticator;
    }

    public int getLastWalkX() {
        return this.lastWalkX;
    }

    public int getLastWalkY() {
        return this.lastWalkY;
    }

    public List<MovementLock> getMovementLocks() {
        return this.movementLocks;
    }

    public Runnable getPostSaveFunction() {
        return this.postSaveFunction;
    }

    public void setPostSaveFunction(final Runnable postSaveFunction) {
        this.postSaveFunction = postSaveFunction;
    }

    public boolean isNulled() {
        return this.nulled;
    }

    public void setNulled(final boolean nulled) {
        this.nulled = nulled;
    }

    public Queue<Location> getTolerancePositionQueue() {
        return this.tolerancePositionQueue;
    }

    public enum StopType {
        ROUTE_EVENT(p -> {
            p.setRouteEvent(null);
            if (p.getFaceEntity() >= 0) {
                p.setFaceEntity(null);
            }
        }),
        INTERFACES(p -> {
            p.getTemporaryAttributes().remove("skillDialogue");
            p.getInterfaceHandler().closeInterfaces();
        }),
        WORLD_MAP(p -> {
            if (p.getWorldMap().isVisible() && p.getWorldMap().isFullScreen()) {
                p.getWorldMap().close();
            }
        }),
        WALK(p -> {
            p.getPacketDispatcher().resetMapFlag();
            p.resetWalkSteps();
        }),
        ACTIONS(p -> {
            p.getActionManager().forceStop();
            p.getDelayedActionManager().forceStop();
        }),
        ANIMATIONS(p -> p.setAnimation(Animation.STOP));
        private final Consumer<Player> consumer;

        StopType(final Consumer<Player> consumer) {
            this.consumer = consumer;
        }
    }

    private static final class ProjPacket {
        private final Location sender;
        private final GamePacketEncoder packet;

        public ProjPacket(final Location sender, final GamePacketEncoder packet) {
            this.sender = sender;
            this.packet = packet;
        }
    }

    public void notification(String title, String text, int color) {
        GameInterface.NOTIFICATION.open(this);
        packetDispatcher.sendClientScript(3343, title, text, color);
        WorldTasksManager.schedule(() -> interfaceHandler.closeInterface(GameInterface.NOTIFICATION), 13);
    }

    public void awaitInputString(final StringDialogue dialogue) {
        temporaryAttributes.put("interfaceInput", dialogue);
    }

    public void awaitInputInt(final CountDialogue dialogue) {
        temporaryAttributes.put("interfaceInput", dialogue);
    }

    public void awaitInputIntNoClose(final CountDialogue dialogue) {
        temporaryAttributes.put("interfaceInput", dialogue);
        temporaryAttributes.put("interfaceInputNoCloseOnButton", true);
    }

    public String getTitleName() {
        StringBuilder sb = new StringBuilder();
        sb.append("<title>");
        sb.append(privilege.getCrown());
        sb.append("</title>");
        if (!sb.toString().contains("img"))
            sb = new StringBuilder();
        sb.append(getPlayerInformation().getDisplayname());
        return sb.toString();
    }

    public final Number getNumericAttributeOrDefault(final String key, final int defaultValue) {
        final Object object = attributes.get(key);
        if (object == null || !(object instanceof Number)) {
            return defaultValue;
        }
        return (Number) object;
    }

}
