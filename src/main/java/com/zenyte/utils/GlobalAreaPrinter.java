package com.zenyte.utils;

import com.zenyte.GameEngine;
import com.zenyte.game.world.region.Area;
import com.zenyte.game.world.region.GlobalAreaManager;
import com.zenyte.game.world.region.RSPolygon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;

/**
 * @author Kris | 15/04/2019 14:09
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class GlobalAreaPrinter implements MapPrinter {
    private static final Logger log = LoggerFactory.getLogger(GlobalAreaPrinter.class);

    public static void main(final String[] args) throws IOException {
        GameEngine.main(new String[]{});
        final ArrayList<Callable<Void>> list = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            final int plane = i;
            list.add(() -> {
                new GlobalAreaPrinter().load(plane);
                return null;
            });
        }
        ForkJoinPool.commonPool().invokeAll(list);
        System.exit(-1);
    }

    @Override
    public String path(final int plane) {
        return "data/map/produced global areas image " + plane + ".png";
    }

    @Override
    public void draw(final Graphics2D graphics, final int plane) throws IOException {
        log.info("Drawing map image");
        final ArrayList<Color> list = new ArrayList<>();
        for (float x = 0; x < 360; x++) {
            Color c = Color.getHSBColor(x / 360, 1, 1);
            list.add(new Color(c.getRed(), c.getGreen(), c.getBlue(), 100));
        }
        Collections.shuffle(list);
        int colI = 0;
        final HashMap<Area, ArrayList<Polygon>> ps = new HashMap<>();
        for (Area area : GlobalAreaManager.getAllAreas()) {
            for (final RSPolygon polygon : area.getPolygons()) {
                if (!polygon.getPlanes().contains(plane)) continue;
                final int[][] points = polygon.getPoints();
                final int[] xPoints = new int[points.length];
                final int[] yPoints = new int[points.length];
                for (int i = 0; i < points.length; i++) {
                    xPoints[i] = getX(points[i][0]);
                    yPoints[i] = getY(points[i][1]);
                }
                final Polygon poly = new Polygon(xPoints, yPoints, points.length);
                if (!ps.containsKey(area)) {
                    ps.put(area, new ArrayList<>());
                }
                ps.get(area).add(poly);
            }
        }
        for (final Map.Entry<Area, ArrayList<Polygon>> polygon : ps.entrySet()) {
            for (final Polygon poly : polygon.getValue()) {
                graphics.setColor(list.get(colI++));
                graphics.fillPolygon(poly);
            }
        }
        for (final Map.Entry<Area, ArrayList<Polygon>> polygon : ps.entrySet()) {
            for (final Polygon poly : polygon.getValue()) {
                final FontMetrics metrics = graphics.getFontMetrics();
                final Rectangle2D rect = poly.getBounds2D();
                final double rectX = rect.getX();
                final double rectY = rect.getY();
                // Determine the X coordinate for the text
                int x = (int) (rectX + (rect.getWidth() - metrics.stringWidth(polygon.getKey().name())) / 2.0F);
                // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
                int y = (int) (rectY + ((rect.getHeight() - metrics.getHeight()) / 2) + metrics.getAscent());
                // Draw the String
                graphics.setColor(Color.white);
                graphics.drawString(polygon.getKey().name(), x, y);
            }
        }
    }
}
