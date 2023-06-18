package core.joran.conditional;

import core.joran.event.SaxEvent;

import java.util.List;

public class ElseAction extends ThenOrElseActionBase {
    @Override
    void registerEventList(IfAction ifAction, List<SaxEvent> eventList) {
        ifAction.setElseSaxEventList(eventList);

    }
}
