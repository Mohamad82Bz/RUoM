package me.mohamad82.ruom.translators.skin.exceptions;

public class NoSuchAccountNameException extends Exception {

    public NoSuchAccountNameException() {
        super("Entered account name does not exist.");
    }

}
