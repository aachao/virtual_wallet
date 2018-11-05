/* Author: Aaron Chao
 * Date  : November 4, 2018
 */

import static org.junit.Assert.*;
import org.junit.Test;

public class VirtualWalletTest {

    public static int NUMBER_THREADS = 20;

    @Test
    public void testVirtualWallet() throws InsufficientBalanceException, InvalidAccountException {
        testConstructor();
        testCreateAccount();
        testCloseAccount();
        testWithdraw();
        testDeposit();
        testTransfer();
        testGetTransactions();
        testConcurrency();
    }

    public void testConstructor() {
        VirtualWallet wallet = new VirtualWallet();
        assertEquals(0, wallet.getAccountIDs().size());
    }

    public void testCreateAccount() {
        VirtualWallet wallet = new VirtualWallet();
        wallet.createAccount();
        assertEquals(1, wallet.getAccountIDs().size());
        assertTrue(wallet.getAccountIDs().contains(0));
        wallet.createAccount();
        assertEquals(2, wallet.getAccountIDs().size());
        assertTrue(wallet.getAccountIDs().contains(0));
        assertTrue(wallet.getAccountIDs().contains(1));
        assertEquals(2, wallet.getAccountIDs().size());
        wallet.createAccount();
        assertTrue(wallet.getAccountIDs().contains(0));
        assertTrue(wallet.getAccountIDs().contains(1));
        assertTrue(wallet.getAccountIDs().contains(2));
    }

    public void testCloseAccount() throws InvalidAccountException {
        VirtualWallet wallet = new VirtualWallet();
        wallet.createAccount();
        wallet.createAccount();
        wallet.createAccount();
        assertEquals(3, wallet.getAccountIDs().size());
        wallet.closeAccount(0);
        assertEquals(2, wallet.getAccountIDs().size());
        assertTrue(wallet.getAccountIDs().contains(1));
        assertTrue(wallet.getAccountIDs().contains(2));
    }

    public void testDeposit() throws InvalidAccountException, InsufficientBalanceException {
        VirtualWallet wallet = new VirtualWallet();
        wallet.createAccount();
        wallet.createAccount();
        wallet.deposit(0,100.00);
        assertEquals(100, wallet.getBalance(0), 0.001);
        assertEquals(0, wallet.getBalance(1), 0.001);

        wallet.deposit(1,23.4);
        wallet.deposit(1, 100);
        assertEquals(123.4, wallet.getBalance(1), 0.001);

        boolean thrown = false;
        try {
            wallet.deposit(2,1);
        } catch (InvalidAccountException e) {
            thrown = true;
        }
        assertTrue(thrown);
    }

    public void testWithdraw() throws InvalidAccountException, InsufficientBalanceException {
        VirtualWallet wallet = new VirtualWallet();
        wallet.createAccount();
        wallet.createAccount();
        wallet.deposit(0, 100);
        wallet.deposit(1, 50);
        wallet.withdraw(0, 25.5);
        assertEquals(74.5, wallet.getBalance(0), 0.001);
        assertEquals(50, wallet.getBalance(1), 0.001);


        boolean thrown = false;
        try {
            wallet.withdraw(0, 75);
        } catch (InsufficientBalanceException e) {
            thrown = true;
        }
        assertTrue(thrown);
        assertEquals(74.5, wallet.getBalance(0), 0.001);

        thrown = false;
        try {
            wallet.withdraw(3, 1);
        } catch (InvalidAccountException e) {
            thrown = true;
        }
        assertTrue(thrown);
    }

    public void testTransfer() throws InvalidAccountException, InsufficientBalanceException {
        VirtualWallet wallet = new VirtualWallet();
        wallet.createAccount();
        wallet.createAccount();
        wallet.createAccount();

        wallet.deposit(0, 10);
        wallet.deposit(1,10);
        wallet.deposit(2, 10);

        wallet.transfer(1,2,5.5);
        assertEquals(4.5, wallet.getBalance(1), 0.001);
        assertEquals(15.5, wallet.getBalance(2), 0.001);
        assertEquals(10, wallet.getBalance(0), 0.001);

        boolean thrown = false;
        try {
            wallet.transfer(2,1,16);
        } catch (InsufficientBalanceException e) {
            thrown = true;
        }
        assertTrue(thrown);
        assertEquals(4.5, wallet.getBalance(1), 0.001);
        assertEquals(15.5, wallet.getBalance(2), 0.001);

        thrown = false;
        try {
            wallet.transfer(2, 3, 1);
        } catch (InvalidAccountException e) {
            thrown = true;
        }
        assertTrue(thrown);
        assertEquals(15.5, wallet.getBalance(2), 0.001);
    }

    public void testGetTransactions() throws InvalidAccountException, InsufficientBalanceException {
        VirtualWallet wallet = new VirtualWallet();
        wallet.createAccount();
        wallet.createAccount();
        wallet.deposit(0, 10);
        wallet.deposit(0, 10);
        wallet.deposit(1, 10);
        wallet.deposit(1, 10);
        wallet.deposit(1, 10);
        wallet.deposit(1, 10);
        wallet.deposit(1, 10);
        wallet.deposit(1, 10);
        assertEquals(Math.min(2, Account.TRANSACTION_HISTORY_LIMIT), wallet.getTransactions(0).size());
        assertEquals(Math.min(6, Account.TRANSACTION_HISTORY_LIMIT), wallet.getTransactions(1).size());
    }

    public class WalletUser extends Thread {

        VirtualWallet wallet;

        WalletUser(VirtualWallet wallet) {
            super();
            this.wallet = wallet;
        }

        public void run() {
            try {
                this.wallet.createAccount();
                this.wallet.deposit(0, 100);
                this.wallet.deposit(1, 100);
                this.wallet.deposit(2, 100);
                this.wallet.withdraw(0, 10);
                this.wallet.withdraw(1, 20);
                this.wallet.withdraw(2, 30);
                this.wallet.transfer(0, 1, 10);
                this.wallet.transfer(2, 0, 10);
                this.wallet.createAccount();
            } catch (Exception e) {

            }
        }
    }

    public void testConcurrency() throws InvalidAccountException {
        VirtualWallet wallet = new VirtualWallet();
        wallet.createAccount();
        wallet.createAccount();
        wallet.createAccount();

        WalletUser[] threads = new WalletUser[NUMBER_THREADS];
        for (int i=0; i < NUMBER_THREADS; i++) {
            threads[i] = new WalletUser(wallet);
            threads[i].start();
        }

        try {
            for (WalletUser w: threads) {
                w.join();
            }
        } catch (InterruptedException e) {

        }

        assertEquals(90*NUMBER_THREADS, wallet.getBalance(0), 0.001);
        assertEquals(90*NUMBER_THREADS, wallet.getBalance(1), 0.001);
        assertEquals(60*NUMBER_THREADS, wallet.getBalance(2), 0.001);
        assertEquals(2*NUMBER_THREADS+3, wallet.getAccountIDs().size());
        int n1 = wallet.getTransactions(0).get(Account.TRANSACTION_HISTORY_LIMIT-1).getTransactionID();
        int n2 = wallet.getTransactions(1).get(Account.TRANSACTION_HISTORY_LIMIT-1).getTransactionID();
        int n3 = wallet.getTransactions(2).get(Account.TRANSACTION_HISTORY_LIMIT-1).getTransactionID();
        assertEquals(10 * NUMBER_THREADS - 1, Math.max(n3, Math.max(n1, n2)));
    }
}
