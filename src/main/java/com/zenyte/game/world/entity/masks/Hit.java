package com.zenyte.game.world.entity.masks;

import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.Entity;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author Kris | 6. nov 2017 : 14:30.53
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 */
public class Hit {
    private Entity source;
    private HitType hitType;
    private int damage;
    private int delay;
    private long scheduleTime;
    private boolean forcedHitsplat;
    private Map<String, Object> attributes;

    public Hit(final int damage, final HitType look) {
        this(null, damage, look, 0);
    }

    public Hit(final Entity source, final int damage, final HitType look) {
        this(source, damage, look, 0);
    }

    public Hit(final Entity source, final int damage, final HitType hitType, final int delay) {
        this.source = source;
        this.damage = damage;
        this.hitType = hitType;
        this.delay = delay;
        scheduleTime = Utils.currentTimeMillis();
    }

    public void putAttribute(final String key, final Object value) {
        if (attributes == null) {
            attributes = new Object2ObjectOpenHashMap<>();
        }
        attributes.put(key, value);
    }

    public Hit onLand(final Consumer<Hit> consumer) {
        putAttribute("on_hit_land", consumer);
        return this;
    }

    @SuppressWarnings("all")
    public Consumer<Hit> getOnLandConsumer() {
        if (attributes == null) {
            return null;
        }
        final Object attachment = attributes.get("on_hit_land");
        if (attachment instanceof Consumer) {
            return (Consumer<Hit>) attachment;
        }
        return null;
    }

    @SuppressWarnings("unchecked cast")
    public Predicate<Hit> getPredicate() {
        if (attributes == null) {
            return null;
        }
        final Object attachment = attributes.get("predicate");
        if (attachment instanceof Predicate) {
            return (Predicate<Hit>) attachment;
        }
        return null;
    }

    public void setPredicate(final Predicate<Hit> attachment) {
        putAttribute("predicate", attachment);
    }

    public boolean executeIfLocked() {
        return containsAttribute("execute_if_locked");
    }

    public Hit setExecuteIfLocked() {
        putAttribute("execute_if_locked", Boolean.TRUE);
        return this;
    }

    public Object getWeapon() {
        if (attributes == null) {
            return null;
        }
        return attributes.get("weapon");
    }

    public void setWeapon(final Object weapon) {
        putAttribute("weapon", weapon);
    }

    public boolean containsAttribute(final String key) {
        if (attributes == null) {
            return false;
        }
        return attributes.containsKey(key);
    }

    public Entity getSource() {
        return this.source;
    }

    public void setSource(final Entity source) {
        this.source = source;
    }

    public HitType getHitType() {
        return this.hitType;
    }

    public void setHitType(final HitType hitType) {
        this.hitType = hitType;
    }

    public int getDamage() {
        return this.damage;
    }

    public void setDamage(final int damage) {
        this.damage = Math.min(32767, Math.max(0, damage));
    }

    public int getDelay() {
        return this.delay;
    }

    public void setDelay(final int delay) {
        this.delay = Math.min(32767, Math.max(0, delay));
    }

    public long getScheduleTime() {
        return this.scheduleTime;
    }

    public void setScheduleTime(final long scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

    public boolean isForcedHitsplat() {
        return this.forcedHitsplat;
    }

    public void setForcedHitsplat(final boolean forcedHitsplat) {
        this.forcedHitsplat = forcedHitsplat;
    }

    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    public HitType getAppliedSplat() {
        return damage == 0 && !forcedHitsplat ? HitType.MISSED : hitType;
    }

}
