package com.zenyte.game.music;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Kris | 27. juuli 2018 : 21:28:20
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class Music {
    public static final Map<String, Music> map = new HashMap<>(600);
    public static final Map<String, Music> lowercaseMap = new HashMap<>(600);
    private String name;
    private String hint;
    private int musicId;
    private int duration;
    private List<Integer> regionIds;
    private boolean defaultLocked;

    public static Music get(@NotNull final String name) {
        final Music music = map.get(name);
        if (music == null) {
            throw new IllegalStateException("Music track '" + name + "' does not exist.");
        }
        return music;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getHint() {
        return this.hint;
    }

    public void setHint(final String hint) {
        this.hint = hint;
    }

    public int getMusicId() {
        return this.musicId;
    }

    public void setMusicId(final int musicId) {
        this.musicId = musicId;
    }

    public int getDuration() {
        return this.duration;
    }

    public void setDuration(final int duration) {
        this.duration = duration;
    }

    public List<Integer> getRegionIds() {
        return this.regionIds;
    }

    public void setRegionIds(final List<Integer> regionIds) {
        this.regionIds = regionIds;
    }

    public boolean isDefaultLocked() {
        return this.defaultLocked;
    }

    public void setDefaultLocked(final boolean defaultLocked) {
        this.defaultLocked = defaultLocked;
    }
}
