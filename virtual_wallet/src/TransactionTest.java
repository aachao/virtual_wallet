/* Author: Aaron Chao
 * Date  : November 4, 2018
 */

import static org.junit.Assert.*;
import org.junit.Test;

public class TransactionTest {

    @Test
    public void testTransaction() {
        testConstructor();
    }

    public void testConstructor() {
        Transaction t = new Transaction(0, 12.34);
        assertEquals(0, t.getTransactionID());
        assertEquals("java.lang.Long", ((Object) t.getTime()).getClass().getName());
        assertEquals(12.34, t.getAmountDifference(), 0.001);
    }
}
