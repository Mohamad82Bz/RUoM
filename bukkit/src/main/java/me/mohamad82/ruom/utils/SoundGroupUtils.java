package me.mohamad82.ruom.utils;

import com.cryptomorin.xseries.ReflectionUtils;
import me.mohamad82.ruom.nmsaccessors.*;
import me.mohamad82.ruom.string.StringUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

import java.lang.reflect.Field;

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

    private static Object getSoundMinecraftKey(Object soundEffect) throws Exception {
        Field minecraftKeyField;
        String minecraftKeyMethod;

        switch (ReflectionUtils.VER) {
            case 12:
            case 16:
            case 17:
                minecraftKeyMethod = "b";
                break;
            case 13:
            case 14:
            case 15:
                minecraftKeyMethod = "a";
                break;
            default:
                return null;
        }

        minecraftKeyField = soundEffect.getClass().getDeclaredField(minecraftKeyMethod);
        minecraftKeyField.setAccessible(true);

        return minecraftKeyField.get(soundEffect);
    }

    private static Object getBlockSoundEffect(SoundType type, Object soundEffectType) throws Exception {
        Field soundEffectField;
        String soundEffectMethod = "";

        switch (type) {
            case BREAK:
                switch (ReflectionUtils.VER) {
                    case 17:
                        soundEffectMethod = "aA";
                        break;
                    case 16:
                        soundEffectMethod = "breakSound";
                        break;
                    case 15:
                        soundEffectMethod = "z";
                        break;
                    case 14:
                        soundEffectMethod = "y";
                        break;
                    case 13:
                        soundEffectMethod = "q";
                        break;
                    case 12:
                        soundEffectMethod = "o";
                        break;
                    default:
                        return null;
                }
                break;
            case PLACE:
                switch (ReflectionUtils.VER) {
                    case 17:
                        soundEffectMethod = "aC";
                        break;
                    case 16:
                        soundEffectMethod = "placeSound";
                        break;
                    case 15:
                        soundEffectMethod = "B";
                        break;
                    case 14:
                        soundEffectMethod = "A";
                        break;
                    case 13:
                        soundEffectMethod = "s";
                        break;
                    case 12:
                        soundEffectMethod = "q";
                        break;
                    default:
                        return null;
                }
                break;
            case HIT:
                switch (ReflectionUtils.VER) {
                    case 17:
                        soundEffectMethod = "aD";
                        break;
                    case 16:
                        soundEffectMethod = "hitSound";
                        break;
                    case 15:
                        soundEffectMethod = "C";
                        break;
                    case 14:
                        soundEffectMethod = "B";
                        break;
                    case 13:
                        soundEffectMethod = "t";
                        break;
                    case 12:
                        soundEffectMethod = "r";
                        break;
                    default:
                        return null;
                }
                break;
            case STEP:
                switch (ReflectionUtils.VER) {
                    case 17:
                        soundEffectMethod = "aB";
                        break;
                    case 16:
                        soundEffectMethod = "stepSound";
                        break;
                    case 15:
                        soundEffectMethod = "A";
                        break;
                    case 14:
                        soundEffectMethod = "z";
                        break;
                    case 13:
                        soundEffectMethod = "r";
                        break;
                    case 12:
                        soundEffectMethod = "p";
                        break;
                    default:
                        return null;
                }
                break;
            case FALL:
                switch (ReflectionUtils.VER) {
                    case 17:
                        soundEffectMethod = "aE";
                        break;
                    case 16:
                        soundEffectMethod = "fallSound";
                        break;
                    case 15:
                        soundEffectMethod = "D";
                        break;
                    case 14:
                        soundEffectMethod = "C";
                        break;
                    case 13:
                        soundEffectMethod = "u";
                        break;
                    case 12:
                        soundEffectMethod = "s";
                        break;
                    default:
                        return null;
                }
                break;
        }

        soundEffectField = soundEffectType.getClass().getDeclaredField(soundEffectMethod);
        soundEffectField.setAccessible(true);

        return soundEffectField.get(soundEffectType);
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
