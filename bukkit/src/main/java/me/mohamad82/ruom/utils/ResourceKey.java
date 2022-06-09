package me.mohamad82.ruom.utils;

import me.mohamad82.ruom.nmsaccessors.ResourceKeyAccessor;
import me.mohamad82.ruom.nmsaccessors.ResourceLocationAccessor;

public class ResourceKey {

    private final String key;
    private final String value;

    public ResourceKey(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public Object getNmsResourceLocation() {
        try {
            return ResourceLocationAccessor.getConstructor1().newInstance(key, value);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String toString() {
        return key + ":" + value;
    }

}
