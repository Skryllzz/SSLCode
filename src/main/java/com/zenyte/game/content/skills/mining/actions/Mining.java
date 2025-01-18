package com.zenyte.game.content.skills.mining.actions;

import com.zenyte.game.content.achievementdiary.AchievementDiaries;
import com.zenyte.game.content.achievementdiary.diaries.*;
import com.zenyte.game.content.minigame.castlewars.CastlewarsRockPatch;
import com.zenyte.game.content.minigame.motherlode.OreVein;
import com.zenyte.game.content.minigame.motherlode.Paydirt;
import com.zenyte.game.content.minigame.motherlode.UpperMotherlodeArea;
import com.zenyte.game.content.skills.mining.MiningDefinitions;
import com.zenyte.game.content.skills.mining.MiningDefinitions.OreDefinitions;
import com.zenyte.game.content.skills.mining.MiningDefinitions.PickaxeDefinitions;
import com.zenyte.game.content.skills.mining.MiningDefinitions.ShapeDefinitions;
import com.zenyte.game.content.treasuretrails.ClueItem;
import com.zenyte.game.content.treasuretrails.clues.CharlieTask;
import com.zenyte.game.content.treasuretrails.clues.SherlockTask;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.item.SkillcapePerk;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.Projectile;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.pathfinding.events.npc.NPCObjectEvent;
import com.zenyte.game.world.entity.pathfinding.events.player.ObjectEvent;
import com.zenyte.game.world.entity.pathfinding.strategy.ObjectStrategy;
import com.zenyte.game.world.entity.player.Action;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.entity.player.container.Container;
import com.zenyte.game.world.entity.player.container.impl.Inventory;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import com.zenyte.game.world.entity.player.dailychallenge.DailyChallengeManager;
import com.zenyte.game.world.entity.player.dailychallenge.challenge.SkillingChallenge;
import com.zenyte.game.world.entity.player.perk.PerkWrapper;
import com.zenyte.game.world.object.ObjectId;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.game.world.region.CharacterLoop;
import com.zenyte.plugins.dialogue.PlainChat;
import com.zenyte.plugins.itemonnpc.ItemOnBarricadeAction;
import com.zenyte.plugins.itemonobject.PotionOnCastlewarsRocks;

import java.util.Optional;

import static com.zenyte.game.content.skills.woodcutting.actions.Woodcutting.BURN_GFX;

/**
 * @author Noele | Nov 9, 2017 : 12:22:34 AM
 * see https://noeles.life || noele@zenyte.com
 */
public class Mining extends Action {

    public static final Graphics ROCKFALL_EXPLOSION = new Graphics(305);

    public static final Projectile ROCKFALL_PROJECTILE = new Projectile(645, 255, 0, 0, 0, 25, 64, 5);

    private final OreDefinitions ore;

    private WorldObject rock;

    private NPC npc;

    private PickaxeDefinitions tool;

    private Item pickaxe;

    private boolean pickaxeEquipment;

    private Container container;

    private int slotId;

    public Mining(final WorldObject rock) {
        this.rock = rock;
        ore = OreDefinitions.getDef(rock.getId());
    }

    public Mining(final OreDefinitions ore, final NPC npc) {
        this.ore = ore;
        this.npc = npc;
    }

    private static int randomGem() {
        final int random = Utils.random(127);
        if (random < 60) {
            return 1625;
        } else if (random < 90) {
            return 1627;
        } else if (random < 105) {
            return 1629;
        } else if (random < 114) {
            return 1623;
        } else if (random < 119) {
            return 1621;
        } else if (random < 124) {
            return 1619;
        }
        return 1617;
    }

    @Override
    public boolean interruptedByCombat() {
        return false;
    }

    @Override
    public boolean start() {
        if (!checkTool())
            return false;
        if (!check()) {
            return false;
        }
        player.sendFilteredMessage("You swing your pick at the rock.");
        final int time = (ore.equals(OreDefinitions.ESSENCE)) ? 1 : tool.getMineTime();
        delay(time);
        return true;
    }

    public boolean success() {
        if (ore.equals(OreDefinitions.ESSENCE)) {
            return true;
        }
        assert ore.getSpeed() > 0;
        int level = player.getSkills().getLevel(Skills.MINING) + (player.inArea("Mining Guild") ? 7 : 0);
        if (ore == OreDefinitions.GEM) {
            final Item amulet = player.getAmulet();
            if (amulet != null && amulet.getName().toLowerCase().contains("glory")) {
                level += 30;
            }
        }
        final int advancedLevels = level - ore.getSpeed();
        if (ore == OreDefinitions.PAYDIRT) {
            return 25 + (level / 2.25F) > Utils.random(100);
        }
        final int chance = Math.max(Math.min(Math.round(advancedLevels * 0.8F) + 20, 70), 4) * 2;
        return chance > Utils.random(100);
    }

    @Override
    public boolean process() {
        if (!check()) {
            return false;
        }
        final boolean altAnim = this.ore == OreDefinitions.AMETHYST || this.ore == OreDefinitions.PAYDIRT || this.ore == OreDefinitions.CWARS_WALL;
        player.setAnimation(altAnim ? tool.getAlternateAnimation() : tool.getAnim());
        return checkObject();
    }

    @Override
    public int processWithDelay() {
        if (!success()) {
            return (ore.equals(OreDefinitions.ESSENCE)) ? 1 : tool.getMineTime();
        }
        if (ore == OreDefinitions.CWARS_WALL) {
            final CastlewarsRockPatch wallData = CastlewarsRockPatch.getData(rock);
            if (wallData == null)
                return -1;
            player.sendMessage("You've collapsed the tunnel!");
            World.sendGraphics(ItemOnBarricadeAction.EXPLOSION, wallData.getPatch());
            World.spawnObject(wallData.getPatch());
            PotionOnCastlewarsRocks.processVarbits(rock, true);
            CharacterLoop.forEach(wallData.getPatch(), 1, Entity.class, entity -> {
                if (Utils.collides(wallData.getPatch().getX(), wallData.getPatch().getY(), 2, entity.getX(), entity.getY(), entity.getSize())) {
                    if (entity instanceof Player) {
                        entity.applyHit(new Hit(entity.getHitpoints(), HitType.REGULAR));
                    }
                }
            });
            return -1;
        }
        if (ore == OreDefinitions.CWARS_ROCKS) {
            final boolean initial = rock.getId() == 4437;
            if (initial) {
                rock = new WorldObject(ObjectId.ROCKS_4438, rock.getType(), rock.getRotation(), rock);
                World.spawnObject(rock);
            } else {
                World.removeObject(rock);
                PotionOnCastlewarsRocks.processVarbits(rock, false);
            }
            return initial ? tool.getMineTime() : -1;
        }
        final Skills skills = player.getSkills();
        if (ore == OreDefinitions.ROCKFALL) {
            skills.addXp(Skills.MINING, ore.getXp());
            World.removeObject(rock);
            WorldTasksManager.schedule(() -> {
                final int[] elements = new int[] { -1, 1 };
                World.sendProjectile(rock.transform(elements[Utils.random(elements.length - 1)], elements[Utils.random(elements.length - 1)], 0), rock, ROCKFALL_PROJECTILE);
                World.sendProjectile(rock.transform(elements[Utils.random(elements.length - 1)], elements[Utils.random(elements.length - 1)], 0), rock, ROCKFALL_PROJECTILE);
                WorldTasksManager.schedule(() -> {
                    CharacterLoop.forEach(rock, 1, Entity.class, entity -> {
                        if (Utils.collides(rock.getX(), rock.getY(), 1, entity.getX(), entity.getY(), entity.getSize())) {
                            if (entity instanceof Player) {
                                entity.applyHit(new Hit(Utils.random(1, 4), HitType.DEFAULT));
                            }
                            if (entity instanceof Player) {
                                entity.setRouteEvent(new ObjectEvent(((Player) entity), new ObjectStrategy(rock), null));
                            } else {
                                entity.setRouteEvent(new NPCObjectEvent(((NPC) entity), new ObjectStrategy(rock)));
                            }
                        }
                    });
                    World.sendGraphics(ROCKFALL_EXPLOSION, rock);
                    World.spawnObject(rock);
                });
            }, this.ore.getTime());
            return -1;
        }
        if (ore == OreDefinitions.PAYDIRT) {
            if (rock instanceof OreVein) {
                ((OreVein) rock).start();
            }
            if (!UpperMotherlodeArea.polygon.contains(rock) && Utils.random(0, 2) == 0) {
                final int emptyId = rock.getId() + 4;
                final WorldObject empty = new WorldObject(emptyId, rock.getType(), rock.getRotation(), rock.getX(), rock.getY(), rock.getPlane());
                World.spawnObject(empty);
                final int size = World.getPlayers().size();
                final float percentage = Math.min(size, 500) / 1000.0F;
                final int time = (int) (ore.getTime() - (ore.getTime() * percentage));
                WorldTasksManager.schedule(() -> World.spawnObject(rock), time);
            }
        }
        final Inventory inventory = player.getInventory();
        if (ore == OreDefinitions.VOLCANIC_ASH) {
            skills.addXp(Skills.MINING, ore.getXp());
            final int level = skills.getLevel(Skills.MINING);
            int amount = level >= 97 ? 6 : level >= 82 ? 5 : level >= 67 ? 4 : level >= 52 ? 3 : level >= 37 ? 2 : 1;
            final Item ore = new Item(this.ore.getOre(), amount);
            player.sendFilteredMessage("You manage to mine some " + ore.getName().toLowerCase() + ".");
            inventory.addItem(ore);
            ClueItem.roll(player, this.ore.getBaseClueGeodeChance(), skills.getLevel(Skills.MINING), ClueItem::getClueGeode);
            if (Utils.random(8) == 0) {
                final WorldObject empty = new WorldObject(ShapeDefinitions.getEmpty(rock.getId()), rock.getType(), rock.getRotation(), rock.getX(), rock.getY(), rock.getPlane());
                World.spawnObject(empty);
                WorldTasksManager.schedule(() -> World.spawnObject(rock), this.ore.getTime());
                return -1;
            }
            return tool.getMineTime();
        }
        if (ore == OreDefinitions.GEM) {
            skills.addXp(Skills.MINING, ore.getXp());
            final Item gem = new Item(randomGem());
            if (gem.getId() == ItemId.UNCUT_RED_TOPAZ) {
                player.getAchievementDiaries().update(KaramjaDiary.MINE_A_RED_TOPAZ);
            }
            player.sendFilteredMessage("You manage to mine some " + gem.getName().toLowerCase().replace("uncut ", "") + ".");
            inventory.addItem(gem);
            ClueItem.roll(player, this.ore.getBaseClueGeodeChance(), skills.getLevel(Skills.MINING), ClueItem::getClueGeode);
            final WorldObject empty = new WorldObject(ShapeDefinitions.getEmpty(rock.getId()), rock.getType(), rock.getRotation(), rock.getX(), rock.getY(), rock.getPlane());
            World.spawnObject(empty);
            WorldTasksManager.schedule(() -> World.spawnObject(rock), ore.getTime());
            return -1;
        }
        final boolean valid = player.getPerkManager().isValid(PerkWrapper.MASTER_MINER);
        int amount = player.getPerkManager().isValid(PerkWrapper.MASTER_MINER) && Utils.random(0, 100) <= 15 ? 2 : 1;
        if (ore.isExtraOre() && Utils.random(99) < 5 && SkillcapePerk.MINING.isEffective(player)) {
            amount++;
        }
        if (ore == OreDefinitions.TE_SALT || ore == OreDefinitions.EFH_SALT || ore == OreDefinitions.URT_SALT) {
            amount = Utils.random(2, 5);
        }
        final int body = player.getEquipment().getId(EquipmentSlot.PLATE);
        if (body >= 13104 && body <= 13107) {
            final MiningDefinitions.OreDefinitions limit = body == 13104 ? OreDefinitions.COAL : body == 13105 ? OreDefinitions.MITHRIL : body == 13106 ? OreDefinitions.ADAMANTITE : OreDefinitions.AMETHYST;
            if (Utils.random(10) == 0 && ore.ordinal() <= limit.ordinal()) {
                amount++;
            }
        }
        final boolean deplete = ore.getDepletionRate() > 0 && Utils.random(ore.getDepletionRate() - 1) == 0;
        if (deplete && npc == null) {
            World.spawnObject(new WorldObject(ShapeDefinitions.getEmpty(rock.getId()), rock.getType(), rock.getRotation(), rock.getX(), rock.getY(), rock.getPlane()));
            WorldTasksManager.schedule(() -> World.spawnObject(rock), ore.getTime());
        }
        if (ore.equals(OreDefinitions.SANDSTONE)) {
            final int type = Utils.random(3);
            final int ore = 6971 + (type * 2);
            final int experience = 30 + (type * 10);
            skills.addXp(Skills.MINING, experience);
            inventory.addOrDrop(new Item(ore, amount));
            ClueItem.roll(player, this.ore.getBaseClueGeodeChance(), skills.getLevel(Skills.MINING), ClueItem::getClueGeode);
            player.sendFilteredMessage("You manage to mine some sandstone.");
            if (valid) {
                player.getPerkManager().consume(PerkWrapper.MASTER_MINER);
            }
            return -1;
        } else if (ore.equals(OreDefinitions.GRANITE)) {
            final int type = Utils.random(2);
            final int ore = 6979 + (type * 2);
            final int experience = type == 0 ? 50 : type == 1 ? 60 : 75;
            skills.addXp(Skills.MINING, experience);
            inventory.addOrDrop(new Item(ore, amount));
            ClueItem.roll(player, this.ore.getBaseClueGeodeChance(), skills.getLevel(Skills.MINING), ClueItem::getClueGeode);
            player.sendFilteredMessage("You manage to mine some granite.");
            if (valid) {
                player.getPerkManager().consume(PerkWrapper.MASTER_MINER);
            }
            return -1;
        }
        if (ore.equals(OreDefinitions.RUNITE_GOLEM_ROCKS)) {
            if (npc != null) {
                npc.finish();
            }
        }
        if (ore.equals(OreDefinitions.IRON)) {
            CharlieTask.MINE_IRON.progress(player);
        }
        final AchievementDiaries diaries = player.getAchievementDiaries();
        final DailyChallengeManager daily = player.getDailyChallengeManager();
        if (ore.equals(OreDefinitions.ESSENCE)) {
            daily.update(SkillingChallenge.MINE_ESSENCE);
        } else if (ore.equals(OreDefinitions.CLAY)) {
            if (player.getX() >= 3399 && player.getX() <= 3424 && player.getY() >= 3152 && player.getY() <= 3169) {
                diaries.update(DesertDiary.MINE_CLAY);
            }
            daily.update(SkillingChallenge.MINE_CLAY);
        } else if (ore.equals(OreDefinitions.IRON)) {
            diaries.update(WildernessDiary.MINE_IRON_ORE);
            diaries.update(VarrockDiary.MINE_IRON);
            diaries.update(LumbridgeDiary.MINE_IRON);
            diaries.update(WesternProvincesDiary.MINE_IRON_NEAR_PISCATORIS);
            diaries.update(KourendDiary.MINE_IRON_IN_MT_KARUULM);
            daily.update(SkillingChallenge.MINE_IRON_ORES);
        } else if (ore.equals(OreDefinitions.COAL)) {
            diaries.update(KandarinDiary.MINE_COAL);
            diaries.update(FremennikDiary.MINE_COAL_IN_RELLEKKA);
        } else if (ore.equals(OreDefinitions.SILVER)) {
            diaries.update(FremennikDiary.CRAFT_A_TIARA, 1);
            daily.update(SkillingChallenge.MINE_SILVER_ORES);
        } else if (ore.equals(OreDefinitions.GOLD)) {
            diaries.update(FaladorDiary.MINE_GOLD_ORE);
            diaries.update(KaramjaDiary.MINE_GOLD);
            daily.update(SkillingChallenge.MINE_GOLD_ORES);
        } else //TODO: Convert the boundary to diary restriction on its own.
        if (ore.equals(OreDefinitions.MITHRIL)) {
            diaries.update(WildernessDiary.MINE_MITHRIL_ORE);
            diaries.update(MorytaniaDiary.MINE_MITHRIL_IN_ABANDONED_MINE);
            daily.update(SkillingChallenge.MINE_MITHRIL_ORES);
            SherlockTask.MINE_MITHRIL_ORE.progress(player);
        } else if (ore.equals(OreDefinitions.ADAMANTITE)) {
            diaries.update(FremennikDiary.MINE_ADAMANTITE_ORE);
            diaries.update(WesternProvincesDiary.MINE_ADAMANTITE_IN_TIRANNWN);
        } else if (ore.equals(OreDefinitions.RUNITE) || ore.equals(OreDefinitions.RUNITE_GOLEM_ROCKS)) {
            daily.update(SkillingChallenge.MINE_RUNITE_ORES);
        } else if (ore.equals(OreDefinitions.AMETHYST)) {
            daily.update(SkillingChallenge.MINE_AMETHYST);
        } else if (ore.equals(OreDefinitions.LOVAKITE)) {
            diaries.update(KourendDiary.MINE_SOME_LOVAKITE);
        }
        skills.addXp(Skills.MINING, amount * ore.getXp());
        if (ore.getIncinerationExperience() > 0 && pickaxe.getCharges() > 0 && tool.equals(PickaxeDefinitions.INFERNAL) && Utils.random(2) == 0) {
            skills.addXp(Skills.SMITHING, ore.getIncinerationExperience());
            player.getChargesManager().removeCharges(pickaxe, 1, container, slotId);
            player.setAnimation(Animation.STOP);
            player.sendFilteredMessage("You manage to mine some " + ore.getName() + ".");
            player.sendSound(2725);
            player.setGraphics(BURN_GFX);
            ClueItem.roll(player, this.ore.getBaseClueGeodeChance(), skills.getLevel(Skills.MINING), ClueItem::getClueGeode);
            return ore.equals(OreDefinitions.ESSENCE) ? 1 : -1;
        } else {
            final int essence = skills.getLevel(Skills.MINING) < 30 ? 1436 : 7936;
            if (ore.equals(OreDefinitions.CLAY)) {
                final Item bracelet = player.getEquipment().getItem(EquipmentSlot.HANDS);
                if (bracelet != null && bracelet.getId() == ItemId.BRACELET_OF_CLAY) {
                    player.getChargesManager().removeCharges(bracelet, 1, container, slotId);
                    inventory.addOrDrop(new Item(1761, amount));
                } else {
                    inventory.addItem(ore.getOre(), amount).onFailure(remainder -> World.spawnFloorItem(remainder, player));
                }
                ClueItem.roll(player, this.ore.getBaseClueGeodeChance(), skills.getLevel(Skills.MINING), ClueItem::getClueGeode);
            } else {
                if (ore.getOre() != -1) {
                    if (ore == OreDefinitions.PAYDIRT) {
                        final Optional<Paydirt> generated = Paydirt.generate(player);
                        generated.ifPresent(paydirt -> {
                            final Item item = new Item(ore.getOre(), 1);
                            item.setAttribute("paydirt ore id", paydirt.getId());
                            inventory.addItem(item).onFailure(remainder -> World.spawnFloorItem(remainder, player));
                            player.sendFilteredMessage("You manage to mine some pay-dirt.");
                        });
                    } else {
                        inventory.addItem(ore.equals(OreDefinitions.ESSENCE) ? essence : ore.getOre(), amount).onFailure(remainder -> World.spawnFloorItem(remainder, player));
                        ClueItem.roll(player, this.ore.getBaseClueGeodeChance(), skills.getLevel(Skills.MINING), ClueItem::getClueGeode);
                    }
                }
            }
        }
        if (valid) {
            player.getPerkManager().consume(PerkWrapper.MASTER_MINER);
        }
        return ore.equals(OreDefinitions.ESSENCE) ? 1 : deplete ? -1 : tool.getMineTime();
    }

    @Override
    public void stop() {
        player.setAnimation(Animation.STOP);
    }

    private boolean check() {
        if (ore.equals(OreDefinitions.ROCKFALL))
            return checkLevel();
        return (checkLevel() && player.getInventory().checkSpace());
    }

    private boolean checkTool() {
        final Optional<MiningDefinitions.PickaxeDefinitions.PickaxeResult> axe = PickaxeDefinitions.get(player, true);
        if (!axe.isPresent()) {
            player.getDialogueManager().start(new PlainChat(player, "You need a pickaxe to mine this rock. You do not have a pickaxe which you have the Mining level to use."));
            return false;
        }
        final MiningDefinitions.PickaxeDefinitions.PickaxeResult definitions = axe.get();
        this.pickaxeEquipment = definitions.getContainer() == player.getEquipment().getContainer();
        this.slotId = definitions.getSlot();
        this.tool = definitions.getDefinitions();
        this.container = definitions.getContainer();
        this.pickaxe = definitions.getItem();
        return true;
    }

    private boolean checkLevel() {
        if (player.getSkills().getLevel(Skills.MINING) < ore.getLevel()) {
            player.getDialogueManager().start(new PlainChat(player, "You need a Mining level of " + ore.getLevel() + " to mine this rock."));
            return false;
        }
        return true;
    }

    private boolean checkObject() {
        if (ore.equals(OreDefinitions.RUNITE_GOLEM_ROCKS)) {
            return npc != null && !npc.isFinished();
        }
        if (ore.equals(OreDefinitions.CWARS_ROCKS)) {
            return (World.getRegion(rock.getRegionId()).containsObject(4437, rock.getType(), rock) || World.getRegion(rock.getRegionId()).containsObject(4438, rock.getType(), rock));
        }
        return World.getRegion(rock.getRegionId()).containsObject(rock.getId(), rock.getType(), rock);
    }
}
