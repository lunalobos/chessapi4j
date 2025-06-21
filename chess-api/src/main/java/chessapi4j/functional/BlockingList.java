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
package chessapi4j.functional;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 * @author lunalobos
 * @since 1.2.9
 */
class BlockingList<E> extends AbstractList<E> implements Deque<E>, RandomAccess, Cloneable {
    private static final Object[] EMPTY_DATA = new Object[0];
    private static final int[] EMPTY_ROTATIONS = new int[0];

    private static final int[] SHIFTS = {
            0, 0, 0, 0, 2, 2, 2, 3,
            4, 5, 6, 7, 8, 9, 10, 10,
            11, 11, 12, 12, 13, 13, 14, 14,
            15, 15, 16, 16, 16, 16, 16, 16
    };

    private static final int MIN_SIZE = 16; // Minimum is set at 4 blocks of 4 elements each

    private E[] data;
    private int[] rotations;

    private int size;
    private int offset; // global rotation offset, ranges from 0 to data.length - 1
    private int shift; // block size as a power of 2, ranges from 2 to 16 (4 to 65336 bytes)
    private volatile boolean blocked;

    /**
     * Constructs a new instance containing the elements of the given collection
     * in the order returned by the collection.
     *
     * @param collection a collection to use for initial elements, cannot be
     *                   {@code null}
     * @throws NullPointerException if any argument is {@code null}
     */
    public BlockingList(Collection<? extends E> collection) {
        blocked = false;
        int size = collection.size();

        if (size == 0) {
            clear();
        } else {
            int allocationSize = Math.max(MIN_SIZE, Integer.highestOneBit(size - 1) << 1);

            this.shift = SHIFTS[Integer.numberOfTrailingZeros(allocationSize)];

            @SuppressWarnings("unchecked")
            E[] array = (E[]) new Object[allocationSize];

            collection.toArray(array); // ignore return value, if it doesn't work per specification for the given
            // collection, then just break

            this.data = array;
            this.rotations = new int[allocationSize >> shift];
            this.size = size;
        }
    }

    /**
     * Constructs a new instance with no elements.
     */
    public BlockingList() {
        blocked = false;
        clear();
    }

    /**
     * Makes this list immutable
     */
    BlockingList<E> block(){
        blocked = true;
        return this;
    }

    void checkBlocked() {
        if (blocked) {
            throw new UnsupportedOperationException("this list is immutable");
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public E get(int index) {
        Objects.checkIndex(index, size);

        int blockMask = (1 << shift) - 1;
        int mask = data.length - 1;
        int position = (index + offset) & mask;
        int block = position >> shift;
        int baseIndex = position & ~blockMask;

        return data[baseIndex | ((rotations[block] + position) & blockMask)];
    }

    @Override
    public void add(int index, E element) {
        checkBlocked();
        final int size = this.size;

        if (index == size) {
            addLast(element);

            return;
        }

        if (index == 0) {
            addFirst(element);

            return;
        }

        Objects.checkIndex(index, size + 1);

        modCount++;

        if (shouldIncreaseCapacity()) {
            doubleCapacity(); // modifies data, rotations, shift, offset
        }

        final int BLOCK_SHIFT = shift;
        final int BLOCK_SIZE = 1 << shift;
        final int BLOCK_MASK = BLOCK_SIZE - 1;

        final int[] rotations = this.rotations;
        final E[] data = this.data;
        final boolean insertNearEnd = index >= size >> 1;
        final int position;
        final int insertionBlock;
        final int rotationsMask = rotations.length - 1;

        int writeRotation;
        int gapIndex;

        if (insertNearEnd) { // starting from last block, copy last element from previous block to first
            // element of next block
            position = toPosition(index);

            int writeBlock = toPosition(size) >> BLOCK_SHIFT;

            insertionBlock = position >> BLOCK_SHIFT;
            writeRotation = (rotations[writeBlock] - 1) & BLOCK_MASK;
            gapIndex = (writeBlock << BLOCK_SHIFT) | writeRotation;

            while (writeBlock != insertionBlock) {
                int readBlock = (writeBlock - 1) & rotationsMask;
                int readRotation = (rotations[readBlock] - 1) & BLOCK_MASK; // last element
                int readIndex = (readBlock << BLOCK_SHIFT) | readRotation;

                rotations[writeBlock] = writeRotation; // shift right
                data[gapIndex] = data[readIndex]; // copy last element from previous block to first element of next

                writeBlock = readBlock;
                writeRotation = readRotation;
                gapIndex = readIndex;
            }
        } else { // starting from first block, copy first element from next block to last element
            // of previous block
            moveOffsetLeft(); // make space at start for one element (will influence result of toPosition)

            position = toPosition(index);

            int writeBlock = toPosition(0) >> BLOCK_SHIFT;

            insertionBlock = position >> BLOCK_SHIFT;
            writeRotation = rotations[writeBlock];
            gapIndex = (writeBlock << BLOCK_SHIFT) | writeRotation;

            while (writeBlock != insertionBlock) {
                int readBlock = (writeBlock + 1) & rotationsMask;
                int readRotation = rotations[readBlock]; // first element
                int readIndex = (readBlock << BLOCK_SHIFT) | readRotation;

                rotations[writeBlock] = (writeRotation + 1) & BLOCK_MASK; // shift left
                data[gapIndex] = data[readIndex]; // copy first element of next block to last element of previous

                writeBlock = readBlock;
                writeRotation = readRotation;
                gapIndex = readIndex;
            }
        }

        /*
         * After shifting of all preceding or succeeding blocks has completed, the
         * element
         * must be inserted still in the target block. This requires a traditional
         * moving
         * of elements within the target block. The location of the gap in the block
         * depends
         * on which direction elements were shifted, and the block's rotation. It can
         * either
         * be a gap representing the lowest element in the block or the highest.
         * Elements
         * can be either shifted left or right, possibly with an update to the blocks
         * rotation to shift as few elements as possible. The parameters are:
         *
         * - Is the gap representing the lowest or highest element (given by
         * insertNearEnd)
         * - Is the gap less than 0.5 * BLOCK_SIZE elements away from the insertion
         * position
         * - Is the gap physically located before or after the insertion position (this
         * differs from the external index location due to the block's rotation)
         * - Shift elements either left or right (circular shifts are used)
         *
         * The above parameters result in 16 possible cases, 8 of which will shift more
         * elements than needed. Discarding those leaves 8 cases, 4 of which are optimal
         * by shifting left, and the remaining 4 are optimal by shifting right.
         */

        int blockBase = insertionBlock << BLOCK_SHIFT;
        int insertIndex = blockBase | ((writeRotation + position + 1) & BLOCK_MASK);
        int distance = (gapIndex - insertIndex) & BLOCK_MASK;

        if (distance < BLOCK_SIZE >> 1) { // shift right
            int length = distance;

            /*
             * A: A high gap is at the end, but far
             * [ . a b c d e f:g ] -- insert between f and g, but gap (h) is far
             * [ a b c d e f : g ] -- 1: shift left, rotation is reduced by one; many
             * elements moved
             * [ g a b c d e f : ] -- 2: shift right, no rotation change; few elements moved
             * <-- PREFERRED
             * B: A high gap is at the end, and near
             * [ a b c d e f:g . ] -- insert between f and g, gap (h) is near
             * [ b c d e f : g a ] -- 1: shift left, reduce rotation by one; many elements
             * moved
             * [ a b c d e f : g ] -- 2: shift right, no rotation change; few elements moved
             * <-- PREFERRED
             * C: A low gap is at the beginning, but far
             * [ . b c d e f g:h ] -- insert between g and h, but gap (a) is far
             * [ b c d e f g : h ] -- 1: shift left, no rotation change; many elements moved
             * [ h b c d e f g : ] -- 2: shift right, rotation increased by one; few
             * elements moved <-- PREFERRED
             * D: A low gap is at the beginning, and near
             * [ b c d e f:g h . ] -- insert between f and g, gap (a) is near
             * [ c d e f : g h b ] -- 1: shift left, no rotation change; many elements moved
             * [ b c d e f : g h ] -- 2: shift right, rotation increased by one; few
             * elements moved <-- PREFERRED
             */

            circularShiftRight(data, blockBase, BLOCK_SIZE, insertIndex, length);

            if (!insertNearEnd) { // if gap was a low index, then right shift moved a high index element in the
                // gap; adjust rotation so this is reflected
                rotations[insertionBlock] = (writeRotation + 1) & BLOCK_MASK;
            }
        } else {
            int length = BLOCK_MASK - distance; // BLOCK_MASK happens to be BLOCK_SIZE - 1

            /*
             * A: A high gap is at the end, and near
             * [ . a b:c d e f g ] -- insert between b and c, gap (h) is near
             * [ a b : c d e f g ] -- 1: shift left, reduce rotation by one; few elements
             * moved <-- PREFERRED
             * [ g a b : c d e f ] -- 2: shift right, no rotation change; many elements
             * moved
             * B: A high gap is at the end, but far
             * [ a b:c d e f g . ] -- insert between b and c, but gap (h) is far
             * [ b : c d e f g a ] -- 1: shift left, reduce rotation by one; few elements
             * moved <-- PREFERRED
             * [ a b : c d e f g ] -- 2: shift right, no rotation change; many elements
             * moved
             * C: A low gap is at the beginning, and near
             * [ . b c:d e f g h ] -- insert between c and d, gap (a) is near
             * [ b c : d e f g h ] -- 1: shift left, no rotation change; few elements moved
             * <-- PREFERRED
             * [ h b c : d e f g ] -- 2: shift right, rotation increased by one; many
             * elements moved
             * D: A low gap is at the beginning, but far
             * [ b c:d e f g h . ] -- insert between c and d, but gap (a) is far
             * [ c : d e f g h b ] -- 1: shift left, no rotation change; few elements moved
             * <-- PREFERRED
             * [ b c : d e f g h ] -- 2: shift right, rotation increased by one; many
             * elements moved
             */

            insertIndex = blockBase | ((insertIndex - 1) & BLOCK_MASK); // when shifting left, the element before the
            // insertion point will become the gap, so
            // reduce insert index by 1

            circularShiftLeft(data, blockBase, BLOCK_SIZE, blockBase | ((gapIndex + 1) & BLOCK_MASK), length); // +1 for
            // shift
            // left
            // positioning

            if (insertNearEnd) { // if gap was a high index, then left shift moved a low index element in the
                // gap; adjust rotation so this is reflected
                rotations[insertionBlock] = writeRotation; // last loop happens to have the adjusted rotation already
            }
        }

        data[insertIndex] = element;

        this.size = size + 1;
    }

    @Override
    public boolean add(E e) {
        checkBlocked();
        addLast(e);

        return true;
    }

    @Override
    public void addFirst(E e) {
        checkBlocked();
        modCount++;

        if (shouldIncreaseCapacity()) {
            doubleCapacity();
        }

        moveOffsetLeft();
        noShiftAdd(0, e);
    }

    @Override
    public void addLast(E e) {
        checkBlocked();
        modCount++;

        if (shouldIncreaseCapacity()) {
            doubleCapacity();
        }

        noShiftAdd(size, e);
    }

    private boolean shouldIncreaseCapacity() {
        return size >= data.length - (1 << shift);
    }

    private void noShiftAdd(int index, E e) {
        this.data[toDataIndex(index)] = e;

        size++;
    }

    private void doubleCapacity() {
        int newLength = Math.max(MIN_SIZE, data.length * 2);

        @SuppressWarnings("unchecked")
        E[] newData = (E[]) new Object[newLength];

        /*
         * The goal when growing the capacity is to put all new space between
         * the block containing the last element and the block containing
         * the first element:
         *
         * [FL] -> [FL..] / [.FL.] (with offset adjustment) or [..FL]
         * [LF] -> [L..F]
         *
         * The offset always points to the location of the first element, so
         * its best to reason from the block containing the first element. The
         * exact split doesn't matter, as long as the new unused space is between
         * last and first blocks. So to avoid having to adjust the offset, always
         * put the block containing the first element at the same distance from
         * the end of the array.
         *
         * When also changing the block size, currently the array is just recreated
         * completely with all rotations reset.
         */

        int preferredShift = SHIFTS[Integer.numberOfTrailingZeros(newLength)];

        if (preferredShift > shift) {
            final int blockMask = (1 << shift) - 1;
            final int mask = data.length - 1;
            final int baseMask = ~blockMask;

            for (int index = 0; index < size; index++) {
                int position = (index + offset) & mask;
                int block = position >> shift;
                int baseIndex = position & baseMask;

                newData[index] = data[baseIndex | ((rotations[block] + position) & blockMask)];
            }

            this.data = newData;
            this.rotations = new int[newLength >> preferredShift];
            this.shift = preferredShift;
            this.offset = 0;

            return;
        }

        final int firstElementIndex = toPosition(0);
        final int BLOCK_SHIFT = shift;

        unrotate(firstElementIndex >> BLOCK_SHIFT);

        int endLength = data.length - firstElementIndex;

        System.arraycopy(data, firstElementIndex, newData, firstElementIndex, endLength);
        System.arraycopy(data, 0, newData, data.length, firstElementIndex);

        this.data = newData;

        int[] newRotations = new int[Math.max(1, newLength >> BLOCK_SHIFT)];

        int secondHalfLength = firstElementIndex >> BLOCK_SHIFT;
        int firstHalfLength = rotations.length - secondHalfLength;

        System.arraycopy(rotations, secondHalfLength, newRotations, secondHalfLength, firstHalfLength);
        System.arraycopy(rotations, 0, newRotations, rotations.length, secondHalfLength);

        this.rotations = newRotations;
    }

    private void unrotate(int block) {
        int rotation = rotations[block];

        if (rotation != 0) {
            int baseIndex = block << shift;
            int blockSize = 1 << shift;

            reverse(data, baseIndex, baseIndex + rotation - 1);
            reverse(data, baseIndex + rotation, baseIndex + blockSize - 1);
            reverse(data, baseIndex, baseIndex + blockSize - 1);

            rotations[block] = 0;
        }
    }

    private static <T> void reverse(T[] data, int start, int end) {
        int s = start;
        int e = end;

        while (s < e) {
            T temp = data[s];

            data[s++] = data[e];
            data[e--] = temp;
        }
    }

    @Override
    public E set(int index, E e) {
        checkBlocked();
        Objects.checkIndex(index, size);

        E[] data = this.data;
        int dataIndex = toDataIndex(index);
        E old = data[dataIndex];

        data[dataIndex] = e;

        return old;
    }

    @Override
    public E removeFirst() {
        checkBlocked();
        final int newSize = this.size - 1;

        if (newSize < 0) {
            throw new NoSuchElementException();
        }

        modCount++;

        int dataIndex = toDataIndex(0);
        E deletedElement = this.data[dataIndex];

        this.data[dataIndex] = null;
        this.size = newSize;

        moveOffsetRight();

        return deletedElement;
    }

    @Override
    public E removeLast() {
        checkBlocked();
        final int newSize = this.size - 1;

        if (newSize < 0) {
            throw new NoSuchElementException();
        }

        modCount++;

        int dataIndex = toDataIndex(newSize);
        E deletedElement = this.data[dataIndex];

        this.data[dataIndex] = null;
        this.size = newSize;

        return deletedElement;
    }

    @Override
    public E remove(int index) {
        checkBlocked();
        Objects.checkIndex(index, size); // Check this first to throw correct exception (as removeLast will throw a
        // different one)

        if (index == size - 1) {
            return removeLast();
        }

        // Future: more optimization is possible still for cases removing near the front

        modCount++;

        final int BLOCK_SHIFT = shift;
        final int BLOCK_MASK = (1 << shift) - 1;
        final int position = toPosition(index);

        int deletionBlock = position >> BLOCK_SHIFT;

        final int baseIndex = deletionBlock << BLOCK_SHIFT;
        final int startRotation = rotations[deletionBlock];
        final int deleteIndex = baseIndex | ((startRotation + position) & BLOCK_MASK);
        final E deletedElement = data[deleteIndex];

        int gapIndex = baseIndex | ((startRotation - 1) & BLOCK_MASK);

        if (gapIndex < deleteIndex) {
            System.arraycopy(data, gapIndex, data, gapIndex + 1, deleteIndex - gapIndex);

            rotations[deletionBlock] = (startRotation + 1) & BLOCK_MASK;
            gapIndex++;
        } else {
            System.arraycopy(data, deleteIndex + 1, data, deleteIndex, gapIndex - deleteIndex);
        }

        /*
         * Adjust blocks to the right of deletion block, moving one element to the left:
         *
         * deletion block last block
         * +-----------------+-----------------+-----------------+
         * | G | F | F |
         * +-----------------+-----------------+-----------------+
         * ^ | |
         * \___/ |
         * ^ |
         * \_________________/
         *
         * F = first element
         * G = gap
         */

        final int rotationsMask = rotations.length - 1;
        final int lastBlock = toPosition(size - 1) >> BLOCK_SHIFT;

        while (deletionBlock != lastBlock) {
            deletionBlock = (deletionBlock + 1) & rotationsMask;

            int readRotation = rotations[deletionBlock];
            int readIndex = (deletionBlock << BLOCK_SHIFT) | readRotation;

            rotations[deletionBlock] = (readRotation + 1) & BLOCK_MASK; // adjust first index of next block
            data[gapIndex] = data[readIndex]; // copy first element from next block to last element of previous block

            gapIndex = readIndex;
        }

        data[gapIndex] = null;
        size--;

        return deletedElement;
    }

    @Override
    public void clear() {
        checkBlocked();
        modCount++;

        @SuppressWarnings("unchecked")
        E[] emptyData = (E[]) EMPTY_DATA;

        this.data = emptyData;
        this.rotations = EMPTY_ROTATIONS;
        this.offset = 0;
        this.size = 0;
        this.shift = 0;
    }

    /**
     * Returns a shallow copy of this instance; the elements themselves are not
     * copied.
     *
     * @return a clone of this instance, never {@code null}
     */
    @Override
    public Object clone() {
        try {
            @SuppressWarnings("unchecked")
            chessapi4j.functional.BlockingList<E> clone = (chessapi4j.functional.BlockingList<E>) super.clone();

            clone.data = Arrays.copyOf(this.data, this.data.length);
            clone.rotations = Arrays.copyOf(this.rotations, this.rotations.length);

            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    private void moveOffsetLeft() {
        int mask = data.length - 1;

        offset = (offset - 1) & mask; // make space at start for one element
    }

    private void moveOffsetRight() {
        int mask = data.length - 1;

        offset = (offset + 1) & mask;
    }

    private int toDataIndex(int index) {
        int blockMask = (1 << shift) - 1;
        int mask = data.length - 1;
        int position = (index + offset) & mask;
        int block = position >> shift;
        int baseIndex = position & ~blockMask;

        return baseIndex | ((rotations[block] + position) & blockMask);
    }

    private int toPosition(int index) {
        int mask = data.length - 1;

        return (index + offset) & mask;
    }

    String dataToString() {
        StringBuilder sb = new StringBuilder();

        int startPosition = toPosition(0);
        int endPosition = toPosition(size - 1);

        final int BLOCK_SIZE = 1 << shift;
        final int BLOCK_SHIFT = shift;
        final int BLOCK_MASK = BLOCK_SIZE - 1;

        for (int j = 0; j < data.length; j += BLOCK_SIZE) {
            sb.append("[");

            for (int i = j; i < j + Math.min(data.length, BLOCK_SIZE); i++) {
                int idx = j + ((-rotations[j >> BLOCK_SHIFT] + (i & BLOCK_MASK)) & BLOCK_MASK);

                if (i != j) {
                    sb.append(", ");
                }

                if (((rotations[j >> BLOCK_SHIFT]) & BLOCK_MASK) == (i & BLOCK_MASK)) {
                    sb.append(">");
                }
                if (idx == startPosition) {
                    sb.append("$");
                }

                sb.append(data[i] == null ? "-" : data[i].toString());

                if (idx == endPosition) {
                    sb.append("^");
                }
                if (((rotations[j >> BLOCK_SHIFT] - 1) & BLOCK_MASK) == (i & BLOCK_MASK)) {
                    sb.append("<");
                }
            }

            sb.append("]");
        }

        return sb.toString();
    }

    private static <T> void circularShiftRight(T[] data, int offset, int length, int index, int count) {
        // mimics: System.arraycopy(data, index, data, index + 1, count) with wrap
        // around in range [offset, offset + length)
        int leftOver = count;
        int lastShiftIndex = index + count;
        int lastRangeIndex = offset + length;
        int overflowLength = lastShiftIndex - lastRangeIndex;

        if (overflowLength >= 0) {
            System.arraycopy(data, offset, data, offset + 1, overflowLength);

            leftOver -= overflowLength + 1;
            data[offset] = data[lastRangeIndex - 1];
        }

        System.arraycopy(data, index, data, index + 1, leftOver);
    }

    private static <T> void circularShiftLeft(T[] data, int offset, int length, int index, int count) {
        // mimics: System.arraycopy(data, index, data, index - 1, count) with wrap
        // around in range [offset, offset + length)
        int leftOver = count;
        int lastRangeIndex = offset + length;

        if (index - offset >= 1) {
            int max = Math.min(count, lastRangeIndex - index);

            leftOver -= max;

            System.arraycopy(data, index, data, index - 1, max);
        }

        if (leftOver > 0) {
            data[lastRangeIndex - 1] = data[offset];

            leftOver--;

            System.arraycopy(data, offset + 1, data, offset, leftOver);
        }
    }

    /*
     * Implementation of Deque interface:
     */

    @Override
    public boolean offerFirst(E e) {
        checkBlocked();
        addFirst(e);

        return true;
    }

    @Override
    public boolean offerLast(E e) {
        checkBlocked();
        addLast(e);

        return true;
    }

    @Override
    public E pollFirst() {
        checkBlocked();
        return isEmpty() ? null : removeFirst();
    }

    @Override
    public E pollLast() {
        checkBlocked();
        return isEmpty() ? null : removeLast();
    }

    @Override
    public E getFirst() {
        return super.getFirst();
    }

    @Override
    public E getLast() {
        return super.getLast();
    }

    @Override
    public E peekFirst() {
        return isEmpty() ? null : getFirst();
    }

    @Override
    public E peekLast() {
        return isEmpty() ? null : getLast();
    }

    @Override
    public boolean removeFirstOccurrence(Object o) {
        checkBlocked();
        int index = indexOf(o);

        if (index == -1) {
            return false;
        }

        remove(index);

        return true;
    }

    @Override
    public boolean removeLastOccurrence(Object o) {
        checkBlocked();
        int index = lastIndexOf(o);

        if (index == -1) {
            return false;
        }

        remove(index);

        return true;
    }

    @Override
    public boolean offer(E e) {
        checkBlocked();
        return add(e);
    }

    @Override
    public E remove() {
        checkBlocked();
        return removeFirst();
    }

    @Override
    public E poll() {
        checkBlocked();
        return pollFirst();
    }

    @Override
    public E element() {
        return getFirst();
    }

    @Override
    public E peek() {
        return peekFirst();
    }

    @Override
    public void push(E e) {
        checkBlocked();
        addFirst(e);
    }

    @Override
    public E pop() {
        checkBlocked();
        return removeFirst();
    }

    @Override
    public Iterator<E> descendingIterator() {
        return reversed().iterator();
    }

    @Override
    public chessapi4j.functional.BlockingList<E> reversed() {
        return new chessapi4j.functional.BlockingList.ReverseOrderShiftListView<>(this, super.reversed(),
                Deque.super.reversed());
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        checkBlocked();
        return super.addAll(index, c);
    }

    @Override
    public boolean remove(Object o) {
        checkBlocked();
        return super.remove(o);
    }

    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        checkBlocked();
        super.removeRange(fromIndex, toIndex);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        checkBlocked();
        return super.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        checkBlocked();
        return super.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        checkBlocked();
        return super.retainAll(c);
    }

    @Override
    public void replaceAll(UnaryOperator<E> operator) {
        checkBlocked();
        super.replaceAll(operator);
    }

    @Override
    public void sort(Comparator<? super E> c) {
        checkBlocked();
        super.sort(c);
    }

    @Override
    public boolean removeIf(Predicate<? super E> filter) {
        checkBlocked();
        return super.removeIf(filter);
    }

    static final class ReverseOrderShiftListView<E> extends BlockingList<E> {
        final BlockingList<E> originalList;
        final List<E> reversedList;
        final Deque<E> reversedDeque;

        ReverseOrderShiftListView(BlockingList<E> originalList, List<E> reversedList, Deque<E> reversedDeque) {
            this.originalList = originalList;
            this.reversedList = reversedList;
            this.reversedDeque = reversedDeque;
        }

        @Override
        public chessapi4j.functional.BlockingList<E> reversed() {
            return originalList;
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            checkBlocked();
            return reversedList.retainAll(c);
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            checkBlocked();
            return reversedList.removeAll(c);
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            checkBlocked();
            return reversedList.containsAll(c);
        }

        @Override
        public boolean isEmpty() {
            return reversedList.isEmpty();
        }

        @Override
        public Stream<E> parallelStream() {
            return reversedList.parallelStream();
        }

        @Override
        public Stream<E> stream() {
            return reversedList.stream();
        }

        @Override
        public boolean removeIf(Predicate<? super E> filter) {
            checkBlocked();
            return reversedList.removeIf(filter);
        }

        @Override
        public <T> T[] toArray(IntFunction<T[]> generator) {
            return reversedList.toArray(generator);
        }

        @Override
        public void forEach(Consumer<? super E> action) {
            reversedList.forEach(action);
        }

        @Override
        public Iterator<E> iterator() {
            return reversedList.iterator();
        }

        @Override
        public int hashCode() {
            return reversedList.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            return reversedList.equals(o);
        }

        @Override
        public List<E> subList(int fromIndex, int toIndex) {
            return reversedList.subList(fromIndex, toIndex);
        }

        @Override
        public ListIterator<E> listIterator() {
            return reversedList.listIterator();
        }

        @Override
        public void sort(Comparator<? super E> c) {
            checkBlocked();
            reversedList.sort(c);
        }

        @Override
        public void replaceAll(UnaryOperator<E> operator) {
            checkBlocked();
            reversedList.replaceAll(operator);
        }

        @Override
        public Spliterator<E> spliterator() {
            return reversedList.spliterator();
        }

        @Override
        public <T> T[] toArray(T[] a) {
            return reversedList.toArray(a);
        }

        @Override
        public Object[] toArray() {
            return reversedList.toArray();
        }

        @Override
        public ListIterator<E> listIterator(int index) {
            return reversedList.listIterator(index);
        }

        @Override
        public int lastIndexOf(Object o) {

            return reversedList.lastIndexOf(o);
        }

        @Override
        public int indexOf(Object o) {
            return reversedList.indexOf(o);
        }

        @Override
        public E remove(int index) {
            checkBlocked();
            return reversedList.remove(index);
        }

        @Override
        public void add(int index, E element) {
            checkBlocked();
            reversedList.add(index, element);
        }

        @Override
        public E set(int index, E element) {
            checkBlocked();
            return reversedList.set(index, element);
        }

        @Override
        public E get(int index) {

            return reversedList.get(index);
        }

        @Override
        public void clear() {
            checkBlocked();
            reversedList.clear();
        }

        @Override
        public boolean addAll(int index, Collection<? extends E> c) {
            checkBlocked();
            return reversedList.addAll(index, c);
        }

        @Override
        public boolean addAll(Collection<? extends E> c) {
            checkBlocked();
            return reversedList.addAll(c);
        }

        @Override
        public boolean remove(Object o) {
            checkBlocked();
            return reversedList.remove(o);
        }

        @Override
        public boolean add(E e) {
            checkBlocked();
            return reversedList.add(e);
        }

        @Override
        public int size() {
            return reversedList.size();
        }

        @Override
        public boolean contains(Object o) {
            return reversedList.contains(o);
        }

        @Override
        public String toString() {
            return reversedList.toString();
        }

        @Override
        public void addLast(E e) {
            checkBlocked();
            reversedDeque.addLast(e);
        }

        @Override
        public void addFirst(E e) {
            checkBlocked();
            reversedDeque.addFirst(e);
        }

        @Override
        public E removeLast() {
            checkBlocked();
            return reversedDeque.removeLast();
        }

        @Override
        public E removeFirst() {
            checkBlocked();
            return reversedDeque.removeFirst();
        }

        @Override
        public E getLast() {
            return reversedDeque.getLast();
        }

        @Override
        public E getFirst() {
            return reversedDeque.getFirst();
        }

        @Override
        public boolean removeLastOccurrence(Object o) {
            checkBlocked();
            return reversedDeque.removeLastOccurrence(o);
        }

        @Override
        public boolean removeFirstOccurrence(Object o) {
            checkBlocked();
            return reversedDeque.removeFirstOccurrence(o);
        }

        @Override
        public E pop() {
            checkBlocked();
            return reversedDeque.pop();
        }

        @Override
        public void push(E e) {
            checkBlocked();
            reversedDeque.push(e);
        }

        @Override
        public E pollLast() {
            checkBlocked();
            return reversedDeque.pollLast();
        }

        @Override
        public E pollFirst() {
            checkBlocked();
            return reversedDeque.pollFirst();
        }

        @Override
        public E peekLast() {
            return reversedDeque.peekLast();
        }

        @Override
        public E peekFirst() {
            return reversedDeque.peekFirst();
        }

        @Override
        public boolean offerLast(E e) {
            checkBlocked();
            return reversedDeque.offerLast(e);
        }

        @Override
        public boolean offerFirst(E e) {
            checkBlocked();
            return reversedDeque.offerFirst(e);
        }

        @Override
        public boolean offer(E e) {
            checkBlocked();
            return reversedDeque.offer(e);
        }

        @Override
        public E remove() {
            checkBlocked();
            return reversedDeque.remove();
        }

        @Override
        public E poll() {
            checkBlocked();
            return reversedDeque.poll();
        }

        @Override
        public E element() {
            return reversedDeque.element();
        }

        @Override
        public E peek() {
            return reversedDeque.peek();
        }

        @Override
        public Iterator<E> descendingIterator() {
            return reversedDeque.descendingIterator();
        }
    }
}

