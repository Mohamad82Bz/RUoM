package me.Mohamad82.RUoM.translators.skin.exceptions;

public class MineSkinAPIException extends Exception {

    public MineSkinAPIException() {
        super("Could not connect to MineSkinAPI or the api is overloaded.");
    }

}
