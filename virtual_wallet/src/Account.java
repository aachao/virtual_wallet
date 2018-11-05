/* Author: Aaron Chao
 * Date  : November 4, 2018
 */

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/* A transaction account class.
 */
public class Account {
    private int id;
    private double balance;
    private ReentrantLock lock;
    private LinkedList<Transaction> transactions;

    public static final int TRANSACTION_HISTORY_LIMIT = 5;

    public Account(int accountID) {
        this.id = accountID;
        this.balance = 0;
        this.lock = new ReentrantLock();
        this.transactions = new LinkedList<>();
    }

    /* Retrieve the ID of the account
     */
    public int getAccountID() {
        return this.id;
    }

    /* Retrieve the current account balance
     */
    public double getBalance() {
        return this.balance;
    }

    /* Retrieve the N most recent transactions for the account
     */
    public List<Transaction> getTransactions() {
        return new LinkedList<>(this.transactions);
    }

    /* Execute a transaction
     */
    public void executeTransaction(double amountDifference, int transactionID) throws InsufficientBalanceException {
        this.lock.lock();
        if (this.balance + amountDifference < 0) {
            this.lock.unlock();
            throw new InsufficientBalanceException("Account balance is insufficient for this transaction");
        }
        this.balance += amountDifference;
        this.recordTransaction(transactionID, amountDifference);
        this.lock.unlock();
    }

    /* Helper method: add a transaction to the account transaction history
     */
    private void recordTransaction(int transactionID, double amountDifference) {
        Transaction t = new Transaction(transactionID, amountDifference);
        if (this.transactions.size() == TRANSACTION_HISTORY_LIMIT) {
            this.transactions.removeFirst();
        }
        this.transactions.add(t);
    }
}