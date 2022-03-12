package me.mohamad82.ruom.pathfinding;

import com.cryptomorin.xseries.XMaterial;
import com.extollit.gaming.ai.path.model.IBlockObject;
import com.extollit.linalg.immutable.AxisAlignedBBox;
import com.extollit.linalg.immutable.Vec3d;
import me.mohamad82.ruom.utils.ListUtils;
import org.bukkit.block.Block;

import java.util.HashSet;
import java.util.Set;

public class Tile implements IBlockObject {

    public static final Tile AIR = new Tile(XMaterial.AIR, new AxisAlignedBBox(new Vec3d(0, 0, 0), new Vec3d(1, 1, 1)), false, false, false, false, true, false, false, false);

    private static final Set<XMaterial> BUKKIT_FENCES = new HashSet<>();
    private static final Set<XMaterial> BUKKIT_CLIMABLES = new HashSet<>(ListUtils.toList(XMaterial.LADDER));
    private static final Set<XMaterial> BUKKIT_DOORS = new HashSet<>();
    private static final Set<XMaterial> BUKKIT_BURNINGS = new HashSet<>(ListUtils.toList(XMaterial.FIRE));

    private final XMaterial type;
    private final AxisAlignedBBox bounds;
    private final boolean fence, climable, door, impeding, fullyBounded, liquid, incinerating, dynamic;

    public Tile(Block block) {
        bounds = new AxisAlignedBBox(new Vec3d(0, 0, 0), new Vec3d(1, 1, 1)); //TODO: Slabs, Stairs, Trapdoors, etc.

        this.type = XMaterial.matchXMaterial(block.getType());
        this.fence = BUKKIT_FENCES.contains(type);
        this.climable = BUKKIT_CLIMABLES.contains(type);
        this.door = BUKKIT_DOORS.contains(type);
        this.impeding = !block.isPassable();
        this.fullyBounded = true; //TODO: Fullybounded is solid blocks that aren't stairs, slabs, trapdoors
        this.liquid = block.isLiquid();
        this.incinerating = BUKKIT_BURNINGS.contains(type);
        this.dynamic = false; //TODO
    }

    protected Tile(XMaterial type, AxisAlignedBBox bounds, boolean fence, boolean climable, boolean door, boolean impeding, boolean fullyBounded, boolean liquid, boolean incinerating, boolean dynamic) {
        this.type = type;
        this.bounds = bounds;
        this.fence = fence;
        this.climable = climable;
        this.door = door;
        this.impeding = impeding;
        this.fullyBounded = fullyBounded;
        this.liquid = liquid;
        this.incinerating = incinerating;
        this.dynamic = dynamic;
    }

    public XMaterial getType() {
        return type;
    }

    @Override
    public AxisAlignedBBox bounds() {
        return bounds;
    }

    @Override
    public boolean isFenceLike() {
        return fence;
    }

    @Override
    public boolean isClimbable() {
        return climable;
    }

    @Override
    public boolean isDoor() {
        return door;
    }

    @Override
    public boolean isImpeding() {
        return impeding;
    }

    @Override
    public boolean isFullyBounded() {
        return fullyBounded;
    }

    @Override
    public boolean isLiquid() {
        return liquid;
    }

    @Override
    public boolean isIncinerating() {
        return incinerating;
    }

    @Override
    public String toString() {
        return type.toString();
    }

    static {
        for (XMaterial material : XMaterial.values()) {
            if (material.toString().endsWith("FENCE")) {
                BUKKIT_FENCES.add(material);
            }
            if (material.toString().endsWith("GATE") || material.toString().endsWith("_DOOR")) {
                BUKKIT_DOORS.add(material);
            }
        }
    }

}
