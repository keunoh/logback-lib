package core.joran.spi;

import core.Context;
import core.spi.ContextAwareBase;
import core.spi.PropertyContainer;
import core.util.OptionHelper;
import org.xml.sax.Locator;

import java.util.*;

public class InterpretationContext extends ContextAwareBase implements PropertyContainer {

    Stack<Object> objectStack;
    Map<String, Object> objectMap;
    Map<String, String> propertiesMap;
    Interpreter joranInterpreter;
    final List<InPlayListener> listenerList = new ArrayList<>();

    DefaultNestedComponentRegistry defaultNestedComponentRegistry = new DefaultNestedComponentRegistry();

    public InterpretationContext(Context context, Interpreter joranInterpreter) {
        this.context = context;
        this.joranInterpreter = joranInterpreter;
        objectStack = new Stack<Object>();
        objectMap = new HashMap<String, Object>(5);
        propertiesMap = new HashMap<String, String>(5);
    }

    public DefaultNestedComponentRegistry getDefaultNestedComponentRegistry() {
        return defaultNestedComponentRegistry;
    }

    public Map<String, String> getCopyOfPropertyMap() {
        return new HashMap<String, String>(propertiesMap);
    }

    void setPropertiesMap(Map<String, String> propertiesMap) {
        this.propertiesMap = propertiesMap;
    }

    String updateLocationInfo(String msg) {
        Locator locator = joranInterpreter.getLocator();

        if (locator != null) {
            return msg + locator.getLineNumber() + ":" + locator.getColumnNumber();
        } else {
            return msg;
        }
    }

    public Locator getLocator() {
        return joranInterpreter.getLocator();
    }

    public Interpreter getJoranInterpreter() {
        return joranInterpreter;
    }

    public Stack<Object> getObjectStack() {
        return objectStack;
    }

    public boolean isEmpty() {
        return objectStack.isEmpty();
    }

    public Object peekObject() {
        return objectStack.peek();
    }

    public void pushObject(Object o) {
        objectStack.push(o);
    }

    public Object popObject() {
        return objectStack.pop();
    }

    public Object getObject(int i) {
        return objectStack.get(i);
    }

    public Map<String, Object> getObjectMap() {
        return objectMap;
    }

    public void addSubstitutionProperty(String key, String value) {
        if (key == null || value == null) {
            return;
        }
        value = value.trim();
        propertiesMap.put(key, value);
    }

    public void addSubstitutionProperty(Properties props) {
        if (props == null) {
            return;
        }
        for (Object keyObject : props.keySet()) {
            String key = (String) keyObject;
            String val = props.getProperty(key);
            addSubstitutionProperty(key, val);
        }
    }

    public String getProperty(String key) {
        String v = propertiesMap.get(key);
        if (v != null) {
            return v;
        } else {
            return context.getProperty(key);
        }
    }

    public String subst(String value) {
        if (value == null) {
            return null;
        }
        return OptionHelper.substVars(value, this, context);
    }

    public boolean isListenerListEmpty() {
        return listenerList.isEmpty();
    }

    public void addInPlayListener(InPlayListener ipl) {
        if (listenerList.contains(ipl)) {
            addWarn("InPlayListener " + ipl + " has been already registered");
        } else {
            listenerList.add(ipl);
        }
    }

    public boolean removeInPlayListener(InPlayListener ipl) {
        return listenerList.remove(ipl);
    }

    void fireInPlay(SaxEvent event) {
        for (InPlayListener ipl : listenerList) {
            ipl.inPlay(event);
        }
    }
}