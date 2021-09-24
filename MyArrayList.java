import java.util.Arrays;

/*
 * Your indexed functions should throw IndexOutOfBoundsException if index is invalid!
 */
@SuppressWarnings("all")
public class MyArrayList<E> {

    /* Internal Object counter */
    protected int objectCount;
    protected int startPos;
    /* Internal Object array */
    protected E [] internalArray;

    /* Constructor: Create it with whatever capacity you want? */
	@SuppressWarnings("unchecked")
    public MyArrayList() {
        this.internalArray = (E[])new Object[100];
    }
    
    /* Constructor with initial capacity */
	@SuppressWarnings("unchecked")
    public MyArrayList(int initialCapacity){
        this.internalArray = (E[])new Object[initialCapacity];
        this.startPos = initialCapacity / 4;
    }
    
    /* Return the number of active slots in the array list */
    @SuppressWarnings("all")
    public int size() {
        return objectCount;
    }
    
    /* Are there zero objects in the array list? */
    public boolean isEmpty() {
        /* ---- YOUR CODE HERE ---- */
        return objectCount == 0;
    }

    /* GOOD Get the index-th object in the list. */
    public E get(int index) {
        /* ---- YOUR CODE HERE ---- */
        if (index >= objectCount || index < 0)
            throw new ArrayIndexOutOfBoundsException("in GET: Index " + index + " out of bounds for length " + size());

        return internalArray[index + startPos];
    }
    
    /*GOOD Replace the object at index with obj.  returns object that was replaced. */
    public E set(int index, E obj) {
        /* ---- YOUR CODE HERE ---- */
        if (index >= objectCount || index < 0)
            throw new ArrayIndexOutOfBoundsException("in SET: Index " + index + " out of bounds for length " + objectCount);

        index += startPos;
        E replaced = internalArray[index];
        internalArray[index] = obj;
        return replaced;
    }

	/* Returns true if this list contains an element equal to obj;
	 otherwise returns false. */
    public boolean contains(E obj) {
        int size = objectCount + startPos - 1;
        // if obj == internalarray[i] or obj.equals(internalarray[i]), then stop
        // continue if obj != int[i] and !obj.equals(int[i])
        // stop if one is true
        //
        // obj = null, int[i] = null,
        for (; size >= startPos && !(internalArray[size] == obj) && !internalArray[size].equals(obj); size--);
        return size >= startPos;
    }
    
    /* Insert an object at index */
	//@SuppressWarnings("unchecked")
    public void add(int index, E obj) {
        if (index > objectCount)
            throw new ArrayIndexOutOfBoundsException();

        if (objectCount == internalArray.length) {
            increaseCapacity();
        }

        // if index at or before middle and space at beginning, shift left from index
        boolean indexIsBeforeMiddle = index <= objectCount / 2;
        boolean isSpaceAtBegin = startPos != 0;
        boolean isSpaceAtEnd = startPos + objectCount < internalArray.length;
        if ((indexIsBeforeMiddle && isSpaceAtBegin) || !isSpaceAtEnd) {
            // shift left from index
            startPos--;
            for (int i = startPos; i < index + startPos; i++) {
                internalArray[i] = internalArray[i + 1];
            }
        }
        // otherwise shift right from index
        // note: there IS space at the beginning of the array,
        // or increaseCapacity() would have been called
        else {
            // shift right from index
            for (int i = startPos + objectCount; i > startPos + index; i--) {
                internalArray[i] = internalArray[i - 1];
            }
        }

        internalArray[startPos + index] = obj;

        ++objectCount;
//        System.out.println("added" + obj);
    }

    /* Add an object to the end of the list; returns true */
    public boolean add(E obj) {
        add(objectCount, obj);
        return true;
    }

    /* Remove the object at index and shift.  Returns removed object. */
    public E remove(int index) {
        if (index >= objectCount)
            throw new ArrayIndexOutOfBoundsException();

        E ret = internalArray[index + startPos];
        if (index <= objectCount / 2) {
            for (int i = index + startPos; i > startPos; i--) {
                internalArray[i] = internalArray[i - 1];
            }
            startPos++;
        }
        else {
            for (int i = index + startPos; i < objectCount + startPos; i++) {
                internalArray[i] = internalArray[i + 1];
            }
        }

        --objectCount;
        return ret;
    }

    /* Removes the first occurrence of the specified element from this list, 
     * if it is present. If the list does not contain the element, it is unchanged. 
     * More formally, removes the element with the lowest index i such that
     * (o==null ? get(i)==null : o.equals(get(i))) (if such an element exists). 
     * Returns true if this list contained the specified element (or equivalently, 
     * if this list changed as a result of the call). */
    // MAYBE -- cut loops in half by checking beginning and end, then beginning+1 and end-1, etc.???
    public boolean remove(E obj) {
        /* ---- YOUR CODE HERE ---- */
        for (int i = startPos; i < objectCount + startPos; i++) {
            E curr = internalArray[i];
            if (obj == curr || obj.equals(curr)) {
                remove(i);
                return true;
            }
        }
        return false;
    }
    
    /* For testing; your string should output as "[X, X, X, X, ...]" where X, X, X, X, ... are the elements in the ArrayList.
     * If the array is empty, it should return "[]".  If there is one element, "[X]", etc.
     * Elements are separated by a comma and a space. */
    public String toString() {
        StringBuilder retBuilder = new StringBuilder("[");
        //int size = size();
        int i;
        if (!isEmpty()) {
            retBuilder.append(internalArray[startPos]);

            for (i = startPos + 1; i < objectCount + startPos; ++i) {
                E current = internalArray[i];
                retBuilder.append(", ");
                if (current == null)
                    retBuilder.append("null");
                else
                    retBuilder.append(current.toString());
            }
        }
        retBuilder.append("]");
        return retBuilder.toString();
    }

    // For debugging
    public void printArray() {
        System.out.println(Arrays.toString(internalArray));
    }

    public void print() {
        System.out.println(this);
    }

    // moves all instances of obj to back of MyArrayList
    public void moveToBack(E obj) {
        int size = size();
        int numOccurrences = 0;
        for (int i = 0; i < size - numOccurrences; i++) {
            E curr = get(i);
            if (curr == obj || curr.equals(obj)) {
                for (int k = i + 1; k < size; k++) {
                    set(k - 1, get(k));
                }
                i = i - 1;
                set(size - 1 - numOccurrences, obj);
                ++numOccurrences;
            }
        }
    }

    private void increaseCapacity() {
        E[] grownArr = (E[]) new Object[(internalArray.length == 0) ? 1 : internalArray.length << 1];
        startPos = grownArr.length / 4;
        for (int i = 0; i < internalArray.length; i++) {
            grownArr[i + startPos] = internalArray[i];
        }
        internalArray = grownArr;
//        System.out.println("Growing to " + grownArr.length);
    }

    public int trueSize() {
        return internalArray.length;
    }
}