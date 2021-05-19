package me.Mohamad82.RUoM;

import com.google.common.base.Strings;
import org.bukkit.ChatColor;

public class ProgressBar {

    public static String getBar(int current, int max, int total, String symbol, ChatColor completeColor, ChatColor notCompleteColor) {
        float percent = (float) current / max;
        int progressBars = (int) (total * percent);

        return Strings.repeat("" + completeColor + symbol, progressBars)
                + Strings.repeat("" + notCompleteColor + symbol, total - progressBars);
    }

}
