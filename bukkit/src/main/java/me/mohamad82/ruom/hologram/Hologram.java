package me.mohamad82.ruom.hologram;

import com.google.common.collect.ImmutableList;
import me.mohamad82.ruom.Ruom;
import me.mohamad82.ruom.utils.Viewable;
import me.mohamad82.ruom.math.vector.Vector3;
import me.mohamad82.ruom.math.vector.Vector3UtilsBukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Hologram extends Viewable {

    private final List<HoloLine> lines = new ArrayList<>();
    private final Map<HoloLine, Location> lineLocations = new HashMap<>();
    private Location location;

    private Hologram(List<HoloLine> lines, Location location) {
        this.location = location.clone();
        reload(lines, location);
    }

    public static Hologram hologram(List<HoloLine> lines, Location location) {
        return new Hologram(lines, location);
    }

    private void reload(List<HoloLine> lines, Location location) {
        unload();
        List<HoloLine> newLines = new ArrayList<>(lines);
        this.lines.clear();
        this.lineLocations.clear();

        int lineIndex = 0;
        Location suitableLocation = location.clone();
        for (HoloLine line : newLines) {
            if (lineIndex > 0) {
                suitableLocation.add(0, -line.getDistance(), 0);
            }
            lineLocations.put(line, suitableLocation);
            line.initializeNpc(suitableLocation);

            this.lines.add(line);
            lineIndex++;
        }
    }

    public void reload() {
        reload(lines, location);
    }

    public void unload() {
        for (HoloLine line : lines) {
            if (line.getNpc() != null) {
                line.getNpc().removeViewers(Ruom.getOnlinePlayers());
            }
        }
    }

    public void move(Vector3 vector3) {
        location.add(vector3.getX(), vector3.getY(), vector3.getZ());
        for (HoloLine line : lines) {
            Location lineLocation = lineLocations.get(line);
            if (!line.getNpc().move(vector3)) {
                lineLocation.add(vector3.getX(), vector3.getY(), vector3.getZ());
                line.getNpc().teleport(Vector3UtilsBukkit.toVector3(lineLocation), 0, 0);
            }
        }
    }

    public void teleport(Location location) {
        this.location = location;
        for (HoloLine line : lines) {
            Location lineLocation = lineLocations.get(line);
            line.getNpc().teleport(Vector3UtilsBukkit.toVector3(lineLocation), 0, 0);
        }
    }

    public void setLines(List<HoloLine> lines) {
        reload(lines, location);
    }

    public boolean setLine(int index, HoloLine line) {
        if (index < 0 || index >= lines.size()) return false;
        lines.set(index, line);

        reload();
        return true;
    }

    public void addLine(HoloLine line) {
        lines.add(line);

        reload();
    }

    public boolean removeLine(int index) {
        if (index < 0 || index >= lines.size()) return false;
        lines.get(index).getNpc().removeViewers(Ruom.getOnlinePlayers());
        lines.remove(index);

        reload();
        return true;
    }

    /**
     * Returns an immutable list of lines
     * @return Immutable list of lines
     */
    public ImmutableList<HoloLine> getLines() {
        return ImmutableList.copyOf(lines);
    }

    @Override
    protected void addViewer(Player player) {
        for (HoloLine line : lines) {
            if (line.getNpc() != null)
                line.getNpc().addViewers(player);
        }
    }

    @Override
    protected void removeViewer(Player player) {
        for (HoloLine line : lines) {
            if (line.getNpc() != null)
                line.getNpc().removeViewers(player);
        }
    }

}
