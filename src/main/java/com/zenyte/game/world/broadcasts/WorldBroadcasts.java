package com.zenyte.game.world.broadcasts;

import com.zenyte.api.client.query.adventurerslog.AdventurersLogIcon;
import com.zenyte.api.client.webhook.GlobalBroadcastWebhook;
import com.zenyte.game.constants.GameConstants;
import com.zenyte.game.content.follower.Pet;
import com.zenyte.game.content.follower.impl.BossPet;
import com.zenyte.game.content.minigame.inferno.model.InfernoCompletions;
import com.zenyte.game.content.treasuretrails.rewards.BroadcastedTreasure;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.item.enums.RareDrop;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.player.*;
import mgi.types.config.items.ItemDefinitions;
import mgi.types.config.npcs.NPCDefinitions;

import java.util.Arrays;
import java.util.List;

/**
 * @author Tommeh | 4-2-2019 | 22:11
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class WorldBroadcasts {
    public static final List<String> DISABLED_SKILLS = Arrays.asList("attack", "strength", "defence", "hitpoints");
    public static final String[] HELPFUL_TIPS = {"Did you know: Items lost on death will remain invisible on the ground for 3 minutes(boosted to 60 for UIM), followed by 5 minutes of visibility to everyone.", "Did you know: You can filter this message in the game settings.", "Did you know: Loyalty points are given to those actively playing " + GameConstants.SERVER_NAME + ". These points can be spent at the achievement hall for cosmetic items.", "Did you know: You can imbue items such as slayer helmets and dagannoth rings using Imbue Tokens purchasable from the loyalty store.", "Did you know: The box of restoration can be used once every ten minutes to restore your stats including hitpoints and prayer, run energy, and cure poison.", "Did you know: You can use regular teleports, fairy rings, spirit trees, gnome gliders and the " + GameConstants.SERVER_NAME + " portal to travel around the world.", "Did you know: You can complete daily challenges to obtain extra experience.", "Did you know: " + GameConstants.SERVER_NAME + " has full RuneLite integration? You can click the wrench icon on the client to customize the RuneLite settings to your liking.", "Did you know: You can enable two factor authentication (2FA) to prevent unauthorized logins to your accounts. Enable it in the Game noticeboard tab!", "Did you know: You can enable or disable level-up dialogues and broadcasts in the Game Settings menu in the Game noticeboard tab.", "Did you know: You can view the drop rate of any monster or item using the Drop Viewer in your Game noticeboard tab.", "Did you know: You can easily access our Website, Forums, Discord and Store from your Game noticeboard tab.", "Did you know: Your home, Varrock, Camelot, and Watchtower teleport can be right clicked so you can teleport to additional locations.", "Did you know: Runecrafting yields double the normal amount of runes per essence in addition to multiple runes at certain Runecrafting levels.", "Did you know: You can change your displayed experience drops by right clicking the “XP” orb and choosing “XP multiplier.”", "Did you know: You can fully customize your F-keys in the Options tab.", "Did you know: You can rewatch the " + GameConstants.SERVER_NAME + " tutorial by talking to the " + GameConstants.SERVER_NAME + " guide in the achievement hall.", "Did you know: You can change your spellbook using the altar on the second floor of the achievement hall.", "Did you know: The Magic shop sells " + GameConstants.SERVER_NAME + " home teleport tablets that allow you to instantly teleport to the home area.", "Did you know: You can vote for " + GameConstants.SERVER_NAME + " to receive a cash reward and vote points.", "Did you know: You can track your character's progress and achievements by visiting the Adventurer’s Log on our website.", "Did you know: There's a chance Krystilia will upgrade your emblem when completing a Wilderness slayer assignment.", "Did you know: You can buy RFD gloves from the chest in the Lumbridge Castle Basement.", "Did you know: You can view current buy and sell offers in the Grand Exchange with the \"Offers Viewer\" on the G.E Interface.", "Did you know: All farming timers are half that of Oldschool Runescape.", "Did you know: We provide a Wiki command and quick link under the world map that directs you to the Oldschool Runescape Wiki which is rather accurate.", "Did you know: We have a help chat called \"" + GameConstants.SERVER_NAME + "\" for any new and seasoned players alike. Join via the clan chat interface.", "Did you know: Teleport scrolls, which unlock convenient portal boss teleports, can be purchased from other players or from our store.", "Did you know: You can play " + GameConstants.SERVER_NAME + " on your favorite Android mobile device by downloading the APK via our website.", "Did you know: You can purchase a dramen staff from the Magic shop at home to navigate via the fairy rings teleport system.", "Did you know: You can find all of our community made guides with the ::guides command.", "Did you know: Donators start the Fight Caves minigame at wave 31, ::topic 260 for additional rank benefits.", "Did you know: You can start slayer in Burthorpe with Turael. All Slayer Masters and NPCs are in the correct locations.", "Did you know: You can purchase fully charged custom starter weapons from the " + GameConstants.SERVER_NAME + " guide for 200K each.", "Did you know: " + GameConstants.SERVER_NAME + " accepts OSGP donations - check out ::topic 352 or contact a member of staff for more information.", "Did you know: Zahur in Edgeville can add herbs to vials of water and crush secondary ingredients for you.", "Did you know: You can apply to be part of the quality assurance team on our forums and be the first to test upcoming content."};

    public static void sendMessage(final String message, final BroadcastType type, final boolean aboveChatbox) {
        final MessageType messageType = aboveChatbox ? MessageType.GLOBAL_BROADCAST : MessageType.UNFILTERABLE;
        for (final Player player : World.getPlayers()) {
            if (type.getSetting().isPresent() && player.getNumericAttribute(type.getSetting().get().toString()).intValue() == 0) {
                continue;
            }
            player.sendMessage(message, messageType);
        }
    }

    public static void broadcast(final Player player, final BroadcastType type, final Object... args) {
        final StringBuilder builder = new StringBuilder();
        final StringBuilder secondaryBuilder = new StringBuilder();
        final int icon = type.getIcon();
        final String color = type.getColor();
        Boolean broadcast = true;
        builder.append("<img=").append(icon).append("><col=").append(color).append(">").append("<shad=000000>");
        switch (type) {
            case MYSTERY_BOX_RARE_ITEM:
            case COSMETIC_BOX_RARE_ITEM: {
                final int id = Integer.parseInt(args[0].toString());
                final Item it = new Item(id);
                builder.append("News: ");
                secondaryBuilder.append(player.getName());
                secondaryBuilder.append(" has found ");
                secondaryBuilder.append(Utils.getAOrAn(it.getName()));
                secondaryBuilder.append(" ");
                secondaryBuilder.append(it.getName()).append(" in a ").append(type == BroadcastType.MYSTERY_BOX_RARE_ITEM ? "mystery" : "cosmetic").append(" box!");
                builder.append(secondaryBuilder);
                player.sendAdventurersEntry(it.getId() + ".png", secondaryBuilder.toString(), false);
                new GlobalBroadcastWebhook(it.getId() + ".png", secondaryBuilder.toString(), "Unboxed! Rare item found!").execute();
            }
            break;
            case TREASURE_TRAILS:
                if (!(args[0] instanceof Integer) || !(args[1] instanceof String)) {
                    return;
                }
                final int id = Integer.parseInt(args[0].toString());
                if (!BroadcastedTreasure.isBroadcasted(id)) {
                    return;
                }
                final String name = ItemDefinitions.getOrThrow(id).getName();
                builder.append("News: ");
                secondaryBuilder.append(player.getGameMode().getCrown());
                secondaryBuilder.append(player.getName());
                secondaryBuilder.append(" has received ");
                if (name.contains("gloves") || name.contains("vambraces") || name.contains("gauntlets") || name.contains("boots") || name.contains("manacles") || name.contains("sandals") || name.contains("legs")) {
                    secondaryBuilder.append("a pair of ").append(name);
                } else {
                    secondaryBuilder.append(Utils.getAOrAn(name));
                    secondaryBuilder.append(" ");
                    secondaryBuilder.append(name);
                }
                final String tier = args[1].toString();
                secondaryBuilder.append(" from ").append(Utils.getAOrAn(tier)).append(" ").append(tier).append(" clue scroll on casket ").append(player.getNumericAttribute("completed " + tier + " treasure trails"));
                secondaryBuilder.append("!");
                builder.append(secondaryBuilder);
                player.sendAdventurersEntry(id + ".png", secondaryBuilder.toString(), false);
                new GlobalBroadcastWebhook(id + ".png", secondaryBuilder.toString(), "New loot! Treasure found!").execute();
                break;
            case RARE_DROP:
                if (!(args[0] instanceof Item item) || !(args[1] instanceof String)) {
                    return;
                }
                if (!RareDrop.contains(item)) {
                    return;
                }
                builder.append("News: ");
                secondaryBuilder.append(player.getGameMode().getCrown());
                secondaryBuilder.append(player.getName());
                secondaryBuilder.append(" has received ");
                secondaryBuilder.append(Utils.getAOrAn(item.getName()));
                secondaryBuilder.append(" ");
                secondaryBuilder.append(item.getName()).append(" drop ");
                final String fromString = args[1].toString();
                if (fromString.endsWith("Chambers of Xeric")) {
                    secondaryBuilder.append("from ").append(fromString).append(" on chest ").append(player.getNumericAttribute(fromString.startsWith("Challenge Mode") ? "challengechambersofxeric" : "chambersofxeric").intValue());
                } else {
                    secondaryBuilder.append("from ").append(Utils.getAOrAn(fromString)).append(" ").append(fromString);
                    if (NotificationSettings.isKillcountTracked(fromString)) {
                        secondaryBuilder.append(" on killcount ").append(player.getNotificationSettings().getKillcount(fromString) + 1);
                    }
                }
                secondaryBuilder.append("!");
                builder.append(secondaryBuilder);
                player.sendAdventurersEntry(item.getId() + ".png", secondaryBuilder.toString(), false);
                new GlobalBroadcastWebhook(item.getId() + ".png", secondaryBuilder.toString(), "New loot! Rare drop received!").execute();
                break;
            case LVL_99:
                if (!(args[0] instanceof Integer)) {
                    return;
                }
                int skill = (Integer) args[0];
                String skillName = Skills.getSkillName(skill);
                if (player.getCombatXPRate() == 50 && DISABLED_SKILLS.contains(skillName.toLowerCase()))
                    broadcast = false;
                builder.append("News: ");
                secondaryBuilder.append(player.getGameMode().getCrown());
                secondaryBuilder.append(player.getName());
                secondaryBuilder.append(" has achieved level 99 ");
                secondaryBuilder.append(skillName);
                secondaryBuilder.append(" on x").append(player.getCombatXPRate());
                if (player.getCombatXPRate() != player.getSkillingXPRate()) {
                    secondaryBuilder.append('/').append(player.getSkillingXPRate());
                }
                secondaryBuilder.append("!");
                builder.append(secondaryBuilder);
                player.sendAdventurersEntry(skillName.toLowerCase() + ".png", secondaryBuilder.toString(), false);
                new GlobalBroadcastWebhook(skillName.toLowerCase() + ".png", secondaryBuilder.toString(), "Gains! Level 99 reached!").execute();
                break;
            case MAXED:
                builder.append("News: ");
                secondaryBuilder.append(player.getGameMode().getCrown());
                secondaryBuilder.append(player.getName());
                secondaryBuilder.append(" has achieved level 99 in all skills");
                secondaryBuilder.append(" on x").append(player.getCombatXPRate());
                if (player.getCombatXPRate() != player.getSkillingXPRate()) {
                    secondaryBuilder.append('/').append(player.getSkillingXPRate());
                }
                secondaryBuilder.append("!");
                builder.append(secondaryBuilder);
                player.sendAdventurersEntry(AdventurersLogIcon.OVERALL_SKILLING, secondaryBuilder.toString());
                new GlobalBroadcastWebhook(AdventurersLogIcon.OVERALL_SKILLING.getLink(), secondaryBuilder.toString(), "Is it over? New maxed player enters the battlefield!").execute();
                break;
            case XP_200M:
                if (!(args[0] instanceof Integer)) {
                    return;
                }
                skill = (Integer) args[0];
                skillName = Skills.getSkillName(skill);
                builder.append("News: ");
                secondaryBuilder.append(player.getGameMode().getCrown());
                secondaryBuilder.append(player.getName());
                secondaryBuilder.append(" has achieved 200 million XP in ");
                secondaryBuilder.append(skillName);
                secondaryBuilder.append(" on x").append(player.getCombatXPRate());
                if (player.getCombatXPRate() != player.getSkillingXPRate()) {
                    secondaryBuilder.append('/').append(player.getSkillingXPRate());
                }
                secondaryBuilder.append("!");
                builder.append(secondaryBuilder);
                player.sendAdventurersEntry(skillName.toLowerCase() + ".png", secondaryBuilder.toString(), false);
                new GlobalBroadcastWebhook(skillName.toLowerCase() + ".png", secondaryBuilder.toString(), "Maxed! 200m Achieved!").execute();
                break;
            case PET:
                if (!(args[0] instanceof Pet pet)) {
                    return;
                }
                builder.append("News: ");
                secondaryBuilder.append(player.getGameMode().getCrown());
                secondaryBuilder.append(player.getName());
                secondaryBuilder.append(" has received the ");
                secondaryBuilder.append(NPCDefinitions.get(pet.petId()).getName());
                secondaryBuilder.append(" pet!");
                builder.append(secondaryBuilder);
                player.sendAdventurersEntry(pet.itemId() + ".png", secondaryBuilder.toString(), false);
                new GlobalBroadcastWebhook(pet.itemId() + ".png", secondaryBuilder.toString(), "Woof! A new friend has been found!").execute();
                break;
            case GAMBLE_FIRECAPE:
                builder.append("News: ");
                secondaryBuilder.append(player.getGameMode().getCrown());
                secondaryBuilder.append(player.getName());
                secondaryBuilder.append(" has received the TzRek-Jad pet by gambling their fire cape!");
                builder.append(secondaryBuilder);
                player.sendAdventurersEntry("13225.png", secondaryBuilder.toString(), false);
                new GlobalBroadcastWebhook(BossPet.TZREK_JAD.getItemId(), secondaryBuilder.toString(), "Lucky! A new friend appears!").execute();
                break;
            case HCIM_DEATH:
                if (player.getSkills().getTotalLevel() < 500) {
                    return;
                }
                builder.append("News: ");
                secondaryBuilder.append(player.getGameMode().getCrown());
                secondaryBuilder.append(player.getName());
                secondaryBuilder.append(" has died as a Hardcore ");
                secondaryBuilder.append(player.getAppearance().getGender());
                secondaryBuilder.append(" with a skill total of ");
                secondaryBuilder.append(player.getSkills().getTotalLevel());
                if (args[0] instanceof Entity source) {
                    if (source instanceof Player) {
                        if (source.equals(player)) {
                            secondaryBuilder.append(", by self-inflicted damage!");
                        } else {
                            secondaryBuilder.append(", losing against ");
                            secondaryBuilder.append(((Player) source).getName());
                            secondaryBuilder.append("!");
                        }
                    } else if (source instanceof NPC) {
                        secondaryBuilder.append(", fighting against: ");
                        secondaryBuilder.append(((NPC) source).getDefinitions().getName());
                    }
                } else {
                    secondaryBuilder.append(", by unknown damage!");
                }
                builder.append(secondaryBuilder);
                player.sendAdventurersEntry(AdventurersLogIcon.HCIM_DEATH, secondaryBuilder.toString());
                new GlobalBroadcastWebhook(AdventurersLogIcon.HCIM_DEATH.getLink(), secondaryBuilder.toString(), "Unlucky! Another HCIM down!").execute();
                break;
            case HELPFUL_TIP:
                if (!(args[0] instanceof String tip)) {
                    return;
                }
                builder.append(tip);
                break;
            case INFERNO_COMPLETION:
                final GameMode mode = player.getGameMode();
                final int completions = InfernoCompletions.getCompletions(mode);
                final String completion = completions == 1 ? "first" : completions == 2 ? "second" : completions == 3 ? "third" : "";
                builder.append("News: ");
                secondaryBuilder.append(player.getGameMode().getCrown());
                secondaryBuilder.append(player.getName());
                if (!InfernoCompletions.isBroadcasted(player) && !completion.isEmpty()) {
                    secondaryBuilder.append(" is the ").append(completion).append(" ").append(mode.getCrown()).append("</col>").append(GameMode.getTitle(player)).append("<col=").append(type.getColor()).append("> to complete the Inferno").append(", with a combat level of ").append(player.getCombatLevel()).append("!");
                    InfernoCompletions.setBroadcasted(player);
                } else {
                    secondaryBuilder.append(" has completed the Inferno").append(", with a combat level of ").append(player.getCombatLevel()).append("!");
                }
                builder.append(secondaryBuilder);
                new GlobalBroadcastWebhook(ItemId.INFERNAL_CAPE, secondaryBuilder.toString().replaceAll("<col=[a-zA-Z0-9]{6}>", "").replaceAll("</col>", ""), "The JalYt did it!").execute();
                break;
        }
        if (broadcast) {
            sendMessage(builder.toString(), type, false);
        }
    }
}
