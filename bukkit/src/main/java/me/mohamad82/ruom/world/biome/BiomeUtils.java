package me.mohamad82.ruom.world.biome;

import com.mojang.serialization.Lifecycle;
import me.mohamad82.ruom.Ruom;
import me.mohamad82.ruom.nmsaccessors.*;
import me.mohamad82.ruom.utils.NMSUtils;
import me.mohamad82.ruom.utils.ResourceKey;
import org.bukkit.Location;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class BiomeUtils {

    private static Object REGISTRY;
    private static Method CHUNK_SET_BLOCK_METHOD;
    private final static Map<ResourceKey, Object> REGISTERED_BIOMES = new HashMap<>();

    public static void createCustomBiome(ResourceKey key, BiomeEffects biomeEffects) {
        Ruom.run(() -> {
            Object plainsBiome = RegistryAccessor.getMethodGetOrThrow1().invoke(
                    REGISTRY,
                    ResourceKeyAccessor.getMethodCreate1().invoke(
                            null,
                            RegistryAccessor.getFieldBIOME_REGISTRY().get(null),
                            new ResourceKey("minecraft", "plains").getNmsResourceLocation()
                    )
            );
            Object biomeKey = ResourceKeyAccessor.getMethodCreate1().invoke(
                    null,
                    RegistryAccessor.getFieldBIOME_REGISTRY().get(null),
                    key.getNmsResourceLocation()
            );

            Object biomeBuilder = Biome_i_BiomeBuilderAccessor.getConstructor0().newInstance();
            Biome_i_BiomeBuilderAccessor.getMethodPrecipitation1().invoke(biomeBuilder, BiomeAccessor.getMethodGetPrecipitation1().invoke(plainsBiome));
            Biome_i_BiomeBuilderAccessor.getMethodBiomeCategory1().invoke(biomeBuilder, BiomeAccessor.getMethodGetBiomeCategory1().invoke(plainsBiome));
            Biome_i_BiomeBuilderAccessor.getMethodMobSpawnSettings1().invoke(biomeBuilder, BiomeAccessor.getFieldMobSettings().get(plainsBiome));
            Biome_i_BiomeBuilderAccessor.getMethodGenerationSettings1().invoke(biomeBuilder, BiomeAccessor.getFieldGenerationSettings().get(plainsBiome));
            Biome_i_BiomeBuilderAccessor.getMethodTemperature1().invoke(biomeBuilder, 0.2F);
            Biome_i_BiomeBuilderAccessor.getMethodDownfall1().invoke(biomeBuilder, 0.05F);
            Biome_i_BiomeBuilderAccessor.getMethodTemperatureAdjustment1().invoke(biomeBuilder, Biome_i_TemperatureModifierAccessor.getFieldNONE());
            Biome_i_BiomeBuilderAccessor.getMethodSpecialEffects1().invoke(biomeBuilder, biomeEffects.getNmsSpecialEffects(BiomeAccessor.getMethodGetSpecialEffects1().invoke(plainsBiome)));

            Object biome = Biome_i_BiomeBuilderAccessor.getMethodBuild1().invoke(biomeBuilder);

            //RegistryAccessor.getMethodRegister2().invoke(null, REGISTRY, biomeKey, biome);
            MappedRegistryAccessor.getFieldFrozen().set(REGISTRY, false);
            BuiltinRegistriesAccessor.getMethodRegister1().invoke(null, BuiltinRegistriesAccessor.getFieldBIOME().get(null), biomeKey, biome);
            WritableRegistryAccessor.getMethodRegister1().invoke(REGISTRY, biomeKey, biome, Lifecycle.stable());
            MappedRegistryAccessor.getFieldFrozen().set(REGISTRY, true);
            REGISTERED_BIOMES.put(key, biome);
        });
    }

    public static void setBiome(ResourceKey key, Location location) {
        Object biome = REGISTERED_BIOMES.get(key);
        if (biome == null) {
            throw new IllegalArgumentException("Cannot find a registered biome with key " + key);
        }
        try {
            Object holder = HolderAccessor.getMethodDirect1().invoke(null, biome);
            Object chunk = LevelAccessor.getMethodGetChunkAt1().invoke(
                    NMSUtils.getServerLevel(location.getWorld()),
                    BlockPosAccessor.getConstructor0().newInstance(
                            location.getBlockX(),
                            location.getBlockY(),
                            location.getBlockZ()
                    )
            );
            CHUNK_SET_BLOCK_METHOD.invoke(
                    chunk,
                    location.getBlockX() >> 2,
                    location.getBlockY() >> 2,
                    location.getBlockZ() >> 2,
                    holder
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setBiome(ResourceKey key, Collection<Location> locations) {
        for (Location location : locations) {
            setBiome(key, location);
        }
    }

    static {
        try {
            REGISTRY = RegistryAccessAccessor.getMethodOwnedRegistryOrThrow1().invoke(MinecraftServerAccessor.getMethodRegistryAccess1().invoke(NMSUtils.getDedicatedServer()), RegistryAccessor.getFieldBIOME_REGISTRY().get(null));
            CHUNK_SET_BLOCK_METHOD = LevelChunkAccessor.getType().getMethod("setBiome", int.class, int.class, int.class, HolderAccessor.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
