package me.mohamad82.ruom.utils;

import com.cryptomorin.xseries.ReflectionUtils;
import me.mohamad82.ruom.nmsaccessors.*;
import me.mohamad82.ruom.string.StringUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

public class SoundGroupUtils {

    /**
     * @apiNote >= 1.9, For 1.8 use {@link me.mohamad82.ruom.utils.SoundGroupUtils#getBlockSound(SoundType, Block)}
     */
    public static Sound getBlockSound(SoundType type, Material material) {
        if (ServerVersion.supports(14)) {
            BlockData blockData = material.createBlockData();
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
                Object soundType = BlockAccessor.getMethodGetSoundType1().invoke(NMSUtils.getBlockState(material));
                return Sound.valueOf(((String) ResourceLocationAccessor.getMethodGetPath1().invoke(SoundEventAccessor.getMethodGetLocation1().invoke(
                        SoundTypeAccessor.class.getMethod("getField" + StringUtils.capitalize(type.toString()) + "Sound").invoke(soundType)
                ))).replace(".", "_").toUpperCase());
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public static Sound getBlockSound(SoundType type, Block block) {
        if (ServerVersion.supports(9)) {
            return getBlockSound(type, block.getType());
        } else {
            try {
                switch (type) {
                    case HIT:
                    case FALL:
                        return null;
                }
                Object nmsBlock = NMSUtils.getNmsBlock(block);
                Class<?> STEP_SOUND = ReflectionUtils.getNMSClass("Block$StepSound");
                return Sound.valueOf(((String) STEP_SOUND.getMethod("get" + StringUtils.capitalize(type.toString()) + "Sound")
                        .invoke(nmsBlock)).replace(".", "_").toUpperCase());
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
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
