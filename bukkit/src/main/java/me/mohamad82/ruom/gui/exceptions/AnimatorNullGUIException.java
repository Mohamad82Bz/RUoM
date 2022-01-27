package me.mohamad82.ruom.gui.exceptions;

public class AnimatorNullGUIException extends RuntimeException {

    public AnimatorNullGUIException() {
        super("Animator must have a valid Inventory to animate it.");
    }

}
