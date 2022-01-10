package me.mohamad82.ruom.pathfinding;

import com.extollit.gaming.ai.path.model.IInstanceSpace;
import me.mohamad82.ruom.Ruom;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.Map;

public class Instance implements IInstanceSpace {

    private final Map<InstanceChunk.ChunkCord, InstanceChunk> chunks = new HashMap<>();

    private final int maxHeight;

    public Instance(World world) {
        maxHeight = world.getMaxHeight();
    }

    @Override
    public Tile blockObjectAt(int x, int y, int z) {
        final InstanceChunk chunk = columnarSpaceAt(x >> 4, z >> 4);

        if (chunk == null) {
            return null;
        }

        return chunk.blockAt(x & 0xF, y, z & 0xF);
    }

    @Override
    public InstanceChunk columnarSpaceAt(int x, int z) {
        final InstanceChunk.ChunkCord chunkCord = new InstanceChunk.ChunkCord(x, z);
        Ruom.log(chunks.toString());
        return chunks.getOrDefault(chunkCord, null);
    }

    public void setBlock(int x, int y, int z, Tile block, int metadata) {
        final InstanceChunk chunk = columnarSpaceAt(x >> 4, z >> 4);

        if (chunk != null) {
            chunk.setBlockAt(x & 0xF, y, z & 0xF, block);
            chunk.occlusionFields().onBlockChanged(x, y, z, block, metadata);
        }
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public void loadChunk(Chunk bukkitChunk) {
        InstanceChunk.ChunkCord chunkCord = new InstanceChunk.ChunkCord(bukkitChunk.getX(), bukkitChunk.getZ());
        if (chunks.containsKey(chunkCord)) return;
        InstanceChunk chunk = new InstanceChunk(this, chunkCord);

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < maxHeight; y++) {
                    Block block = bukkitChunk.getBlock(x, y, z);
                    if (!block.getType().isAir()) {
                        chunk.setBlockAt(x, y, z, new Tile(block));
                    }
                }
            }
        }

        chunks.put(chunkCord, chunk);
    }

}
