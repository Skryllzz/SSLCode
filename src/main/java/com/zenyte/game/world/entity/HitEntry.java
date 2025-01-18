package com.zenyte.game.world.entity;

import com.zenyte.game.world.entity.masks.Hit;

/**
 * @author Kris | 20/08/2019 20:25
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class HitEntry {
    private final Entity source;
    private final Hit hit;
    private int delay;
    private boolean freshEntry;
    private transient HitEntry next;
    private transient HitEntry previous;

    public HitEntry(final Entity source, final int delay, final Hit hit) {
        this.source = source;
        this.delay = delay;
        this.hit = hit;
        this.freshEntry = true;
    }

    int getAndDecrement() {
        return delay--;
    }

    public Entity getSource() {
        return this.source;
    }

    public int getDelay() {
        return this.delay;
    }

    public Hit getHit() {
        return this.hit;
    }

    public boolean isFreshEntry() {
        return this.freshEntry;
    }

    public void setFreshEntry(final boolean freshEntry) {
        this.freshEntry = freshEntry;
    }

    public HitEntry getNext() {
        return this.next;
    }

    public void setNext(final HitEntry next) {
        this.next = next;
    }

    public HitEntry getPrevious() {
        return this.previous;
    }

    public void setPrevious(final HitEntry previous) {
        this.previous = previous;
    }
}
