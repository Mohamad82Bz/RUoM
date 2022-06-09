package me.mohamad82.ruom.world.biome;

import me.mohamad82.ruom.nmsaccessors.BiomeSpecialEffectsAccessor;
import me.mohamad82.ruom.nmsaccessors.BiomeSpecialEffects_i_BuilderAccessor;
import me.mohamad82.ruom.nmsaccessors.BiomeSpecialEffects_i_GrassColorModifierAccessor;

public class BiomeEffects {

    private int grassColor;
    private int foliageColor;
    private int waterColor;
    private int waterFogColor;
    private int skyColor;
    private int fogColor;

    public BiomeEffects setGrassColor(int grassColor) {
        this.grassColor = grassColor;
        return this;
    }

    public BiomeEffects setFoliageColor(int foliageColor) {
        this.foliageColor = foliageColor;
        return this;
    }

    public BiomeEffects setWaterColor(int waterColor) {
        this.waterColor = waterColor;
        return this;
    }

    public BiomeEffects setWaterFogColor(int waterFogColor) {
        this.waterFogColor = waterFogColor;
        return this;
    }

    public BiomeEffects setSkyColor(int skyColor) {
        this.skyColor = skyColor;
        return this;
    }

    public BiomeEffects setFogColor(int fogColor) {
        this.fogColor = fogColor;
        return this;
    }

    public int getGrassColor() {
        return grassColor;
    }

    public int getFoliageColor() {
        return foliageColor;
    }

    public int getWaterColor() {
        return waterColor;
    }

    public int getWaterFogColor() {
        return waterFogColor;
    }

    public int getSkyColor() {
        return skyColor;
    }

    public int getFogColor() {
        return fogColor;
    }

    public Object getNmsSpecialEffects(Object defaultEffects) {
        try {
            Object effectsBuilder = BiomeSpecialEffects_i_BuilderAccessor.getConstructor0().newInstance();
            BiomeSpecialEffects_i_BuilderAccessor.getMethodGrassColorModifier1().invoke(effectsBuilder, BiomeSpecialEffects_i_GrassColorModifierAccessor.getFieldNONE());
            BiomeSpecialEffects_i_BuilderAccessor.getMethodGrassColorOverride1().invoke(effectsBuilder, grassColor != 0 ? grassColor : BiomeSpecialEffectsAccessor.getMethodGetGrassColorOverride1().invoke(defaultEffects));
            BiomeSpecialEffects_i_BuilderAccessor.getMethodFoliageColorOverride1().invoke(effectsBuilder, foliageColor != 0 ? foliageColor : BiomeSpecialEffectsAccessor.getMethodGetFoliageColorOverride1().invoke(defaultEffects));
            BiomeSpecialEffects_i_BuilderAccessor.getMethodWaterColor1().invoke(effectsBuilder, waterColor != 0 ? waterColor : BiomeSpecialEffectsAccessor.getMethodGetWaterColor1().invoke(defaultEffects));
            BiomeSpecialEffects_i_BuilderAccessor.getMethodWaterFogColor1().invoke(effectsBuilder, waterFogColor != 0 ? waterFogColor : BiomeSpecialEffectsAccessor.getMethodGetWaterFogColor1().invoke(defaultEffects));
            BiomeSpecialEffects_i_BuilderAccessor.getMethodSkyColor1().invoke(effectsBuilder, skyColor != 0 ? skyColor : BiomeSpecialEffectsAccessor.getMethodGetSkyColor1().invoke(defaultEffects));
            BiomeSpecialEffects_i_BuilderAccessor.getMethodFogColor1().invoke(effectsBuilder, fogColor != 0 ? fogColor : BiomeSpecialEffectsAccessor.getMethodGetFogColor1().invoke(defaultEffects));

            return BiomeSpecialEffects_i_BuilderAccessor.getMethodBuild1().invoke(effectsBuilder);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
