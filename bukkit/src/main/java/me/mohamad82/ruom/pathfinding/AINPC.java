package me.mohamad82.ruom.pathfinding;

import com.extollit.gaming.ai.path.model.Gravitation;
import com.extollit.gaming.ai.path.model.IPathingEntity;
import com.extollit.gaming.ai.path.model.Passibility;
import com.extollit.linalg.immutable.Vec3d;
import me.mohamad82.ruom.npc.NPC;
import me.mohamad82.ruom.math.vector.Vector3;
import me.mohamad82.ruom.math.vector.Vector3Utils;

public class AINPC implements IPathingEntity, IPathingEntity.Capabilities {

    private boolean fireResistance, cautious, climber, swimmer, aquatic, avian, aquaphobic, avoidsDoorways, opensDoors, bound;
    public int age;

    private final NPC npc;
    private final com.extollit.linalg.mutable.Vec3d position;

    public AINPC(NPC npc, Vector3 location) {
        this.npc = npc;
        this.position = new com.extollit.linalg.mutable.Vec3d(location.getX(), location.getY(), location.getZ());
        npc.teleport(location);
    }

    @Override
    public int age() {
        return age;
    }

    @Override
    public boolean bound() {
        return bound;
    }

    @Override
    public float searchRange() {
        return 500;
    }

    @Override
    public Capabilities capabilities() {
        return this;
    }

    @Override
    public void moveTo(Vec3d newPosition, Passibility passibility, Gravitation gravitation) {
        Vector3 travelDistance = Vector3Utils.getTravelDistance(Vector3.at(position.x, position.y, position.z), Vector3.at(newPosition.x, newPosition.y, newPosition.z));
        if (!npc.move(travelDistance)) {
            npc.teleport(Vector3.at(newPosition.x, newPosition.y, newPosition.z));
        }
        this.position.set(newPosition);
    }

    @Override
    public Vec3d coordinates() {
        return new Vec3d(position);
    }

    @Override
    public float width() {
        return 0.6f;
    }

    @Override
    public float height() {
        return 1.0f;
    }

    @Override
    public float speed() {
        return 0.01f;
    }

    @Override
    public boolean fireResistant() {
        return fireResistance;
    }

    @Override
    public boolean cautious() {
        return cautious;
    }

    @Override
    public boolean climber() {
        return climber;
    }

    @Override
    public boolean swimmer() {
        return swimmer;
    }

    @Override
    public boolean aquatic() {
        return aquatic;
    }

    @Override
    public boolean avian() {
        return avian;
    }

    @Override
    public boolean aquaphobic() {
        return aquaphobic;
    }

    @Override
    public boolean avoidsDoorways() {
        return avoidsDoorways;
    }

    @Override
    public boolean opensDoors() {
        return opensDoors;
    }

    public void updateTick() {
        age++;
    }

}
