package me.mohamad82.ruom;

import com.cryptomorin.xseries.XMaterial;
import com.extollit.gaming.ai.path.HydrazinePathFinder;
import com.extollit.gaming.ai.path.model.IPath;
import com.google.gson.GsonBuilder;
import me.mohamad82.ruom.adventure.AdventureApi;
import me.mohamad82.ruom.adventure.ComponentUtils;
import me.mohamad82.ruom.database.mysql.MySQLDatabase;
import me.mohamad82.ruom.database.sqlite.SQLiteDatabase;
import me.mohamad82.ruom.event.PlayerUseItemEvent;
import me.mohamad82.ruom.gui.GUITitle;
import me.mohamad82.ruom.hologram.*;
import me.mohamad82.ruom.npc.NPC;
import me.mohamad82.ruom.npc.PlayerNPC;
import me.mohamad82.ruom.npc.entity.FallingBlockNPC;
import me.mohamad82.ruom.npc.entity.ThrowableProjectileNPC;
import me.mohamad82.ruom.pathfinding.AINPC;
import me.mohamad82.ruom.pathfinding.Instance;
import me.mohamad82.ruom.toast.ToastMessage;
import me.mohamad82.ruom.translators.ItemReader;
import me.mohamad82.ruom.translators.skin.MineSkinAPI;
import me.mohamad82.ruom.translators.skin.MinecraftSkin;
import me.mohamad82.ruom.translators.skin.SkinBuilder;
import me.mohamad82.ruom.utils.ListUtils;
import me.mohamad82.ruom.utils.MilliCounter;
import me.mohamad82.ruom.utils.NMSUtils;
import me.mohamad82.ruom.utils.PacketUtils;
import me.mohamad82.ruom.vector.Vector3;
import me.mohamad82.ruom.vector.Vector3Utils;
import me.mohamad82.ruom.worldedit.Schematic;
import me.mohamad82.ruom.worldedit.WorldEdit;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.*;

public final class Testings extends RUoMPlugin implements CommandExecutor {

    Instance instance;
    AINPC aiNpc;
    HydrazinePathFinder pathFinder;

    MySQLDatabase mysql;
    SQLiteDatabase sqlite;

    @Override
    public void onEnable() {
        getCommand("ruom").setExecutor(this);
        new SkinBuilder();
        Ruom.initializeAdventure();
    }

    @Override
    public void onDisable() {
        Ruom.shutdown();
    }

    Schematic schematic = null;

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        MilliCounter counter = new MilliCounter();
        counter.start();
        Player player = (Player) sender;
        try {
            if (args[0].equalsIgnoreCase("pathfinding")) {
                Ruom.runAsync(() -> {
                    instance = new Instance(player.getWorld());
                    Ruom.broadcast("Completed!");
                    instance.loadChunk(player.getLocation().getChunk());
                });
            } else if (args[0].equalsIgnoreCase("load")) {
                instance.loadChunk(player.getLocation().getChunk());
            } else if (args[0].equalsIgnoreCase("checkblock")) {
                Block block = player.getTargetBlock(null, 6);
                Ruom.broadcast("Block: " + instance.blockObjectAt(block.getX(), block.getY(), block.getZ()).getType());
            } else if (args[0].equalsIgnoreCase("ainpc")) {
                PlayerNPC npc = PlayerNPC.playerNPC("Bahoosh", player.getLocation().clone(), Optional.empty());
                npc.addViewers(Ruom.getOnlinePlayers());
                aiNpc = new AINPC(npc, Vector3Utils.toVector3(player.getLocation()));
                pathFinder = new HydrazinePathFinder(aiNpc, instance);
            } else if (args[0].equalsIgnoreCase("movehere")) {

                new BukkitRunnable() {
                    IPath path = pathFinder.initiatePathTo(player.getLocation().clone().getX(), 6, player.getLocation().clone().getZ());
                    public void run() {
                        if (path == null) {
                            Ruom.broadcast("No fucking path");
                            cancel();
                        } else {
                            Ruom.log("On the way: ");
                            Ruom.log("Lenght: " + path.length() + "   cursor: " + path.cursor());
                            path = pathFinder.updatePathFor(aiNpc);
                            if (path.done()) {
                                Ruom.broadcast("Arrived!");
                            }
                        }
                    }
                }.runTaskTimerAsynchronously(this, 0, 5);
            } else if (args[0].equalsIgnoreCase("bow")) {
                new UseItemListener(player.getLocation(), SkinBuilder.getInstance().getSkin(player));
            } else if (args[0].equalsIgnoreCase("npcfire")) {
                PlayerNPC npc = PlayerNPC.playerNPC("TestNPC",player.getLocation(), Optional.empty());
                npc.addViewers(Ruom.getOnlinePlayers());
                npc.setEquipment(NPC.EquipmentSlot.MAINHAND, XMaterial.SHIELD.parseItem());
                Ruom.runSync(new Runnable() {
                    int i = 0;
                    public void run() {
                        if (i % 2 == 0) {
                            Ruom.log("Started using item");
                            npc.startUsingItem(PlayerNPC.InteractionHand.MAIN_HAND);
                        } else {
                            Ruom.log("Stopped using item");
                            npc.stopUsingItem();
                        }
                        i++;
                    }
                }, 20, 40);
            } else if (args[0].equalsIgnoreCase("remove")) {
                NMSUtils.sendPacket(player,
                        PacketUtils.getRemoveEntitiesPacket(player.getEntityId()));
            } else if (args[0].equalsIgnoreCase("gui")) {
                Ruom.runSync(() -> {
                    GUITitle.setTitle(player, ComponentUtils.parse("<rainbow>Khi Khi Khi Khi"));
                }, 40);
            } else if (args[0].equalsIgnoreCase("holo")) {
                Hologram hologram = Hologram.hologram(
                        ListUtils.toList(
                                HologramLine.hologramLine(ComponentUtils.parse("<rainbow>A normal gradient line!"), 0f),
                                AnimatedHologramLine.animatedHologramLine(ListUtils.toList(
                                        ComponentUtils.parse("<aqua>An animated line!"),
                                        ComponentUtils.parse("<gold>An animated line!"),
                                        ComponentUtils.parse("<green>An animated line!"),
                                        ComponentUtils.parse("<blue>An animated line!")
                                ), 10, 0.4f),
                                HologramLine.hologramLine(ComponentUtils.parse("<blue>A 2D item line:"), 0.5f),
                                Item2DHologramLine.item2DHologramLine(new ItemStack(Material.BOW), true, 0.15f),
                                HologramLine.hologramLine(ComponentUtils.parse("<red>A 3D item line:"), 0.65f),
                                Item3DHologramLine.item3DHologramLine(new ItemStack(Material.BLUE_BED), false, 0.2f)
                        ), player.getLocation()
                );
                hologram.addViewers(Ruom.getOnlinePlayers());
            } else if (args[0].equalsIgnoreCase("proj")) {
                ThrowableProjectileNPC throwableProjectileNPC = ThrowableProjectileNPC.throwableProjectileNPC(player.getLocation(), new ItemStack(Material.valueOf(args[1].toUpperCase())));
                throwableProjectileNPC.setNoGravity(true);
                throwableProjectileNPC.addViewers(Ruom.getOnlinePlayers());
            } else if (args[0].equalsIgnoreCase("path")) {
                Ruom.log("initializing");
                schematic = new Schematic(WorldEdit.getClipboardFromSchematic(new File(getDataFolder(), "arena.schem")).get(), player.getLocation(), true);
                schematic.prepare().whenComplete((v, error) -> {
                    Ruom.log("preparation completed");
                    new BukkitRunnable() {
                        final Map<Vector3, FallingBlockNPC> blocks = new HashMap<>();
                        public void run() {
                            Vector3 location = Vector3Utils.toVector3(player.getLocation());
                            Vector3 nearestBlock = schematic.getNearestBlock(schematic.nextLayerIndex(), location);
                            double distance = nearestBlock.distance(location);

                            Set<Vector3> toRemoveBlocks = new HashSet<>();
                            for (Vector3 blockLocation : blocks.keySet()) {
                                double blockDistance = blockLocation.distance(location);
                                if (distance > 30) {
                                    toRemoveBlocks.add(blockLocation);
                                } else {
                                    blocks.get(blockLocation).teleport(Vector3.at(blockLocation.getBlockX(), blockLocation.getBlockY() + (blockDistance / 2) - distance, blockLocation.getBlockZ()), 0, 0);
                                    if (blockLocation.distance(location) < 2) {
                                        Ruom.log("Removing: " + blockLocation);
                                        blocks.get(blockLocation).removeViewers(Ruom.getOnlinePlayers());
                                        schematic.applyAndUpdate(blockLocation);
                                        toRemoveBlocks.add(blockLocation);
                                    }
                                }
                            }
                            for (Vector3 toRemoveBlock : toRemoveBlocks) {
                                blocks.remove(toRemoveBlock);
                            }
                            while (distance < 10) {
                                if (distance < 2) {
                                    schematic.applyAndUpdate(nearestBlock);
                                    if (blocks.containsKey(nearestBlock)) {
                                        blocks.get(nearestBlock).removeViewers(Ruom.getOnlinePlayers());
                                        blocks.remove(nearestBlock);
                                    }
                                } else {
                                    FallingBlockNPC fallingBlock = FallingBlockNPC.fallingBlockNPC(Vector3Utils.toLocation(player.getWorld(), nearestBlock.clone().add(0, 200, 0)), schematic.getBlockData(nearestBlock).getMaterial());
                                    fallingBlock.setNoGravity(true);
                                    fallingBlock.addViewers(Ruom.getOnlinePlayers());
                                    blocks.put(nearestBlock, fallingBlock);
                                    Ruom.log("y: " + nearestBlock.getBlockY() + (distance / 2) + "    distance: " + distance);
                                    fallingBlock.teleport(Vector3.at(nearestBlock.getBlockX(), nearestBlock.getBlockY() + (distance * 5), nearestBlock.getBlockZ()), 0, 0);
                                }

                                schematic.remove(nearestBlock);
                                nearestBlock = schematic.getNearestBlock(schematic.nextLayerIndex(), location);
                                distance = nearestBlock.distance(location);
                            }
                        }
                    }.runTaskTimer(this, 0, 1);
                });
            } else if (args[0].equalsIgnoreCase("paste")) {
                schematic = new Schematic(WorldEdit.getClipboardFromSchematic(new File(getDataFolder(), "arena.schem")).get(), player.getLocation(), true);
                schematic.prepare().whenComplete((v, error) -> {
                    Ruom.broadcast("preparation completed.");

                    new BukkitRunnable() {
                        Location location = player.getLocation().clone();
                        Random random = new Random();
                        int i = 0;
                        public void run() {
                            for (int j = 0; j <= 5; j++) {
                                Vector3 randomBlockLocation = schematic.getRandomBlock(schematic.nextLayerIndex());
                                FallingBlockNPC fallingBlock = FallingBlockNPC.fallingBlockNPC(location.clone().add(0, j * 1.5 + 10, 0), schematic.getBlockData(randomBlockLocation).getMaterial());
                                fallingBlock.setNoGravity(true);
                                fallingBlock.addViewers(Ruom.getOnlinePlayers());
                                fallingBlock.move(Vector3Utils.getTravelDistance(Vector3Utils.toVector3(location.clone().add(0, j * 1.5 + 10, 0)), randomBlockLocation), 100).whenComplete((bool, error) -> {
                                    fallingBlock.removeViewers(Ruom.getOnlinePlayers());
                                    Ruom.runSync(() -> {
                                        schematic.applyAndUpdate(randomBlockLocation);
                                    });
                                });
                                if (random.nextInt(100) < 10) {
                                    ThrowableProjectileNPC throwableProjectile = ThrowableProjectileNPC.throwableProjectileNPC(Vector3Utils.toLocation(player.getWorld(), randomBlockLocation).add(0, 80, 0), new ItemStack(schematic.getBlockData(randomBlockLocation).getMaterial()));
                                    throwableProjectile.setGlowing(true);
                                    throwableProjectile.setNoGravity(true);
                                    throwableProjectile.addViewers(Ruom.getOnlinePlayers());
                                    throwableProjectile.move(Vector3Utils.getTravelDistance(randomBlockLocation.clone().add(0, 80, 0), randomBlockLocation), 100).whenComplete((bool, error) -> {
                                        throwableProjectile.removeViewers(Ruom.getOnlinePlayers());
                                    });
                                }

                                schematic.remove(randomBlockLocation);

                                if (schematic.isDone()) {
                                    cancel();
                                    Ruom.broadcast("Finished");
                                }
                            }
                            i++;
                        }
                    }.runTaskTimerAsynchronously(this, 0, 1);
                });
            } else if (args[0].equalsIgnoreCase("pasteone")) {
                schematic.apply(schematic.getRandomBlock(schematic.randomLayerIndex()));
                schematic.update();
            } else if (args[0].equalsIgnoreCase("category")) {
                Ruom.broadcast(ItemReader.getItemCategory(player.getInventory().getItemInMainHand()).toString());
            } else if (args[0].equalsIgnoreCase("toast")) {
                ToastMessage toastMessage = ToastMessage.toastMessage(args[1], XMaterial.valueOf(args[2].toUpperCase()), ToastMessage.FrameType.valueOf(args[3].toUpperCase()), Boolean.parseBoolean(args[4]));
                toastMessage.send((Player) sender);
            } else if (args[0].equalsIgnoreCase("esc")) {
                Component component = ComponentUtils.parse(args[1]);
                AdventureApi.get().player((Player) sender).sendMessage(component);
                Bukkit.broadcastMessage(new GsonBuilder().setPrettyPrinting().create().toJson(GsonComponentSerializer.gson().serialize(component)));
            } else if (args[0].equalsIgnoreCase("esc2")) {
                Component component = ComponentUtils.parse(args[1]);
                AdventureApi.get().player((Player) sender).sendMessage(component);
                Bukkit.broadcastMessage(new GsonBuilder().setPrettyPrinting().create().toJson(GsonComponentSerializer.gson().serialize(component)));
            } else if (args[0].equalsIgnoreCase("mojang")) {
                MinecraftSkin skin = SkinBuilder.getInstance().getSkin(args[1], true);
                skin.apply((Player) sender);

            } else if (args[0].equalsIgnoreCase("mineskin")) {
                MinecraftSkin skin = SkinBuilder.getInstance().getSkin(args[1], MineSkinAPI.SkinType.NORMAL, true);
                skin.apply((Player) sender);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        counter.stop();
        sender.sendMessage("Took " + counter.get() + "ms");

        return false;
    }

    public static class UseItemListener extends PlayerUseItemEvent {

        private final PlayerNPC npc;

        public UseItemListener(Location location, MinecraftSkin skin) {
            npc = PlayerNPC.playerNPC("Professional", location, Optional.of(skin));
            npc.addViewers(Ruom.getOnlinePlayers());
            npc.setEquipment(NPC.EquipmentSlot.MAINHAND, XMaterial.CROSSBOW.parseItem());

        }

        @Override
        protected void onStartUseItem(Player player, ItemStack item, boolean isMainHand) {
            npc.startUsingItem(PlayerNPC.InteractionHand.MAIN_HAND);
        }

        @Override
        protected void onStopUseItem(Player player, ItemStack item, float holdTime) {
            npc.stopUsingItem();
        }

    }

}
