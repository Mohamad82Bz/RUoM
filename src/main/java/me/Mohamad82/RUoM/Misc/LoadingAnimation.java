package me.Mohamad82.RUoM.Misc;

public class LoadingAnimation {

    private int tick = 1;
    private char animationChar;

    public char get() {
        if (tick == 1 || tick > 4) animationChar = '▟';
        else if (tick == 2) animationChar = '▙';
        else if (tick == 3) animationChar = '▛';
        else if (tick == 4) animationChar = '▜';
        if (tick > 4) tick = 1;

        tick++;

        return animationChar;
    }

}
