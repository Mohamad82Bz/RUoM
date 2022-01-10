package me.mohamad82.ruom.pathfinding;

import com.extollit.gaming.ai.path.model.ColumnarOcclusionFieldList;
import com.extollit.gaming.ai.path.model.IColumnarSpace;
import me.mohamad82.ruom.Ruom;

public class InstanceChunk implements IColumnarSpace {

    private final Instance instance;
    private final ChunkCord location;

    private final ColumnarOcclusionFieldList columnarOcclusionFieldList = new ColumnarOcclusionFieldList(this);

    private final Tile[][][] blocks;
    private final int[][][] metaDatas;

    public InstanceChunk(Instance instance, ChunkCord location) {
        this.instance = instance;
        this.location = location;
        this.blocks = new Tile[16][instance.getMaxHeight()][16];
        this.metaDatas = new int[16][instance.getMaxHeight()][16];
    }

    @Override
    public Tile blockAt(int x, int y, int z) {
        final Tile block = this.blocks[z][y][x];
        return block == null ? Tile.AIR : block;
    }

    public void setBlockAt(int x, int y, int z, Tile block) {
        this.blocks[z][y][x] = block;
    }

    @Override
    public int metaDataAt(int x, int y, int z) {
        return metaDatas[z][y][x];
    }

    @Override
    public ColumnarOcclusionFieldList occlusionFields() {
        return columnarOcclusionFieldList;
    }

    @Override
    public Instance instance() {
        return instance;
    }

    public ChunkCord getLocation() {
        return location;
    }

    public void load() {
        occlusionFields().reset();
    }

    public void unload() {
        occlusionFields().reset();
    }

    public static final class ChunkCord {

        private final int x, z;

        public ChunkCord(int x, int z) {
            this.x = x;
            this.z = z;
        }

        public int getX() {
            return x;
        }

        public int getZ() {
            return z;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof ChunkCord) {
                ChunkCord other = (ChunkCord) o;
                return x == other.x && z == other.z;
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return x * 31 + z;
        }

    }

}
