package com.zenyte.discord;

import com.zenyte.game.util.Utils;
import de.btobastian.sdcf4j.CommandExecutor;
import de.btobastian.sdcf4j.handler.JavacordHandler;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DiscordThread extends Thread {

    private static final String DISCORD_TOKEN = "NDE3MjkxODQyNjc4MjkyNDgw.DXRPIQ.pcDAjV6s5aMyTz15YLpy74jHcnQ";

    private static final Logger log = LoggerFactory.getLogger(DiscordThread.class);

    @Override
    public void run() {
        try {
            init();
        } catch (final Exception e) {
            log.error("", e);
        }
    }

    private void init() {
        final DiscordApi api = new DiscordApiBuilder()
                .setToken(DISCORD_TOKEN)
                .login()
                .join();
        final JavacordHandler handler = new JavacordHandler(api);
        registerCommands(handler);
    }

    private void registerCommands(final JavacordHandler handler) {
        try {
            final Class<?>[] classes = Utils.getClasses("com.zenyte.discord.commands");
            for (final Class<?> c : classes) {
                if (c.isAnonymousClass() || c.isMemberClass()) continue;

                final Object o = c.getDeclaredConstructor().newInstance();
                if (!(o instanceof CommandExecutor command)) continue;

                handler.registerCommand(command);
            }
        } catch (final Exception e) {
            log.error("", e);
        }
    }

}
