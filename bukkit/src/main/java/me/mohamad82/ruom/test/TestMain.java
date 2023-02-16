package me.mohamad82.ruom.test;

import me.mohamad82.ruom.RUoMPlugin;

public class TestMain extends RUoMPlugin {

    @Override
    public void onEnable() {
        getCommand("ruom").setExecutor(new TestCommand());
    }

}
