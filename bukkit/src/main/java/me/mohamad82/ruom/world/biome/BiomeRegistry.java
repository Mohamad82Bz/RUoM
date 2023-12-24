package me.mohamad82.ruom.world.biome;

import com.mojang.serialization.Lifecycle;
import me.mohamad82.ruom.nmsaccessors.*;
import me.mohamad82.ruom.utils.NMSUtils;
import me.mohamad82.ruom.utils.ResourceKey;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class BiomeRegistry {

    private static final BiomeRegistry INSTANCE = new BiomeRegistry();
    protected static Object REGISTRY;
    protected static Method CHUNK_SET_BLOCK_METHOD;

    private final Map<ResourceKey, Biome> biomes = new HashMap<>();

    private BiomeRegistry() {
    }

    public void register(Biome biome) {
        try {
            MappedRegistryAccessor.getFieldFrozen().set(REGISTRY, false);
            BuiltinRegistriesAccessor_1.getMethodRegister1().invoke(null, BuiltinRegistriesAccessor_1.getFieldBIOME().get(null), biome.getNmsKey(), biome.getNmsBiome());
            WritableRegistryAccessor.getMethodRegister1().invoke(REGISTRY, biome.getNmsKey(), biome.getNmsBiome(), Lifecycle.stable());
            MappedRegistryAccessor.getFieldFrozen().set(REGISTRY, true);

            biomes.put(biome.getKey(), biome);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Biome getBiome(ResourceKey key) {
        return biomes.get(key);
    }

    public static BiomeRegistry getInstance() {
        return INSTANCE;
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
