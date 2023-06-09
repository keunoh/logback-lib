package lib.slf4j.helpers;

import lib.slf4j.Logger;
import lib.slf4j.LoggerFactory;

import java.io.ObjectStreamException;
import java.io.Serializable;

abstract class NamedLoggerBase implements Logger, Serializable {

    private static final long serialVersionUID = 7535258609338176893L;

    protected String name;

    public String getName() {
        return name;
    }

    protected Object readResolve() throws ObjectStreamException {
        return LoggerFactory.getLogger(getName());
    }
}
