/* Author: Aaron Chao
 * Date  : November 4, 2018
 */

/* Insufficient Balance Exception class
 * Thrown when an account balance is not high enough to execute the transaction
 */
public class InsufficientBalanceException extends Exception {
    public InsufficientBalanceException(String s) {
        super(s);
    }
}
