package me.Mohamad82.RUoM;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListUtils {

    public static List<Integer> toIntegerList(int... i) {
        List<Integer> list = new ArrayList<>();
        for (int j : i)
            list.add(j);
        return list;
    }

    public static List<String> toStringList(String... str) {
        List<String> list = new ArrayList<>();
        Collections.addAll(list, str);
        return list;
    }

}
