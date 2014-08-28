/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.NoSuchElementException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Steven_2
 */
public class QueueTest {
    
    public QueueTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of isEmpty method, of class Queue.
     */
    @Test
    public void testIsEmpty() {
        System.out.println("isEmpty");
        Queue instance = new Queue();
        boolean expResult = true;
        boolean result = instance.isEmpty();
        assertEquals(expResult, result);
    }

    /**
     * Test of remove method, of class Queue.
     */
    @Test
    public void testRemove() {
        System.out.println("remove");
        Queue instance = new Queue();
        Object expResult = null;
        Object result;
        try {
            result = instance.remove();
        }
        catch (NoSuchElementException e){
            instance.add(null);
            assertEquals(instance.remove(), null);
            return;
        }
        fail("Remove did not throw an exception on an empty Queue");
    }

    /**
     * Test of add method, of class Queue.
     */
    @Test
    public void testAdd() {
        System.out.println("add");
        Object element = null;
        Queue instance = new Queue();
        Queue instance2 = new Queue();
        instance.add(element);
        instance2.add(element);
        assertEquals(instance.peek(), instance2.peek()); 
    }

    /**
     * Test of peek method, of class Queue.
     */
    @Test
    public void testPeek() {
        System.out.println("peek");
        Queue instance = new Queue();
        instance.add(null);
        assertEquals(instance.peek(), null);
        return;
    }
    
}
