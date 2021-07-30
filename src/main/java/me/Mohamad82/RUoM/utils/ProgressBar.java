package me.Mohamad82.RUoM.utils;

import com.google.common.base.Strings;

public class ProgressBar {

    public static String getBar(int current, int max, int total, String completeString, String notCompleteString) {
        float percent = (float) current / max;
        int progressBars = (int) (total * percent);

        return StringUtils.colorize(Strings.repeat(completeString, progressBars)
                + Strings.repeat(notCompleteString, total - progressBars));
    }

}
