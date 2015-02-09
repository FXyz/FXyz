/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fxyz.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * This is an immutable list of some arbitrary size backed by a single element.
 * @author brian
 * @param <E> The type of element in the list
 */
public class ListOfOne<E> implements List<E> {

    private final int size;
    private final E element;
    
    public ListOfOne(E element, int size) {
        if (element == null) {
            throw new IllegalArgumentException("A null element is not allowed.");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("The list must contain at least one element");
        }
        this.element = element;
        this.size = size;
    }
    

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean contains(Object o) {
        return element.equals(o);
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            
            private int n = 0;

            @Override
            public boolean hasNext() {
                return n < (size - 1);
            }

            @Override
            public E next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                n++;
                return element;
            }
            
        };
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public boolean add(E e) {
        throw new UnsupportedOperationException("Adding elements is not allowed");
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("Removing elements is not allowed");
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        boolean yes = true;
        for (Object item : c) {
            if (!item.equals(element)) {
                yes = false;
                break;
            }
        }
        return yes;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public E get(int index) {
        if (index < size) {
            return element;
        }
        else {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
    }

    @Override
    public E set(int index, E element) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void add(int index, E element) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public E remove(int index) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public int indexOf(Object o) {
        int v = -1;
        if (element.equals(o)) {
            v = 0;
        }
        return v;
    }

    @Override
    public int lastIndexOf(Object o) {
        int v = -1;
        if (element.equals(o)) {
            v = size - 1;
        }
        return v;
    }

    @Override
    public ListIterator<E> listIterator() {
        return listIterator(0);
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return new ListIterator<E>() {
            
            private final int s = size - index;
            private int n = 0;

            @Override
            public boolean hasNext() {
                return n < (s - 1);
            }

            @Override
            public E next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
               n++;
               return element;
            }

            @Override
            public boolean hasPrevious() {
                return n >= 0;
            }

            @Override
            public E previous() {
                if (!hasPrevious()) {
                    throw new NoSuchElementException();
                }
                n--;
                return element;
            }

            @Override
            public int nextIndex() {
                return n + 1;
            }

            @Override
            public int previousIndex() {
                return n - 1;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Not supported yet."); 
            }

            @Override
            public void set(E e) {
                throw new UnsupportedOperationException("Not supported yet."); 
            }

            @Override
            public void add(E e) {
                throw new UnsupportedOperationException("Not supported yet."); 
            }
        };
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return new ListOfOne<>(element, toIndex - fromIndex);
    }
    
}
