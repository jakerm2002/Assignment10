/*  Student information for assignment:
 *
 *  On OUR honor, Jake Medina and Thomas Moore,
 *  this programming assignment is OUR own work
 *  and WE have not provided this code to any other student.
 *
 *  Number of slip days used: 0
 *
 *  Student 1 (Student whose Canvas account is being used)
 *  UTEID: jrm7784
 *  email address: jakemedina@utexas.edu
 *  Grader name: Skyler V.
 *  Section number: 52260
 *
 *  Student 2
 *  UTEID: tcm2448
 *  email address: tmooretcm@utexas.edu
 *
 */

import java.util.ArrayList;

/**
 * A class used to represent a priority queue.
 */
public class HuffmanPriority<E extends Comparable<? super E>> {
    private ArrayList<E> queue;

    // Constructs a priority queue using an arraylist as the internal storage container
    public HuffmanPriority() {
        queue = new ArrayList<>();
    }

    /**
     * Adds an item to the priority queue fairly.
     *
     * @param item the item to be added
     * @return whether the list changed as a result of this add
     */
    public boolean add(E item) {
        if (item == null) {
            throw new IllegalArgumentException("Given item can't be null.");
        }
        if (queue.size() == 0) {
            // if the queue is empty, just add this item to the front
            return queue.add(item);
        }
        // loop through until we find the correct spot to add
        for (int i = 0; i < queue.size(); i++) {
            int comparison = queue.get(i).compareTo(item);
            if (comparison > 0) {
                // want to add an item once we find the first instance of a value greater
                queue.add(i, item);
                return true;
            }
        }
        // at this point, item should go at the end
        return queue.add(item);
    }

    /**
     * Removes and returns the item at the front of the queue. Queue must have at least 1 item.
     *
     * @return the item at the front of the queue
     */
    public E remove() {
        if (queue.size() == 0) {
            throw new IllegalArgumentException("Can't remove from empty queue.");
        }
        E val = queue.get(0);
        queue.remove(0);
        return val;
    }

    /**
     * Returns the item at the front of the queue.
     *
     * @return the item at the front of the queue
     */
    public E peek() {
        if (queue.size() == 0) {
            throw new IllegalArgumentException("Can't peek at empty queue.");
        }
        return queue.get(0);
    }

    /**
     * Returns the size of this queue.
     *
     * @return an int of the size of this queue
     */
    public int size() {
        return queue.size();
    }

    /**
     * Checks whether the priority queue contains a given item.
     *
     * @param item the item to check, can't be null
     * @return true if the item is present in the queue, false otherwise
     */
    public boolean contains(E item) {
        if (item == null) {
            throw new IllegalArgumentException("Given item can't be null.");
        }
        return queue.contains(item);
    }

    /**
     * Resets the queue, emptying it.
     */
    public void clear() {
        queue.clear();
    }

    /**
     * Returns a String representation of the priority queue.
     *
     * @return a String representation of the priority queue
     */
    public String toString() {
        return queue.toString();
    }

}