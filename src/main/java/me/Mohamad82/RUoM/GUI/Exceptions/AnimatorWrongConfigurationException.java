package me.Mohamad82.RUoM.GUI.Exceptions;

public class AnimatorWrongConfigurationException extends Exception {

    public AnimatorWrongConfigurationException(String sectionName) {
        super(String.format("Animation of configuration section '%s' failed to read because of missing configurations.", sectionName));
    }

}
