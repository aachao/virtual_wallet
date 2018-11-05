/* Author: Aaron Chao
 * Date  : November 4, 2018
 */

import java.util.Date;

/* A transaction class.
 */
public class Transaction {
    private int id;
    private long time;
    private double amountDifference;

    /* Create a transaction
     */
    public Transaction(int transactionID, double amountDifference) {
        this.id = transactionID;
        this.time = new Date().getTime();
        this.amountDifference = amountDifference;
    }

    /* Get the ID of the transaction
     */
    public int getTransactionID() {
        return this.id;
    }

    /* Get the timestamp of the transaction,
     * specifically the number of milliseconds since January 1, 1970, 00:00:00 GMT
     */
    public long getTime() {
        return this.time;
    }

    /* Get the amount of the transaction
     */
    public double getAmountDifference() {
        return this.amountDifference;
    }
}
