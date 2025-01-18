package com.zenyte.game.content.theatreofblood.interfaces;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.content.theatreofblood.area.VerSinhazaArea;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.util.AccessMask;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.plugins.dialogue.PlainChat;

import java.util.HashSet;

import static com.zenyte.game.content.theatreofblood.party.RaidingParty.getPlayer;

/**
 * @author Tommeh | 5/21/2020 | 7:36 PM
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class PartyInformationInterface extends Interface {
    private static final SoundEffect rejectSound = new SoundEffect(2277);

    @Override
    protected void attach() {
        put(0, 0, "Back");
        put(0, 1, "Refresh");
        put(0, 2, "Unblock");
        put(0, 3, "Set preferred size");
        put(0, 4, "Set preferred combat level");
        put(0, 5, "Apply/Disband/Leave/Withdraw");
        put(0, 6, "Mode");
        for (int i = 0; i < 5; i++) {
            put(0, 7 + i, "PartyMemberOp" + i);
        }
        for (int i = 0; i < 10; i++) {
            put(0, 12 + i, "ApplicantsAcceptOp" + i);
        }
        for (int i = 0; i < 10; i++) {
            put(0, 22 + i, "ApplicantsDeclineOp" + i);
        }
    }

    @Override
    public void open(final Player player) {
        player.getInterfaceHandler().sendInterface(this);
        player.getPacketDispatcher().sendComponentSettings(
                getInterface(),
                0,
                0,
                100,
                AccessMask.CLICK_OP1
        );
    }

    @Override
    protected void build() {
        bind("Back", PartiesOverviewInterface::refresh);
        bind("Refresh", player -> {
            final var party = VerSinhazaArea.getParty(player, true);
            if (party == null) {
                return;
            }
            party.updateInformation(player);
        });
        bind("Apply/Disband/Leave/Withdraw", player -> {
            final var party = VerSinhazaArea.getParty(player, false, true, true);
            if (party == null) {
                return;
            }
            switch (party.getRights(player)) {
            case LEADER: 
                VerSinhazaArea.removeParty(party);
                party.disband();
                break;
            case PARTY_MEMBER: 
                party.removeMember(player);
                break;
            case APPLICANT: 
                party.getApplicants().remove(player.getUsername());
                party.updateInformation(player);
                if (party.getLeader().getInterfaceHandler().isPresent(GameInterface.TOB_PARTY_INFORMATION)) {
                    party.updateInformation(party.getLeader());
                }
                break;
            case CAN_APPLY: 
                final var currentParty = VerSinhazaArea.getParty(player);
                if (currentParty != null) {
                    player.getInterfaceHandler().closeInterface(getInterface());
                    player.getDialogueManager().start(new Dialogue(player) {
                        @Override
                        public void buildDialogue() {
                            options("You are already in a party.", "Stay in my existing party.", "Quit that one and apply to this one.").onOptionOne(() -> party.updateInformation(player)).onOptionTwo(() -> {
                                currentParty.removeMember(player);
                                party.addApplicant(player);
                                party.updateInformation(player);
                            });
                        }
                    });
                    player.sendMessage("You cannot apply for this party if you\'re already in another one.");
                    return;
                }
                if (party.getApplicants().size() == 10) {
                    player.sendMessage("There are too many applicants for this party at the moment.");
                    return;
                }
                if (party.getBlocked().contains(player.getUsername())) {
                    player.sendMessage("You have already applied for this party. Wait until the party leader either accepts or rejects it.");
                    return;
                }
                if (party.getApplicants().contains(player.getUsername())) {
                    return;
                }
                party.addApplicant(player);
                break;
            case BLOCKED_APPLICANT: 
                player.getInterfaceHandler().closeInterfaces();
                player.getDialogueManager().start(new PlainChat(player, "You have been declined by this party."));
                break;
            }
        });
        bind("Set preferred size", player -> {
            final var party = VerSinhazaArea.getParty(player);
            if (party == null) {
                return;
            }
            if (!party.isLeader(player)) {
                return;
            }
            player.getInterfaceHandler().closeInterfaces();
            player.sendInputInt("Set a preferred party size (or 0 to clear it)", s -> {
                final var size = Math.min(5, s);
                party.setPreferredSize(size);
                party.updateInformation(player);
            });
        });
        bind("Set preferred combat level", player -> {
            final var party = VerSinhazaArea.getParty(player);
            if (party == null) {
                return;
            }
            if (!party.isLeader(player)) {
                return;
            }
            player.getInterfaceHandler().closeInterfaces();
            player.sendInputInt("Set a preferred combat level (or 0 to clear it)", l -> {
                final var level = Math.min(126, l);
                party.setPreferredCombatLevel(level);
                party.updateInformation(player);
            });
        });
        bind("Unblock", player -> {
            final var party = VerSinhazaArea.getParty(player);
            if (party == null) {
                return;
            }
            if (!party.isLeader(player)) {
                return;
            }
            final var usernames = new HashSet<>(party.getBlocked());
            party.getBlocked().clear();
            for (final var username : usernames) {
                final var p = getPlayer(username);
                if (p == null) {
                    continue;
                }
                if (p.getInterfaceHandler().isPresent(GameInterface.TOB_PARTY_INFORMATION)) {
                    party.updateInformation(p);
                }
            }
        });
        for (int i = 0; i < 10; i++) {
            bind("ApplicantsAcceptOp" + i, (player, slotId, itemId, option) -> {
                var index = slotId - 12;
                var party = VerSinhazaArea.getParty(player);
                if (party == null) return;
                if (index >= party.getApplicants().size()) {
                    return;
                }
                var username = party.getApplicants().get(index);
                var applicant = getPlayer(username);
                if (applicant == null) return;
                if (party.getMembers().size() == 5) {
                    player.sendMessage("Your raiding party is already full.");
                    return;
                }
                if (VerSinhazaArea.getParty(applicant) != null) {
                    party.getApplicants().remove(applicant.getUsername());
                    player.getInterfaceHandler().closeInterface(getInterface());
                    player.getDialogueManager().start(
                            new PlainChat(player, applicant.getName() + " appears to have joined another party.")
                    );
                    return;
                }
                if (party.getApplicants().remove(applicant.getUsername())) {
                    party.getMembers().add(applicant.getUsername());
                    party.getOriginalMembers().add(applicant.getUsername());
                }
                for (String m : party.getMembers()) {
                    var member = getPlayer(m);
                    if (member == null) continue;
                    PartyOverlayInterface.refresh(member, party);
                }
                if (applicant.getInterfaceHandler().isPresent(GameInterface.TOB_PARTY_INFORMATION)) {
                    party.updateInformation(applicant);
                }
                if (player.getInterfaceHandler().isPresent(GameInterface.TOB_PARTY_INFORMATION)) {
                    party.updateInformation(player);
                }
            });
        }
        for (int i = 0; i < 10; i++) {
            bind("ApplicantsDeclineOp" + i, (player, slotId, itemId, option) -> {
                var index = slotId - 22;
                var party = VerSinhazaArea.getParty(player);
                if (party == null) return;
                if (index >= party.getApplicants().size()) {
                    return;
                }
                var username = party.getApplicants().get(index);
                var applicant = getPlayer(username);
                if (applicant == null) return;
                applicant.sendMessage("Your application to the party of " + party.getLeader().getName() + " has been declined");
                applicant.sendSound(rejectSound);
                party.getBlocked().add(applicant.getUsername());
                party.getApplicants().remove(applicant.getUsername());
                if (applicant.getInterfaceHandler().isPresent(GameInterface.TOB_PARTY_INFORMATION))
                    party.updateInformation(applicant);
                if (player.getInterfaceHandler().isPresent(GameInterface.TOB_PARTY_INFORMATION))
                    party.updateInformation(player);
            });
        }
        for (int i = 0; i < 5; i++) {
            bind("PartyMemberOp" + i, (player, slotId, itemId, option) -> {
                var party = VerSinhazaArea.getParty(player);
                if (party == null) return;
                if (!party.isLeader(player)) return;
                if (slotId == 0) {
                    if (party.getMembers().size() == 1) {
                        VerSinhazaArea.removeParty(party);
                        party.disband();
                    } else {
                        var member = party.getMember(0);
                        if (member == null) return;
                        party.removeMember(member);
                    }
                    return;
                }
                var index = slotId - 7;
                if (index > party.getMembers().size()) return;
                var member = party.getMember(index);
                if (member == null) return;
                party.removeMember(member);
            });
        }
    }

    @Override
    public GameInterface getInterface() {
        return GameInterface.TOB_PARTY_INFORMATION;
    }
}
