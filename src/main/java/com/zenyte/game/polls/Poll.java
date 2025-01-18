package com.zenyte.game.polls;

import com.google.gson.annotations.Expose;
import com.zenyte.game.util.Utils;

import java.time.LocalDate;

/**
 * @author Kris | 26. march 2018 : 3:32.49
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public final class Poll {
    /**
     * The id of this poll.
     */
    @Expose
    private int pollId;
    /**
     * The title of the poll.
     */
    @Expose
    private String title;
    /**
     * The description of the poll, lines separated using the | character.
     */
    @Expose
    private String description;
    /**
     * The hyperlink if applicable. The format should be as follows..
     * The text user to overlay the link, https://zenyte.com/community/
     */
    @Expose
    private String hyperlink;
    /**
     * The local date when the poll was made available.
     */
    @Expose
    private LocalDate startDate;
    /**
     * The local date when the poll was closed.
     */
    @Expose
    private LocalDate endDate;
    /**
     * The number of votes in the poll.
     */
    @Expose
    private int votes;
    /**
     * The array of poll questions.
     */
    @Expose
    private PollQuestion[] questions;
    /**
     * Whether the votes in this poll can be amended or not.
     */
    @Expose
    private boolean amendable;

    public String getFormattedEndDate() {
        final int endDay = endDate.getDayOfMonth();
        final String dayOfWeek = Utils.formatString(endDate.getDayOfWeek().toString());
        final String endMonth = Utils.formatString(endDate.getMonth().toString());
        if (this.isClosed())
            return "This poll closed on " + dayOfWeek + " " + endDay + getSuffix(endDay % 10) + " " + endMonth + ", " + this.endDate.getYear() + ".";
        return "This poll will close on " + dayOfWeek + " " + endDay + getSuffix(endDay % 10) + " " + endMonth + ".";
    }

    /**
     * Formats the poll start and end date into the format seen on the interface.
     *
     * @return a formatted string of the dates.
     */
    public String getFormattedPollDates() {
        final int startDay = startDate.getDayOfMonth();
        final String startMonth = Utils.formatString(startDate.getMonth().toString());
        final String startDate = startDay + getSuffix(startDay % 10) + " " + startMonth;
        final int endDay = endDate.getDayOfMonth();
        final String endMonth = Utils.formatString(endDate.getMonth().toString());
        final String endDate = endDay + getSuffix(endDay % 10) + " " + endMonth;
        return startDate + " - " + endDate + " " + this.endDate.getYear();
    }

    /**
     * Gets the suffix for the day.
     *
     * @param day the day value to obtain the suffix for.
     * @return the suffix for the day, e.g. "st", "nd", "rd", "th".
     */
    private String getSuffix(final int day) {
        return day == 1 ? "st" : day == 2 ? "nd" : day == 3 ? "rd" : "th";
    }

    /**
     * Whether the poll is closed or not
     *
     * @return if closed or not.
     */
    public boolean isClosed() {
        final LocalDate now = LocalDate.now();
        if (now.getYear() > endDate.getYear()) return true;
        return now.getDayOfYear() >= endDate.getDayOfYear();
    }

    /**
     * The id of this poll.
     */
    public int getPollId() {
        return this.pollId;
    }

    /**
     * The id of this poll.
     */
    public void setPollId(final int pollId) {
        this.pollId = pollId;
    }

    /**
     * The title of the poll.
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * The title of the poll.
     */
    public void setTitle(final String title) {
        this.title = title;
    }

    /**
     * The description of the poll, lines separated using the | character.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * The description of the poll, lines separated using the | character.
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * The hyperlink if applicable. The format should be as follows..
     * The text user to overlay the link, https://zenyte.com/community/
     */
    public String getHyperlink() {
        return this.hyperlink;
    }

    /**
     * The hyperlink if applicable. The format should be as follows..
     * The text user to overlay the link, https://zenyte.com/community/
     */
    public void setHyperlink(final String hyperlink) {
        this.hyperlink = hyperlink;
    }

    /**
     * The local date when the poll was made available.
     */
    public LocalDate getStartDate() {
        return this.startDate;
    }

    /**
     * The local date when the poll was made available.
     */
    public void setStartDate(final LocalDate startDate) {
        this.startDate = startDate;
    }

    /**
     * The local date when the poll was closed.
     */
    public LocalDate getEndDate() {
        return this.endDate;
    }

    /**
     * The local date when the poll was closed.
     */
    public void setEndDate(final LocalDate endDate) {
        this.endDate = endDate;
    }

    /**
     * The number of votes in the poll.
     */
    public int getVotes() {
        return this.votes;
    }

    /**
     * The number of votes in the poll.
     */
    public void setVotes(final int votes) {
        this.votes = votes;
    }

    /**
     * The array of poll questions.
     */
    public PollQuestion[] getQuestions() {
        return this.questions;
    }

    /**
     * The array of poll questions.
     */
    public void setQuestions(final PollQuestion[] questions) {
        this.questions = questions;
    }

    /**
     * Whether the votes in this poll can be amended or not.
     */
    public boolean isAmendable() {
        return this.amendable;
    }

    /**
     * Whether the votes in this poll can be amended or not.
     */
    public void setAmendable(final boolean amendable) {
        this.amendable = amendable;
    }
}
