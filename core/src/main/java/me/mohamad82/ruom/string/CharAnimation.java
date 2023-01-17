package me.mohamad82.ruom.string;

public class CharAnimation {

    private byte tick = 1;
    private char animationChar;
    private final Style style;

    public CharAnimation(Style style) {
        this.style = style;
    }

    public char get() {
        if (style.equals(Style.SQUARE_BLOCK)) {
            switch (tick) {
                case 1: animationChar = '▟'; break;
                case 2: animationChar = '▙'; break;
                case 3: animationChar = '▛'; break;
                case 4: animationChar = '▜'; break;
                default:
                    animationChar = '▟';
                    tick = 1;
            }
        } else if (style.equals(Style.SQUARE_LINE)) {
            switch (tick) {
                case 1: animationChar = '◰'; break;
                case 2: animationChar = '◳'; break;
                case 3: animationChar = '◲'; break;
                case 4: animationChar = '◱'; break;
                default:
                    animationChar = '◰';
                    tick = 1;
            }
        }
        tick++;

        return animationChar;
    }

    public enum Style {
        SQUARE_BLOCK,
        SQUARE_LINE
    }

}
