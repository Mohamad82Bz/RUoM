package me.mohamad82.ruom.skin.exceptions;

public class SkinParseException extends Exception {

    public SkinParseException() {
        super("Failed to parse skin of the given URL, Probably because of wrong format or non-direct link.");
    }

}
