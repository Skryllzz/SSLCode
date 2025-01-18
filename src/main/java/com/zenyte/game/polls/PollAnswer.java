package com.zenyte.game.polls;

import com.google.gson.annotations.Expose;

/**
 * @author Kris | 26. march 2018 : 3:35.22
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public final class PollAnswer {
    /**
     * The name of the choice in the poll.
     */
    @Expose
    private String choice;
    /**
     * The number of votes this choice has received.
     */
    @Expose
    private int votes;

    /**
     * The name of the choice in the poll.
     */
    public String getChoice() {
        return this.choice;
    }

    /**
     * The name of the choice in the poll.
     */
    public void setChoice(final String choice) {
        this.choice = choice;
    }

    /**
     * The number of votes this choice has received.
     */
    public int getVotes() {
        return this.votes;
    }

    /**
     * The number of votes this choice has received.
     */
    public void setVotes(final int votes) {
        this.votes = votes;
    }
}
