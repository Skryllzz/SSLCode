package com.zenyte.game.content.achievementdiary;

import com.google.common.eventbus.Subscribe;
import com.zenyte.api.client.query.adventurerslog.AdventurersLogIcon;
import com.zenyte.game.content.achievementdiary.Diary.DiaryComplexity;
import com.zenyte.game.content.achievementdiary.diaries.*;
import com.zenyte.game.util.BitUtils;
import com.zenyte.game.util.Colour;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.VarManager;
import com.zenyte.plugins.dialogue.PlainChat;
import com.zenyte.plugins.events.LoginEvent;
import com.zenyte.processor.Listener;
import com.zenyte.processor.Listener.ListenerType;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import mgi.types.config.npcs.NPCDefinitions;

import java.util.*;
import java.util.function.Predicate;

/**
 * @author Kris | 20. sept 2018 : 23:07:27
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>
 */
public class AchievementDiaries {
    public static final Diary[][] ALL_DIARIES = new Diary[][]{ArdougneDiary.VALUES, DesertDiary.VALUES, FaladorDiary.VALUES, VarrockDiary.VALUES, LumbridgeDiary.VALUES, KaramjaDiary.VALUES, WildernessDiary.VALUES, FremennikDiary.VALUES, KandarinDiary.VALUES, WesternProvincesDiary.VALUES, MorytaniaDiary.VALUES, KourendDiary.VALUES};
    private final Object2IntOpenHashMap<String> map = new Object2IntOpenHashMap<>();
    private final Map<DiaryArea, Map<DiaryReward, Boolean>> pendingRewards = new HashMap<>();
    private transient Player player;

    public AchievementDiaries(final Player player) {
        this.player = player;
        for (final DiaryArea area : DiaryArea.VALUES) {
            pendingRewards.put(area, new HashMap<>());
        }
    }

    public static Diary getDiaryByTaskmaster(final int npcId) {
        for (final Diary[] diary : ALL_DIARIES) {
            if (diary[0].taskMaster() == npcId) {
                return diary[0];
            }
        }
        return null;
    }

    @Listener(type = ListenerType.LOBBY_CLOSE)
    private static void onLogin(final Player player) {
        final AchievementDiaries manager = player.getAchievementDiaries();
        for (final Diary[] diary : ALL_DIARIES) {
            manager.refresh(diary[0]);
        }
    }

    @Subscribe
    public static void onLogin(final LoginEvent event) {
        final Player player = event.getPlayer();
        final AchievementDiaries achDiaries = player.getAchievementDiaries();
        for (final Diary[] allDiaries : ALL_DIARIES) {
            for (final Diary diary : allDiaries) {
                if (diary.autoCompleted()) {
                    continue;
                }
                loop:
                for (final Diary.DiaryComplexity complexity : Diary.DiaryComplexity.VALUES) {
                    final DiaryReward reward = DiaryReward.get(complexity, diary.area());
                    final boolean containsReward = achDiaries.pendingRewards.get(reward.getArea()).containsKey(reward);
                    if (!containsReward) {
                        continue;
                    }
                    final List<Diary> diaries = diary.map().get(complexity);
                    for (final Diary d : diaries) {
                        if (!achDiaries.isCompleted(d)) {
                            final Boolean condition = achDiaries.pendingRewards.get(reward.getArea()).remove(reward);
                            if (condition != null) {
                                if (condition) {
                                    player.addAttribute("Already claimed diary reward: " + reward, 1);
                                }
                                player.sendMessage(Colour.RED.wrap("You no longer have the " + Utils.formatString(complexity.toString().toLowerCase()) + " " + diary.area().getAreaName() + " diary completed."));
                            }
                            continue loop;
                        }
                    }
                }
            }
        }
    }

    public void initialize(final Player player, final Player parser) {
        this.player = player;
        final AchievementDiaries parserData = parser.getAchievementDiaries();
        if (parserData == null) {
            return;
        }
        map.putAll(parserData.map);
        pendingRewards.putAll(parserData.pendingRewards);
    }

    /**
     * Gets the current progress of a diary in either a total value or a mask.
     *
     * @param diary the diary.
     * @return current progress of the diary.
     */
    public int getProgress(final Diary diary) {
        return map.getInt(diary.objectiveName());
    }

    public final void refresh(final Diary diary) {
        final Map<Diary.DiaryComplexity, List<Diary>> map = diary.map();
        final EnumMap<Diary.DiaryComplexity, Integer> tabs = new EnumMap<>(DiaryComplexity.class);
        final Iterator<Map.Entry<Diary.DiaryComplexity, List<Diary>>> iterator = map.entrySet().iterator();
        int tasksAmount = 0;
        int completedAmount = 0;
        while (iterator.hasNext()) {
            final Map.Entry<Diary.DiaryComplexity, List<Diary>> next = iterator.next();
            final Diary.DiaryComplexity type = next.getKey();
            final List<Diary> list = next.getValue();
            for (int i = 0; i < list.size(); i++) {
                final Diary entry = list.get(i);
                tasksAmount++;
                if (getProgress(entry) == entry.objectiveLength()/* || entry.autoCompleted()*/) {
                    tabs.put(type, tabs.getOrDefault(type, 0) + 1);
                    completedAmount++;
                }
            }
        }
        final VarManager varManager = player.getVarManager();
        final DiaryChunk[] chunks = diary.chunks();
        for (int i = 0; i < chunks.length; i++) {
            final DiaryChunk chunk = chunks[i];
            final int index = i;
            final Diary.DiaryComplexity type = Utils.findMatching(DiaryComplexity.VALUES, value -> value.ordinal() == index);
            if (type == null) {
                throw new RuntimeException("Type is null.");
            }
            final Integer completed = tabs.getOrDefault(type, 0);
            if (map.get(type).size() == completed) {
                varManager.sendBit(chunk.getGreenVarbit(), true);
            }
            varManager.sendBit(chunk.getVarbit(), completed);
        }
        if (tasksAmount == completedAmount) {
            for (final int[] i : diary.diaryCompleted()) {
                varManager.sendBit(i[0], i[1]);
            }
            return;
        }
        if (completedAmount > 0) {
            varManager.sendBit(diary.diaryStarted(), true);
        }
    }

    public void finish(final Diary diary) {
        update(diary, diary.objectiveLength(), true);
        refresh(diary);
    }

    public void setFinished(final Diary diary) {
        map.put(diary.objectiveName(), diary.objectiveLength());
    }

    public void reset(final Diary diary) {
        map.put(diary.objectiveName(), 0);
        refresh(diary);
    }

    public void update(final Diary diary) {
        update(diary, 1, false);
    }

    public void update(final Diary diary, final int amount) {
        update(diary, amount, false);
    }

    /**
     * Updates the requested achievement diary if it hasn't already been completed.
     *
     * @param diary  the diary to update.
     * @param amount the amount to enqueue to current progress, or the flag to append if the diary is flag-based.
     */
    public void update(final Diary diary, final int amount, final boolean force) {
        final int progress = getProgress(diary);
        final String objective = diary.objectiveName();
        final Predicate<Player> predicate = diary.predicate();
        if (!force && (progress >= diary.objectiveLength() || predicate != null && !predicate.test(player))) {
            return;
        }
        if (diary.flagging()) {
            final int value = progress | amount;
            if (!force && value == progress) {
                return;
            }
            map.put(objective, Math.min(value, diary.objectiveLength()));
            if (value < diary.objectiveLength()) {
                final int bitsFlagged = BitUtils.getAmountOfBitsFlagged(value);
                player.sendMessage("<col=0040ff>Achievement Diary Stage Task - Current Stage " + bitsFlagged + ".");
            } else {
                final String typeName = diary.type().name().toLowerCase();
                final String areaName = diary.area().getAreaName();
                player.sendMessage("<col=dc143c>Well done! You have completed " + Utils.getAOrAn(typeName) + " " + typeName + " task in the " + areaName + " area. Your Achievement Diary has been updated.");
            }
        } else {
            final int value = progress + amount;
            map.put(objective, Math.min(value, diary.objectiveLength()));
            if (!force && value < diary.objectiveLength()) {
                player.sendMessage("<col=0040ff>Achievement Diary Stage Task - Current Stage " + value + ".");
            } else {
                final String typeName = diary.type().name().toLowerCase();
                final String areaName = diary.area().getAreaName();
                player.sendMessage("<col=dc143c>Well done! You have completed " + Utils.getAOrAn(typeName) + " " + typeName + " task in the " + areaName + " area. Your Achievement Diary has been updated.");
            }
        }
        refresh(diary);
        loop:
        for (final Diary.DiaryComplexity complexity : Diary.DiaryComplexity.VALUES) {
            final List<Diary> diaries = diary.map().get(complexity);
            for (final Diary d : diaries) {
                if (!isCompleted(d)) {
                    continue loop;
                }
            }
            final DiaryReward reward = DiaryReward.get(complexity, diary.area());
            if (reward == null || pendingRewards.get(reward.getArea()).containsKey(reward) || (pendingRewards.get(reward.getArea()).get(reward) != null && pendingRewards.get(reward.getArea()).get(reward))) {
                continue;
            }
            final boolean alreadyClaimed = player.getNumericAttribute("Already claimed diary reward: " + reward).intValue() == 1;
            if (!alreadyClaimed) {
                pendingRewards.get(reward.getArea()).put(reward, false);
            }
            final String claimString = alreadyClaimed ? "" : " Speak to " + NPCDefinitions.get(diary.taskMaster()).getName() + " to claim your reward.";
            player.getDialogueManager().start(new PlainChat(player, "Congratulations! You have completed all of the " + complexity.toString().toLowerCase() + " tasks in the<br><br>" + diary.area().getAreaName() + " area." + claimString));
            player.sendAdventurersEntry(AdventurersLogIcon.DIARY_COMPLETION, player.getName() + " has just completed the " + diary.area().getAreaName() + " " + complexity + " diary!");
        }
    }

    private boolean isCompleted(Diary diary) {
        if (diary.autoCompleted()) {
            return true;
        }
        return getProgress(diary) == diary.objectiveLength();
    }

    public boolean isAllCompleted() {
        for (final Diary[] set : ALL_DIARIES) {
            for (final Diary diary : set) {
                if (!isCompleted(diary)) {
                    return false;
                }
            }
        }
        return true;
    }

    public Map<DiaryArea, Map<DiaryReward, Boolean>> getPendingRewards() {
        return this.pendingRewards;
    }
}
