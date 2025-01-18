package com.zenyte.plugins.dialogue;

import com.zenyte.Constants;
import com.zenyte.GameEngine;
import com.zenyte.api.client.query.StorePurchaseCheckRequest;
import com.zenyte.api.client.query.VoteCheckRequest;
import com.zenyte.api.model.StorePurchase;
import com.zenyte.cores.CoresManager;
import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.content.drops.DropTableBuilder;
import com.zenyte.game.content.drops.table.DropTable;
import com.zenyte.game.content.treasuretrails.ClueItem;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Colour;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.player.GameMode;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.MemberRank;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.ContainerResult;
import com.zenyte.game.world.entity.player.container.impl.Inventory;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import it.unimi.dsi.fastutil.ints.Int2IntAVLTreeMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import mgi.types.config.items.ItemDefinitions;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jetbrains.annotations.NotNull;

public class WiseOldManD extends Dialogue {
    public WiseOldManD(final Player player, final NPC npc) {
        super(player, npc);
    }

    public static void checkVotesWith2FA(final Player player) {
        if (Constants.WORLD_PROFILE.isPrivate() || Constants.WORLD_PROFILE.isBeta()) {
            player.sendMessage("You cannot do that on this world.");
            return;
        }
        if (!player.getAuthenticator().isEnabled()) {
            player.getDialogueManager().start(new Dialogue(player) {
                @Override
                public void buildDialogue() {
                    plain("You have no 2FA enabled. Players who have 2FA enabled will receive an extra 25,000 coins per every vote claimed. Would you like to claim the votes or enable 2FA first?");
                    options(TITLE, new DialogueOption("Claim votes anyways.", () -> claimVotes(player)), new DialogueOption("I'll set 2FA up first."));
                }
            });
            return;
        }
        claimVotes(player);
    }

    private static int getVotePointBonus(final Player player) {
        if (player.getMemberRank().eligibleTo(MemberRank.ZENYTE_MEMBER)) {
            return 2;
        }
        if (player.getMemberRank().eligibleTo(MemberRank.RUBY_MEMBER)) {
            return 1;
        }
        return 0;
    }

    private static void claimVotes(final Player player) {
        player.getInterfaceHandler().closeInterfaces();
        player.getDialogueManager().start(new PlainChat(player, "Checking vote rewards...", false));
        player.lock();
        player.log(LogLevel.INFO, "Claiming votes...");
        CoresManager.getServiceProvider().submit(() -> {
            int totalTimesVoted;
            try {
                totalTimesVoted = new VoteCheckRequest(player.getUsername()).execute();
                WorldTasksManager.schedule(() -> applyVotes(player, totalTimesVoted));
            } catch (Exception e) {
                WorldTasksManager.schedule(() -> {
                    player.unlock();
                    player.log(LogLevel.ERROR, "Failed to retrieve the votes: \n" + ExceptionUtils.getStackTrace(e));
                    player.getDialogueManager().start(new PlainChat(player, "Failed to retrieve your votes, please try again. If further attempts say you have no votes remaining, contact an administrator."));
                });
            }
        });
    }

    private static void applyVotes(@NotNull final Player player, final int amount) {
        int votePoints = amount;
        final int bonusPoints = getVotePointBonus(player);
        if (bonusPoints > 0) {
            for (int i = 0; i < amount; i++) {
                if (player.getAttributes().remove("extra vote point") != null) {
                    votePoints += bonusPoints;
                } else {
                    player.getAttributes().put("extra vote point", true);
                }
            }
        }
        final boolean twofactor = player.getAuthenticator().isEnabled();
        final int totalAmount = votePoints;
        player.log(LogLevel.INFO, "User claimed " + amount + "(+" + (votePoints - amount) + " bonus) vote points and had 2FA " + (twofactor ? "enabled" : "disabled") + ".");
        player.unlock();
        player.getInterfaceHandler().closeInterfaces();
        if (totalAmount > 0) {
            final int gpReward = twofactor ? 75000 : 50000;
            player.getInventory().addOrDrop(new Item(995, gpReward * totalAmount));
            player.sendMessage("You received " + Utils.format(gpReward * totalAmount) + " gold pieces for voting!");
            rollClues(player, totalAmount);
            player.addAttribute("vote_points", player.getNumericAttribute("vote_points").intValue() + totalAmount);
            GameInterface.GAME_NOTICEBOARD.getPlugin().ifPresent(plugin -> player.getPacketDispatcher().sendComponentText(GameInterface.GAME_NOTICEBOARD, plugin.getComponent("Vote credits"), "Vote credits: <col=ffffff>" + player.getNumericAttribute("vote_points").intValue() + "</col>"));
        }
        final StringBuilder sb = new StringBuilder();
        if (totalAmount == 0) {
            sb.append("You have no pending votes.");
        } else {
            sb.append(amount).append(" votes have been successfully claimed.");
            if (totalAmount > amount) {
                final int extraVotes = totalAmount - amount;
                sb.append(" You receive ");
                sb.append(extraVotes);
                sb.append(" extra vote");
                if (extraVotes > 1) {
                    sb.append("s");
                }
                sb.append(".");
            }
        }
        player.getDialogueManager().start(new PlainChat(player, sb.toString()));
    }

    private static final DropTable voteNon2faTable = new DropTableBuilder().append(ClueItem.MEDIUM.getScrollBox(), 70).append(ClueItem.HARD.getScrollBox(), 20).append(ClueItem.ELITE.getScrollBox(), 10).build();
    private static final DropTable vote2faTable = new DropTableBuilder().append(ClueItem.MEDIUM.getScrollBox(), 68).append(ClueItem.HARD.getScrollBox(), 20).append(ClueItem.ELITE.getScrollBox(), 10).append(ClueItem.MASTER.getScrollBox(), 2).build();

    public static void rollClues(@NotNull final Player player, final int amount) {
        if (amount <= 0) {
            return;
        }
        final int existingRolls = player.getNumericAttribute("claimed vote points").intValue();
        player.addAttribute("claimed vote points", existingRolls + amount);
        final boolean twoFactorAuthenticator = player.getAuthenticator().isEnabled();
        final Inventory inventory = player.getInventory();
        //Offset it by one so that when the player claims their very first vote, they don't immediately receive a clue.
        final int length = 1 + existingRolls + amount;
        final int interval = 3;
        final Int2IntAVLTreeMap map = new Int2IntAVLTreeMap();
        for (int i = 1 + existingRolls; i < length; i++) {
            if (i % interval != 0) {
                continue;
            }
            final Item scrollBox = generateRandomClue(twoFactorAuthenticator);
            inventory.addOrDrop(scrollBox);
            map.put(scrollBox.getId(), map.get(scrollBox.getId()) + scrollBox.getAmount());
        }
        if (map.isEmpty()) {
            return;
        }
        player.sendMessage(Colour.RED.wrap("You've received the following scroll boxes for voting: "));
        for (final Int2IntMap.Entry entry : map.int2IntEntrySet()) {
            if (entry.getIntKey() == ClueItem.MASTER.getScrollBox()) {
                player.sendMessage(Colour.RS_PURPLE.wrap("2FA Special: ") + Colour.RED.wrap(entry.getIntValue() + " x " + ItemDefinitions.getOrThrow(entry.getIntKey()).getName()));
                continue;
            }
            player.sendMessage(Colour.RED.wrap(entry.getIntValue() + " x " + ItemDefinitions.getOrThrow(entry.getIntKey()).getName()));
        }
    }

    private static Item generateRandomClue(final boolean twofactorAuthenticator) {
        final DropTable table = twofactorAuthenticator ? vote2faTable : voteNon2faTable;
        return table.rollItem();
    }

    private static void claimDonations(final Player player) {
        player.getInterfaceHandler().closeInterfaces();
        player.getDialogueManager().start(new PlainChat(player, "Checking donations...", false));
        player.lock();
        player.log(LogLevel.INFO, "Claiming donations...");
        CoresManager.getServiceProvider().submit(() -> {
            StorePurchase[] request;
            try {
                request = new StorePurchaseCheckRequest(player.getUsername()).execute();
                WorldTasksManager.schedule(() -> applyDonations(player, request));
            } catch (Exception e) {
                WorldTasksManager.schedule(() -> {
                    GameEngine.logger.error("", e);
                    player.unlock();
                    player.log(LogLevel.ERROR, "Failed to retrieve the items: \n" + ExceptionUtils.getStackTrace(e));
                    player.getDialogueManager().start(new PlainChat(player, "Failed to retrieve your items, please try again. If further attempts say you have no items pending, contact an administrator."));
                });
            }
        });
    }

    private static void applyDonations(@NotNull final Player player, @NotNull final StorePurchase[] request) {
        player.syncTotalDonated();
        player.unlock();
        player.getInterfaceHandler().closeInterfaces();
        final StringBuilder builder = new StringBuilder();
        for (final StorePurchase item : request) {
            builder.append("Id: ").append(item.getId()).append(", name: ").append(item.getItemName()).append(", amount: ").append(item.getAmount()).append(", quantity purchased: ").append(item.getItemQuantity()).append(", price: ").append(item.getPrice()).append(", discount: ").append(item.getDiscount()).append("\n");
            if (player.getGameMode().equals(GameMode.ULTIMATE_IRON_MAN)) {
                player.getInventory().addItem(new Item(item.getId(), item.getItemQuantity() * item.getAmount())).onFailure(r -> {
                    player.sendMessage("<col=ff0000>Some of the donation rewards were dropped on the ground due to lack of space.");
                    World.spawnFloorItem(r, player);
                });
            } else {
                player.getInventory().addItem(new Item(item.getId(), item.getItemQuantity() * item.getAmount())).onFailure(remaining -> {
                    final ContainerResult result = player.getBank().add(remaining);
                    result.onFailure(lastRemaining -> {
                        player.sendMessage("<col=ff0000>Some of the donation rewards were dropped on the ground due to lack of space.");
                        World.spawnFloorItem(lastRemaining, player);
                    });
                    if (result.getSucceededAmount() > 0) {
                        player.sendMessage("<col=ff0000>Some of the donation rewards were sent to your bank due to lack of space.");
                    }
                });
            }
            if (item.getId() == ItemId.DARKLIGHT || item.getId() == ItemId.DARKLIGHT_8281) {
                player.addAttribute("demon_kills", 100);
            }
        }
        if (builder.length() >= 2) {
            builder.delete(builder.length() - 2, builder.length());
        }
        player.log(LogLevel.INFO, "Successfully retrieved donations: \n" + builder);
        player.getDialogueManager().start(new PlainChat(player, request.length == 0 ? "You have no pending items." : "All donations successfully claimed."));
    }

    @Override
    public void buildDialogue() {
        if (Constants.WORLD_PROFILE.isPrivate() || Constants.WORLD_PROFILE.isBeta()) {
            npc("Sorry, I cannot help you on this world.");
            return;
        }
        npc("What can I help you with, " + player.getName() + "?");
        options("Choose an option", "Check donations", "Check vote rewards", "Cancel").onOptionOne(() -> claimDonations(player)).onOptionTwo(() -> checkVotesWith2FA(player));
    }
}
