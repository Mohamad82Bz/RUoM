package me.mohamad82.ruom;

import com.extollit.gaming.ai.path.HydrazinePathFinder;
import com.extollit.gaming.ai.path.PathOptions;
import com.extollit.gaming.ai.path.SchedulingPriority;
import com.extollit.gaming.ai.path.model.IPath;
import com.extollit.linalg.immutable.Vec3d;
import com.extollit.linalg.immutable.Vec3i;
import me.mohamad82.ruom.adventure.ComponentUtils;
import me.mohamad82.ruom.event.packet.ChatPreviewEvent;
import me.mohamad82.ruom.event.packet.PlayerInteractAtEntityEvent;
import me.mohamad82.ruom.hologram.Hologram;
import me.mohamad82.ruom.hologram.HologramLine;
import me.mohamad82.ruom.math.vector.Vector3;
import me.mohamad82.ruom.math.vector.Vector3UtilsBukkit;
import me.mohamad82.ruom.nmsaccessors.CreeperAccessor;
import me.mohamad82.ruom.nmsaccessors.EntityAccessor;
import me.mohamad82.ruom.nmsaccessors.SynchedEntityDataAccessor;
import me.mohamad82.ruom.npc.LivingEntityNPC;
import me.mohamad82.ruom.npc.NPC;
import me.mohamad82.ruom.npc.PlayerNPC;
import me.mohamad82.ruom.npc.entity.ArrowNPC;
import me.mohamad82.ruom.pathfinding.AINPC;
import me.mohamad82.ruom.pathfinding.Instance;
import me.mohamad82.ruom.utils.ListUtils;
import me.mohamad82.ruom.utils.LocUtils;
import me.mohamad82.ruom.utils.NMSUtils;
import me.mohamad82.ruom.utils.ResourceKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Optional;

public class Testings extends RUoMPlugin implements CommandExecutor {

    @Override
    public void onEnable() {
        getCommand("ruom").setExecutor(this);

        PlayerNPC npc = PlayerNPC.playerNPC("TestName",
                new Location(Bukkit.getWorld("world"), 0, 0, 0, 0, 0),
                Optional.empty());
    }

    @Override
    public void onDisable() {
        if (aiNpc != null) {
            aiNpc.getNpc().discard();
        }
    }

    Instance instance;
    AINPC aiNpc;
    HydrazinePathFinder pathFinder;
    ResourceKey biomeKey = new ResourceKey("custombiome", "koobs");
    ChatPreview chatPreview;

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("ruom")) {
            if (args[0].equalsIgnoreCase("debug")) {
                PlayerNPC npc = PlayerNPC.playerNPC("test", new Location(Bukkit.getWorld("world"), 0, 0, 0), Optional.empty());
                Ruom.log(NMSUtils.getItemCategory(new ItemStack(Material.DIAMOND)));
                return true;
            }
            Player player = (Player) sender;
            if (args[0].equalsIgnoreCase("holo")) {
                Hologram holo = Hologram.hologram(
                        ListUtils.toList(
                                HologramLine.hologramLine(ComponentUtils.parse("<rainbow>Test----------"), 0f)
                        ),
                        player.getLocation().add(0, 2, 0)
                );
                holo.addViewers(player);
                return true;
            }
            if (args[0].equalsIgnoreCase("creeper")) {
                Creeper creeper = (Creeper) player.getWorld().spawnEntity(player.getLocation(), EntityType.CREEPER);
                Object nmsEntity = NMSUtils.getNmsEntity(creeper);
                new BukkitRunnable() {
                    public void run() {
                        if (creeper.isDead()) {
                            cancel();
                            return;
                        }
                        Ruom.run(() -> {
                            Object entityData = EntityAccessor.getMethodGetEntityData1().invoke(nmsEntity);
                            int swell = (int) SynchedEntityDataAccessor.getMethodGet1().invoke(entityData, CreeperAccessor.getFieldDATA_SWELL_DIR().get(null));
                            Ruom.broadcast("Is ignited: " + (swell == -1 ? "false" : "true"));
                        });
                    }
                }.runTaskTimer(this, 0, 1);
                return true;
            }
            if (args[0].equalsIgnoreCase("arrow")) {
                Ruom.broadcast("Spawned");
                ArrowNPC npc = ArrowNPC.arrowNPC(player.getLocation());
                npc.addViewers(Ruom.getOnlinePlayers());
                return true;
            }
            if (args[0].equalsIgnoreCase("npc")) {
                PlayerNPC npc = PlayerNPC.playerNPC("test", player.getLocation(), Optional.empty());
                npc.addViewers(player);
                npc.setPose(NPC.Pose.CROUCHING, true);
                return true;
            }
            if (args[0].equalsIgnoreCase("setpreview")) {
                chatPreview.setDisplayChatPreview(player, Boolean.parseBoolean(args[1]));
                return true;
            }
            if (args[0].equalsIgnoreCase("preview")) {
                chatPreview = new ChatPreview();
                Ruom.broadcast("Registered");
                return true;
            }
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
                                player.getWorld().spawnParticle(Particle.REDSTONE, LocUtils.simplifyToCenter(new Location(player.getWorld(), cords.x, cords.y + 0.5, cords.z, 0, 0)), 1, new Particle.DustOptions(Color.BLUE, 1f));
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

    public static class ChatPreview extends ChatPreviewEvent {
        @Override
        protected void onPreviewRequest(Player player, int queryId, String message) {
            Component item = NMSUtils.getItemStackComponent(player.getInventory().getItemInMainHand());
            sendPreview(player, queryId, MiniMessage.miniMessage().deserialize(message, TagResolver.resolver("item", Tag.inserting(item))));
        }
    }

}
