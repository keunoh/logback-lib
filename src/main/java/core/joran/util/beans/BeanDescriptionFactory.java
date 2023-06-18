package core.joran.util.beans;

import core.Context;
import core.spi.ContextAwareBase;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class BeanDescriptionFactory extends ContextAwareBase {

    BeanDescriptionFactory(Context context) {
        setContext(context);
    }

    public BeanDescription create(Class<?> clazz) {
        Map<String, Method> propertyNameToGetter = new HashMap<String, Method>();
        Map<String, Method> propertyNameToSetter = new HashMap<String, Method>();
        Map<String, Method> propertyNameToAdder = new HashMap<String, Method>();
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (method.isBridge()) {
                continue;
            }
            if (BeanUtil.isGetter(method)) {
                String propertyName = BeanUtil.getPropertyName(method);
                Method oldGetter = propertyNameToGetter.put(propertyName, method);
                if (oldGetter != null) {
                    if (oldGetter.getName().startsWith(BeanUtil.PREFIX_GETTER_IS)) {
                        propertyNameToGetter.put(propertyName, oldGetter);
                    }
                    String message = String.format("Class '%s' contains multiple getters for the same property '%s'.", clazz.getCanonicalName(), propertyName);
                    addWarn(message);
                }
            } else if (BeanUtil.isSetter(method)) {
                String propertyName = BeanUtil.getPropertyName(method);
                Method oldSetter = propertyNameToSetter.put(propertyName, method);
                if (oldSetter != null) {
                    String message = String.format("Class '%s' contains multiple setters for the same property '%s'.", clazz.getCanonicalName(), propertyName);
                    addWarn(message);
                }
            } else if (BeanUtil.isAdder(method)) {
                String propertyName = BeanUtil.getPropertyName(method);
                Method oldAdder = propertyNameToAdder.put(propertyName, method);
                if (oldAdder != null) {
                    String message = String.format("Class '%s' contains multiple adders for the same property '%s'.", clazz.getCanonicalName(), propertyName);
                    addWarn(message);
                }
            }
        }
        return new BeanDescription(clazz, propertyNameToGetter, propertyNameToSetter, propertyNameToAdder);
    }
}
