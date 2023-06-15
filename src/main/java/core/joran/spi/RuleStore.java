package core.joran.spi;

import core.joran.action.Action;

import java.util.List;

public interface RuleStore {

    void addRule(ElementSelector elementSelector, String actionClassStr) throws ClassNotFoundException;

    void addRule(ElementSelector elementSelector, Action action);

    List<Action> matchActions(ElementPath elementPath);
}
