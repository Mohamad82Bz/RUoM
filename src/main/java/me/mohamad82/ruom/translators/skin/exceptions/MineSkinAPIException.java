package me.mohamad82.ruom.translators.skin.exceptions;

public class MineSkinAPIException extends Exception {

    public MineSkinAPIException() {
        super("Could not connect to MineSkinAPI or the api is overloaded.");
    }

}
