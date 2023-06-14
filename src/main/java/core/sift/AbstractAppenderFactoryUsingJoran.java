package core.sift;

import core.Appender;
import core.Context;
import core.joran.spi.JoranException;

import java.util.List;
import java.util.Map;

public abstract class AbstractAppenderFactoryUsingJoran<E> implements AppenderFactory<E> {

    final List<SaxEvent> eventList;
    protected String key;
    protected Map<String, String> parentPropertyMap;

    public AbstractAppenderFactoryUsingJoran(List<SaxEvent> eventList, String key, Map<String, String> parentPropertyMap) {
        this.eventList = removeSiftElement(eventList);
        this.key = key;
        this.parentPropertyMap = parentPropertyMap;
    }

    List<SaxEvent> removeSiftElement(List<SaxEvent> eventList) {
        return eventList.subList(1, eventList.size() - 1);
    }

    public abstract SiftingJoranConfiguratorBase<E> getSiftingJoranConfigurator(String k);

    public Appender<E> buildAppender(Context context, String discriminatingValue) throws JoranException {
        SiftingJoranConfiguratorBase<E> sjc = getSiftingJoranConfigurator(discriminatingValue);
        sjc.setContext(context);
        sjc.doConfigure(eventList);
        return sjc.getAppender();
    }

    public List<SaxEvent> getEventList() {
        return eventList;
    }
}
