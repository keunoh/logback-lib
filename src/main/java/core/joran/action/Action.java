package core.joran.action;

import core.joran.spi.ActionException;
import core.joran.spi.InterpretationContext;
import core.joran.spi.Interpreter;
import core.spi.ContextAwareBase;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;


public abstract class Action extends ContextAwareBase {

    public static final String NAME_ATTRIBUTE = "name";
    public static final String KEY_ATTRIBUTE = "key";
    public static final String VALUE_ATTRIBUTE = "value";
    public static final String FILE_ATTRIBUTE = "file";
    public static final String CLASS_ATTRIBUTE = "class";
    public static final String PATTERN_ATTRIBUTE = "pattern";
    public static final String SCOPE_ATTRIBUTE = "scope";

    public static final String ACTION_CLASS_ATTRIBUTE = "actionClass";

    public abstract void begin(InterpretationContext ic, String name, Attributes attributes) throws ActionException;

    public void body(InterpretationContext ic, String body) throws ActionException {
        // NOP
    }

    public abstract void end(InterpretationContext ic, String name) throws ActionException;

    public String toString() {
        return this.getClass().getName();
    }

    protected int getColumnNumber(InterpretationContext ic) {
        Interpreter ji = ic.getJoranInterpreter();
        Locator locator = ji.getLocator();
        if (locator != null) {
            return locator.getColumnNumber();
        }
        return -1;
    }

    protected int getLineNumber(InterpretationContext ic) {
        Interpreter ji = ic.getJoranInterpreter();
        Locator locator = ji.getLocator();
        if (locator != null) {
            return locator.getLineNumber();
        }
        return -1;
    }

    protected String getLineColStr(InterpretationContext ic) {
        return "line: " + getLineNumber(ic) + ", column: " + getColumnNumber(ic);
    }
}