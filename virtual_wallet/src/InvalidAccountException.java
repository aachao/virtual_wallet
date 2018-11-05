/* Author: Aaron Chao
 * Date  : November 4, 2018
 */

/* Invalid Account Exception class
 * Thrown when an account ID does not exist in the wallet
 */
public class InvalidAccountException extends Exception {
    public InvalidAccountException(String s) {
        super(s);
    }
}
