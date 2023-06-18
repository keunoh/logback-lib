package core.joran.conditional;

import core.spi.ContextAwareBase;
import core.spi.PropertyContainer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class PropertyEvalScriptBuilder extends ContextAwareBase {

    private static String SCRIPT_PREFIX = "" + "public boolean evaluate() { return ";
    private static String SCRIPT_SUFFIX = "" + "; }";

    final PropertyContainer localPropContainer;

    PropertyEvalScriptBuilder(PropertyContainer localPropContainer) {
        this.localPropContainer = localPropContainer;
    }

    Map<String, String> map = new HashMap<String, String>();

    public Condition build(String script) throws IllegalAccessException, CompileException, InstantiationException, SecurityException, NoSuchMethodException,
            IllegalArgumentException, InvocationTargetException {

        ClassBodyEvaluator cbe = new ClassBodyEvaluator();
        cbe.setImplementedInterfaces(new Class[] { Condition.class });
        cbe.setExtendedClass(PropertyWrapperForScripts.class);
        cbe.setParentClassLoader(ClassBodyEvaluator.class.getClassLoader());
        cbe.cook(SCRIPT_PREFIX + script + SCRIPT_SUFFIX);

        Class<?> clazz = cbe.getClazz();
        Condition instance = (Condition) clazz.newInstance();
        Method setMapMethod = clazz.getMethod("setPropertyContainers", PropertyContainer.class, PropertyContainer.class);
        setMapMethod.invoke(instance, localPropContainer, context);

        return instance;
    }
}
