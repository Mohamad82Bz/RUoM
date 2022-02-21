package me.mohamad82.ruom.skin.exceptions;

public class NoSuchAccountNameException extends Exception {

    public NoSuchAccountNameException() {
        super("Entered account name does not exist.");
    }

}
