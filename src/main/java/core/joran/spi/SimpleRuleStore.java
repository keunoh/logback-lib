package core.joran.spi;

import core.Context;
import core.joran.action.Action;
import core.spi.ContextAwareBase;
import core.util.OptionHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SimpleRuleStore extends ContextAwareBase implements RuleStore {

    static String KLEENE_STAR = "*";
    HashMap<ElementSelector, List<Action>> rules = new HashMap<>();

    public SimpleRuleStore(Context context) {
        setContext(context);
    }

    public void addRule(ElementSelector elementSelector, Action action) {
        action.setContext(context);

        List<Action> a4p = rules.get(elementSelector);

        if (a4p == null) {
            a4p = new ArrayList<>();
            rules.put(elementSelector, a4p);
        }

        a4p.add(action);
    }

    public void addRule(ElementSelector elementSelector, String actionClassName) {
        Action action = null;

        try {
            action = (Action) OptionHelper.instantiateByClassName(actionClassName, Action.class, context);
        } catch (Exception e) {
            addError("Could not instantiate class [" + actionClassName + "]", e);
        }
        if (action != null) {
            addRule(elementSelector, action);
        }
    }

    public List<Action> matchActions(ElementPath elementPath) {
        List<Action> actionList;

        if ((actionList = fullPathMatch(elementPath)) != null) {
            return actionList;
        } else if ((actionList = suffixMatch(elementPath)) != null) {
            return actionList;
        } else if ((actionList = prefixMatch(elementPath)) != null) {
            return actionList;
        } else if ((actionList = middleMatch(elementPath)) != null) {
            return actionList;
        } else {
            return null;
        }
    }

    List<Action> fullPathMatch(ElementPath elementPath) {
        for (ElementSelector selector : rules.keySet()) {
            if (selector.fullPathMatch(elementPath))
                return rules.get(selector);
        }
        return null;
    }

    List<Action> suffixMatch(ElementPath elementPath) {
        int max = 0;
        ElementSelector longestMatchingElementSelector = null;

        for (ElementSelector selector : rules.keySet()) {
            if (isSuffixPattern(selector)) {
                int r = selector.getTailMatchLength(elementPath);
                if (r > max) {
                    max = r;
                    longestMatchingElementSelector = selector;
                }
            }
        }

        if (longestMatchingElementSelector != null) {
            return rules.get(longestMatchingElementSelector);
        } else {
            return null;
        }
    }

    private boolean isSuffixPattern(ElementSelector p) {
        return (p.size() > 1) && p.get(0).equals(KLEENE_STAR);
    }

    List<Action> prefixMatch(ElementPath elementPath) {
        int max = 0;
        ElementSelector longestMatchingElementSelector = null;

        for (ElementSelector selector : rules.keySet()) {
            String last = selector.peekLast();
            if (isKleeneStar(last)) {
                int r = selector.getPrefixMatchLength(elementPath);
                // to qualify the match length must equal p's size omitting the '*'
                if ((r == selector.size() - 1) && (r > max)) {
                    max = r;
                    longestMatchingElementSelector = selector;
                }
            }
        }

        if (longestMatchingElementSelector != null) {
            return rules.get(longestMatchingElementSelector);
        } else {
            return null;
        }
    }

    private boolean isKleeneStar(String last) {
        return KLEENE_STAR.equals(last);
    }

    List<Action> middleMatch(ElementPath path) {

        int max = 0;
        ElementSelector longestMatchingElementSelector = null;

        for (ElementSelector selector : rules.keySet()) {
            String last = selector.peekLast();
            String first = null;
            if (selector.size() > 1) {
                first = selector.get(0);
            }
            if (isKleeneStar(last) && isKleeneStar(first)) {
                List<String> copyOfPartList = selector.getCopyOfPartList();
                if (copyOfPartList.size() > 2) {
                    copyOfPartList.remove(0);
                    copyOfPartList.remove(copyOfPartList.size() - 1);
                }

                int r = 0;
                ElementSelector clone = new ElementSelector(copyOfPartList);
                if (clone.isContainedIn(path)) {
                    r = clone.size();
                }
                if (r > max) {
                    max = r;
                    longestMatchingElementSelector = selector;
                }
            }
        }

        if (longestMatchingElementSelector != null) {
            return rules.get(longestMatchingElementSelector);
        } else {
            return null;
        }
    }

    public String toString() {
        final String TAB = "  ";

        StringBuilder retValue = new StringBuilder();

        retValue.append("SimpleRuleStore ( ").append("rules = ").append(this.rules).append(TAB).append(" )");

        return retValue.toString();
    }
}
























