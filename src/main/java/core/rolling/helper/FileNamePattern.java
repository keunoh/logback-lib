package core.rolling.helper;

import core.Context;
import core.pattern.Converter;
import core.pattern.ConverterUtil;
import core.pattern.LiteralConverter;
import core.pattern.parser.Node;
import core.pattern.parser.Parser;
import core.pattern.util.AlmostAsIsEscapeUtil;
import core.spi.ContextAwareBase;
import core.spi.ScanException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FileNamePattern extends ContextAwareBase {

    static final Map<String, String> CONVERTER_MAP = new HashMap<>();
    static {
        CONVERTER_MAP.put(IntegerTokenConverter.CONVERTER_KEY, IntegerTokenConverter.class.getName());
    }

    String pattern;
    Converter<Object> headTokenConverter;

    public FileNamePattern(String patternArg, Context contextArg) {
        // the pattern is slashified
        setPattern(FileFilterUtil.slashify(patternArg));
        setContext(contextArg);
        parse();
        ConverterUtil.startConverters(this.headTokenConverter);
    }

    void parse() {
        try {
            String patternForParsing = escapeRightParenthesis(pattern);
            Parser<Object> p = new Parser<>(patternForParsing, new AlmostAsIsEscapeUtil());
            p.setContext(context);
            Node t = p.parse();
            this.headTokenConverter = p.compile(t, CONVERTER_MAP);
        } catch (ScanException sce) {
            addError("Failed to parse pattern \"" + pattern + "\".", sce);
        }
    }

    String escapeRightParenthesis(String in) {
        return pattern.replace(")", "\\)");
    }

    public String toString() {
        return pattern;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((pattern == null) ? 0 : pattern.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FileNamePattern other = (FileNamePattern) obj;
        if (pattern == null) {
            if (other.pattern != null)
                return false;
        } else if (!pattern.equals(other.pattern))
            return false;
        return true;
    }

    public DateTokenConverter<Object> getPrimaryDateTokenConverter() {
        Converter<Object> p = headTokenConverter;

        while (p != null) {
            if (p instanceof DateTokenConverter) {
                DateTokenConverter<Object> dtc = (DateTokenConverter<Object>) p;
                // only primary converters should be returned as
                if (dtc.isPrimary())
                    return dtc;
            }

            p = p.getNext();
        }

        return null;
    }

    public IntegerTokenConverter getIntegerTokenConverter() {
        Converter<Object> p = headTokenConverter;

        while (p != null) {
            if (p instanceof IntegerTokenConverter) {
                return (IntegerTokenConverter) p;
            }

            p = p.getNext();
        }
        return null;
    }

    public boolean hasIntegerTokenConverter() {
        IntegerTokenConverter itc = getIntegerTokenConverter();
        return itc != null;
    }

    public String convertMultipleArguments(Object... objectList) {
        StringBuilder buf = new StringBuilder();
        Converter<Object> c = headTokenConverter;
        while (c != null) {
            if (c instanceof MonoTypedConverter) {
                MonoTypedConverter monoTyped = (MonoTypedConverter) c;
                for (Object o : objectList) {
                    if (monoTyped.isApplicable(o)) {
                        buf.append(c.convert(o));
                    }
                }
            } else {
                buf.append(c.convert(objectList));
            }
            c = c.getNext();
        }
        return buf.toString();
    }

    public String convert(Object o) {
        StringBuilder buf = new StringBuilder();
        Converter<Object> p = headTokenConverter;
        while (p != null) {
            buf.append(p.convert(o));
            p = p.getNext();
        }
        return buf.toString();
    }

    public String convertInt(int i) {
        return convert(i);
    }

    public void setPattern(String pattern) {
        if (pattern != null) {
            // Trailing spaces in the pattern are assumed to be undesired.
            this.pattern = pattern.trim();
        }
    }

    public String getPattern() {
        return pattern;
    }

    public String toRegexForFixedDate(Date date) {
        StringBuilder buf = new StringBuilder();
        Converter<Object> p = headTokenConverter;
        while (p != null) {
            if (p instanceof LiteralConverter) {
                buf.append(p.convert(null));
            } else if (p instanceof IntegerTokenConverter) {
                buf.append("(\\d{1,5})");
            } else if (p instanceof DateTokenConverter) {
                buf.append(p.convert(date));
            }
            p = p.getNext();
        }
        return buf.toString();
    }

    public String toRegex() {
        StringBuilder buf = new StringBuilder();
        Converter<Object> p = headTokenConverter;
        while (p != null) {
            if (p instanceof LiteralConverter) {
                buf.append(p.convert(null));
            } else if (p instanceof IntegerTokenConverter) {
                buf.append("\\d{1,2}");
            } else if (p instanceof DateTokenConverter) {
                DateTokenConverter<Object> dtc = (DateTokenConverter<Object>) p;
                buf.append(dtc.toRegex());
            }
            p = p.getNext();
        }
        return buf.toString();
    }
}

























