package me.mohamad82.ruom.scoreboard;

import me.mohamad82.ruom.Ruom;
import me.mohamad82.ruom.nmsaccessors.ObjectiveCriteriaAccessor;
import me.mohamad82.ruom.nmsaccessors.ObjectiveCriteria_i_RenderTypeAccessor;
import me.mohamad82.ruom.nmsaccessors.ScoreboardAccessor;
import me.mohamad82.ruom.utils.ListUtils;
import me.mohamad82.ruom.utils.NMSUtils;
import me.mohamad82.ruom.utils.PacketUtils;
import me.mohamad82.ruom.utils.Viewable;
import net.kyori.adventure.platform.bukkit.MinecraftComponentSerializer;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;

public class Scoreboard extends Viewable {

    private final static String[] COLOR_CODES = Arrays.stream(ChatColor.values())
            .map(Object::toString)
            .toArray(String[]::new);

    private final String id;
    private final Map<Integer, ScoreboardLine> lines = new HashMap<>();

    private final Object objective;

    public static Scoreboard scoreboard(String id, Component title) {
        return new Scoreboard(id, title);
    }

    private Scoreboard(String id, Component title) {
        this.id = id;
        this.objective = createObjective(title);
    }

    public void setLine(int index, Component line) {
        ScoreboardLine scoreboardLine = createLine(index, line);

        NMSUtils.sendPacket(getViewers(), scoreboardLine.getScorePacket(), scoreboardLine.getTeamPacket());

        lines.put(index, scoreboardLine);
    }

    public void setTitle(Component title) {
        //TODO
    }

    public void discard() {
        for (Player viewer : getViewers()) {
            removeViewer(viewer);
        }
    }

    private ScoreboardLine createLine(int index, Component line) {
        Object scorePacket = PacketUtils.getSetScorePacket(
                id,
                COLOR_CODES[index],
                0
        );
        Object teamPacket = getTeamCreatePacket(index, line);

        return new ScoreboardLine(line, scorePacket, teamPacket, getTeamRemovePacket(index));
    }

    private Object createObjective(Component title) {
        try {
            Object scoreboard = ScoreboardAccessor.getConstructor0().newInstance();
            ScoreboardAccessor.getMethodAddObjective1().invoke(
                    scoreboard,
                    id,
                    ObjectiveCriteriaAccessor.getFieldTRIGGER().get(null),
                    MinecraftComponentSerializer.get().serialize(title),
                    ObjectiveCriteria_i_RenderTypeAccessor.getFieldINTEGER().get(null)
            );
            return ScoreboardAccessor.getMethodGetOrCreateObjective1().invoke(scoreboard, id);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Error(e);
        }
    }

    private Object getTeamCreatePacket(int score, Component line) {
        return PacketUtils.getTeamCreatePacket(
                id + ":" + score,
                line,
                Component.empty(),
                PacketUtils.NameTagVisibility.NEVER,
                PacketUtils.CollisionRule.NEVER,
                ChatColor.GRAY,
                ListUtils.toList(COLOR_CODES[score]),
                false
        );
    }

    private List<Object> getAddLinesPackets() {
        List<Object> packets = new ArrayList<>();

        for (int i = 0; i <= Collections.max(lines.keySet()); i++) {
            ScoreboardLine line;
            if (lines.containsKey(i)) {
                line = lines.get(i);
            } else {
                line = createLine(i, Component.empty());
            }
            packets.add(line.getScorePacket());
            packets.add(line.getTeamPacket());
        }

        return packets;
    }

    private List<Object> getRemoveLinesPackets() {
        List<Object> packets = new ArrayList<>();

        for (int i = 0; i < Collections.max(lines.keySet()); i++) {
            packets.add(getTeamRemovePacket(i));
        }

        return packets;
    }

    private Object getTeamRemovePacket(int score) {
        return PacketUtils.getTeamRemovePacket(id + ":" + score);
    }

    @Override
    protected void addViewer(Player player) {
        List<Object> packets = new ArrayList<>();
        packets.add(PacketUtils.getSetObjectivePacket(objective, 0));
        packets.add(PacketUtils.getSetDisplayObjectivePacket(objective));
        packets.addAll(getAddLinesPackets());

        for (Object packet : packets) {
            NMSUtils.sendPacket(player, packet);
        }
    }

    @Override
    protected void removeViewer(Player player) {
        List<Object> packets = new ArrayList<>();
        packets.add(PacketUtils.getSetObjectivePacket(objective, 1));
        packets.add(PacketUtils.getSetDisplayObjectivePacket(objective));
        packets.addAll(getRemoveLinesPackets());

        NMSUtils.sendPacket(
                player,
                packets
        );
    }

}
