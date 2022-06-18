package me.mohamad82.ruom.world.biome;

import me.mohamad82.ruom.nmsaccessors.*;
import me.mohamad82.ruom.utils.ResourceKey;

public class Biome {

    private final ResourceKey key;
    private final BiomeEffects effects;
    private final Object nmsBiome;
    private final Object nmsKey;

    public Biome(ResourceKey key, BiomeEffects effects) {
        this.key = key;
        this.effects = effects;

        try {
            Object plainsBiome = RegistryAccessor.getMethodGetOrThrow1().invoke(
                    BiomeRegistry.REGISTRY,
                    ResourceKeyAccessor.getMethodCreate1().invoke(
                            null,
                            RegistryAccessor.getFieldBIOME_REGISTRY().get(null),
                            new ResourceKey("minecraft", "plains").getNmsResourceLocation()
                    )
            );
            this.nmsKey = ResourceKeyAccessor.getMethodCreate1().invoke(
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
            Biome_i_BiomeBuilderAccessor.getMethodSpecialEffects1().invoke(biomeBuilder, effects.getNmsSpecialEffects(BiomeAccessor.getMethodGetSpecialEffects1().invoke(plainsBiome)));

            this.nmsBiome = Biome_i_BiomeBuilderAccessor.getMethodBuild1().invoke(biomeBuilder);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to create biome " + key + ": " + e.getMessage());
        }
    }

    public ResourceKey getKey() {
        return key;
    }

    public BiomeEffects getEffects() {
        return effects;
    }

    public Object getNmsBiome() {
        return nmsBiome;
    }

    public Object getNmsKey() {
        return nmsKey;
    }

}
