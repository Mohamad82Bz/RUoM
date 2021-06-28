package me.Mohamad82.RUoM.Misc;

import me.Mohamad82.RUoM.Misc.Enums.CharAnimationStyle;

public class CharAnimation {

    private int tick = 1;
    private char animationChar;
    private final CharAnimationStyle style;

    public CharAnimation(CharAnimationStyle style) {
        this.style = style;
    }

    public char get() {
        if (style.equals(CharAnimationStyle.SQUARE_BLOCK)) {
            switch (tick) {
                case 1: animationChar = '▟';
                case 2: animationChar = '▙';
                case 3: animationChar = '▛';
                case 4: animationChar = '▜';
                default:
                    animationChar = '▟';
                    tick = 1;
            }
            tick++;
        } else if (style.equals(CharAnimationStyle.SQUARE_LINE)) {
            switch (tick) {
                case 1: animationChar = '◳';
                case 2: animationChar = '◲';
                case 3: animationChar = '◱';
                case 4: animationChar = '◰';
                default:
                    animationChar = '◳';
                    tick = 1;
            }
        }

        return animationChar;
    }

}
