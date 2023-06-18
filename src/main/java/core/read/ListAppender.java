package core.read;

import core.AppenderBase;

import java.util.ArrayList;
import java.util.List;

public class ListAppender<E> extends AppenderBase<E> {
    public List<E> list = new ArrayList<E>();

    protected void append(E e) {
        list.add(e);
    }
}
