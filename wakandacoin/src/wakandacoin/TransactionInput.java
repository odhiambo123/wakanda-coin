/**
 * TransactionInput.java
 *
 * Group Alpha - CMSC 495
 * Summer 2018 - Section 7381
 * 7/21/2018
 *
 * The TransactionInput class represents a prior transaction. During the processing of a transaction
 * in the method Transaction.processTransaction(), the unspent transaction UTXO, is populated.
 *
 * The class does not have any methods and only includes the constructor.
 *
 * Note: This code is based on the article at:
 * https://medium.com/programmers-blockchain/creating-your-first-blockchain-with-java-part-2-transactions-2cdac335e0ce
 *
 */

package wakandacoin;

public class TransactionInput {

    public String transactionOutputId; //Reference to TransactionOutputs -> transactionId
    public TransactionOutput UTXO; //Contains the Unspent transaction output

    public TransactionInput(String transactionOutputId) {
        this.transactionOutputId = transactionOutputId;
    }
}
