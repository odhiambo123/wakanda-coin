/**
 * Wallet.java
 *
 * Group Alpha - CMSC 495
 * Summer 2018 - Section 7381
 * 7/22/2018
 *
 * The Wallet class is used to keep track of a user's coins and send coins to another user (wallet).
 * Inside of the Wallet class, the public and private keys are generated for the Wallet but
 * essentially these are the keys for the user.
 *
 * Note: This code is based on the article at:
 * https://medium.com/programmers-blockchain/creating-your-first-blockchain-with-java-part-2-transactions-2cdac335e0ce
 *
 */

package wakandacoin;

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
//import org.bouncycastle.*;


public class Wallet {
    public PrivateKey privateKey;
    public PublicKey publicKey;
    public HashMap<String,TransactionOutput> UTXOs = new HashMap<>(); //only UTXOs owned by this wallet.

    //Constructor
    public Wallet(){
        generateKeyPair();
    }

    /**
     * This method is used to generate the public and private keys for the Wallet. The keys are
     * generated using Elliptic Curve Digital Signature Algorithm. A 256-bit domain parameter is
     * used.
     */
    public void generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
            //KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp256r1"); //256-bit

            // Initialize the key generator and generate a KeyPair
            keyGen.initialize(ecSpec, random);   //256 bytes provides an acceptable security level
            KeyPair keyPair = keyGen.generateKeyPair();

            // Set the public and private keys from the keyPair
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    } // end method generateKeyPair()

    /**
     * This method calculates the Wallet's available balance by comparing unspent transactions
     * in the main coin class, WakandaCoin, seeing if the public key matches, and if so adding
     * those TransactionOutput objects to the HashMap UTXOs in the Wallet. While iterating through
     * TransactionOutput objects, this method sums the float value of those objects (coins).
     *
     * @return - A float representation of the sum of the coin value in the unspent TransactionOutput
     *           objects for this Wallet.
     */
    public float getBalance() {
        float total = 0;
        for (Map.Entry<String, TransactionOutput> item : WakandaCoin.unspentTxs.entrySet()) {
            TransactionOutput UTXO = item.getValue();
            if (UTXO.isMine(publicKey)) { //if output belongs to me ( if coins belong to me )
                UTXOs.put(UTXO.id, UTXO); //add it to our list of unspent transactions.
                total += UTXO.value;
            }
        }
        return total;
    } // end method getBalance()


    /**
     * This method sends coins to a recipient by essentially iterating through unspent transactions,
     * confirming that enough coins are available and if so, a new Transaction object with the
     * details of the transaction. This method makes use of the getBalance() helper method to
     * confirm the available coin balance is sufficient prior to proceeding with the transaction.
     *
     * @param _recipient - This is the public key of the Wallet that coins are being sent to.
     * @param value - This is the float value of the number of coins being sent.
     * @return - A Transaction object is returned with the details of the transaction or null is
     *           returned if there weren't enough coins.
     */
    public Transaction sendFunds(PublicKey _recipient, float value) {
        if (getBalance() < value) { //gather balance and check funds.
            System.out.println("#Not Enough funds to send transaction. Transaction Discarded.");
            return null;
        }
        //create array list of inputs
        ArrayList<TransactionInput> inputs = new ArrayList<>();

        float total = 0;
        for (Map.Entry<String, TransactionOutput> item : UTXOs.entrySet()) {
            TransactionOutput UTXO = item.getValue();
            total += UTXO.value;
            inputs.add(new TransactionInput(UTXO.id));
            if (total > value) {
                break;
            }
        }

        Transaction newTransaction = new Transaction(publicKey, _recipient, value, inputs);
        newTransaction.generateSignature(privateKey);

        for (TransactionInput input : inputs) {
            UTXOs.remove(input.transactionOutputId);
        }
        return newTransaction;
    } // end method sendFunds(PublicKey _recipient, float value)

} // end class Wallet
