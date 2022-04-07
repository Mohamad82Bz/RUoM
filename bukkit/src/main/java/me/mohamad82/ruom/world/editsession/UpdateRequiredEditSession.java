package me.mohamad82.ruom.world.editsession;

public interface UpdateRequiredEditSession extends EditSession {

    public void apply();

    public void update();

}
