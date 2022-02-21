package me.mohamad82.ruom.gui.exceptions;

public class AnimationWrongConfigurationException extends Exception {

    public AnimationWrongConfigurationException(String sectionName) {
        super(String.format("Animation of configuration section '%s' failed to read because of missing configurations.", sectionName));
    }

}
