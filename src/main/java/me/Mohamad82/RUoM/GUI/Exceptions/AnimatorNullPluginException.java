package me.Mohamad82.RUoM.GUI.Exceptions;

public class AnimatorNullPluginException extends RuntimeException {

    public AnimatorNullPluginException() {
        super("Animator must have a valid JavaPlugin for it's tasks.");
    }

}
