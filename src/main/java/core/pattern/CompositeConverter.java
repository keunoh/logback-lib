package core.pattern;

abstract public class CompositeConverter<E> extends DynamicConverter<E> {

    Converter<E> childConverter;

    public String convert(E event) {
        StringBuilder buf = new StringBuilder();

        for (Converter<E> c = childConverter; c != null; c = c.next) {
            c.write(buf, event);
        }
        String intermediary = buf.toString();
        return transform(event, intermediary);
    }

    abstract protected String transform(E event, String in);

    public Converter<E> getChildConverter() {
        return childConverter;
    }

    public void setChildConverter(Converter<E> child) {
        childConverter = child;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("CompositeConverter<");

        if (formattingInfo != null) {
            buf.append(formattingInfo);
        }

        if (childConverter != null) {
            buf.append(", children: ").append(childConverter);
        }
        buf.append(">");
        return buf.toString();
    }
}
