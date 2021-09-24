package me.Mohamad82.RUoM.translators.skin.exceptions;

public class NoSuchAccountNameException extends Exception {

    public NoSuchAccountNameException() {
        super("Entered account name does not exist.");
    }

}
