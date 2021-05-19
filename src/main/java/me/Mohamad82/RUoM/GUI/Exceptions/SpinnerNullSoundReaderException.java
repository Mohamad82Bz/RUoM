package me.Mohamad82.RUoM.GUI.Exceptions;

public class SpinnerNullSoundReaderException extends RuntimeException {

    public SpinnerNullSoundReaderException() {
        super("Spinner must have a valid Sound Reader to play sounds!");
    }

}
