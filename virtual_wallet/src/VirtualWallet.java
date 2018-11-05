/* Author: Aaron Chao
 * Date  : November 4, 2018
 */

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

/* A virtual wallet is used to access one or more transaction accounts.
 * Using this library, a user will be able to:
 *    - create a new wallet for a user
 *    - return the current balance for an account
 *    - perform a withdrawal transaction on an account
 *    - perform a deposit transaction on an account
 *    - perform a transfer transaction from one account to another account
 *    - return the last N transactions for an account
 */
public class VirtualWallet {

    private Map<Integer,Account> accounts;
    private AtomicInteger nextAccountID;
    private AtomicInteger nextTransactionID;

    /* Create a new virtual wallet.
     */
    public VirtualWallet() {
        this.accounts = new ConcurrentHashMap<>();
        this.nextAccountID = new AtomicInteger(0);
        this.nextTransactionID = new AtomicInteger(0);
    }

    /* Create a new account in the virtual wallet.
     */
    public int createAccount() {
        int accountID = this.nextAccountID.getAndIncrement();
        Account acc = new Account(accountID);
        this.accounts.put(accountID, acc);

        return accountID;
    }

    /* Close an account in the virtual wallet.
     * Throws exception in case of invalid account ID.
     *
     * Params:
     * accountID - account ID
     */
    public void closeAccount(int accountID) throws InvalidAccountException {
        if (!this.accounts.containsKey(accountID)) {
            throw new InvalidAccountException("Invalid Account ID specified");
        }
        this.accounts.remove(accountID);
    }

    /* Get the current balance for an account.
     * Throws exception in case of invalid account ID.
     *
     * Params:
     * accountID - account ID
     *
     * Returns: int
     */
    public double getBalance(int accountID) throws InvalidAccountException {
        if (!this.accounts.containsKey(accountID)) {
            throw new InvalidAccountException("Invalid Account ID specified");
        }
        Account acc = this.accounts.get(accountID);

        return acc.getBalance();
    }

    /* Attempt to withdraw money from an account.
     * Throws exceptions in case of insufficient balance or invalid account ID.
     *
     * Params:
     * accountID - account ID
     * amount    - amount to be withdrawn
     */
    public void withdraw(int accountID, double amount) throws InsufficientBalanceException, InvalidAccountException {
        int transactionID = this.nextTransactionID.getAndIncrement();

        if (!this.accounts.containsKey(accountID)) {
            throw new InvalidAccountException("Invalid Account ID specified");
        }
        Account acc = this.accounts.get(accountID);
        acc.executeTransaction(amount * -1, transactionID);
    }

    /* Deposit money into an account.
     * Throws exception in case of invalid account ID.
     *
     * Params:
     * accountID - account ID
     * amount    - amount to be deposited
     */
    public void deposit(int accountID, double amount) throws InsufficientBalanceException, InvalidAccountException {
        int transactionID = this.nextTransactionID.getAndIncrement();

        if (!this.accounts.containsKey(accountID)) {
            throw new InvalidAccountException("Invalid Account ID specified");
        }
        Account acc = this.accounts.get(accountID);
        acc.executeTransaction(amount, transactionID);
    }

    /* Transfer money from one account to another account.
     * Throws exception in case of insufficient balance or invalid account ID.
     *
     * Params:
     * fromAccountID - account ID of source
     * toAccountID   - account ID of destination
     * amount        - amount to be transferred
     */
    public void transfer(int fromAccountID, int toAccountID, double amount)
            throws InsufficientBalanceException, InvalidAccountException {
        if (!this.accounts.containsKey(fromAccountID)) {
            throw new InvalidAccountException("Invalid Source Account ID specified");
        }
        if (!this.accounts.containsKey(toAccountID)) {
            throw new InvalidAccountException("Invalid Destination Account ID specified");
        }

        withdraw(fromAccountID, amount);
        deposit(toAccountID, amount);
    }

    /* Return the last N transactions for an account.
     *
     * Params:
     * accountID - account ID
     *
     * Returns: List of transactions
     */
    public List<Transaction> getTransactions(int accountID) throws InvalidAccountException {
        if (!this.accounts.containsKey(accountID)) {
            throw new InvalidAccountException("Invalid Account ID specified");
        }
        Account acc = this.accounts.get(accountID);
        return acc.getTransactions();
    }

    /* Return all account IDs for the wallet
     *
     * Returns: Set of account IDs
     */
    public Set<Integer> getAccountIDs() {
        return new HashSet<>(this.accounts.keySet());
    }
}
