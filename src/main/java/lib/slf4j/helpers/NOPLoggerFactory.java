package lib.slf4j.helpers;

import lib.slf4j.ILoggerFactory;
import lib.slf4j.Logger;

public class NOPLoggerFactory implements ILoggerFactory {

    public NOPLoggerFactory() {}

    public Logger getLogger(String name) {
        return NOPLogger.NOP_LOGGER;
    }
}
