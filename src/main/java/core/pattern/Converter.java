package core.pattern;

abstract public class Converter<E> {

    Converter<E> next;

    public abstract String convert(E event);

    public void write(StringBuilder buf, E event) {
        buf.append(convert(event));
    }

    public final void setNext(Converter<E> next) {
        if (this.next != null) {
            throw new IllegalStateException("Next converter has been already set");
        }
        this.next = next;
    }

    public final Converter<E> getNext() {
        return next;
    }
}
