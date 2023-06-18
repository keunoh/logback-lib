package core.joran.conditional;

import core.joran.action.Action;
import core.joran.event.InPlayListener;
import core.joran.event.SaxEvent;
import core.joran.spi.ActionException;
import core.joran.spi.InterpretationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.jar.Attributes;

public class ThenOrElseActionBase extends Action {
    Stack<ThenActionState> stateStack = new Stack<>();

    @Override
    public void begin(InterpretationContext ic, String name, Attributes attributes) throws ActionException {
        if (!weAreActive(ic))
            return;

        ThenActionState state = new ThenActionState();
        if (ic.isListenerListEmpty()) {
            ic.addInPlayListener(state);
            state.isRegistered = true;
        }
        stateStack.push(state);
    }

    boolean weAreActive(InterpretationContext ic) {
        Object o = ic.peekObject();
        if (!(o instanceof IfAction))
            return false;
        IfAction ifAction = (IfAction) o;
        return ifAction.isActive();
    }

    @Override
    public void end(InterpretationContext ic, String name) throws ActionException {
        if (!weAreActive(ic))
            return;

        ThenActionState state = stateStack.pop();
        if (state.isRegistered) {
            ic.removeInPlayListener(state);
            Object o = ic.peekObject();
            if (o instanceof IfAction) {
                IfAction ifAction = (IfAction) o;
                removeFirstAndLastFromList(state.eventList);
                registerEventList(ifAction, state.eventList);
            } else {
                throw new IllegalStateException("Missing IfAction on top of stack");
            }
        }
    }

    abstract void registerEventList(IfAction ifAction, List<SaxEvent> eventList);

    void removeFirstAndLastFromList(List<SaxEvent> eventList) {
        eventList.remove(0);
        eventList.remove(eventList.size() - 1);
    }
}

class ThenActionState implements InPlayListener {

    List<SaxEvent> eventList = new ArrayList<SaxEvent>();
    boolean isRegistered = false;

    public void inPlay(SaxEvent event) {
        eventList.add(event);
    }
}
