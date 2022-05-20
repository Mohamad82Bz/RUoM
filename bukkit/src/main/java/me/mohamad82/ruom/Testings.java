package me.mohamad82.ruom;

import com.extollit.gaming.ai.path.HydrazinePathFinder;
import com.extollit.gaming.ai.path.PathOptions;
import com.extollit.gaming.ai.path.SchedulingPriority;
import com.extollit.gaming.ai.path.model.IPath;
import com.extollit.linalg.immutable.Vec3d;
import com.extollit.linalg.immutable.Vec3i;
import me.mohamad82.ruom.adventure.ComponentUtils;
import me.mohamad82.ruom.event.packet.PlayerInteractAtEntityEvent;
import me.mohamad82.ruom.math.vector.Vector3;
import me.mohamad82.ruom.math.vector.Vector3UtilsBukkit;
import me.mohamad82.ruom.npc.LivingEntityNPC;
import me.mohamad82.ruom.npc.PlayerNPC;
import me.mohamad82.ruom.npc.entity.ArmorStandNPC;
import me.mohamad82.ruom.pathfinding.AINPC;
import me.mohamad82.ruom.pathfinding.Instance;
import me.mohamad82.ruom.utils.NMSUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Optional;

public class Testings extends RUoMPlugin implements CommandExecutor {

    @Override
    public void onEnable() {
        getCommand("ruom").setExecutor(this);
    }

    Instance instance;
    AINPC aiNpc;
    HydrazinePathFinder pathFinder;

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("ruom")) {
            Player player = (Player) sender;
            if (args[0].equalsIgnoreCase("itemcategory")) {
                Ruom.broadcast(NMSUtils.getItemCategory(player.getInventory().getItemInMainHand()));
            } else if (args[0].equalsIgnoreCase("inter")) {
                Ruom.initializePacketListener();
                new InteractListener();
            } else if (args[0].equalsIgnoreCase("pathfinding")) {
                Ruom.runAsync(() -> {
                    instance = new Instance(player.getWorld());
                    for (byte chunkX = -3; chunkX < 3; chunkX++) {
                        for (byte chunkZ = -3; chunkZ < 3; chunkZ++) {
                            byte finalChunkX = chunkX;
                            byte finalChunkZ = chunkZ;
                            Ruom.runAsync(() -> {
                                instance.loadChunk(player.getWorld().getChunkAt(player.getLocation().getChunk().getX() + finalChunkX, player.getLocation().getChunk().getZ() + finalChunkZ));
                                Ruom.broadcast("Loaded chunk: " + (player.getLocation().getChunk().getX() + finalChunkX) + "   " + (player.getLocation().getChunk().getZ() + finalChunkZ));
                            });
                        }
                    }
                    Ruom.broadcast("Completed!");
                });
            } else if (args[0].equalsIgnoreCase("load")) {
                instance.loadChunk(player.getLocation().getChunk());
            } else if (args[0].equalsIgnoreCase("checkblock")) {
                Block block = player.getTargetBlock(null, 6);
                Ruom.broadcast("Block: " + instance.blockObjectAt(block.getX(), block.getY(), block.getZ()).getType());
            } else if (args[0].equalsIgnoreCase("ainpc")) {
                PlayerNPC npc = PlayerNPC.playerNPC("Bahoosh", player.getLocation().clone(), Optional.empty());
                npc.addViewers(Ruom.getOnlinePlayers());
                aiNpc = new AINPC(npc, Vector3UtilsBukkit.toVector3(player.getLocation()));
                pathFinder = new HydrazinePathFinder(aiNpc, instance);
                pathFinder.schedulingPriority(SchedulingPriority.extreme);
            } else if (args[0].equalsIgnoreCase("movehere")) {

                final IPath path = pathFinder.computePathTo(new Vec3d(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ()));
                if (path == null) {
                    Ruom.broadcast("Destination is unreachable.");
                } else {
                    for (int i = 1; i < path.length(); i++) {
                        Ruom.log(path.at(i).coordinates().toString());
                        Vec3i cords = path.at(i).coordinates();
                        new BukkitRunnable() {
                            int i = 0;
                            public void run() {
                                player.getWorld().spawnParticle(Particle.REDSTONE, new Location(player.getWorld(), cords.x, cords.y + 0.5, cords.z, 0, 0), 1, new Particle.DustOptions(Color.BLUE, 1f));

                                i++;
                                if (i == 20) {
                                    cancel();
                                }
                            }
                        }.runTaskTimer(this, 0, 5);
                    }
                    Ruom.broadcast("Reachable!");
                }
            } else if (args[0].equalsIgnoreCase("movehere2")) {
                new BukkitRunnable() {
                    IPath path = pathFinder.initiatePathTo(new Vec3d(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ()), new PathOptions().targetingStrategy(PathOptions.TargetingStrategy.gravitySnap));
                    public void run() {
                        if (path == null) {
                            Ruom.broadcast("Path is finished, either completed or non-reachable.");
                            cancel();
                        } else {
                            if (path.done()) {
                                Ruom.broadcast("Arrived!");
                                cancel();
                            }
                            path = pathFinder.updatePathFor(aiNpc);
                        }
                    }
                }.runTaskAsynchronously(this);
            }
            switch (args[0].toLowerCase()) {
                case "sign": {
                    Sign sign = (Sign) player.getTargetBlock(null, 5).getState();
                    NMSUtils.setSignLine(sign, Integer.parseInt(args[1]), ComponentUtils.parse(args[2]));
                    NMSUtils.updateSign(sign);

                    ComponentUtils.send(player, NMSUtils.getSignLine(sign, Integer.parseInt(args[1])));
                    player.sendMessage("-------");
                    for (Component component : NMSUtils.getSignLines(sign)) {
                        ComponentUtils.send(player, component);
                    }
                    break;
                }
            }
            return true;
        }
        return false;
    }

    public static class InteractListener extends PlayerInteractAtEntityEvent {

        @Override
        protected void onInteract(Player player, LivingEntityNPC.InteractionHand hand, int entityId) {
            Ruom.broadcast("1");
        }

        @Override
        protected void onInteractAt(Player player, LivingEntityNPC.InteractionHand hand, Vector3 location, int entityId) {
            Ruom.broadcast("2");
        }

        @Override
        protected void onAttack(Player player, int entityId) {
            Ruom.broadcast("3");
        }
    }

}
