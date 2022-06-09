package me.mohamad82.ruom.utils;

import com.google.common.collect.ImmutableSet;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public abstract class Viewable {

    private final Set<Player> viewers = new HashSet<>();

    protected abstract void addViewer(Player player);

    protected abstract void removeViewer(Player player);

    public void onAddViewers(Player... players) {
        //Optional event that can be overriden
    }

    public void onRemoveViewers(Player... players) {
        //Optional event that can be overriden
    }

    public void addViewers(Player... players) {
        onAddViewers(players);
        for (Player player : players) {
            addViewer(player);
            viewers.add(player);
        }
    }

    public void addViewers(Set<Player> players) {
        addViewers(players.toArray(new Player[0]));
    }

    public void removeViewers(Player... players) {
        onRemoveViewers(players);
        for (Player player : players) {
            removeViewer(player);
            viewers.remove(player);
        }
    }

    public void removeViewers(Set<Player> players) {
        removeViewers(players.toArray(new Player[0]));
    }

    protected Set<Player> getMutableViewers() {
        return viewers;
    }

    public ImmutableSet<Player> getViewers() {
        return ImmutableSet.copyOf(viewers);
    }

}
