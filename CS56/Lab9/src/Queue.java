import java.util.LinkedList;

/**
 *
 * @author Peter Cappello
 */
public class Queue<T>
{
    private final LinkedList<T> q = new LinkedList<>();
    
    /**
     * Answers the question is the queue empty.
     * @return true if and only if it is empty.
     */
    public boolean isEmpty() { return q.isEmpty(); }
    
    /**
     * Removes and retrieves the queue's head element.
     * @return the head of the queue.
     * @throws NoSuchElementException when empty.
     */
    public T remove() { return q.remove(); }
    
    /**
     * Enqueues an element on the queue.
     * @param element 
     */
    public void add( T element ) { q.add( element ); }
    
    /**
     * Retrieves but does not remove the head element of the queue.
     * @return the head element of the queue.
     * @throws NoSuchElementException when empty.
     */
    public T peek() { return q.peek(); }
}