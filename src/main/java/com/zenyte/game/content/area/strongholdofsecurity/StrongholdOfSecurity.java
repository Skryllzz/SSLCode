package com.zenyte.game.content.area.strongholdofsecurity;

import com.zenyte.game.parser.scheduled.ScheduledExternalizable;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.DefaultGson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Kris | 4. sept 2018 : 00:50:29
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>
 */
public class StrongholdOfSecurity implements ScheduledExternalizable {
    private static final Logger log = LoggerFactory.getLogger(StrongholdOfSecurity.class);
    private static final List<Question> QUESTIONS = new ArrayList<>(35);

    public static Question getRandomQuestion() {
        if (QUESTIONS.isEmpty()) {
            throw new RuntimeException("SoS Questions haven't been initialized yet.");
        }
        return QUESTIONS.get(Utils.random(QUESTIONS.size() - 1));
    }

    @Override
    public Logger getLog() {
        return log;
    }

    @Override
    public int writeInterval() {
        return 0;
    }

    @Override
    public void read(final BufferedReader reader) {
        final StrongholdOfSecurity.Question[] questions = DefaultGson.fromGson(reader, Question[].class);
        Collections.addAll(QUESTIONS, questions);
    }

    @Override
    public void write() {
    }

    @Override
    public String path() {
        return "data/stronghold of security questions.json";
    }

    public static final class QuestionMessage {
        private final String[] strings;

        public QuestionMessage(final String... question) {
            strings = question;
        }

        public String[] getStrings() {
            return this.strings;
        }
    }

    public static final class AnswerMessage {
        private final String option;
        private final String[] message;

        public AnswerMessage(final String option, final String... message) {
            this.option = option;
            this.message = message;
        }

        public String getOption() {
            return this.option;
        }

        public String[] getMessage() {
            return this.message;
        }
    }

    public static class Question {
        private final QuestionMessage question;
        private final AnswerMessage[] answers;

        private Question(final QuestionMessage question, final AnswerMessage... answers) {
            this.question = question;
            this.answers = answers;
        }

        public QuestionMessage getQuestion() {
            return this.question;
        }

        public AnswerMessage[] getAnswers() {
            return this.answers;
        }
    }
}
