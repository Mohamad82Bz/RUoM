package me.mohamad82.ruom.world.biome;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import me.mohamad82.ruom.Ruom;
import me.mohamad82.ruom.nmsaccessors.HolderAccessor;
import me.mohamad82.ruom.utils.NMSUtils;
import org.bukkit.Chunk;
import org.bukkit.Location;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class BiomeEditSession {

    private final static ThreadFactory THREAD_FACTORY = new ThreadFactoryBuilder().setNameFormat(Ruom.getPlugin().getName().toLowerCase() + "-biomeeditsession-thread-%d").build();

    private final ExecutorService executor;
    private final int poolingSize;

    public BiomeEditSession(int poolingSize) {
        this.executor = Executors.newFixedThreadPool(poolingSize, THREAD_FACTORY);
        this.poolingSize = poolingSize;
    }

    public void setBiome(Biome biome, Location... locations) {
        try {
            Object holder = HolderAccessor.getMethodDirect1().invoke(null, biome);
            for (Location location : locations) {
                executor.submit(() -> {
                    Object nmsChunk = NMSUtils.getLevelChunk(location.getChunk());
                    Ruom.run(() -> BiomeRegistry.CHUNK_SET_BLOCK_METHOD.invoke(
                            nmsChunk,
                            location.getBlockX() >> 2,
                            location.getBlockY() >> 2,
                            location.getBlockZ() >> 2,
                            holder
                    ));
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setBiome(Biome biome, Chunk... chunks) {
        for (Chunk chunk : chunks) {
            for (int x = 0; x < 16; x++) {
                for (int y = 0; y < chunk.getWorld().getMaxHeight(); y++) {
                    for (int z = 0; z < 16; z++) {
                        setBiome(biome, chunk.getBlock(x, y, z).getLocation());
                    }
                }
            }
        }
    }

}
