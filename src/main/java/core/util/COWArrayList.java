package core.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class COWArrayList<E> implements List<E> {

    AtomicBoolean fresh = new AtomicBoolean(false);
    CopyOnWriteArrayList<E> underlyingList = new CopyOnWriteArrayList<E>();
    E[] ourCopy;
    final E[] modelArray;

    public COWArrayList(E[] modelArray) {
        this.modelArray = modelArray;
    }

    @Override
    public int size() {
        return underlyingList.size();
    }

    @Override
    public boolean isEmpty() {
        return underlyingList.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return underlyingList.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return underlyingList.iterator();
    }

    private void refreshCopyIfNecessary() {
        if (!isFresh()) {
            refreshCopy();
        }
    }

    private boolean isFresh() {
        return fresh.get();
    }

    private void refreshCopy() {
        ourCopy = underlyingList.toArray(modelArray);
        fresh.set(true);
    }

    @Override
    public Object[] toArray() {
        refreshCopyIfNecessary();
        return ourCopy;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T[] toArray(T[] a) {
        refreshCopyIfNecessary();
        return (T[]) ourCopy;
    }

    /**
     * Return an array of type E[]. The returned array is intended to be iterated over.
     * If the list is modified, subsequent calls to this method will return different/modified
     * array instances.
     *
     * @return
     */
    public E[] asTypedArray() {
        refreshCopyIfNecessary();
        return ourCopy;
    }

    private void markAsStale() {
        fresh.set(false);
    }

    public void addIfAbsent(E e) {
        underlyingList.addIfAbsent(e);
        markAsStale();
    }

    @Override
    public boolean add(E e) {
        boolean result = underlyingList.add(e);
        markAsStale();
        return result;
    }

    @Override
    public boolean remove(Object o) {
        boolean result = underlyingList.remove(o);
        markAsStale();
        return result;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return underlyingList.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean result = underlyingList.addAll(c);
        markAsStale();
        return result;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> col) {
        boolean result = underlyingList.addAll(index, col);
        markAsStale();
        return result;
    }

    @Override
    public boolean removeAll(Collection<?> col) {
        boolean result = underlyingList.removeAll(col);
        markAsStale();
        return result;
    }

    @Override
    public boolean retainAll(Collection<?> col) {
        boolean result = underlyingList.retainAll(col);
        markAsStale();
        return result;
    }

    @Override
    public void clear() {
        underlyingList.clear();
        markAsStale();
    }

    @Override
    public E get(int index) {
        refreshCopyIfNecessary();
        return (E) ourCopy[index];
    }

    @Override
    public E set(int index, E element) {
        E e = underlyingList.set(index, element);
        markAsStale();
        return e;
    }

    @Override
    public void add(int index, E element) {
        underlyingList.add(index, element);
        markAsStale();
    }

    @Override
    public E remove(int index) {
        E e = (E) underlyingList.remove(index);
        markAsStale();
        return e;
    }

    @Override
    public int indexOf(Object o) {
        return underlyingList.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return underlyingList.lastIndexOf(o);
    }

    @Override
    public ListIterator<E> listIterator() {
        return underlyingList.listIterator();
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return underlyingList.listIterator(index);
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return underlyingList.subList(fromIndex, toIndex);
    }
}
