package me.mohamad82.ruom;

import com.cryptomorin.xseries.XMaterial;
import com.google.gson.GsonBuilder;
import me.mohamad82.ruom.adventure.Adventure;
import me.mohamad82.ruom.adventure.ComponentUtils;
import me.mohamad82.ruom.database.mysql.MySQLDatabase;
import me.mohamad82.ruom.database.sqlite.SQLiteDatabase;
import me.mohamad82.ruom.events.packets.PacketContainer;
import me.mohamad82.ruom.events.packets.clientbound.ClientboundPacketEvent;
import me.mohamad82.ruom.gui.GUITitle;
import me.mohamad82.ruom.hologram.*;
import me.mohamad82.ruom.npc.entity.FallingBlockNPC;
import me.mohamad82.ruom.npc.entity.ThrowableProjectileNPC;
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
import me.mohamad82.ruom.worldedit.Schematic;
import me.mohamad82.ruom.worldedit.WorldEdit;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class Testings extends RUoMPlugin implements CommandExecutor {

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
        //Ruom.shutdown();
    }

    Schematic schematic = null;

    public static Set<Vector3> cylinder(double radiusX, double radiusZ, boolean allDirections, boolean filled) {
        Set<Vector3> points = new HashSet<>();
        radiusX += 0.5;
        radiusZ += 0.5;

        final double invRadiusX = 1 / radiusX;
        final double invRadiusZ = 1 / radiusZ;

        final int ceilRadiusX = (int) Math.ceil(radiusX);
        final int ceilRadiusZ = (int) Math.ceil(radiusZ);

        double nextX = 0;
        xLoop: for (int x = 0; x <= ceilRadiusX; ++x) {
            final double xn = nextX;
            nextX = (x + 1) * invRadiusX;
            double nextZn = 0;
            for (int z = 0; z <= ceilRadiusZ; ++z) {
                final double zn = nextZn;
                nextZn = (z + 1) * invRadiusZ;

                double distanceSq = lengthSq(xn, zn);
                if (distanceSq > 1) {
                    if (z == 0) {
                        break xLoop;
                    }
                    break;
                }

                if (!filled) {
                    if (lengthSq(nextX, zn) <= 1 && lengthSq(xn, nextZn) <= 1) {
                        continue;
                    }
                }

                points.add(Vector3.at(x, 0, z));
                if (allDirections) {
                    points.add(Vector3.at(x, 0, -z));
                    points.add(Vector3.at(-x, 0, z));
                    points.add(Vector3.at(-x, 0, -z));
                }
            }
        }

        return points;
    }

    private static double lengthSq(double x, double z) {
        return (x * x) + (z * z);
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        MilliCounter counter = new MilliCounter();
        counter.start();
        Player player = (Player) sender;
        try {
            if (args[0].equalsIgnoreCase("remove")) {
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
            } else if (args[0].equalsIgnoreCase("paste")) {
                schematic = new Schematic(WorldEdit.getClipboardFromSchematic(new File(getDataFolder(), "arena.schematic")).get(), player.getLocation(), true);
                schematic.prepare().whenComplete((v, error) -> {
                    Ruom.broadcast("preparation completed.");

                    new BukkitRunnable() {
                        Location location = player.getLocation().clone();
                        Map<Vector3, Integer> map = new HashMap<>();
                        Map<Vector3, FallingBlockNPC> fallingBlocks = new HashMap<>();
                        int i = 0;
                        public void run() {

                            Vector3 randomBlockLocation = schematic.getRandomBlock(schematic.randomLayerIndex());
                            FallingBlockNPC fallingBlock = FallingBlockNPC.fallingBlockNPC(location, schematic.getBlockData(randomBlockLocation).getMaterial());
                            fallingBlock.addViewers(Ruom.getOnlinePlayers());
                            fallingBlock.move(randomBlockLocation, 100).whenComplete((bool, error) -> {
                                fallingBlock.removeViewers(Ruom.getOnlinePlayers());
                            });
                            map.put(randomBlockLocation, 0);
                            fallingBlocks.put(randomBlockLocation, fallingBlock);

                            Set<Vector3> toRemoveBlocks = new HashSet<>();

                            for (Vector3 blockLocation : map.keySet()) {
                                int i = map.get(blockLocation);
                                map.put(blockLocation, i + 1);

                                if (i > 100) {
                                    toRemoveBlocks.add(blockLocation);
                                    schematic.applyAndUpdate(blockLocation);
                                    fallingBlocks.get(blockLocation).removeViewers(Ruom.getOnlinePlayers());
                                }
                            }
                            toRemoveBlocks.forEach(blockLocation -> {
                                map.remove(blockLocation);
                                fallingBlocks.remove(blockLocation);
                            });
                            if (schematic.isDone()) {
                                cancel();
                                Ruom.broadcast("Finished");
                            }
                            i++;
                        }
                    }.runTaskTimer(this, 0, 1);
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
                Adventure.get().player((Player) sender).sendMessage(component);
                Bukkit.broadcastMessage(new GsonBuilder().setPrettyPrinting().create().toJson(GsonComponentSerializer.gson().serialize(component)));
            } else if (args[0].equalsIgnoreCase("esc2")) {
                Component component = ComponentUtils.parse(args[1]);
                Adventure.get().player((Player) sender).sendMessage(component);
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

    /*public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (label.equalsIgnoreCase("ruom")) {
            WEManager weManager = new WEManager(this, WEType.FAWE);

            SchemProgress progress = weManager.buildSchematic(Bukkit.getPlayerExact("Mohamad82").getLocation(), new File(getDataFolder(),
                    "test.schem"), PastePattern.valueOf(args[0]), PasteSpeed.valueOf(args[1]), Boolean.parseBoolean(args[2]));

            new BukkitRunnable() {
                public void run() {
                    Bukkit.broadcastMessage("Progress: " + progress.getProgress());
                    if (progress.isDone()) {
                        cancel();
                    }
                }
            }.runTaskTimer(this, 20, 20);
        }
        return true;
    }*/

    @EventHandler
    public void onClientboundPacket(ClientboundPacketEvent event) {
        Player player = event.getPlayer();
        PacketContainer packet = event.getPacket();
    }

}
