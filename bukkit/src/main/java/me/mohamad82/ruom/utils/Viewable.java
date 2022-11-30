package me.mohamad82.ruom.utils;

import com.google.common.collect.ImmutableSet;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public abstract class Viewable {

    private final Set<Player> viewers = new HashSet<>();

    protected abstract void addViewer(Player player);

    protected abstract void removeViewer(Player player);

    public void onPreAddViewers(Player... players) {
        //Optional event that can be overriden
    }

    public void onPreRemovePlayers(Player... players) {
        //Optional event that can be overriden
    }

    public void onPostAddViewers(Player... players) {
        //Optional event that can be overriden
    }

    public void onPostRemoveViewers(Player... players) {
        //Optional event that can be overriden
    }

    public void addViewers(Player... players) {
        onPreAddViewers(players);
        for (Player player : players) {
            addViewer(player);
            viewers.add(player);
        }
        onPostAddViewers(players);
    }

    public void addViewers(Set<Player> players) {
        addViewers(players.toArray(new Player[0]));
    }

    public void removeViewers(Player... players) {
        onPreRemovePlayers(players);
        for (Player player : players) {
            removeViewer(player);
            viewers.remove(player);
        }
        onPostRemoveViewers(players);
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
