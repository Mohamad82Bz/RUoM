package me.mohamad82.ruom.scoreboard;

import net.kyori.adventure.text.Component;

public class ScoreboardLine {

    private final Component line;

    private final Object scorePacket;
    private final Object teamPacket;

    private final Object removeTeamPacket;

    public ScoreboardLine(Component line, Object scorePacket, Object teamPacket, Object removeTeamPacket) {
        this.line = line;
        this.scorePacket = scorePacket;
        this.teamPacket = teamPacket;
        this.removeTeamPacket = removeTeamPacket;
    }

    public Component getLine() {
        return line;
    }

    public Object getScorePacket() {
        return scorePacket;
    }

    public Object getTeamPacket() {
        return teamPacket;
    }

    public Object getRemoveTeamPacket() {
        return removeTeamPacket;
    }

}
