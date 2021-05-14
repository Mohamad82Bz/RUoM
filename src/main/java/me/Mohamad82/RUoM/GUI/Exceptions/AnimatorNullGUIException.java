package me.Mohamad82.RUoM.GUI.Exceptions;

public class AnimatorNullGUIException extends RuntimeException {

    public AnimatorNullGUIException() {
        super("Animator must have a valid Inventory to animate it.");
    }

}
