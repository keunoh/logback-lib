package core.html;

public interface IThrowableRenderer<E> {
    void render(StringBuilder sbuf, E event);
}
