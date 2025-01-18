package com.zenyte.game.polls;

import com.google.gson.annotations.Expose;

import java.time.LocalDateTime;

/**
 * @author Kris | 26. march 2018 : 23:51.03
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public final class AnsweredPoll {
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
     * The array of poll questions.
     */
    @Expose
    private AnsweredPollQuestion[] questions;
    /**
     * The date and time when the player submitted their answered to this poll.
     */
    @Expose
    private LocalDateTime submitDate;

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
     * The array of poll questions.
     */
    public AnsweredPollQuestion[] getQuestions() {
        return this.questions;
    }

    /**
     * The array of poll questions.
     */
    public void setQuestions(final AnsweredPollQuestion[] questions) {
        this.questions = questions;
    }

    /**
     * The date and time when the player submitted their answered to this poll.
     */
    public LocalDateTime getSubmitDate() {
        return this.submitDate;
    }

    /**
     * The date and time when the player submitted their answered to this poll.
     */
    public void setSubmitDate(final LocalDateTime submitDate) {
        this.submitDate = submitDate;
    }

    public static final class AnsweredPollQuestion {
        /**
         * The question in the poll.
         */
        @Expose
        private String question;
        /**
         * The answer the player chose.
         */
        @Expose
        private String answer;

        public String getQuestion() {
            return this.question;
        }

        public void setQuestion(final String question) {
            this.question = question;
        }

        public String getAnswer() {
            return this.answer;
        }

        public void setAnswer(final String answer) {
            this.answer = answer;
        }
    }
}
