package me.mohamad82.ruom.utils;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import me.mohamad82.ruom.vector.Vector3;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;

import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

public class BlockUtils {

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

    public static void placeDoor(Location location, BlockData blockData) {
        Block bottomBlock = location.getBlock();
        Block upperBlock = bottomBlock.getRelative(BlockFace.UP);
        BlockData upperBlockData = Bukkit.createBlockData(blockData.getAsString().replace("half=lower", "half=upper"));
        bottomBlock.setType(blockData.getMaterial(), false);
        upperBlock.setType(blockData.getMaterial(), false);
        upperBlock.setBlockData(upperBlockData);
    }

    public static void breakDoor(Location location) {
        Block block = location.getBlock();
        BlockData blockData = block.getBlockData().clone();
        block.setType(Material.AIR, false);
        if (blockData.getAsString().contains("half=lower")) {
            block.getRelative(0, 1, 0).setType(Material.AIR, false);
        } else if (blockData.getAsString().contains("half=upper")) {
            block.getRelative(0, -1, 0).setType(Material.AIR, false);
        }
    }

    public static void placeBed(Location location, BlockData blockData) {
        Block block = location.getBlock();
        String blockDataString = blockData.getAsString();
        block.setType(blockData.getMaterial(), false);
        block.setBlockData(Bukkit.createBlockData(blockDataString));

        Block otherPartBlock = getOtherBedPart(location, blockData).getBlock();
        otherPartBlock.setType(blockData.getMaterial(), false);
        otherPartBlock.setBlockData(Bukkit.createBlockData(blockDataString.replace("part=foot", "part=head")));
    }

    public static void breakBed(Location location) {
        Block block = location.getBlock();
        BlockData blockData = block.getBlockData();

        block.setType(Material.AIR, false);
        getOtherBedPart(location, blockData).getBlock().setType(Material.AIR, false);
    }

    public static Location getOtherBedPart(Location location, BlockData blockData) {
        String blockDataString = blockData.getAsString();
        if (!blockDataString.contains("part=")) {
            throw new IllegalStateException("Given BlockData is not a bed's BlockData.");
        }
        boolean isFootPart = blockDataString.contains("part=foot");

        if (blockDataString.contains("facing=north")) {
            return location.clone().add(0, 0, isFootPart ? -1 : 1);
        } else if (blockDataString.contains("facing=east")) {
            return location.clone().add(isFootPart ? 1 : -1, 0, 0);
        } else if (blockDataString.contains("facing=south")) {
            return location.clone().add(0, 0, isFootPart ? 1 : -1);
        } else if (blockDataString.contains("facing=west")) {
            return location.clone().add(isFootPart ? -1 : 1, 0, 0);
        }

        throw new IllegalStateException("Given BlockData is not a bed's BlockData.");
    }

    /**
     * Gets a blockData sound for opening/closing wooden/iron doors, switch on/off levers and buttons.
     * @param blockData The blockData of a block
     * @return An optional with a SoundContainer. Returned optional will be empty if an invalid/unsupported blockData was given.
     */
    public static Optional<SoundContainer> getBlockDataSound(BlockData blockData) {
        String blockDataString = blockData.getAsString();
        if (blockData.getMaterial().toString().contains("_DOOR")) {
            boolean isIronDoor = blockData.getMaterial().equals(XMaterial.IRON_DOOR.parseMaterial());
            return Optional.of(SoundContainer.soundContainer(blockDataString.contains("open=false") ? isIronDoor ?
                    XSound.BLOCK_IRON_DOOR_CLOSE : XSound.BLOCK_WOODEN_DOOR_CLOSE : isIronDoor ?
                    XSound.BLOCK_IRON_DOOR_OPEN : XSound.BLOCK_WOODEN_DOOR_OPEN));
        } else if (blockData.getMaterial().toString().contains("TRAPDOOR")) {
            boolean isIronTrapDoor = blockData.getMaterial().equals(XMaterial.IRON_TRAPDOOR.parseMaterial());
            return Optional.of(SoundContainer.soundContainer(blockDataString.contains("open=false") ? isIronTrapDoor ?
                    XSound.BLOCK_IRON_TRAPDOOR_CLOSE : XSound.BLOCK_WOODEN_TRAPDOOR_CLOSE : isIronTrapDoor ?
                    XSound.BLOCK_IRON_TRAPDOOR_OPEN : XSound.BLOCK_WOODEN_TRAPDOOR_OPEN));
        } else if (blockData.getMaterial().equals(XMaterial.LEVER.parseMaterial())) {
            return Optional.of(blockDataString.contains("powered=false") ?
                    SoundContainer.soundContainer(XSound.BLOCK_LEVER_CLICK, 1f, 0.4f) :
                    SoundContainer.soundContainer(XSound.BLOCK_LEVER_CLICK, 1f, 0.6f));
        } else if (blockData.getMaterial().toString().contains("BUTTON")) {
            boolean isStoneButton = blockData.getMaterial().toString().contains("STONE");
            return Optional.of(blockDataString.contains("powered=false") ? isStoneButton ?
                    SoundContainer.soundContainer(XSound.BLOCK_STONE_BUTTON_CLICK_OFF, 1f, 0.4f) :
                    SoundContainer.soundContainer(XSound.BLOCK_WOODEN_BUTTON_CLICK_OFF, 1f, 0.4f) : isStoneButton ?
                    SoundContainer.soundContainer(XSound.BLOCK_STONE_BUTTON_CLICK_ON, 1f, 0.6f) :
                    SoundContainer.soundContainer(XSound.BLOCK_WOODEN_BUTTON_CLICK_ON, 1f, 0.6f));
        }

        return Optional.empty();
    }

    public static void spawnBlockBreakParticles(Location blockLocation, Material material) {
        Location center = LocUtils.simplifyToCenter(blockLocation);
        for (int i = 0; i <= 30; i++) {
            blockLocation.getWorld().spawnParticle(Particle.BLOCK_CRACK,
                    center.clone().add(getRandomInBlock(), getRandomInBlock() + 0.5, getRandomInBlock()),
                    2, material.createBlockData());
        }
    }

    private static float getRandomInBlock() {
        return (float) (new Random().nextInt(10) - 5) / 10;
    }

}
