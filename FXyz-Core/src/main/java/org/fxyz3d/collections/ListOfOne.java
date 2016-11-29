/**
 * ListOfOne.java
 *
 * Copyright (c) 2013-2016, F(X)yz
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *     * Neither the name of F(X)yz, any associated website, nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL F(X)yz BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 

package org.fxyz3d.collections;

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
