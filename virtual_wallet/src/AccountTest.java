/* Author: Aaron Chao
 * Date  : November 4, 2018
 */

import java.util.List;
import static org.junit.Assert.*;
import org.junit.Test;

public class AccountTest {
    @Test
    public void testAccount() throws Exception {
        testConstructor();
        testDepositTransactions();
        testWithdrawTransactions();
        testGetTransactions();
    }

    public void testConstructor() {
        Account acc = new Account(123);
        assertEquals(0, acc.getBalance(), 0.001);
        assertEquals(123, acc.getAccountID());
    }

    public void testDepositTransactions() throws InsufficientBalanceException {
        Account acc = new Account(123);
        acc.executeTransaction(95.99, 0);
        assertEquals(95.99, acc.getBalance(), 0.001);
        acc.executeTransaction(100, 1);
        assertEquals(195.99, acc.getBalance(), 0.001);
        acc.executeTransaction(0.01, 2);
        assertEquals(196, acc.getBalance(), 0.001);
    }

    public void testWithdrawTransactions() throws InsufficientBalanceException {
        Account acc = new Account(123);
        acc.executeTransaction(100, 0);
        acc.executeTransaction(-25, 1);
        assertEquals(75, acc.getBalance(), 0.001);
        acc.executeTransaction(-70.5, 2);
        assertEquals(4.5, acc.getBalance(), 0.001);
        boolean thrown = false;
        try {
            acc.executeTransaction(-5, 3);
        } catch (InsufficientBalanceException e) {
            thrown = true;
        }
        assertTrue(thrown);
        assertEquals(4.5, acc.getBalance(), 0.001);
        acc.executeTransaction(-4.5, 3);
        assertEquals(0, acc.getBalance(), 0.001);
    }

    public void testGetTransactions() throws InsufficientBalanceException {
        Account acc = new Account(123);
        acc.executeTransaction(10, 0);
        acc.executeTransaction(10, 1);
        acc.executeTransaction(10, 2);
        List<Transaction> transactions = acc.getTransactions();
        assertEquals(Math.min(3, Account.TRANSACTION_HISTORY_LIMIT), transactions.size());
        assertEquals(0, transactions.get(0).getTransactionID());
        acc.executeTransaction(10, 5);
        acc.executeTransaction(10, 6);
        acc.executeTransaction(10, 7);
        List<Transaction> transactions2 = acc.getTransactions();
        assertEquals(Math.min(6, Account.TRANSACTION_HISTORY_LIMIT), transactions2.size());
        assertEquals(1, transactions2.get(0).getTransactionID());
    }
}
