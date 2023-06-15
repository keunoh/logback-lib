package core.joran;

import core.Appender;
import core.joran.spi.ElementSelector;
import core.joran.spi.InterpretationContext;
import core.joran.spi.Interpreter;
import core.joran.spi.RuleStore;

import java.util.HashMap;
import java.util.Map;

abstract public class JoranConfiguratorBase<E> extends GenericConfigurator {

    @Override
    protected void addInstanceRules(RuleStore rs) {
        // is "configuration/variable" referenced in the docs?
        rs.addRule(new ElementSelector("configuration/variable"), new PropertyAction());
        rs.addRule(new ElementSelector("configuration/property"), new PropertyAction());

        rs.addRule(new ElementSelector("configuration/substitutionProperty"), new PropertyAction());

        rs.addRule(new ElementSelector("configuration/timestamp"), new TimestampAction());
        rs.addRule(new ElementSelector("configuration/shutdownHook"), new ShutdownHookAction());
        rs.addRule(new ElementSelector("configuration/define"), new DefinePropertyAction());

        // the contextProperty pattern is deprecated. It is undocumented
        // and will be dropped in future versions of logback
        rs.addRule(new ElementSelector("configuration/contextProperty"), new ContextPropertyAction());

        rs.addRule(new ElementSelector("configuration/conversionRule"), new ConversionRuleAction());

        rs.addRule(new ElementSelector("configuration/statusListener"), new StatusListenerAction());

        rs.addRule(new ElementSelector("configuration/appender"), new AppenderAction<E>());
        rs.addRule(new ElementSelector("configuration/appender/appender-ref"), new AppenderRefAction<E>());
        rs.addRule(new ElementSelector("configuration/newRule"), new NewRuleAction());
        rs.addRule(new ElementSelector("*/param"), new ParamAction(getBeanDescriptionCache()));
    }

    @Override
    protected void addImplicitRules(Interpreter interpreter) {
        // The following line adds the capability to parse nested components
        NestedComplexPropertyIA nestedComplexPropertyIA = new NestedComplexPropertyIA(getBeanDescriptionCache());
        nestedComplexPropertyIA.setContext(context);
        interpreter.addImplicitAction(nestedComplexPropertyIA);

        NestedBasicPropertyIA nestedBasicIA = new NestedBasicPropertyIA(getBeanDescriptionCache());
        nestedBasicIA.setContext(context);
        interpreter.addImplicitAction(nestedBasicIA);
    }

    @Override
    protected void buildInterpreter() {
        super.buildInterpreter();
        Map<String, Object> omap = interpreter.getInterpretationContext().getObjectMap();
        omap.put(ActionConst.APPENDER_BAG, new HashMap<String, Appender<?>>());
    }

    public InterpretationContext getInterpretationContext() {
        return interpreter.getInterpretationContext();
    }
}
