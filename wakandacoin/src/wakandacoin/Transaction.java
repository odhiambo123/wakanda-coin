/**
 * Transaction.java
 *
 * Group Alpha - CMSC 495
 * Summer 2018 - Section 7381
 * 7/21/2018
 *
 * The Transaction class is the main class that represents a transaction on the blockchain. A
 * transaction is represented by an ID, sender, recipient, value, and a SHA256 signature.
 *
 *
 * Note: This code is based on the article at:
 * https://medium.com/programmers-blockchain/creating-your-first-blockchain-with-java-part-2-transactions-2cdac335e0ce
 *
 */

package wakandacoin;

import java.security.*;
import java.util.ArrayList;

public class Transaction {

    public String transactionId; // this is also the hash of the transaction.
    public PublicKey sender; // The sender's public key.
    public PublicKey recipient; // Recipients address/public key.
    public float value;
    public byte[] signature; // This represents a digitally signed signature of the transaction.

    public ArrayList<TransactionInput> inputs = new ArrayList<>();
    public ArrayList<TransactionOutput> outputs = new ArrayList<>();

    private static int sequence = 0; // a rough count of how many transactions have been generated.

    // Constructor:
    public Transaction(PublicKey from, PublicKey to, float value, ArrayList<TransactionInput> inputs) {
        this.sender = from;
        this.recipient = to;
        this.value = value;
        this.inputs = inputs;
    }


    /**
     * This method is used to generate a SHA-256 hash that will be used as the transaction ID.
     * The hash is calculated based on String representations of the public keys of the sender and
     * recipient, and also a String representation of the value (coins) of the transaction plus
     * a String of a static integer sequence, which gets incremented during each call of
     * calculateHash in order to ensure no two transactions can have the same hash.
     *
     * @return - The SHA-256 hash of the transaction ID is returned as a String.
     */
    private String calculateHash() {
        sequence++; //increase the sequence to avoid 2 identical transactions having the same hash
        return StringUtil.applySha256(
                StringUtil.getStringFromKey(sender)
                        + StringUtil.getStringFromKey(recipient)
                        + Float.toString(value) + sequence
        );
    } //end method calculateHash()

    /**
     * This method is used to assign a value to the signature variable using an Elliptic Curve,
     * Digital Signature Algorithm signed with the private key of the sender of the transaction.
     * This signature is used for validating transactions on the blockchain so that they cannot
     * be tampered with. Any modification to a transaction will invalidate the signature.
     *
     * @param privateKey - This parameter represents the private key of the sender of the transaction.
     */
    public void generateSignature(PrivateKey privateKey) {
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient) + Float.toString(value);
        signature = StringUtil.applyECDSASig(privateKey, data);
    } // end method generateSignature(PrivateKey privateKey)

    //Verifies the data we signed hasnt been tampered with

    /**
     *  This method is used to verify the signature of a transaction. It does its verification
     *  by calling a helper method in the StringUtil class, verifyECDSASig, passing in the sender's
     *  public key, a String representation of the sender and recipient's public keys plus the coin
     *  (value), and the transaction's signature.
     *
     * @return - The method will return true if the String data calculated matches the signature and
     *           false otherwise.
     */
    public boolean verifySignature() {
        String data = StringUtil.getStringFromKey(sender) +
                StringUtil.getStringFromKey(recipient) +
                Float.toString(value);
        return StringUtil.verifyECDSASig(sender, data, signature);
    } // end method verifySignature

    //Returns true if new transaction could be created.

    /**
     * This method does the bulk of the work to actually process a transaction. It performs a number
     * of validation checks to ensure that a transaction is valid before being fully processed: 1)
     * the signature is verified, 2) the transactionInputs are retrieved from the HashMap of
     * all unspent transactions in the main coin class WakandaCoin, 3) the transaction must be at
     * least .1 coin. Once those checks are passed, the transaction is processed which sends the coins
     * to the recipient and removes the coins from the sender and updates the master list of unspent
     * transactions.
     *
     * @return - Returns false if the signature is invalid or the transaction is too small, otherwise
     *           returns true once the transaction is completed.
     */
    public boolean processTransaction() {

        if (verifySignature() == false) {
            System.out.println("#Transaction Signature failed to verify");
            return false;
        }

        //gather transaction inputs (Make sure they are unspent):
        for (TransactionInput i : inputs) {
            i.UTXO = WakandaCoin.unspentTxs.get(i.transactionOutputId);
        }

        //check if transaction is valid:
        if (getInputsValue() < WakandaCoin.minimumTransaction) {
            System.out.println("#Transaction Inputs to small: " + getInputsValue());
            return false;
        }

        //generate transaction outputs:
        float leftOver = getInputsValue() - value; //get value of inputs then the left over change:
        transactionId = calculateHash();
        outputs.add(new TransactionOutput(this.recipient, value, transactionId)); //send value to recipient
        outputs.add(new TransactionOutput(this.sender, leftOver, transactionId)); //send the left over 'change' back to sender

        //add outputs to Unspent list
        for (TransactionOutput o : outputs) {
            WakandaCoin.unspentTxs.put(o.id, o);
        }

        //remove transaction inputs from UTXO lists as spent:
        for (TransactionInput i : inputs) {
            if (i.UTXO == null) {
                continue; //if Transaction can't be found skip it
            }
            WakandaCoin.unspentTxs.remove(i.UTXO.id);
        }

        return true;
    } // end method processTransaction()

    //returns sum of inputs(UTXOs) values

    /**
     * This is a helper method which is used by the processTransaction() method. This method
     * iterates through the array list of TransactionInputs and calculates the value of each
     * unspent transaction and stores the value in a float.
     *
     * @return - This method returns the float value of all the unspent transactions or 0 if there
     *           aren't any.
     */
    public float getInputsValue() {
        float total = 0;
        for (TransactionInput i : inputs) {
            if (i.UTXO == null) {
                continue; //if Transaction can't be found skip it
            }
            total += i.UTXO.value;
        }
        return total;
    } // end method getInputsValue()
    

    /**
     * This method is used to calculate the value, in float, of all the TransactionOuputs that 
     * are linked to this transaction. It iterates through the array list of TransactionOutput outputs.
     * 
     * 
     * @return - A float representation of the sum of all the TransactionOutput objects linked to 
     *           this transaction or 0.0 if there aren't any.
     *           
     */
    public float getOutputsValue() {
        float total = 0;
        for (TransactionOutput o : outputs) {
            total += o.value;
        }
        return total;
    } // end method getOutputsValue()
} //end class Transaction
