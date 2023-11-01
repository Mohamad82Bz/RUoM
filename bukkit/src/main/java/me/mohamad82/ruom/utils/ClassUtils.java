package me.mohamad82.ruom.utils;

import com.google.common.collect.Lists;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ClassUtils {

    public static <T> T copy(T object, T newObject) {
        try {
            Set<Field> fields = new HashSet<>();

            Class<?> clazz = object.getClass();
            while (!clazz.equals(Object.class)) {
                fields.addAll(Lists.newArrayList(clazz.getDeclaredFields()));
                clazz = clazz.getSuperclass();
            }

            for (Field field : fields) {
                field.setAccessible(true);
                Object fieldObject = field.get(object);
                if (fieldObject == null) continue;
                if (fieldObject instanceof Collection) {
                    //noinspection JavaReflectionInvocation
                    fieldObject = fieldObject.getClass().getConstructor(Collection.class).newInstance(fieldObject);
                } else {
                    try {
                        fieldObject = fieldObject.getClass().getDeclaredMethod("clone").invoke(fieldObject);
                    } catch (NoSuchMethodException ignored) {}
                }
                field.set(newObject, fieldObject);
            }
            return newObject;
        } catch (Exception e) {
            throw new Error(e);
        }
    }

}
