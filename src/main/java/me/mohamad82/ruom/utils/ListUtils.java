package me.mohamad82.ruom.utils;

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

    @SafeVarargs
    public static <T> List<T> toList(T... t) {
        List<T> list = new ArrayList<>();
        Collections.addAll(list, t);
        return list;
    }

}
