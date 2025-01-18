package com.zenyte;

import com.google.common.eventbus.Subscribe;
import com.zenyte.game.content.skills.magic.Magic;
import com.zenyte.game.content.skills.magic.spells.MagicSpell;
import com.zenyte.game.item.*;
import com.zenyte.game.item.pluginextensions.ItemPlugin;
import com.zenyte.game.parser.scheduled.ScheduledExternalizable;
import com.zenyte.game.parser.scheduled.ScheduledExternalizableManager;
import com.zenyte.game.ui.ButtonAction;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.ui.NewInterfaceHandler;
import com.zenyte.game.ui.UserInterface;
import com.zenyte.game.world.entity.npc.AbstractNPCManager;
import com.zenyte.game.world.entity.npc.Spawnable;
import com.zenyte.game.world.entity.npc.actions.NPCHandler;
import com.zenyte.game.world.entity.npc.actions.NPCPlugin;
import com.zenyte.game.world.entity.npc.drop.matrix.DropProcessor;
import com.zenyte.game.world.entity.npc.drop.matrix.DropProcessorLoader;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.ObjectHandler;
import com.zenyte.game.world.region.Area;
import com.zenyte.game.world.region.GlobalAreaManager;
import com.zenyte.plugins.MethodicPluginHandler;
import com.zenyte.plugins.PluginManager;
import com.zenyte.plugins.equipment.EquipmentPlugin;
import com.zenyte.plugins.equipment.EquipmentPluginLoader;
import com.zenyte.plugins.equipment.equip.EquipPlugin;
import com.zenyte.plugins.equipment.equip.EquipPluginLoader;
import com.zenyte.plugins.flooritem.FloorItemPlugin;
import com.zenyte.plugins.flooritem.FloorItemPluginLoader;
import com.zenyte.plugins.handlers.InterfaceSwitchHandler;
import com.zenyte.plugins.handlers.InterfaceSwitchPlugin;
import com.zenyte.processor.Listener;
import com.zenyte.utils.ClassInitializer;
import com.zenyte.utils.StaticInitializer;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;

/**
 * A class dedicated to scan the entire source for specific classes.
 *
 * @author Kris | 16. juuni 2018 : 12:49:16
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class Scanner {
    private static final Logger log = LoggerFactory.getLogger(Scanner.class);

    public void scan(final Class<?> plugin) {
        final ClassGraph scanner = new ClassGraph();
        scanner.acceptPackages("com.zenyte");
        try (ScanResult result = scanner.scan()) {
            result.getAllClasses().forEach(clazz -> {
                if (plugin.equals(NPCPlugin.class)) {
                    if (clazz.extendsSuperclass(NPCPlugin.class.getName())) {
                        NPCHandler.add(clazz.loadClass());
                    }
                }
            });
        }
    }

    public void scan() {
        final ClassGraph scanner = new ClassGraph();
        scanner.ignoreMethodVisibility();
        scanner.enableAnnotationInfo();
        scanner.acceptPackages("com.zenyte");
        log.info("Scanning classpath.");
        try (ScanResult result = scanner.scan()) {
            final ObjectArrayList<Callable<Void>> callables = new ObjectArrayList<>(1000);
            final Object lock = new Object();
            result.getAllClasses().forEach(clazz -> callables.add(() -> {
                if (!Constants.WORLD_PROFILE.isDevelopment() || Constants.CYCLE_DEBUG) {
                    ClassInitializer.initialize(clazz.loadClass());
                }
                if (clazz.implementsInterface(ObjectAction.class.getName())) {
                    final Class<?> obj = clazz.loadClass();
                    synchronized (lock) {
                        ObjectHandler.add(obj);
                    }
                }
                if (clazz.implementsInterface(ItemOnObjectAction.class.getName())) {
                    final Class<?> obj = clazz.loadClass();
                    synchronized (lock) {
                        ItemOnObjectHandler.add(obj);
                    }
                }
                if (clazz.implementsInterface(ItemOnNPCAction.class.getName())) {
                    final Class<?> obj = clazz.loadClass();
                    synchronized (lock) {
                        ItemOnNPCHandler.add(obj);
                    }
                }
                if (clazz.implementsInterface(ItemOnItemAction.class.getName())) {
                    final Class<?> obj = clazz.loadClass();
                    synchronized (lock) {
                        ItemOnItemHandler.add(obj);
                    }
                }
                if (clazz.implementsInterface(ItemOnPlayerPlugin.class.getName())) {
                    final Class<? extends ItemOnPlayerPlugin> obj = (Class<? extends ItemOnPlayerPlugin>) clazz.loadClass();
                    synchronized (lock) {
                        ItemOnPlayerHandler.add(obj);
                    }
                }
                if (clazz.implementsInterface(ItemOnFloorItemAction.class.getName())) {
                    final Class<?> obj = clazz.loadClass();
                    synchronized (lock) {
                        ItemOnFloorItemHandler.add(obj);
                    }
                }
                if (clazz.implementsInterface(UserInterface.class.getName())) {
                    final Class<?> obj = clazz.loadClass();
                    synchronized (lock) {
                        ButtonAction.add(obj);
                    }
                }
                if (clazz.implementsInterface(InterfaceSwitchPlugin.class.getName())) {
                    final Class<? extends InterfaceSwitchPlugin> obj = (Class<? extends InterfaceSwitchPlugin>) clazz.loadClass();
                    synchronized (lock) {
                        InterfaceSwitchHandler.add(obj);
                    }
                }
                if (clazz.implementsInterface(FloorItemPlugin.class.getName())) {
                    final Class<?> obj = clazz.loadClass();
                    synchronized (lock) {
                        FloorItemPluginLoader.add(obj);
                    }
                }
                if (clazz.implementsInterface(EquipPlugin.class.getName())) {
                    final Class<?> obj = clazz.loadClass();
                    synchronized (lock) {
                        EquipPluginLoader.add(obj);
                    }
                }
                if (clazz.implementsInterface(ScheduledExternalizable.class.getName())) {
                    final Class<? extends ScheduledExternalizable> obj = (Class<? extends ScheduledExternalizable>) clazz.loadClass();
                    synchronized (lock) {
                        ScheduledExternalizableManager.add(obj);
                    }
                }
                if (clazz.implementsInterface(Spawnable.class.getName())) {
                    final Class<? extends Spawnable> obj = (Class<? extends Spawnable>) clazz.loadClass();
                    synchronized (lock) {
                        AbstractNPCManager.add(obj);
                    }
                }
                if (clazz.implementsInterface(MagicSpell.class.getName())) {
                    final Class<? extends MagicSpell> obj = (Class<? extends MagicSpell>) clazz.loadClass();
                    synchronized (lock) {
                        Magic.add(obj);
                    }
                }
                if (clazz.hasMethodAnnotation(Listener.class.getName())) {
                    clazz.getMethodInfo().forEach(method -> {
                        if (method.hasAnnotation(Listener.class.getName())) {
                            final Method meth = method.loadClassAndGetMethod();
                            final Class<?> c = meth.getDeclaringClass();
                            synchronized (lock) {
                                MethodicPluginHandler.register(c, meth);
                            }
                        }
                    });
                }
                if (clazz.hasMethodAnnotation(Subscribe.class.getName())) {
                    clazz.getMethodInfo().forEach(method -> {
                        if (method.hasAnnotation(Subscribe.class.getName())) {
                            final Method meth = method.loadClassAndGetMethod();
                            final Class<?> obj = clazz.loadClass();
                            synchronized (lock) {
                                PluginManager.register(obj, meth);
                            }
                        }
                    });
                }
                if (clazz.hasAnnotation(StaticInitializer.class.getName())) {
                    final Class<?> obj = clazz.loadClass();
                    synchronized (lock) {
                        ClassInitializer.initialize(obj);
                    }
                }
                if (clazz.extendsSuperclass(DropProcessor.class.getName())) {
                    final Class<?> obj = clazz.loadClass();
                    synchronized (lock) {
                        DropProcessorLoader.add(obj);
                    }
                }
                if (clazz.extendsSuperclass(NPCPlugin.class.getName())) {
                    final Class<?> obj = clazz.loadClass();
                    synchronized (lock) {
                        NPCHandler.add(obj);
                    }
                }
                if (clazz.extendsSuperclass(ItemPlugin.class.getName())) {
                    final Class<?> obj = clazz.loadClass();
                    synchronized (lock) {
                        ItemActionHandler.add(obj);
                    }
                }
                if (clazz.extendsSuperclass(EquipmentPlugin.class.getName())) {
                    final Class<?> obj = clazz.loadClass();
                    synchronized (lock) {
                        EquipmentPluginLoader.add(obj);
                    }
                }
                if (clazz.extendsSuperclass(Area.class.getName())) {
                    final Class<? extends Area> obj = (Class<? extends Area>) clazz.loadClass();
                    synchronized (lock) {
                        GlobalAreaManager.add(obj);
                    }
                }
                if (clazz.extendsSuperclass(Interface.class.getName())) {
                    final Class<? extends Interface> obj = (Class<? extends Interface>) clazz.loadClass();
                    synchronized (lock) {
                        NewInterfaceHandler.add(obj);
                    }
                }
                return null;
            }));
            log.info("Processing scan result.");
            ForkJoinPool.commonPool().invokeAll(callables);
        }
    }
}
