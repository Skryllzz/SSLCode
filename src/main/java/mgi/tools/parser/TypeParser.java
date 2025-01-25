package mgi.tools.parser;

import com.esotericsoftware.kryo.Kryo;
import com.google.common.io.Files;
import com.moandjiezana.toml.Toml;
import com.zenyte.Constants;
import com.zenyte.Game;
import com.zenyte.Scanner;
import com.zenyte.game.constants.GameConstants;
import com.zenyte.game.content.achievementdiary.AchievementDiaries;
import com.zenyte.game.content.achievementdiary.Diary;
import com.zenyte.game.content.achievementdiary.DiaryArea;
import com.zenyte.game.ui.testinterfaces.BountyHunterRewardType;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.npc.actions.NPCPlugin;
import com.zenyte.game.world.object.NullObjectID;
import com.zenyte.game.world.object.ObjectId;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.game.world.region.MapUtils;
import com.zenyte.game.world.region.XTEALoader;
import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import kotlin.text.Charsets;
import mgi.custom.*;
import mgi.tools.jagcached.ArchiveType;
import mgi.tools.jagcached.GroupType;
import mgi.tools.jagcached.cache.Archive;
import mgi.tools.jagcached.cache.Cache;
import mgi.tools.jagcached.cache.Group;
import mgi.types.Definitions;
import mgi.types.config.StructDefinitions;
import mgi.types.config.enums.EnumDefinitions;
import mgi.types.config.npcs.NPCDefinitions;
import mgi.types.draw.sprite.SpriteGroupDefinitions;
import mgi.types.worldmap.WorldMapDefinitions;
import mgi.utilities.ByteBuffer;
import net.lingala.zip4j.ZipFile;
import net.runelite.api.ObjectID;
import net.runelite.cache.definitions.loaders.LocationsLoader;
import net.runelite.cache.definitions.loaders.MapLoaderPre209;
import net.runelite.cache.definitions.savers.LocationSaver;
import net.runelite.cache.definitions.savers.MapSaver;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Tommeh | 16/01/2020 | 01:06
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class TypeParser {

    private static final Logger log = LoggerFactory.getLogger(TypeParser.class);

    private static final Int2ObjectMap<List<String>> optionsMap = new Int2ObjectOpenHashMap<>(10 * 1024);

    private static final List<Definitions> definitions = new ArrayList<>();

    public static final Kryo KRYO = new Kryo();

    public static final boolean ENABLED_MAP_PACKING = true;

    private static void cleanDirectory(String dir) {
        for (File file : new File(dir).listFiles()) {
            if (file.isHidden() || file.getName().startsWith(".")) {
                continue;
            }
            file.delete();
        }
    }

    public static void main(final String[] args) throws IOException {
        String type = "";
        if (args.length > 0) {
            type = args[0];
        }
        final long startTime = System.nanoTime();
        if (true || type.equals("--unzip")) {
            String cacheDir = "data/cache";
            String cache211Dir = "data/cache-211";
            cleanDirectory(cacheDir);
            cleanDirectory(cache211Dir);
            final ZipFile zipFile = new ZipFile("data/cache-211.zip");
            zipFile.extractAll(cacheDir);
            zipFile.extractAll(cache211Dir);
        }
        XTEALoader.load("data/objects/xteas.json");
        Game.load();
        Definitions.loadDefinitions(Definitions.lowPriorityDefinitions);
        initializeKryo();
        parse(new File("assets/types"));
        pack(NPCDefinitions.class);
        repackNPCOptions();
        packDynamicConfigs();
        packHighRevision();
        removeCATasks();
        pack(ArrayUtils.addAll(Definitions.highPriorityDefinitions, Definitions.lowPriorityDefinitions));
        packClientBackground();
        packModels();
        packClientScripts();
        packInvs();
        packInterfaces();
        packEnums();
        packStructs();
        packParams();
        packMaps();
        increaseVarclientAmount();
        Game.getCacheMgi().close();
        log.info("Cache repack took " + Utils.nanoToMilli(System.nanoTime() - startTime) + " milliseconds!");
    }

    public static void increaseVarclientAmount() {
        final ByteBuffer buffer = new ByteBuffer(1);
        buffer.writeByte(0);
        Game.getCacheMgi().getArchive(ArchiveType.CONFIGS).findGroupByID(GroupType.VARCLIENT).addFile(new mgi.tools.jagcached.cache.File(2000, buffer));
    }

    private static void initializeKryo() {
        for (final Class<?> d : Definitions.lowPriorityDefinitions) {
            KRYO.register(d);
        }
        for (final Class<?> d : Definitions.highPriorityDefinitions) {
            KRYO.register(d);
        }
        KRYO.register(int[].class);
        KRYO.register(short[].class);
        KRYO.register(String[].class);
        KRYO.register(Int2ObjectOpenHashMap.class);
    }

    private static void repackNPCOptions() {
        if (true)
            return;
        new NPCDefinitions().load();
        new Scanner().scan(NPCPlugin.class);
        Game.setCacheMgi(Cache.openCache("./data/cache-original/"));
        new NPCDefinitions().load();
        for (final NPCDefinitions npc : NPCDefinitions.definitions) {
            if (npc == null) {
                continue;
            }
            final List<String> list = optionsMap.computeIfAbsent(npc.getId(), n -> new ArrayList<>());
            final String[] options = npc.getOptions();
            for (final String option : options) {
                if (option == null) {
                    list.add(null);
                    continue;
                }
                final NPCPlugin.NPCPluginHandler plugin = NPCPlugin.getHandler(npc.getId(), option);
                list.add(plugin == null ? null : option);
            }
        }
        Game.setCacheMgi(Cache.openCache("./data/cache/"));
        new NPCDefinitions().load();
        for (final NPCDefinitions npc : NPCDefinitions.definitions) {
            if (npc == null) {
                continue;
            }
            final List<String> options = optionsMap.get(npc.getId());
            if (options == null) {
                continue;
            }
            assert options.size() == 5;
            npc.setOptions(options.toArray(new String[0]));
            npc.pack();
        }
        log.info("Finished repacking npc options.");
    }

    private static void parse(final File folder) {
        File f = null;
        try {
            for (final java.io.File file : folder.listFiles()) {
                f = file;
                if (file.getPath().endsWith("exclude")) {
                    continue;
                }
                if (file.isDirectory()) {
                    parse(file);
                } else {
                    if (!Files.getFileExtension(file.getName()).equals("toml")) {
                        continue;
                    }
                    final String fileString = FileUtils.readFileToString(file, Charsets.UTF_8).replace("%SERVER_NAME%", GameConstants.SERVER_NAME);
                    final Toml toml = new Toml().read(fileString);
                    if (file.getPath().startsWith(Paths.get("assets", "types", "component").toString())) {
                        final TypeReader reader = TypeReader.readersMap.get("component");
                        definitions.addAll(reader.read(toml));
                    } else {
                        for (final Map.Entry<String, Object> entry : toml.entrySet()) {
                            final TypeReader reader = TypeReader.readersMap.get(entry.getKey());
                            if (reader == null) {
                                System.err.println(TypeReader.readers);
                                throw new RuntimeException("Could not find a reader for: " + entry.getKey());
                            }
                            final Object value = entry.getValue();
                            final ArrayList<Toml> types = new ArrayList<>();
                            if (value instanceof Toml) {
                                types.add((Toml) value);
                            } else {
                                types.addAll((ArrayList<Toml>) value);
                            }
                            for (final Toml type : types) {
                                final Map<String, Object> properties = type.toMap();
                                definitions.addAll(reader.read(properties));
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Something went wrong in " + f.getPath());
            e.printStackTrace();
            System.exit(0);
        }
    }

    private static void pack(final Class<?>... types) {
        final ArrayList<Definitions> filtered = definitions.stream().filter(d -> ArrayUtils.contains(types, d.getClass())).collect(Collectors.toCollection(ArrayList::new));
        filtered.forEach(Definitions::pack);
        if (!filtered.isEmpty()) {
            log.info("Finished packing " + filtered.size() + " type" + (filtered.size() == 1 ? "" : "s."));
        }
    }

    private static void packClientBackground() throws IOException {
        if (true)
            return;
        final byte[] desktop = java.nio.file.Files.readAllBytes(Paths.get("assets/sprites/background" + "/background_desktop.png"));
        final Cache cache = Game.getCacheMgi();
        final Archive desktopArchive = cache.getArchive(ArchiveType.BINARY);
        desktopArchive.findGroupByID(0).findFileByID(0).setData(new ByteBuffer(desktop));
        final Archive spritesArchive = cache.getArchive(ArchiveType.SPRITES);
        Group logoGroup = spritesArchive.findGroupByName("logo");
        final BufferedImage image = ImageIO.read(Paths.get("assets/sprites/background/background_logo" + ".png").toFile());
        final SpriteGroupDefinitions sprite = new SpriteGroupDefinitions(logoGroup.getID(), image.getWidth(), image.getHeight());
        sprite.setWidth(image.getWidth());
        sprite.setHeight(image.getHeight());
        sprite.setImage(0, image);
        sprite.pack();
    }

    private static void packHighRevision() throws IOException {
        if (true)
            return;
        //new DiceBagPacker().pack();
        //new TrickPacker().pack();
        new ThanksgivingPacker().pack();
        new HighDefinitionPets().packFull();
        new CustomTeleport().packAll();
        new TrickEmote().packAll();
        new DiceBag().packAll();
        new MusicEnumPacker().pack();
        FramePacker.write();
        AnimationBase.pack();
        //new HalloweenMapPacker().pack();
    }

    private static void packDynamicConfigs() {
        EnumDefinitions enumDef;
        enumDef = new EnumDefinitions();
        enumDef.setId(1974);
        enumDef.setKeyType("int");
        enumDef.setValueType("namedobj");
        enumDef.setDefaultInt(-1);
        enumDef.setValues(new HashMap<>());
        int id = 0;
        for (final BountyHunterRewardType reward : BountyHunterRewardType.values()) {
            enumDef.getValues().put(id++, reward.getId());
        }
        definitions.add(enumDef);
        final Diary[][] diaries = AchievementDiaries.ALL_DIARIES;
        for (final Diary[] diaryEnum : diaries) {
            final HashMap<Integer, Object> values = new HashMap<>();
            DiaryArea area = null;
            for (final Diary diary : diaryEnum) {
                if (diary.autoCompleted()) {
                    continue;
                }
                final Diary.DiaryComplexity complexity = diary.type();
                area = diary.area();
                values.put(complexity.ordinal(), (int) (values.get(complexity.ordinal()) == null ? 0 : values.get(complexity.ordinal())) + 1);
            }
            enumDef = new EnumDefinitions();
            enumDef.setId(2501 + area.getIndex());
            enumDef.setKeyType("int");
            enumDef.setValueType("int");
            enumDef.setDefaultInt(-1);
            enumDef.setValues(values);
            definitions.add(enumDef);
        }
        for (int enumId : new int[] { 812, 817 }) {
            enumDef = EnumDefinitions.get(enumId);
            enumDef.getValues().put(enumDef.getLargestIntKey() + 1, "Silent Knight");
            enumDef.getValues().put(enumDef.getLargestIntKey() + 2, "Smorgasbord");
            definitions.add(enumDef);
        }
        enumDef = EnumDefinitions.get(818);
        enumDef.getValues().put(enumDef.getLargestIntKey() + 1, enumDef.getLargestIntKey() + 2);
        enumDef.getValues().put(enumDef.getLargestIntKey() + 2, enumDef.getLargestIntKey() + 3);
        definitions.add(enumDef);
        enumDef = EnumDefinitions.get(819);
        enumDef.getValues().put(enumDef.getLargestIntKey() + 1, enumDef.getValues().get(1));
        enumDef.getValues().put(enumDef.getLargestIntKey() + 2, enumDef.getValues().get(1));
        definitions.add(enumDef);
    }

    private static void packModels() {
        try {
            packModel(38000, java.nio.file.Files.readAllBytes(Paths.get("assets/models/zenyte_portal_model.dat")));
            packModel(38001, java.nio.file.Files.readAllBytes(Paths.get("assets/models/tournament_supplies.dat")));
            packModel(38002, java.nio.file.Files.readAllBytes(Paths.get("assets/models/pets/cute_creature.dat")));
            packModel(38003, java.nio.file.Files.readAllBytes(Paths.get("assets/models/pets/stray_dog.dat")));
            packModel(38004, java.nio.file.Files.readAllBytes(Paths.get("assets/models/pets/evil_creature.dat")));
            packModel(38005, java.nio.file.Files.readAllBytes(Paths.get("assets/models/bonds/cyan_bond.dat")));
            packModel(38006, java.nio.file.Files.readAllBytes(Paths.get("assets/models/bonds/red_bond.dat")));
            packModel(50000, java.nio.file.Files.readAllBytes(Paths.get("assets/models/zenyte_teletab_50000.dat")));
            packModel(50001, java.nio.file.Files.readAllBytes(Paths.get("assets/models/healing fountain.dat")));
            packModel(52505, java.nio.file.Files.readAllBytes(Paths.get("assets/models/zenyte armour/Zenyte Boots(drop)b.dat")));
            packModel(52506, java.nio.file.Files.readAllBytes(Paths.get("assets/models/zenyte armour/Zenyte Bootsb.dat")));
            packModel(52507, java.nio.file.Files.readAllBytes(Paths.get("assets/models/zenyte armour/Zenyte Cape(drop)b.dat")));
            packModel(52508, java.nio.file.Files.readAllBytes(Paths.get("assets/models/zenyte armour/Zenyte Capeb.dat")));
            packModel(52509, java.nio.file.Files.readAllBytes(Paths.get("assets/models/zenyte armour/Zenyte Gloves(drop)b.dat")));
            packModel(52510, java.nio.file.Files.readAllBytes(Paths.get("assets/models/zenyte armour/Zenyte Glovesb.dat")));
            packModel(52511, java.nio.file.Files.readAllBytes(Paths.get("assets/models/zenyte armour/Zenyte Helmet(drop)b.dat")));
            packModel(52512, java.nio.file.Files.readAllBytes(Paths.get("assets/models/zenyte armour/Zenyte Helmetb.dat")));
            packModel(52513, java.nio.file.Files.readAllBytes(Paths.get("assets/models/zenyte armour/Zenyte Platebody(drop)b.dat")));
            packModel(52514, java.nio.file.Files.readAllBytes(Paths.get("assets/models/zenyte armour/Zenyte Platebodyb.dat")));
            packModel(52515, java.nio.file.Files.readAllBytes(Paths.get("assets/models/zenyte armour/Zenyte Platelegs(drop)b.dat")));
            packModel(52516, java.nio.file.Files.readAllBytes(Paths.get("assets/models/zenyte armour/Zenyte Platelegsb.dat")));
            packModel(52517, java.nio.file.Files.readAllBytes(Paths.get("assets/models/starter/Starter bow ground.dat")));
            packModel(52518, java.nio.file.Files.readAllBytes(Paths.get("assets/models/starter/Starter bow.dat")));
            packModel(52519, java.nio.file.Files.readAllBytes(Paths.get("assets/models/starter/Starter staff ground.dat")));
            packModel(52520, java.nio.file.Files.readAllBytes(Paths.get("assets/models/starter/Starter staff.dat")));
            packModel(52521, java.nio.file.Files.readAllBytes(Paths.get("assets/models/starter/Starter sword ground.dat")));
            packModel(52522, java.nio.file.Files.readAllBytes(Paths.get("assets/models/starter/Starter sword.dat")));
            packModel(52523, java.nio.file.Files.readAllBytes(Paths.get("assets/models/Rare drop table.dat")));

            //custom
            packModel(52535, java.nio.file.Files.readAllBytes(Paths.get("assets/models/custom/model_51732.dat")));
            packModel(52536, java.nio.file.Files.readAllBytes(Paths.get("assets/models/custom/model_51733.dat")));
            packModel(52537, java.nio.file.Files.readAllBytes(Paths.get("assets/models/custom/model_51753.dat")));
            packModel(52538, java.nio.file.Files.readAllBytes(Paths.get("assets/models/custom/model_51754.dat")));
            packModel(52539, java.nio.file.Files.readAllBytes(Paths.get("assets/models/custom/model_51795.dat")));
            packModel(52540, java.nio.file.Files.readAllBytes(Paths.get("assets/models/custom/model_51796.dat")));
            packModel(52541, java.nio.file.Files.readAllBytes(Paths.get("assets/models/custom/model_51797.dat")));


            //Jonas
            packModel(52524, java.nio.file.Files.readAllBytes(Paths.get("assets/halloween/staticmodels/jonas/34041.dat")));
            packModel(52525, java.nio.file.Files.readAllBytes(Paths.get("assets/halloween/staticmodels/jonas/34044.dat")));
            packModel(52526, java.nio.file.Files.readAllBytes(Paths.get("assets/halloween/staticmodels/jonas/34046.dat")));
            packModel(52527, java.nio.file.Files.readAllBytes(Paths.get("assets/halloween/staticmodels/jonas/34047.dat")));
            //Grim reaper
            packModel(52528, java.nio.file.Files.readAllBytes(Paths.get("assets/halloween/staticmodels/grim reaper/28985.dat")));
            packModel(52529, java.nio.file.Files.readAllBytes(Paths.get("assets/halloween/staticmodels/grim reaper/34166.dat")));
            packModel(52530, java.nio.file.Files.readAllBytes(Paths.get("assets/halloween/staticmodels/grim reaper/34167.dat")));
            //Thanksgiving
            packModel(52531, java.nio.file.Files.readAllBytes(Paths.get("assets/models/thanksgiving/thanksgiving turkey model.dat")));
            packModel(52532, java.nio.file.Files.readAllBytes(Paths.get("assets/models/thanksgiving/thanksgiving poof model.dat")));
            //Christmas scythe
            packModel(52533, java.nio.file.Files.readAllBytes(Paths.get("assets/models/christmas scythe inv.dat")));
            packModel(52534, java.nio.file.Files.readAllBytes(Paths.get("assets/models/christmas scythe wield.dat")));
            packModel(2450, java.nio.file.Files.readAllBytes(Paths.get("assets/models/Treasure trails reward casket.dat")));
            if (Constants.CHRISTMAS) {
                Iterator<File> it = FileUtils.iterateFiles(new File("assets/christmas/christmas-y entities models/"), null, false);
                final Int2ObjectAVLTreeMap<java.io.File> sortedMap = new Int2ObjectAVLTreeMap<>();
                while (it.hasNext()) {
                    final java.io.File file = it.next();
                    final int originalId = Integer.parseInt(file.getName().replace(".dat", ""));
                    sortedMap.put(originalId, file);
                }
                for (final Int2ObjectMap.Entry<java.io.File> entry : sortedMap.int2ObjectEntrySet()) {
                    final java.io.File file = entry.getValue();
                    final byte[] bytes = IOUtils.toByteArray(new FileInputStream(file));
                    packModel(Integer.parseInt(file.getName().replace(".dat", "")), bytes);
                }
            }
            //Scroll boxes
            packModel(53000, java.nio.file.Files.readAllBytes(Paths.get("assets/models/scroll boxes/39028.dat")));
            packModel(53001, java.nio.file.Files.readAllBytes(Paths.get("assets/models/scroll boxes/39029.dat")));
            packModel(53002, java.nio.file.Files.readAllBytes(Paths.get("assets/models/scroll boxes/39030.dat")));
            packModel(53003, java.nio.file.Files.readAllBytes(Paths.get("assets/models/scroll boxes/39031.dat")));
            packModel(53004, java.nio.file.Files.readAllBytes(Paths.get("assets/models/scroll boxes/39032.dat")));
            packModel(53005, java.nio.file.Files.readAllBytes(Paths.get("assets/models/scroll boxes/39033.dat")));
            packModel(57577, java.nio.file.Files.readAllBytes(Paths.get("assets/models/clue progresser/1.dat")));
            packModel(57578, java.nio.file.Files.readAllBytes(Paths.get("assets/models/clue progresser/2.dat")));
            packModel(57579, java.nio.file.Files.readAllBytes(Paths.get("assets/models/clue progresser/3.dat")));
            packModel(57580, java.nio.file.Files.readAllBytes(Paths.get("assets/models/clue progresser/4.dat")));
            packModel(57581, java.nio.file.Files.readAllBytes(Paths.get("assets/models/clue progresser/5.dat")));
            TypeParser.packModel(57576, org.apache.commons.compress.utils.IOUtils.toByteArray(new FileInputStream(new File("assets/dice bag/item_model.dat"))));
        } catch (IOException e) {
            log.error("", e);
        }
    }

    public static void packModel(final int id, final byte[] bytes) {
        Game.getCacheMgi().getArchive(ArchiveType.MODELS).addGroup(new Group(id, new mgi.tools.jagcached.cache.File(new ByteBuffer(bytes))));
    }

    public static void packSound(final int id, final byte[] bytes) {
        Game.getCacheMgi().getArchive(ArchiveType.SYNTHS).addGroup(new Group(id, new mgi.tools.jagcached.cache.File(new ByteBuffer(bytes))));
    }

    private static void packClientScripts() throws IOException {
        //packClientScript(73, java.nio.file.Files.readAllBytes(Paths.get("assets/cs2/bank_command/73.cs2")));
        //packClientScript(386, java.nio.file.Files.readAllBytes(Paths.get("assets/cs2/tourny_fog/386.cs2")));
        var files = Paths.get("assets/cs2/old_jagex/").toFile().listFiles();
        for (var file : files) {
            var id = Integer.parseInt(file.getName().replaceAll(".cs2", ""));
            packClientScript(id, java.nio.file.Files.readAllBytes(file.toPath()));
        }
        packClientScript(393, java.nio.file.Files.readAllBytes(Paths.get("assets/cs2/skill_tab_construction/393.cs2")));
        //packClientScript(395, java.nio.file.Files.readAllBytes(Paths.get("assets/cs2/skill_tab_construction/395.cs2")));
        //packClientScript(687, java.nio.file.Files.readAllBytes(Paths.get("assets/cs2/ironman_setup/687.cs2")));
        //packClientScript(1004, java.nio.file.Files.readAllBytes(Paths.get("assets/cs2/experience_drops_multiplier.cs2")));
        packClientScript(1261, java.nio.file.Files.readAllBytes(Paths.get("assets/cs2/tourny_fog/1261.cs2")));
        packClientScript(1705, java.nio.file.Files.readAllBytes(Paths.get("assets/cs2/edgeville_map_link/1705.cs2")));
        //packClientScript(2066, java.nio.file.Files.readAllBytes(Paths.get("assets/cs2/broadcast_custom_links/2066.cs2")));
        //packClientScript(2094, java.nio.file.Files.readAllBytes(Paths.get("assets/cs2/ironman_setup/2094.cs2")));
        //packClientScript(2096, java.nio.file.Files.readAllBytes(Paths.get("assets/cs2/ironman_setup/2096.cs2")));
        packClientScript(2186, java.nio.file.Files.readAllBytes(Paths.get("assets/cs2/tourny_viewer/2186.cs2")));
        //packClientScript(2200, java.nio.file.Files.readAllBytes(Paths.get("assets/cs2/achievement_diary_sizes/2200.cs2")));
        //packClientScript(699, java.nio.file.Files.readAllBytes(Paths.get("assets/cs2/emote_tab/699.cs2")));
        //packClientScript(701, java.nio.file.Files.readAllBytes(Paths.get("assets/cs2/emote_tab/701.cs2")));
        //packClientScript(702, java.nio.file.Files.readAllBytes(Paths.get("assets/cs2/emote_tab/702.cs2")));
        /*for (int id = 3500; id <= 3505; id++) {
            packClientScript(id, java.nio.file.Files.readAllBytes(Paths.get("assets/cs2/game_noticeboard/" + id + ".cs2")));
        }*/
        for (int id = 20585; id <= 20588; id++) {
            packClientScript(id, java.nio.file.Files.readAllBytes(Paths.get("assets/cs2/game_noticeboard/" + id)));
        }
        for (int id = 20000; id <= 20015; id++) {
            packClientScript(id, java.nio.file.Files.readAllBytes(Paths.get("assets/cs2/teleport_menu/" + id)));
        }
        packClientScript(10100, java.nio.file.Files.readAllBytes(Paths.get("assets/cs2/ironman_setup/10100.cs2")));
        for (int i = 10034; i <= 10048; i++) {
            packClientScript(i, java.nio.file.Files.readAllBytes(Paths.get("assets/cs2/wheel_of_fortune/" + i + ".cs2")));
        }
        for (int id = 10102; id <= 10121; id++) {
            packClientScript(id, java.nio.file.Files.readAllBytes(Paths.get("assets/cs2/drop_viewer/" + id + ".cs2")));
        }
        for (int id = 10200; id <= 10202; id++) {
            packClientScript(id, java.nio.file.Files.readAllBytes(Paths.get("assets/cs2/game_settings/" + id + ".cs2")));
        }
        for (int id = 20300; id <= 20306; id++) {
            packClientScript(id, java.nio.file.Files.readAllBytes(Paths.get("assets/cs2/daily_challenges/" + id)));
        }
        for (int id = 10400; id <= 10405; id++) {
            packClientScript(id, java.nio.file.Files.readAllBytes(Paths.get("assets/cs2/tourny_info/" + id + ".cs2")));
        }
        /*for (int id = 10500; id <= 10518; id++) {
            packClientScript(id, java.nio.file.Files.readAllBytes(Paths.get("assets/cs2/tourny_presets/" + id + ".cs2")));
        }*/
        packClientScript(10600, java.nio.file.Files.readAllBytes(Paths.get("assets/cs2/tourny_viewer/10600.cs2")));
        packClientScript(10700, java.nio.file.Files.readAllBytes(Paths.get("assets/cs2/hide_roofs/10700.cs2")));
        for (var file : Objects.requireNonNull(new File("assets/cs2/ge_offers/").listFiles())) {
            if (!file.isFile())
                continue;
            var id = Integer.parseInt(file.getName());
            packClientScript(id, Files.toByteArray(file));
        }
        packClientScript(336, java.nio.file.Files.readAllBytes(Paths.get("assets/cs2/godwars_dungeon/336.cs2")));
        packClientScript(342, java.nio.file.Files.readAllBytes(Paths.get("assets/cs2/godwars_dungeon/342.cs2")));
        for (int i = 10900; i <= 10912; i++) {
            packClientScript(i, java.nio.file.Files.readAllBytes(Paths.get("assets/cs2/eco_presets/" + i + ".cs2")));
        }
        packClientScript(1311, java.nio.file.Files.readAllBytes(Paths.get("assets/cs2/tog_sidepanel_timer.cs2")));
        packRustyScripts("assets/scripts/out/");
        for (File file : new File("assets/cs2/custom").listFiles()) {
            if (!file.isFile())
                continue;
            String name = file.getName();
            if (!name.endsWith(".cs2"))
                continue;
            int id = Integer.parseInt(name.substring(0, name.length() - 4));
            packClientScript(id, Files.toByteArray(file));
        }
    }

    public static void packRustyScripts(String folderPath) {
        for (File file : Objects.requireNonNull(Paths.get(folderPath).toFile().listFiles())) {
            try {
                final int id = Integer.parseInt(file.getName().replace("-0.bin", "").replace("12-", ""));
                packClientScript(id, java.nio.file.Files.readAllBytes(file.toPath()));
            } catch (Exception e) {
                System.err.println("File name of " + file + " must be an integer!");
                e.printStackTrace();
            }
        }
    }

    public static void packClientScript(final int id, final byte[] bytes) {
        Game.getCacheMgi().getArchive(ArchiveType.CLIENTSCRIPTS).addGroup(new Group(id, new mgi.tools.jagcached.cache.File(new ByteBuffer(bytes))));
    }

    private static void packInterfaces() throws IOException {
        final Cache cache = Game.getCacheMgi();
        packInterfacesInner(cache, Paths.get("assets/interfaces").toFile().listFiles());
    }

    private static void packInterfacesInner(final Cache cache, final File[] folders) {
        for (File interfaceFolder : folders) {
            if (!interfaceFolder.isDirectory()) {
                continue;
            }
            final int groupId = Integer.parseInt(interfaceFolder.getName());
            System.out.println("Creating interface[" + groupId + "]");
            final Group group = new Group(groupId);
            Arrays.stream(Objects.requireNonNull(interfaceFolder.listFiles())).mapToInt(file -> Integer.parseInt(file.getName())).sorted().forEach(id -> {
                // System.out.println("\t adding["+id+"]");
                final Path path = interfaceFolder.toPath().resolve(Integer.toString(id));
                try {
                    group.addFile(new mgi.tools.jagcached.cache.File(new ByteBuffer(java.nio.file.Files.readAllBytes(path))));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            cache.getArchive(ArchiveType.INTERFACES).addGroup(group);
        }
    }

    public static final boolean PACK_ZENYTE_HOME = false;

    public static void packMap(final int id, final byte[] landscape, final byte[] map) {
        var cache = Game.getCacheMgi();
        var archive = cache.getArchive(ArchiveType.MAPS);
        var xteas = XTEALoader.getXTEAs(id);
        var regionX = id >> 8;
        var regionY = id & 0xFF;

        var mapGroup = archive.findGroupByName("m" + regionX + "_" + regionY);

        var landGroup = archive.findGroupByName("l" + regionX + "_" + regionY, xteas);

        if (map != null) {
            if (landGroup != null) {
                landGroup.findFileByID(0).setData(new ByteBuffer(map));
            } else {
                var newLandGroup = new Group(archive.getFreeGroupID(), new mgi.tools.jagcached.cache.File(new ByteBuffer(map)));
                newLandGroup.setName("l" + regionX + "_" + regionY);
                archive.addGroup(newLandGroup);
            }
        }

        if (landscape != null) {
            if (mapGroup != null) {
                mapGroup.findFileByID(0).setData(new ByteBuffer(landscape));
            } else {
                var newMapGroup = new Group(archive.getFreeGroupID() + 1, new mgi.tools.jagcached.cache.File(new ByteBuffer(landscape)));
                newMapGroup.setName("m" + regionX + "_" + regionY);
                newMapGroup.setXTEA(xteas);
                archive.addGroup(newMapGroup);
            }
        }

    }


    private static void packMaps() throws IOException {


        packMap(6213, java.nio.file.Files.readAllBytes(Paths.get("assets/map/1473.dat")), java.nio.file.Files.readAllBytes(Paths.get("assets/map/1474.dat")));


        //How to implement
/*
        Collection<WorldObject> homeObjects = Arrays.asList(
                new WorldObject(NullObjectID.NULL, 10, 0, new Location(3117, 3474, 0)), //Lectern w/ study option at home.
                new WorldObject(NullObjectID.NULL, 10, 0, new Location(3117, 3481, 0)), //Lectern w/ study option at home.
                new WorldObject(StashUnit.EDGEVILLE_GENERAL_STORE.getObjectId(), 10, 0, new Location(3079, 3484, 0)), //Map hyperlink for edgeville dungeon.
                new WorldObject(35008, 10, 3, new Location(3092, 3508, 1)), //Crystal chest
                new WorldObject(NullObjectID.NULL_14108, 22, 0, new Location(3101, 3487, 0)), //Map icon for portal.
                new WorldObject(ObjectId.CLOSED_CHEST_172, 10, 2, new Location(3091, 3511, 0)), //Map icon for spiritual tree.
                new WorldObject(NullObjectID.NULL_7389, 22, 0, new Location(3097, 3503, 0)), //Map icon for emblem trader.
                new WorldObject(NullObjectID.NULL_7389, 22, 0, new Location(3097, 3488, 0)), //Spiritual fairy ring.
                new WorldObject(NullObjectID.NULL_673, 22, 0, new Location(3119, 3511, 0)), //Portal
                new WorldObject(35003, 10, 1, new Location(3096, 3487, 0)), //Box of Restoration
                new WorldObject(35000, 10, 2, new Location(3095, 3503, 0)), //Wilderness statistics
                new WorldObject(35001, 10, 0, new Location(3090, 3486, 0)), //pottery wheel icon
                new WorldObject(ObjectId.WILDERNESS_STATISTICS, 10, 0, new Location(3085, 3509, 0)), //water source icon
                new WorldObject(NullObjectID.NULL_2774, 22, 0, new Location(3114, 3506, 0)), //anvil icon
                new WorldObject(NullObjectID.NULL_2771, 22, 0, new Location(3109, 3505, 0)), //furnace icon
                new WorldObject(NullObjectID.NULL_2743, 22, 0, new Location(3116, 3502, 0)), //Daily board
                new WorldObject(NullObjectID.NULL_2742, 22, 0, new Location(3113, 3497, 0)), //Magic storage unit
                new WorldObject(35023, 4, 3, new Location(3085, 3486, 0)), // Easter modifications
                new WorldObject(35024, 10, 0, new Location(3093, 3511, 0))
        );

        modernProcess.processMapWithObject(6213,java.nio.file.Files.readAllBytes(Paths.get("assets/map/1474.dat")),
                java.nio.file.Files.readAllBytes(Paths.get("assets/map/1473.dat")), null, null);

       */


        /**
         *  if you want to inject objects -> {@code parameters(region: int, landscapePath: final byte[], MapUptils.inject(mapPath: final byte[], Predicate<WorldObject>, WorldObject...)
         *  else
         *  {@code parameters(region: int, landscapePath: final byte[], mapPath: final byte[])
         */

        if (PACK_ZENYTE_HOME) {
            packMap(12342, java.nio.file.Files.readAllBytes(Paths.get("assets/map/home28_l.dat")), MapUtils.inject(java.nio.file.Files.readAllBytes(Paths.get("assets/map/home28_m.dat")), null,
                    new WorldObject(20132, 22, 0, new Location(3092, 3503, 0))));
        }

        packMapPre209(9261, java.nio.file.Files.readAllBytes(Paths.get("assets/map/island_l_regular.dat")), MapUtils.inject(java.nio.file.Files.readAllBytes(Paths.get("assets/map/island_m_regular.dat")), o -> {
            if (o.getId() == 46087) {
                o.setId(46089);
            }
            return false;
        }));
        packMapPre209(10388, java.nio.file.Files.readAllBytes(Paths.get("assets/map/yanille/328.dat")), java.nio.file.Files.readAllBytes(Paths.get("assets/map/yanille/329.dat")));
        packMapPre209(11567, null, MapUtils.inject(11567, null, new WorldObject(ObjectId.GNOME_GLIDER, 10, 1, new Location(2919, 3054, 0))));
        packMapPre209(11595, null, MapUtils.inject( 11595, null, new WorldObject(ObjectId.BANK_DEPOSIT_BOX_26254, 10, 0, new Location(2931, 4822, 0)), new WorldObject(ObjectId.BANK_DEPOSIT_BOX_26254, 10, 0, new Location(2896, 4821, 0)), new WorldObject(ObjectId.BANK_DEPOSIT_BOX_26254, 10, 1, new Location(2900, 4845, 0)), new WorldObject(ObjectId.BANK_DEPOSIT_BOX_26254, 10, 3, new Location(2920, 4848, 0))));

        // SSL Home Objects
        packMapPre209(7223, null, MapUtils.inject(7223, null, new WorldObject(ObjectID.ROCKS_2584, 10, 0, new Location(1845, 3556, 0)))); //Beginner Dungeon Entrance
        //End of Home Objects
        packMapPre209(13109, null, MapUtils.inject(13109, null, new WorldObject(ObjectId.GNOME_GLIDER, 10, 1, new Location(3322, 3428, 0))));
        packMapPre209(14477, java.nio.file.Files.readAllBytes(Paths.get("assets/map/dmm_tourny/m56_141.dat")), java.nio.file.Files.readAllBytes(Paths.get("assets/map/dmm_tourny/l56_141.dat")));
        packMapPre209(14478, java.nio.file.Files.readAllBytes(Paths.get("assets/map/dmm_tourny/m56_142.dat")), java.nio.file.Files.readAllBytes(Paths.get("assets/map/dmm_tourny/l56_142.dat")));
        packMapPre209(14733, java.nio.file.Files.readAllBytes(Paths.get("assets/map/dmm_tourny/m57_141.dat")), java.nio.file.Files.readAllBytes(Paths.get("assets/map/dmm_tourny/l57_141.dat")));
        packMapPre209(14734, java.nio.file.Files.readAllBytes(Paths.get("assets/map/dmm_tourny/m57_142.dat")), java.nio.file.Files.readAllBytes(Paths.get("assets/map/dmm_tourny/l57_142.dat")));
        packMapPre209(15245, java.nio.file.Files.readAllBytes(Paths.get("assets/map/tournament/2.dat")), java.nio.file.Files.readAllBytes(Paths.get("assets/map/tournament/3.dat")));
        packMapPre209(15248, java.nio.file.Files.readAllBytes(Paths.get("assets/map/tournament/0.dat")), MapUtils.inject(java.nio.file.Files.readAllBytes(Paths.get("assets/map/tournament/1.dat")), null, new WorldObject(35005, 10, 3, new Location(3806, 9245, 0)), new WorldObject(35006, 10, 1, new Location(3813, 9256, 0)), new WorldObject(35007, 10, 0, new Location(3799, 9256, 0))));
        packMapPre209(13139, null, MapUtils.inject(13139, null, new WorldObject(35020, 10, 0, new Location(3279, 5345, 2)), new WorldObject(35020, 10, 0, new Location(3312, 5344, 2))));
        packMapPre209(13395, null, MapUtils.inject(13395, null, new WorldObject(35020, 10, 0, new Location(3343, 5346, 2))));
        packMapPre209(4674, java.nio.file.Files.readAllBytes(Paths.get("assets/map/godwars-instances/Armadyl/0.dat")), MapUtils.inject(java.nio.file.Files.readAllBytes(Paths.get("assets/map/godwars-instances/Armadyl/1.dat")), o -> {
            if (o.getId() == ObjectId.EXIT_PORTAL_20843) {
                o.setId(35016);
            } else if (o.getId() == ObjectId.CREVICE_26769) {
                o.setId(35013);
            } else if (o.getId() == ObjectId.DANGER_SIGN_23708) {
                o.setId(35019);
            }
            return false;
        }));
        packMapPre209(4675, java.nio.file.Files.readAllBytes(Paths.get("assets/map/godwars-instances/Bandos/0.dat")), MapUtils.inject(java.nio.file.Files.readAllBytes(Paths.get("assets/map/godwars-instances/Bandos/1.dat")), o -> {
            if (o.getId() == ObjectId.PRIVATE_PORTAL) {
                o.setId(35014);
            } else if (o.getId() == ObjectId.CREVICE_26769) {
                o.setId(35013);
            } else if (o.getId() == ObjectId.DANGER_SIGN_23708) {
                o.setId(35019);
            }
            return o.hashInRegion() == new Location(1191, 4306, 0).hashInRegion();
        }, new WorldObject(35019, 10, 0, new Location(1189, 4313, 0))));
        packMapPre209(4676, java.nio.file.Files.readAllBytes(Paths.get("assets/map/godwars-instances/Zamorak/0.dat")), MapUtils.inject(java.nio.file.Files.readAllBytes(Paths.get("assets/map/godwars-instances/Zamorak/1.dat")), o -> {
            if (o.getId() == ObjectId.PORTAL_34752) {
                o.setId(35015);
            } else if (o.getId() == ObjectId.CREVICE_26769) {
                o.setId(35013);
            } else if (o.getId() == ObjectId.DANGER_SIGN_23708) {
                o.setId(35019);
            }
            //Removes object which produces ambient waterfall sound and the stash unit.
            return o.getId() == 16399 || o.getId() == 29054;
        }));
        packMapPre209(4677, java.nio.file.Files.readAllBytes(Paths.get("assets/map/godwars-instances/Saradomin/0.dat")), MapUtils.inject(java.nio.file.Files.readAllBytes(Paths.get("assets/map/godwars-instances/Saradomin/1.dat")), o -> {
            if (o.getId() == ObjectId.PORTAL_26740) {
                o.setId(35017);
            } else if (o.getId() == ObjectId.STEPPING_STONE_21120) {
                o.setId(35018);
            } else if (o.getId() == ObjectId.DANGER_SIGN_23708) {
                o.setId(35019);
            }
            return o.getId() == 26375 || (o.getXInRegion() == (1203 & 63) && o.getYInRegion() == (4422 & 63));
        }, new WorldObject(NullObjectID.NULL_17030, 22, 0, 1195, 4440, 0)));
        packMapPre209(11346, java.nio.file.Files.readAllBytes(Paths.get("assets/map/godwars-instances/Armadyl/1858.dat")), MapUtils.inject(java.nio.file.Files.readAllBytes(Paths.get("assets/map/godwars-instances/Armadyl/1859.dat")), o -> {
            if (o.getId() == ObjectId.EXIT_PORTAL_20843) {
                o.setId(35016);
            } else if (o.getId() == ObjectId.CREVICE_26769) {
                o.setId(35013);
            } else if (o.getId() == ObjectId.DANGER_SIGN_23708) {
                o.setId(35019);
            }
            return false;
        }, new WorldObject(26502, 10, 3, 2839, 5295, 2), new WorldObject(NullObjectID.NULL, 10, 0, 2840, 5294, 2), new WorldObject(NullObjectID.NULL, 10, 0, 2838, 5294, 2)));
        packMapPre209(11347, java.nio.file.Files.readAllBytes(Paths.get("assets/map/godwars-instances/Bandos/1860.dat")), MapUtils.inject(java.nio.file.Files.readAllBytes(Paths.get("assets/map/godwars-instances/Bandos/1861.dat")), o -> {
            if (o.getId() == ObjectId.PRIVATE_PORTAL) {
                o.setId(35014);
            } else if (o.getId() == ObjectId.CREVICE_26769) {
                o.setId(35013);
            } else if (o.getId() == ObjectId.DANGER_SIGN_23708) {
                o.setId(35019);
            }
            return o.hashInRegion() == new Location(2856, 5357, 2).hashInRegion();
        }, new WorldObject(35019, 10, 0, new Location(2854, 5364, 2))));
        packMapPre209(11602, java.nio.file.Files.readAllBytes(Paths.get("assets/map/godwars-instances/Saradomin/1862.dat")), MapUtils.inject(java.nio.file.Files.readAllBytes(Paths.get("assets/map/godwars-instances/Saradomin/1863.dat")), o -> {
            if (o.getId() == ObjectId.PORTAL_26740) {
                o.setId(35017);
            } else if (o.getId() == ObjectId.STEPPING_STONE_21120) {
                o.setId(35018);
            } else if (o.getId() == ObjectId.DANGER_SIGN_23708) {
                o.setId(35019);
            }
            return false;
        }, new WorldObject(NullObjectID.NULL_17030, 22, 0, 2923, 5272, 0)));
        packMapPre209(11603, MapUtils.processTiles(new ByteBuffer(java.nio.file.Files.readAllBytes(Paths.get("assets/map/godwars-instances/Zamorak/1856.dat"))), tile -> {
            if (tile.getUnderlayId() == 23) {
                tile.setUnderlayId((byte) 0);
            }
            if (tile.getOverlayId() == 33) {
                tile.setOverlayId((byte) 0);
            }
        }).getBuffer(), MapUtils.inject(java.nio.file.Files.readAllBytes(Paths.get("assets/map/godwars-instances/Zamorak/1857.dat")), o -> {
            if (o.getId() == ObjectId.PORTAL_34752) {
                o.setId(35015);
            } else if (o.getId() == ObjectId.CREVICE_26769) {
                o.setId(35013);
            } else if (o.getId() == ObjectId.DANGER_SIGN_23708) {
                o.setId(35019);
            }
            return false;
        }));
        final WorldMapDefinitions godwarsDefs = WorldMapDefinitions.decode("godwars");
        godwarsDefs.updateFullChunks(11602, 11601, 0, 1, 4);
        godwarsDefs.updateFullChunks(11603, 2);
        godwarsDefs.updateFullChunks(11346, 2);
        godwarsDefs.updateFullChunks(11347, 2);
        godwarsDefs.encode("godwars");
        final WorldMapDefinitions defs = WorldMapDefinitions.decode("main");
        defs.setName("Surface");
        defs.update(9261, 0);
        defs.update(11567, 0);
        defs.update(12342, 0);
        defs.update(13109, 0);
        defs.encode("main");
    }

    public static void removeCATasks() {
        final String[] tasksToRemove = { "Fighting as Intended II", "Fighting as Intended", "Fragment of Seren Speed-Trialist", "Galvek Speed-Trialist", "Glough Speed-Trialist", "The Flame Skipper", "Arooo No More", "Perfect Olm (Solo)", "Perfect Olm (Trio)", "A Not So Special Lizard", "Chambers of Xeric: CM (5-Scale) Speed-Chaser", "Chambers of Xeric: CM (Solo) Speed-Chaser", "Moving Collateral", "Perfect Corrupted Hunllef", "Perfect Crystalline Hunllef", "Perfect Nightmare", "Perfect Maiden", "Pop It", "Perfect Nylocas", "Perfect Verzik", "Perfect Sotesteg", "Perfect Bloat", "Can't Drain This", "Perfect Xarpus", "Nibblers, Begone!", "You Didn't Say Anything About a Bat", "Fight Caves Speed-Chaser", "Denying the Healers", "The Walk", "Perfect Zulrah", "Chambers of Xeric (Solo) Speed-Runner", "Chambers of Xeric (5-Scale) Speed-Runner", "Chambers of Xeric (Trio) Speed-Runner", "Chambers of Xeric: CM (Solo) Speed-Runner", "Egniol Diet II", "Corrupted Gauntlet Speed-Runner", "Defence Matters", "Perfect Nex", "Perfect Phosani's Nightmare", "Phosani's Speedrunner", "Nightmare (5-Scale) Speed-Runner", "Terrible Parent", "A Long Trip", "Perfect Theatre", "Theatre (5-Scale) Speed-Runner", "Theatre (Duo) Speed-Runner", "Theatre (4-Scale) Speed-Runner", "Theatre (Trio) Speed-Runner", "Wasn't Even Close", "Nibbler Chaser", "The Floor Is Lava", "No Luck Required", "Jad? What Are You Doing Here?", "Budget Setup", "Playing with Jads", "Facing Jad Head-on II", "Denying the Healers II", "No Time for a Drink", "Morytania Only", "Expert Tomb Explorer", "Something of an expert myself", "Expert tomb looter", "Ba-bananza", "Rockin' around the croc", "Doesn't bug me", "All out of medics", "Warden't you believe it", "Resourceful raider", "But... Damage", "Fancy feet", "Tombs speed runner ii", "Tombs speed runner iii", "Amascut's remnant", "Maybe I'm the boss.", "Expert tomb raider", "Akkhan't do it", "All praise zebak", "Perfection of het", "Perfection of apmeken", "Perfection of crondis", "Perfection of scabaras", "Insanity", "Tomb Explorer", "Hardcore raiders", "Hardcore tombs", "Helpful spirit who?", "Dropped the ball", "No skipping allowed", "Down do specs", "Perfect het", "Perfect apmeken", "Perfect crondis", "I'm in a rush", "You are not prepared", "Tomb looter", "Tomb raider", "Tombs speed runner", "Better get movin'", "Chompington", "Perfect akkha", "Perfect ba-ba", "Perfect zebak", "Perfect scabaras", "Perfect kephri", "Perfect Wardens", "Novice Tomb explorer", "Novice tomb looter", "Movin' on up", "Confident raider", "Novice tomb raider", "Into the den of giants", "Not so great after all", "Tempoross novice", "Master of buckets", "Calm before the storm", "Fire in the hole!", "Tempoross Champion", "The Lone Angler", "Dress Like You Mean It", "Why Cook?", "Theatre of Blood: SM Adept", "Anticoagulants", "Appropriate Tools", "They Won't Expect This", "Chally Time", "Nylocas, On the Rocks", "Just To Be Safe", "Don't Look at Me!", "No-Pillar", "Attack, Step, Wait", "Pass It On", "Theatre of Blood: SM Speed-Chaser", "The II Jad Challenge", "TzHaar-Ket-Rak's Speed-Trialist", "Facing Jad Head-on III", "The IV Jad Challenge", "TzHaar-Ket-Rak's Speed-Chaser", "Facing Jad Head-on IV", "Supplies? Who Needs 'em?", "Multi-Style Specialist", "Hard Mode? Completed It", "The VI Jad Challenge", "TzHaar-Ket-Rak's Speed-Runner", "It Wasn't a Fluke", "Versatile Drainer", "Blind Spot", "Hard Mode? Completed It", "Stop Right There!", "Personal Space", "Royal Affairs", "Harder Mode I", "Harder Mode II", "Nylo Sniper", "Team Work Makes the Dream Work", "Harder Mode III", "Pack Like a Yak", "Theatre: HM (Trio) Speed-Runner", "Theatre: HM (4-Scale) Speed-Runner", "Theatre: HM (5-Scale) Speed-Runner", "Theatre of Blood: HM Grandmaster" };
        //TODO add desc rewriting for some tasks based on the desc in the document
        final int[] tierEnums = { 3981, 3982, 3983, 3984, 3985, 3986 };
        for (int enumId : tierEnums) {
            EnumDefinitions enumDefs = EnumDefinitions.get(enumId);
            final HashMap<Integer, Object> values = new HashMap<>();
            final int highestKey = enumDefs.getValues().size() + 1;
            for (Map.Entry<Integer, Object> entry : enumDefs.getValues().entrySet()) {
                StructDefinitions struct = StructDefinitions.get((int) entry.getValue());
                final String name = struct.getParamAsString(1308);
                values.put(entry.getKey(), entry.getValue());
                for (String task : tasksToRemove) {
                    if (task.equalsIgnoreCase(name)) {
                        values.remove(entry.getKey());
                        //System.out.printf("Removed task -> %s\n", name);
                        break;
                    }
                }
            }
            final HashMap<Integer, Object> filteredValues = new HashMap<>();
            int filterIndx = 0;
            for (int i = 0; i <= highestKey; i++) {
                if (values.containsKey(i)) {
                    filteredValues.put(filterIndx++, values.get(i));
                }
            }
            enumDefs.setValues(filteredValues);
            definitions.add(enumDefs);
        }
    }

    public static List<Definitions> getDefinitions() {
        return TypeParser.definitions;
    }

    public static void packMapRawPre209(Cache cache, int regionID, byte[] landData, byte[] mapData) {
        int x = (regionID >> 8) & 0xFF;
        int y = regionID & 0xFF;
        byte[] outputMapData = Optional.ofNullable(mapData).map(data -> new MapSaver().save(new MapLoaderPre209().load(x, y, data))).orElse(null);
        byte[] outputLandData = Optional.ofNullable(landData).map(data -> new LocationSaver().save(new LocationsLoader().load(x, y, data))).orElse(null);
        packMap(cache, regionID, outputMapData, outputLandData);
    }

    public static void packMapPre209(final int id, String landscapeFilePath, String mapFilePath) throws IOException {
        try {
            packMapRawPre209(Game.getCacheMgi(), id, java.nio.file.Files.readAllBytes(Paths.get(mapFilePath)), java.nio.file.Files.readAllBytes(Paths.get(landscapeFilePath)));
            System.err.println("Packed map[" + id + "] land = " + landscapeFilePath + ", map = " + mapFilePath);
        } catch (Exception e) {
            System.err.println("Failed to pack map[" + id + "] land = " + landscapeFilePath + ", map = " + mapFilePath);
            e.printStackTrace();
        }
    }

    public static void packMapPre209(final int id, final byte[] landscape, final byte[] map) {
        try {
            packMapRawPre209(Game.getCacheMgi(), id, map, landscape);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to pack map[" + id + "]");
        }
    }

    public static void packMap(final int id, String landscapeFilePath, String mapFilePath) throws IOException {
        packMap(Game.getCacheMgi(), id, java.nio.file.Files.readAllBytes(Paths.get(landscapeFilePath)), java.nio.file.Files.readAllBytes(Paths.get(mapFilePath)));
    }
/*
    public static void packMap(final int id, final byte[] map, final byte[] landscape) {
        packMap(Game.getCacheMgi(), id, landscape, map);
    }
*/
    public static void packMap(final Cache cache, final int id, final byte[] landscape, final byte[] map) {
        if (!ENABLED_MAP_PACKING) {
            System.out.println("Skipping packing map[" + id + ']');
            return;
        }
        try {
            final Archive archive = cache.getArchive(ArchiveType.MAPS);
            final int[] xteas = XTEALoader.getXTEAs(id);
            final int regionX = id >> 8;
            final int regionY = id & 255;
            final Group mapGroup = archive.findGroupByName("m" + regionX + "_" + regionY);
            Group landGroup = archive.findGroupByName("l" + regionX + "_" + regionY, null, false);
            if (landGroup != null) {
                archive.deleteGroup(landGroup);
                landGroup = null;
            }
            if (map != null) {
                if (landGroup != null) {
                    landGroup.findFileByID(0).setData(new ByteBuffer(map));
                } else {
                    final Group newLandGroup = new Group(archive.getFreeGroupID(), new mgi.tools.jagcached.cache.File(new ByteBuffer(map)));
                    newLandGroup.setName("l" + regionX + "_" + regionY);
                    archive.addGroup(newLandGroup);
                }
            }
            if (landscape != null) {
                if (mapGroup != null) {
                    mapGroup.findFileByID(0).setData(new ByteBuffer(landscape));
                } else {
                    final Group newMapGroup = new Group(archive.getFreeGroupID() + 1, new mgi.tools.jagcached.cache.File(new ByteBuffer(landscape)));
                    newMapGroup.setName("m" + regionX + "_" + regionY);
                    newMapGroup.setXTEA(null);
                    archive.addGroup(newMapGroup);
                }
            }
            System.out.println("Packed map[" + id + "]");
        } catch (Exception e) {
            System.err.println("Failed to pack map[" + id + "]");
            e.printStackTrace();
        }
    }

    public static void packInv(final int id, final byte[] bytes) {
        Game.getCacheMgi().getArchive(ArchiveType.CONFIGS).findGroupByID(GroupType.INV).addFile(new mgi.tools.jagcached.cache.File(id, new ByteBuffer(bytes)));
    }

    public static void packInvs() throws IOException {
        for (File file : Paths.get("assets/inv").toFile().listFiles()) {
            if (!file.getName().contains(".")) {
                try {
                    final int id = Integer.parseInt(file.getName());
                    packInv(id, java.nio.file.Files.readAllBytes(file.toPath()));
                } catch (Exception e) {
                    System.err.println("Failed to pack enum file " + file + " name must be an int!");
                    e.printStackTrace();
                }
            }
        }
    }

    public static void packEnums() throws IOException {
        for (File file : Paths.get("assets/enum").toFile().listFiles()) {
            if (!file.getName().contains(".")) {
                try {
                    final int id = Integer.parseInt(file.getName());
                    packEnum(id, java.nio.file.Files.readAllBytes(file.toPath()));
                } catch (Exception e) {
                    System.err.println("Failed to pack enum file " + file + " name must be an int!");
                    e.printStackTrace();
                }
            }
        }
    }

    public static void packEnum(final int id, final byte[] bytes) {
        Game.getCacheMgi().getArchive(ArchiveType.CONFIGS).findGroupByID(GroupType.ENUM).addFile(new mgi.tools.jagcached.cache.File(id, new ByteBuffer(bytes)));
    }

    public static void packStructs() throws IOException {
        for (File file : Paths.get("assets/structs").toFile().listFiles()) {
            if (!file.getName().contains(".")) {
                try {
                    final int id = Integer.parseInt(file.getName());
                    packStruct(id, java.nio.file.Files.readAllBytes(file.toPath()));
                } catch (Exception e) {
                    System.err.println("Failed to pack struct file " + file + " name must be an int!");
                    e.printStackTrace();
                }
            }
        }
    }

    public static void packStruct(final int id, final byte[] bytes) {
        Game.getCacheMgi().getArchive(ArchiveType.CONFIGS).findGroupByID(GroupType.STRUCT).addFile(new mgi.tools.jagcached.cache.File(id, new ByteBuffer(bytes)));
    }

    public static void packParams() throws IOException {
        for (File file : Paths.get("assets/params").toFile().listFiles()) {
            if (!file.getName().contains(".")) {
                try {
                    final int id = Integer.parseInt(file.getName());
                    packParam(id, java.nio.file.Files.readAllBytes(file.toPath()));
                } catch (Exception e) {
                    System.err.println("Failed to pack param file " + file + " name must be an int!");
                    e.printStackTrace();
                }
            }
        }
    }

    public static void packParam(final int id, final byte[] bytes) {
        Game.getCacheMgi().getArchive(ArchiveType.CONFIGS).findGroupByID(GroupType.PARAMS).addFile(new mgi.tools.jagcached.cache.File(id, new ByteBuffer(bytes)));
    }
}
