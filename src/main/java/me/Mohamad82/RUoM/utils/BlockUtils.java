package me.Mohamad82.RUoM.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;

import java.util.Random;

public class BlockUtils {

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
