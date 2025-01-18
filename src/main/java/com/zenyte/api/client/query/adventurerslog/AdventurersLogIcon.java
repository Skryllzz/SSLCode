package com.zenyte.api.client.query.adventurerslog;

/**
 * @author Kris | 15/05/2019 14:28
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public enum AdventurersLogIcon {
    RARE_DROP(null),
    LEVEL_99(null),
    OVERALL_SKILLING("overall.png"),
    MAX_XP(null),
    PET_DROP(null),
    HCIM_DEATH("hardcore_ironman.png"),
    DIARY_COMPLETION("diary.png"),
    QUEST_COMPLETION("quest.png"),
    MINIGAME("minigame.png");
    private final String link;

    AdventurersLogIcon(final String link) {
        this.link = link;
    }

    public String getLink() {
        return this.link;
    }
}
