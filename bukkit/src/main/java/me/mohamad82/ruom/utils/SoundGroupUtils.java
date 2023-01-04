package me.mohamad82.ruom.utils;

import com.cryptomorin.xseries.ReflectionUtils;
import me.mohamad82.ruom.nmsaccessors.*;
import me.mohamad82.ruom.string.StringUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

public class SoundGroupUtils {

    private static Class<?> CRAFT_BLOCKDATA, IBLOCKDATA;

    static {
        try {
            if (ServerVersion.supports(9)) {
                CRAFT_BLOCKDATA = ReflectionUtils.getCraftClass("block.data.CraftBlockData");
                IBLOCKDATA = ReflectionUtils.getNMSClass("world.level.block.state", "IBlockData");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Sound getBlockSound(SoundType type, Block block) {
        if (ServerVersion.supports(14)) {
            BlockData blockData = block.getBlockData();
            switch (type) {
                case BREAK:
                    return blockData.getSoundGroup().getBreakSound();
                case PLACE:
                    return blockData.getSoundGroup().getPlaceSound();
                case STEP:
                    return blockData.getSoundGroup().getStepSound();
                case HIT:
                    return blockData.getSoundGroup().getHitSound();
                case FALL:
                    return blockData.getSoundGroup().getFallSound();
                default: {
                    return null;
                }
            }
        } else {
            try {
                if (ServerVersion.supports(9)) {
                    Object soundType = BlockAccessor.getMethodGetSoundType1().invoke(NMSUtils.getBlockState(block.getType()));
                    return Sound.valueOf(((String) ResourceLocationAccessor.getMethodGetPath1().invoke(SoundEventAccessor.getMethodGetLocation1().invoke(
                            SoundTypeAccessor.class.getMethod("getField" + StringUtils.capitalize(type.toString()) + "Sound").invoke(soundType)
                    ))).replace(".", "_").toUpperCase());
                } else {
                    switch (type) {
                        case HIT:
                        case FALL:
                            return null;
                    }
                    Object nmsBlock = getNmsBlock(block);
                    Class<?> STEP_SOUND = ReflectionUtils.getNMSClass("Block$StepSound");
                    return Sound.valueOf(((String) STEP_SOUND.getMethod("get" + StringUtils.capitalize(type.toString()) + "Sound")
                            .invoke(nmsBlock)).replace(".", "_").toUpperCase());
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public static Object getNmsBlock(Block block) {
        try {
            if (ServerVersion.supports(16)) {
                return BlockBehaviour_i_BlockStateBaseAccessor.getMethodGetBlock1().invoke(
                        LevelAccessor.getMethodGetBlockState1().invoke(NMSUtils.getServerLevel(block.getWorld()), BlockPosAccessor.getConstructor0().newInstance(block.getX(), block.getY(), block.getZ()))
                );
            } else if (ServerVersion.supports(9)) {
                return BlockStateAccessor.getMethodGetBlock1().invoke(LevelAccessor.getMethodC1().invoke(NMSUtils.getServerLevel(block.getWorld()),
                        BlockPosAccessor.getConstructor0().newInstance(block.getX(), block.getY(), block.getZ())));
            } else {
                return LevelAccessor.getMethodC1().invoke(NMSUtils.getServerLevel(block.getWorld()),
                        BlockPosAccessor.getConstructor0().newInstance(block.getX(), block.getY(), block.getZ()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object getNmsBlockData(Material material) {
        try {
            Object craftBlockData = CRAFT_BLOCKDATA.getMethod("newData", Material.class, String.class)
                    .invoke(null, material, null);

            return CRAFT_BLOCKDATA.getMethod("getState").invoke(craftBlockData);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public enum SoundType {
        PLACE,
        BREAK,
        HIT,
        STEP,
        FALL
    }

}
