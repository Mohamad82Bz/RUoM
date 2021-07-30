package me.Mohamad82.RUoM.gui.exceptions;

public class AnimatorNullGUIException extends RuntimeException {

    public AnimatorNullGUIException() {
        super("Animator must have a valid Inventory to animate it.");
    }

}
