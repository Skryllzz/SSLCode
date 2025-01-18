package mgi.custom;

import com.zenyte.Game;
import com.zenyte.GameEngine;
import mgi.types.Definitions;
import mgi.types.config.AnimationDefinitions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Scanner;

/**
 * @author Kris | 08/10/2019
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class AnimationFrame {
    public static void main(final String... args) {
        System.out.println("Loading cache.");
        Game.load();
        System.out.println("Loading sequence definitions.");
        Definitions.loadDefinitions(new Class<?>[]{AnimationDefinitions.class});
        final Scanner scanner = new Scanner(System.in);
        final LoaderOptions loaderOptions = new LoaderOptions();
        final Yaml yaml = new Yaml(new Constructor(AnimationDefinitions.class, loaderOptions));
        do {
            System.out.println("Enter id of the sequence to extract, or type exit to close: ");
            try {
                final String line = scanner.nextLine();
                if (line.toLowerCase().contains("exit")) {
                    System.exit(-1);
                    return;
                }
                final int id = Integer.parseInt(line);
                try (PrintWriter pw = new PrintWriter(id + ".seq", StandardCharsets.UTF_8)) {
                    pw.println(yaml.dump(AnimationDefinitions.get(id)));
                } catch (final Exception e) {
                    GameEngine.logger.error("", e);
                }
            } catch (Exception e) {
                GameEngine.logger.error("", e);
            }
        } while (true);
    }

    public static void pack(final String fileName, final AnimationBase base, String framesFolder) throws IOException {
        final File file = new File("assets/animations/sequence/" + fileName + ".seq");
        final LoaderOptions loaderOptions = new LoaderOptions();
        final Yaml yaml = new Yaml(new Constructor(AnimationDefinitions.class, loaderOptions));
        final AnimationDefinitions anim = yaml.loadAs(new FileReader(file), AnimationDefinitions.class);
        final int[] ids = anim.getFrameIds();
        for (int i = 0; i < ids.length; i++) {
            ids[i] = (ids[i] & 65535) | (base.getBaseId() << 16);
        }
        Arrays.sort(ids);
        /*for (int id : ids) {
            Game.getLibrary().getIndex(0).addArchive(id >> 16).addFile(id & 0xFFFF,
                    IOUtils.toByteArray(new FileInputStream(new File("assets/animations/frames/" + framesFolder + "/" + id + ".dat"))));
        }*/
        anim.pack();
    }
}
