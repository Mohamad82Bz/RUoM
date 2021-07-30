package me.Mohamad82.RUoM.gui.exceptions;

public class AnimatorNullPluginException extends RuntimeException {

    public AnimatorNullPluginException() {
        super("Animator must have a valid JavaPlugin for it's tasks.");
    }

}
