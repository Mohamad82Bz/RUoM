package me.mohamad82.ruom;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Ruom {

    private final static Logger LOGGER = LogManager.getLogger(Ruom.class);

    public void log(String message) {
        LOGGER.info(message);
    }
    
    public void log(String message, Throwable throwable) {
        LOGGER.info(message, throwable);
    }

    public void warn(String message) {
        LOGGER.warn(message);
    }

    public void warn(String message, Throwable throwable) {
        LOGGER.warn(message, throwable);
    }

    public void error(String message) {
        LOGGER.error(message);
    }

    public void error(String message, Throwable throwable) {
        LOGGER.error(message, throwable);
    }

    public void fatal(String message) {
        LOGGER.fatal(message);
    }

    public void fatal(String message, Throwable throwable) {
        LOGGER.fatal(message, throwable);
    }

}
