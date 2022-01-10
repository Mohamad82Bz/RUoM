package me.mohamad82.ruom.npc;

import me.mohamad82.ruom.Ruom;
import me.mohamad82.ruom.nmsaccessors.EntityTypeAccessor;
import org.bukkit.entity.EntityType;

public enum NPCType {
    AREA_EFFECT_CLOUD,
    ARMOR_STAND,
    ARROW,
    BAT,
    BEE,
    BLAZE,
    BOAT,
    CAT,
    CAVE_SPIDER,
    CHICKEN,
    COD,
    COW,
    CREEPER,
    DOLPHIN,
    DONKEY,
    DRAGON_FIREBALL,
    DROWNED,
    ELDER_GUARDIAN,
    END_CRYSTAL,
    ENDER_DRAGON,
    ENDERMAN,
    EVOKER,
    EXPERIENCE_ORB,
    EYE_OF_ENDER,
    FALLING_BLOCK,
    FIREWORK_ROCKET,
    FISHING_BOBBER,
    FOX,
    GHAST,
    GIANT,
    GUARDIAN,
    HOGLIN,
    HORSE,
    HUSK,
    ILLUSIONER,
    IRON_GOLEM,
    ITEM,
    ITEM_FRAME,
    FIREBALL,
    LIGHTNING_BOLT,
    LLAMA,
    LLAMA_SPIT,
    MAGMA_CUBE,
    MINECART,
    CHEST_MINECART,
    COMMAND_BLOCK_MINECART,
    FURNACE_MINECART,
    HOPPER_MINECART,
    SPAWNER_MINECART,
    TNT_MINECART,
    MULE,
    MOOSHROOM,
    OCELOT,
    PAINTING,
    PANDA,
    PARROT,
    PHANTOM,
    PIG,
    PIGLIN,
    PIGLIN_BRUTE,
    PILLAGER,
    TNT,
    PUFFERFISH,
    RABBIT,
    RAVAGER,
    SALMON,
    SHEEP,
    SHULKER,
    SHULKER_BULLET,
    SILVERFISH,
    SKELETON,
    SKELETON_HORSE,
    SLIME,
    SMALL_FIREBALL,
    SNOW_GOLEM,
    SNOWBALL,
    SPECTRAL_ARROW,
    SPIDER,
    SQUID,
    STRAY,
    STRIDER,
    EGG,
    ENDER_PEARL,
    EXPERIENCE_BOTTLE,
    POTION,
    TRIDENT,
    TRADER_LLAMA,
    TROPICAL_FISH,
    TURTLE,
    VEX,
    VILLAGER,
    VINDICATOR,
    WANDERING_TRADER,
    WITCH,
    WITHER,
    WITHER_SKELETON,
    WITHER_SKULL,
    WOLF,
    ZOGLIN,
    ZOMBIE,
    ZOMBIE_HORSE,
    ZOMBIE_VILLAGER,
    ZOMBIFIED_PIGLIN,
    PLAYER;

    private Object nmsEntityType;

    NPCType() {
        Ruom.run(() -> nmsEntityType = EntityTypeAccessor.class.getMethod("getField" + toString().toUpperCase()).invoke(null));
    }

    public Object getNmsEntityType() {
        return nmsEntityType;
    }

    public static NPCType getByEntityType(EntityType entityType) {
        switch (entityType) {
            case SPLASH_POTION: return POTION;
            case DROPPED_ITEM: return ITEM;
            case FISHING_HOOK: return FISHING_BOBBER;
            default: return valueOf(entityType.toString());
        }
    }

}
