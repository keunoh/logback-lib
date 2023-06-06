package core.pattern.parser;

import core.pattern.CompositeConverter;
import core.pattern.Converter;
import core.pattern.DynamicConverter;
import core.pattern.LiteralConverter;
import core.spi.ContextAwareBase;
import core.status.ErrorStatus;
import core.util.OptionHelper;

import java.util.Map;

public class Compiler<E> extends ContextAwareBase {

    Converter<E> head;
    Converter<E> tail;
    final Node top;
    final Map converterMap;

    Compiler(final Node top, final Map converterMap) {
        this.top = top;
        this.converterMap = converterMap;
    }

    Converter<E> compile() {
        head = tail = null;
        for (Node n = top; n != null; n = n.next) {
            switch (n.type) {
                case Node.LITERAL:
                    addToList(new LiteralConverter<E>((String) n.getValue()));
                    break;
                case Node.COMPOSITE_KEYWORD:
                    CompositeNode cn = (CompositeNode) n;
                    CompositeConverter<E> compositeConverter = createCompositeConverter(cn);
                    if (compositeConverter == null) {
                        addError("Failed to create converter for [%" + cn.getValue() + "] keyword");
                        addToList(new LiteralConverter<E>("%PARSER_ERROR[" + cn.getValue() + "]"));
                        break;
                    }
                    compositeConverter.setFormattingInfo(cn.getFormatInfo());
                    compositeConverter.setOptionList(cn.getOptions());
                    Compiler<E> childCompiler = new Compiler<E>(cn.getChildNode(), converterMap);
                    childCompiler.setContext(context);
                    Converter<E> childConverter = childCompiler.compile();
                    compositeConverter.setChildConverter(childConverter);
                    addToList(compositeConverter);
                    break;
                case Node.SIMPLE_KEYWORD:
                    SimpleKeywordNode kn = (SimpleKeywordNode) n;
                    DynamicConverter<E> dynaConverter = createConverter(kn);
                    if (dynaConverter != null) {
                        dynaConverter.setFormattingInfo(kn.getFormatInfo());
                        dynaConverter.setOptionList(kn.getOptions());
                        addToList(dynaConverter);
                    } else {
                        // if the appropriate dynaconverter cannot be found, then replace
                        // it with a dummy LiteralConverter indicating an error.
                        Converter<E> errConveter = new LiteralConverter<E>("%PARSER_ERROR[" + kn.getValue() + "]");
                        addStatus(new ErrorStatus("[" + kn.getValue() + "] is not a valid conversion word", this));
                        addToList(errConveter);
                    }

            }
        }
        return head;
    }

    private void addToList(Converter<E> c) {
        if (head == null)
            head = tail = c;
        else {
            tail.setNext(c);
            tail = c;
        }
    }

    @SuppressWarnings("unchecked")
    DynamicConverter<E> createConverter(SimpleKeywordNode kn) {
        String keyword = (String) kn.getValue();
        String converterClassStr = (String) converterMap.get(keyword);

        if (converterClassStr != null) {
            try {
                return (DynamicConverter) OptionHelper.instantiateByClassName(converterClassStr, DynamicConverter.class, context);
            } catch (Exception e) {
                addError("Failed to instantiate converter class [" + converterClassStr + "] for keyword [" + keyword + "]", e);
                return null;
            }
        } else {
            addError("There is no conversion class registered for conversion word [" + keyword + "]");
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    CompositeConverter<E> createCompositeConverter(CompositeNode cn) {
        String keyword = (String) cn.getValue();
        String converterClassStr = (String) converterMap.get(keyword);

        if (converterClassStr != null) {
            try {
                return (CompositeConverter) OptionHelper.instantiateByClassName(converterClassStr, CompositeConverter.class, context);
            } catch (Exception e) {
                addError("Failed to instantiate converter class [" + converterClassStr + "] as a composite converter for keyword [" + keyword + "]", e);
                return null;
            }
        } else {
            addError("There is no conversion class registered for composite conversion word [" + keyword + "]");
            return null;
        }
    }
}