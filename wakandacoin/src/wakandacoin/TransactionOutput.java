/**
 * TransactionOutput.java
 *
 * Group Alpha - CMSC 495
 * Summer 2018 - Section 7381
 * 7/21/2018
 *
 * The TransactionOutput class represents the output of a transaction. In other words, the recipient
 * of a given transaction, the coins, and a reference to the parent transaction (the TransactionInput).
 *
 * Note: This code is based on the article at:
 * https://medium.com/programmers-blockchain/creating-your-first-blockchain-with-java-part-2-transactions-2cdac335e0ce
 *
 */
 
package wakandacoin;

import java.security.PublicKey;

public class TransactionOutput {

    public String id; //This is a unique identifier for this TransactionOutput
    public PublicKey recipient; //Where the coins are being sent to
    public float value; //The amount of coins in this output
    public String parentTransactionId; //Reference to the transaction where this output started

    //Constructor
    public TransactionOutput(PublicKey recipient, float value, String parentTransactionId) {
        this.recipient = recipient;
        this.value = value;
        this.parentTransactionId = parentTransactionId;
        this.id = StringUtil.applySha256(StringUtil.getStringFromKey(recipient) + Float.toString(value) + parentTransactionId);
    }

    /**
     * isMine - This method is a helper method which is used to determine if a given transaction
     * belongs to a particular Wallet based on the publicKey.
     *
     * @param publicKey - This is the public key of the recipient.
     * @return - The method returns true if the public key of the transaction matches the public
     *           key of the recipient.
     */
    public boolean isMine(PublicKey publicKey) {
        return (publicKey == recipient);
    }

}
