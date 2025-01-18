package com.zenyte.utils;

import com.zenyte.Game;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.region.Chunk;
import com.zenyte.game.world.region.RSPolygon;
import com.zenyte.utils.efficientarea.Area;
import com.zenyte.utils.efficientarea.EfficientArea;
import com.zenyte.utils.efficientarea.Polygon;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import mgi.tools.jagcached.ArchiveType;
import mgi.tools.jagcached.cache.Archive;
import mgi.tools.jagcached.cache.Cache;
import mgi.tools.jagcached.cache.Group;

import java.awt.*;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Kris | 26. sept 2018 : 23:07:25
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a> TODO: Convert force multi area method from entity/region
 */
public class MultiwayArea implements MapPrinter {
    private static final Logger log = Logger.getLogger(MultiwayArea.class.getName());
    private static final Area[] MULTIWAY_AREA_POLYGONS = new Area[4];
    private static final List<List<Area>> polygons = new ArrayList<>(4);
    private static final int MIN_PLANE = 0;
    private static final int MAX_PLANE = 1;
    private static final Int2ObjectOpenHashMap<List<EfficientArea>> region2polygonmultis = new Int2ObjectOpenHashMap<>();

    static {
        for (int i = 0; i < 4; i++) polygons.add(new ArrayList<>());
    }

    public static void loadAndMap() {
        load();
        map();
    }

    public static void load() {
        for (int i = 0; i < 4; i++) {
            MULTIWAY_AREA_POLYGONS[i] = MapLocations.getMulticombat(i);
        }
        for (int z = 0; z < 4; z++) {
            final Area area = MULTIWAY_AREA_POLYGONS[z];
            final ArrayList<int[]> list = new ArrayList<>();
            final PathIterator it = area.getPathIterator(null);
            while (!it.isDone()) {
                final float[] coords = new float[6];
                it.currentSegment(coords);
                list.add(new int[]{(int) coords[0], (int) coords[1]});
                it.next();
            }
            final int[][] coords = new int[list.size()][2];
            int i = 0;
            for (final int[] intarr : list) {
                coords[i++] = intarr;
            }
            final ArrayList<RSPolygon> polygonList = new ArrayList<>();
            List<int[]> currentPolygon = new ArrayList<>();
            for (i = coords.length - 1; i >= 0; i--) {
                int x = coords[i][0];
                int y = coords[i][1];
                if (x == 0 && y == 0) {
                    constructAndAddPolygon(currentPolygon, polygonList);
                    currentPolygon = new ArrayList<>();
                    continue;
                }
                currentPolygon.add(coords[i]);
            }
            constructAndAddPolygon(currentPolygon, polygonList);
            for (RSPolygon rsPolygon : polygonList) {
                polygons.get(z).add(new Area(rsPolygon.getPolygon()));
            }
        }
    }

    private static void constructAndAddPolygon(final List<int[]> currentPolygon, final List<RSPolygon> polygonList) {
        if (!currentPolygon.isEmpty()) {
            int[][] currentPolyLines = new int[currentPolygon.size()][2];
            for (int a = 0; a < currentPolygon.size(); a++) {
                int[] ppp = currentPolygon.get(a);
                currentPolyLines[a] = ppp;
            }
            polygonList.add(new RSPolygon(currentPolyLines, 0));
        }
    }

    public static void map() {
        final IntOpenHashSet regionList = new IntOpenHashSet(2000);
        final java.util.List<java.util.List<Area>> allPolygons = MultiwayArea.getPolygons();
        final Cache cache = Game.getCacheMgi();
        final Archive archive = cache.getArchive(ArchiveType.MAPS);
        for (int rx = 0; rx < 100; rx++) {
            for (int ry = 0; ry < 256; ry++) {
                final Group group = archive.findGroupByName("m" + rx + "_" + ry);
                final int id = group == null ? -1 : group.getID();
                if (id != -1) {
                    regionList.add(rx << 8 | ry);
                }
            }
        }
        for (int z = 0; z < 4; z++) {
            final java.util.List<Area> polygons = allPolygons.get(z);
            for (int id : regionList) {
                final int x = (id >> 8) << 6;
                final int y = (id & 255) << 6;
                final Polygon chunkPolygon = new RSPolygon(new int[][]{new int[]{x, y}, new int[]{x + 64, y}, new int[]{x + 64, y + 64}, new int[]{x, y + 64}}, 0).getPolygon();
                final Area chunkArea = new Area(chunkPolygon);
                for (int i = polygons.size() - 1; i >= 0; i--) {
                    final Area polygon = polygons.get(i);
                    if (!polygon.getBounds2D().intersects(chunkArea.getBounds2D())) {
                        continue;
                    }
                    final Area area = new Area(polygon);
                    area.intersect(chunkArea);
                    if (!area.isEmpty()) {
                        final int regionId = regionHash(x, y, z);
                        java.util.List<EfficientArea> list = region2polygonmultis.get(regionId);
                        if (list == null) {
                            list = new ArrayList<>();
                            region2polygonmultis.put(regionId, list);
                        }
                        list.add(new EfficientArea(polygon));
                    }
                }
            }
        }
        WorldTasksManager.schedule(() -> {
            for (final Player player : World.getPlayers()) {
                player.checkMultiArea();
            }
            for (final NPC npc : World.getNPCs()) {
                npc.checkMultiArea();
            }
        });
    }

    private static int regionHash(final int x, final int y, final int z) {
        return (((x >> 6) << 8) + (y >> 6) | z << 16);
    }

    public static List<EfficientArea> addDynamicMultiArea(final Area area, final int plane) {
        final ArrayList<EfficientArea> efficientAreas = new ArrayList<>();
        final Rectangle2D bounds = area.getBounds2D();
        final int minRX = (int) bounds.getMinX() >> 6;
        final int minRY = (int) bounds.getMinY() >> 6;
        final int width = (int) Math.ceil(bounds.getWidth() / 64.0F);
        final int height = (int) Math.ceil(bounds.getHeight() / 64.0F);
        for (int rx = minRX; rx < (minRX + width); rx++) {
            for (int ry = minRY; ry < (minRY + height); ry++) {
                final int x = rx << 6;
                final int y = ry << 6;
                final Polygon chunkPolygon = new RSPolygon(new int[][]{new int[]{x, y}, new int[]{x + 64, y}, new int[]{x + 64, y + 64}, new int[]{x, y + 64}}, 0).getPolygon();
                final Area chunkArea = new Area(chunkPolygon);
                chunkArea.intersect(area);
                if (!area.isEmpty()) {
                    final int regionId = regionHash(x, y, plane);
                    java.util.List<EfficientArea> list = region2polygonmultis.get(regionId);
                    if (list == null) {
                        list = new ArrayList<>();
                        region2polygonmultis.put(regionId, list);
                    }
                    final EfficientArea a = new EfficientArea(area);
                    list.add(a);
                    efficientAreas.add(a);
                }
            }
        }
        return efficientAreas;
    }

    public static void removeDynamicMultiArea(final Chunk.RSArea area) {
        final Rectangle2D bounds = area.getArea().getBounds2D();
        final int minRX = (int) bounds.getMinX() >> 6;
        final int minRY = (int) bounds.getMinY() >> 6;
        final int width = (int) Math.ceil(bounds.getWidth() / 64.0F);
        final int height = (int) Math.ceil(bounds.getHeight() / 64.0F);
        for (int rx = minRX; rx < (minRX + width); rx++) {
            for (int ry = minRY; ry < (minRY + height); ry++) {
                final int regionId = regionHash(rx << 6, ry << 6, area.getHeight());
                final java.util.List<EfficientArea> list = region2polygonmultis.get(regionId);
                if (list != null) {
                    list.remove(area.getArea());
                    if (list.isEmpty()) {
                        region2polygonmultis.remove(regionId);
                    }
                }
            }
        }
    }

    /**
     * Checks whether the location is in a multi zone or not.
     *
     * @param location the location to check.
     * @return whether the location is in a multi zone or not.
     */
    public static boolean isMultiArea(final Location location) {
        final int regionId = regionHash(location.getX(), location.getY(), location.getPlane());
        final java.util.List<EfficientArea> list = region2polygonmultis.get(regionId);
        if (list == null) {
            return false;
        }
        final int x = location.getX();
        final int y = location.getY();
        EfficientArea polygon;
        for (int i = list.size() - 1; i >= 0; i--) {
            polygon = list.get(i);
            if (polygon.contains(x, y)) return true;
        }
        return false;
    }

    public static void main(final String[] args) throws IOException {
        load();
        for (int i = MIN_PLANE; i < MAX_PLANE; i++) new MultiwayArea().load(i);
    }

    public static List<List<Area>> getPolygons() {
        return MultiwayArea.polygons;
    }

    @Override
    public void draw(final Graphics2D graphics, final int plane) throws IOException {
        log.info("Drawing map image");
        final Color cyan = new Color(0, 255, 255, 127);
        final Color purple = new Color(140, 0, 116);
        final Area area = MULTIWAY_AREA_POLYGONS[plane];
        final ArrayList<int[]> list = new ArrayList<>();
        final PathIterator it = area.getPathIterator(null);
        while (!it.isDone()) {
            final float[] coords = new float[6];
            it.currentSegment(coords);
            list.add(new int[]{(int) coords[0], (int) coords[1]});
            it.next();
        }
        final int[][] coords = new int[list.size()][2];
        int i = 0;
        for (final int[] intarr : list) {
            coords[i++] = intarr;
        }
        final ArrayList<RSPolygon> polygonList = new ArrayList<>();
        List<int[]> currentPolygon = null;
        for (i = coords.length - 1; i >= 0; i--) {
            int x = coords[i][0];
            int y = coords[i][1];
            if (x == 0 && y == 0) {
                if (currentPolygon != null) {
                    int[][] currentPolyLines = new int[currentPolygon.size()][2];
                    for (int a = 0; a < currentPolygon.size(); a++) {
                        int[] ppp = currentPolygon.get(a);
                        currentPolyLines[a] = ppp;
                    }
                    polygonList.add(new RSPolygon(currentPolyLines, 0));
                    currentPolygon = new ArrayList<>();
                    continue;
                }
                currentPolygon = new ArrayList<>();
                continue;
            }
            coords[i][0] = getX(x);
            coords[i][1] = getY(y);
            currentPolygon.add(coords[i]);
        }
        for (RSPolygon polygon : polygonList) {
            graphics.setColor(cyan);
            Polygon p = polygon.getPolygon();
            graphics.fillPolygon(p.xpoints, p.ypoints, p.npoints);
            graphics.setColor(purple);
            graphics.fillPolygon(p.xpoints, p.ypoints, p.npoints);
        }
    }

    @Override
    public String path(final int plane) {
        return "data/map/produced map image " + plane + ".png";
    }
}
