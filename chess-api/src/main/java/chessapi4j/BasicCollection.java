/*
 * Copyright 2025 Miguel Angel Luna Lobos
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://github.com/lunalobos/chessapi4j/blob/master/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package chessapi4j;

import java.util.Collection;
import java.util.Iterator;

/**
 * An experimental class. It is intended to be used in a future release.
 * 
 * @author lunalobos
 * @since 1.2.3
 */
class BasicCollection<T> implements Collection<T> {

    private Node<T> head;
    private Node<T> current;
    private int size;

    public BasicCollection() {
        head = new Node<T>();
        current = head;
        size = 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return head.getNext() == null;
    }

    @Override
    public boolean contains(Object o) {
        Node<T> currentNode = head.getNext();
        while (currentNode != null) {
            if ((o == null && currentNode.getValue() == null) ||
                    (o != null && o.equals(currentNode.getValue()))) {
                return true;
            }
            currentNode = currentNode.getNext();
        }
        return false;
    }

    @Override
    public Iterator<T> iterator() {
        return new BasicIterator<>(head);
    }

    @Override
    public Object[] toArray() {
        Object[] array = new Object[size];
        Node<T> currentNode = head.getNext();
        int index = 0;
        while (currentNode != null) {
            array[index++] = currentNode.getValue();
            currentNode = currentNode.getNext();
        }
        return array;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <U> U[] toArray(U[] a) {
        if (a.length < size) {
            a = (U[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size);
        }
        Node<T> currentNode = head.getNext();
        int index = 0;
        while (currentNode != null) {
            a[index++] = (U) currentNode.getValue();
            currentNode = currentNode.getNext();
        }
        if (a.length > size) {
            a[size] = null;
        }
        return a;
    }

    @Override
    public boolean add(T e) {
        Node<T> node = new Node<>(e);
        node.setPrev(current);
        current.setNext(node);
        current = node;
        size++;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        Node<T> currentNode = head.getNext();
        while (currentNode != null) {
            if ((o == null && currentNode.getValue() == null) ||
                    (o != null && o.equals(currentNode.getValue()))) {

                Node<T> prevNode = currentNode.getPrev();
                Node<T> nextNode = currentNode.getNext();

                if (prevNode != null) {
                    prevNode.setNext(nextNode);
                }
                if (nextNode != null) {
                    nextNode.setPrev(prevNode);
                }

                if (currentNode == current) {
                    current = prevNode;
                }

                size--;
                return true;
            }
            currentNode = currentNode.getNext();
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        Iterator<?> iterator = c.iterator();
        while (iterator.hasNext()) {
            if (!contains(iterator.next())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        Iterator<? extends T> iterator = c.iterator();
        while (iterator.hasNext()) {
            add(iterator.next());
        }
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        Iterator<?> iterator = c.iterator();
        while (iterator.hasNext()) {
            remove(iterator.next());
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean retainAll(Collection<?> c) {
        var newCollection = new BasicCollection<T>();
        Iterator<?> iterator = c.iterator();
        while (iterator.hasNext()) {
            if(contains(iterator.next())) {
                newCollection.add((T)iterator.next());
            }
        }
        clear();
        head = newCollection.head;
        current = head;
        size = newCollection.size;
        return true;
    }

    @Override
    public void clear() {
        head.setNext(null);
        current = head;
        size = 0;
    }

}

class Node<T> {
    private T value;
    private Node<T> next;
    private Node<T> prev;

    public Node() {
    }

    public Node(T value) {
        this.value = value;
    }

    public void setNext(Node<T> next) {
        this.next = next;
    }

    public void setPrev(Node<T> prev) {
        this.prev = prev;
    }

    public T getValue() {
        return value;
    }

    public Node<T> getNext() {
        return next;
    }

    public Node<T> getPrev() {
        return prev;
    }

    public void setValue(T value) {
        this.value = value;
    }
}

class BasicIterator<T> implements Iterator<T> {
    private Node<T> current;

    public BasicIterator(Node<T> head) {
        current = head;
    }

    @Override
    public boolean hasNext() {
        return current.getNext() != null;
    }

    @Override
    public T next() {
        T value = current.getValue();
        current = current.getNext();
        return value;
    }
}