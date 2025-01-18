package com.zenyte.tools;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zenyte.Constants;
import com.zenyte.game.util.Utils;
import mgi.Indice;
import mgi.types.component.ComponentDefinitions;

import java.io.PrintWriter;
import java.util.*;

/**
 * @author Kris | 28. mai 2018 : 03:24:47
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
@SuppressWarnings("all")
public class InterfaceExtractor implements Extractor {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(InterfaceExtractor.class);
    private static final TreeMap<Integer, InterfaceInfo> DEFINITIONS = new TreeMap<Integer, InterfaceInfo>();

    public static final void save() {
        final Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
        final String toJson = gson.toJson(DEFINITIONS.values());
        try {
            final PrintWriter pw = new PrintWriter("info/#" + Constants.REVISION + " Interface component definitions.json", "UTF-8");
            pw.println(toJson);
            pw.close();
        } catch (final Exception e) {
            log.error("", e);
        }
    }

    @Override
    public void extract() {
        for (int interId = 0; interId < Utils.getIndiceSize(Indice.INTERFACE_DEFINITIONS); interId++) {
            final com.zenyte.tools.InterfaceExtractor.InterfaceInfo interConfig = new InterfaceInfo(interId);
            for (int compId = 0; compId < Utils.getIndiceSize(Indice.INTERFACE_DEFINITIONS, interId); compId++) {
                final mgi.types.component.ComponentDefinitions defs = ComponentDefinitions.get(interId, compId);
                if (defs == null) {
                    continue;
                }
                final com.zenyte.tools.InterfaceExtractor.ComponentInfo compConfig = new ComponentInfo();
                compConfig.load(defs);
                //if (compConfig.names != null || compConfig.actions != null || compConfig.scripts != null) {
                interConfig.components.add(compConfig);
                //}
            }
            if (!interConfig.components.isEmpty()) {
                DEFINITIONS.put(interId, interConfig);
            }
        }
        save();
    }


    private static final class ComponentInfo {
        private static final Map<Integer, String> componentTypes = new HashMap<Integer, String>();

        static {
            componentTypes.put(0, "Layer");
            componentTypes.put(3, "Rectangle");
            componentTypes.put(4, "Text");
            componentTypes.put(5, "Graphic");
            componentTypes.put(6, "Model");
            componentTypes.put(9, "Line");
        }

        private int componentId;
        private String format;
        private String type;
        private List<String> names;
        private List<String> actions;
        private Map<String, Script> scripts;

        public void load(final ComponentDefinitions defs) {
            componentId = defs.getComponentId();
            type = componentTypes.get(defs.getType());
            format = defs.isIf3() ? "New" : "Old";
            if (verifyString(defs.getText())) {
                addName(defs.getText());
            }
            if (verifyString(defs.getAlternateText())) {
                addName(defs.getAlternateText());
            }
            if (verifyString(defs.getOpBase())) {
                addName(defs.getOpBase());
            }
            if (verifyString(defs.getSpellName())) {
                addName(defs.getSpellName());
            }
            if (verifyString(defs.getTooltip())) {
                addName(defs.getTooltip());
            }
            if (verifyString(defs.getTargetVerb())) {
                addAction(defs.getTargetVerb());
            }
            if (defs.getActions() != null) {
                for (final String s : defs.getActions()) {
                    if (verifyString(s)) {
                        addAction(s);
                    }
                }
            }
            if (defs.getConfigActions() != null) {
                for (final String s : defs.getConfigActions()) {
                    if (verifyString(s)) {
                        addAction(s);
                    }
                }
            }
            if (defs.getOnLoadListener() != null) {
                addScript("Load", defs.getOnLoadListener());
            }
            if (defs.getOnClickListener() != null) {
                addScript("Click", defs.getOnClickListener());
            }
            if (defs.getOnClickRepeatListener() != null) {
                addScript("Repeated click", defs.getOnClickRepeatListener());
            }
            if (defs.getOnReleaseListener() != null) {
                addScript("Release", defs.getOnReleaseListener());
            }
            if (defs.getOnHoldListener() != null) {
                addScript("Hold", defs.getOnHoldListener());
            }
            if (defs.getOnMouseOverListener() != null) {
                addScript("Mouse over", defs.getOnMouseOverListener());
            }
            if (defs.getOnMouseRepeatListener() != null) {
                addScript("Mouse repeat", defs.getOnMouseRepeatListener());
            }
            if (defs.getOnMouseLeaveListener() != null) {
                addScript("Mouse leave", defs.getOnMouseLeaveListener());
            }
            if (defs.getOnDragListener() != null) {
                addScript("Drag", defs.getOnDragListener());
            }
            if (defs.getOnDragCompleteListener() != null) {
                addScript("Drag release", defs.getOnDragCompleteListener());
            }
            if (defs.getOnTargetEnterListener() != null) {
                addScript("Target enter", defs.getOnTargetEnterListener());
            }
            if (defs.getOnTargetLeaveListener() != null) {
                addScript("Target leave", defs.getOnTargetLeaveListener());
            }
            if (defs.getOnVarTransmitListener() != null) {
                addScript("Var transmit", defs.getOnVarTransmitListener());
            }
            if (defs.getVarTransmitTriggers() != null) {
                final Object[] triggers = new Object[defs.getVarTransmitTriggers().length];
                for (int i = 0; i < triggers.length; i++) {
                    triggers[i] = defs.getVarTransmitTriggers()[i];
                }
                addScript("Var transmit trigger", triggers);
            }
            if (defs.getOnStatTransmitListener() != null) {
                addScript("Stat transmit", defs.getOnStatTransmitListener());
            }
            if (defs.getStatTransmitTriggers() != null) {
                final Object[] triggers = new Object[defs.getStatTransmitTriggers().length];
                for (int i = 0; i < triggers.length; i++) {
                    triggers[i] = defs.getStatTransmitTriggers()[i];
                }
                addScript("Stat transmit trigger", triggers);
            }
            if (defs.getOnTimerListener() != null) {
                addScript("Timer", defs.getOnTimerListener());
            }
            if (defs.getOnOpListener() != null) {
                addScript("Op", defs.getOnOpListener());
            }
            if (defs.getOnScrollWheelListener() != null) {
                addScript("Scroll wheel", defs.getOnScrollWheelListener());
            }
        }

        private boolean verifyString(final String string) {
            return string != null && !string.isEmpty() && !string.equals("*") && !string.equals("Ok");
        }

        private void addName(final String name) {
            if (names == null) {
                names = new ArrayList<String>();
            }
            names.add(name);
        }

        private void addAction(final String action) {
            if (actions == null) {
                actions = new ArrayList<String>();
            }
            actions.add(action);
        }

        private void addScript(final String name, final Object[] args) {
            if (scripts == null) {
                scripts = new HashMap<String, Script>();
            }
            final Object[] arguments = new Object[args.length - 1];
            System.arraycopy(args, 1, arguments, 0, arguments.length);
            for (int i = 0; i < arguments.length; i++) {
                final java.lang.Object arg = arguments[i];
                if (arg instanceof Integer) {
                    final int integer = (Integer) arg;
                    if (integer == -2147483647) {
                        arguments[i] = "Param -> Current mouse X coordinate";
                        continue;
                    }
                    if (integer == -2147483646) {
                        arguments[i] = "Param -> Current mouse Y coordinate";
                        continue;
                    }
                    if (integer == -2147483645) {
                        arguments[i] = "Param -> Bitpacked source component";
                        continue;
                    }
                    if (integer == -2147483644) {
                        arguments[i] = "Param -> Option id";
                        continue;
                    }
                    if (integer == -2147483643) {
                        arguments[i] = "Param -> Source id";
                        continue;
                    }
                    if (integer == -2147483642) {
                        arguments[i] = "Param -> Bitpacked target component";
                        continue;
                    }
                    if (integer == -2147483641) {
                        arguments[i] = "Param -> Target id";
                        continue;
                    }
                    if (integer == -2147483640) {
                        arguments[i] = "Param -> Typed key code";
                        continue;
                    }
                    if (integer == -2147483639) {
                        arguments[i] = "Param -> Typed key char";
                        continue;
                    }
                    final int interId = integer >> 16;
                    if (interId > 0 && interId <= Utils.getIndiceSize(Indice.INTERFACE_DEFINITIONS)) {
                        arguments[i] = interId + " << 16 | " + (integer & 65535) + "; " + arg;
                    }
                }
            }
            final Script script = new Script(args[0], arguments);
            scripts.put(name, script);
        }
    }


    private static final class InterfaceInfo {
        private final int interfaceId;
        private final List<ComponentInfo> components = new ArrayList<ComponentInfo>();

        public InterfaceInfo(final int interfaceId) {
            this.interfaceId = interfaceId;
        }
    }


    private static final class Script {
        private final int scriptId;
        private List<Object> parameters;

        public Script(final Object script, final Object[] params) {
            scriptId = (int) script;
            if (params != null && params.length != 0) {
                parameters = new ArrayList<Object>();
                for (int i = 0; i < params.length; i++) {
                    parameters.add(params[i]);
                }
            }
        }
    }
}
