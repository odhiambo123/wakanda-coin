/**
 * WakandaCoin.java
 *
 * Group Alpha - CMSC 495
 * Summer 2018 - Section 7381
 * 7/22/2018
 *
 * The WakandaCoin class is the main class for the project. This class instantiates the blockchain,
 * and uses methods from the other classes to create Wallets, create Transactions (which add new
 * blocks to the blockchain), and validates the integrity of the block.
 *
 * Note: This code is based on the article at:
 * https://medium.com/programmers-blockchain/creating-your-first-blockchain-with-java-part-2-transactions-2cdac335e0ce
 *
 */

package wakandacoin;

//import com.sun.jna.Function;
//import com.sun.jna.NativeLibrary;
//import com.sun.jna.Pointer;
//import com.sun.jna.platform.win32.WinNT.HANDLE;
import java.util.ArrayList;
import java.security.Security;
import java.util.HashMap;

public class WakandaCoin {

    public static ArrayList<Block> blockchain = new ArrayList<>();
    public static HashMap<String,TransactionOutput> unspentTxs =
            new HashMap<>();

    public static int difficulty = 5;
    public static float minimumTransaction = 0.1f;

    public static Wallet walletA;
    public static Wallet walletB;

    public static Transaction genesisTransaction;


    public static void main(String[] args) {

        /** basic example
         Block genesisBlock = new Block("Hi im the first block", "0");
         System.out.println("Hash for block 1 : " + genesisBlock.hash);

         Block secondBlock = new Block("Yo im the second block", genesisBlock.hash);
         System.out.println("Hash for block 2 : " + secondBlock.hash);

         Block thirdBlock = new Block("Hey im the third block", secondBlock.hash);
         System.out.println("Hash for block 3 : " + thirdBlock.hash);
         **/


        //ArrayList implementation
        /**
         blockchain.add(new Block("First block.", "0"));
         System.out.println("Mining first block");
         blockchain.get(0).mineBlock(difficulty);

         blockchain.add(new Block("The second block", blockchain.get(blockchain.size() -1).hash));
         System.out.println("Mining second block");
         blockchain.get(1).mineBlock(difficulty);

         blockchain.add(new Block("The third block", blockchain.get(blockchain.size() -1).hash));
         System.out.println("Mining third block");
         blockchain.get(2).mineBlock(difficulty);

         System.out.println("\nBlockchain is Valid: " + isChainValid());

         //JSON representation of output
         String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
         System.out.println("\nThe block chain: ");
         System.out.println(blockchainJson);
         **/

        //System.out.println("This app is running on " + System.getProperty("os.name"));
        /**
         * I originally put this section of code in when we first started the project and were
         * going with a Java app but not on the Android platform. The idea behind this was to 
         * look at the local certificate store on the PC, and find a valid certificate. Then 
         * once a certificate was found, whenever the user wanted to open their wallet or 
         * send a coins, we'd actually use the appropriate keys from the certificate. After we
         * discussed in Week4 to do an Android app, I scrapped this approach.  
         * 
         * -Shawn
         * 
        String os = System.getProperty("os.name");
        if (os.matches("^[Windows].*")) {
            System.out.println("Windows version: " + System.getProperty("os.name"));

            //Read from User's cert store in Windows

            NativeLibrary cryptUI = NativeLibrary.getInstance("Cryptui");
            NativeLibrary crypt32 = NativeLibrary.getInstance("Crypt32");
            Function functionCertOpenSystemStore = crypt32.getFunction("CertOpenSystemStoreA");
            Object[] argsCertOpenSystemStore = new Object[]{0, "MY"};
            HANDLE h = (HANDLE) functionCertOpenSystemStore.invoke(HANDLE.class, argsCertOpenSystemStore);

            Function functionCryptUIDlgSelectCertificateFromStore = cryptUI.getFunction("CryptUIDlgSelectCertificateFromStore");
            System.out.println(functionCryptUIDlgSelectCertificateFromStore.getName());
            Object[] argsCryptUIDlgSelectCertificateFromStore = new Object[]{h, 0, 0, 0, 16, 0, 0};
            Pointer ptrCertContext = (Pointer) functionCryptUIDlgSelectCertificateFromStore.invoke(Pointer.class, argsCryptUIDlgSelectCertificateFromStore);

            Function functionCertGetNameString = crypt32.getFunction("CertGetNameStringW");
            char[] ptrName = new char[128];
            Object[] argsCertGetNameString = new Object[]{ptrCertContext, 5, 0, 0, ptrName, 128};
            functionCertGetNameString.invoke(argsCertGetNameString);
            System.out.println("Selected certificate is " + new String(ptrName));

            Function functionCertFreeCertificateContext = crypt32.getFunction("CertFreeCertificateContext");
            Object[] argsCertFreeCertificateContext = new Object[]{ptrCertContext};
            functionCertFreeCertificateContext.invoke(argsCertFreeCertificateContext);

            Function functionCertCloseStore = crypt32.getFunction("CertCloseStore");
            Object[] argsCertCloseStore = new Object[]{h, 0};
            functionCertCloseStore.invoke(argsCertCloseStore);
        }
         **/

        //More complex example with wallets
        //Setup Bouncey castle as a Security Provider
        // #### figure out how to get bouncycastle to work on Android
        // Done by changing view in Android Studio from Android to Project, then imported
        // the jar
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());


        //Create the new wallets
        walletA = new Wallet();
        walletB = new Wallet();
        Wallet coinbase = new Wallet();

        //create genesis transaction, which sends 100 NoobCoin to walletA:
        genesisTransaction = new Transaction(coinbase.publicKey, walletA.publicKey, 100f, null);
        genesisTransaction.generateSignature(coinbase.privateKey);	 //manually sign the genesis transaction
        genesisTransaction.transactionId = "0"; //manually set the transaction id
        genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.recipient, genesisTransaction.value, genesisTransaction.transactionId)); //manually add the Transactions Output
        unspentTxs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0)); //its important to store our first transaction in the UTXOs list.

        System.out.println("Creating and Mining Genesis block... ");
        Block genesis = new Block("0");
        genesis.addTransaction(genesisTransaction);
        addBlock(genesis);

        Block block1 = new Block(genesis.hash);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("\nWalletA is Attempting to send funds (40) to WalletB...");
        block1.addTransaction(walletA.sendFunds(walletB.publicKey, 40f));
        addBlock(block1);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        Block block2 = new Block(block1.hash);
        System.out.println("\nWalletA Attempting to send more funds (1000) than it has...");
        block2.addTransaction(walletA.sendFunds(walletB.publicKey, 1000f));
        addBlock(block2);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        Block block3 = new Block(block2.hash);
        System.out.println("\nWalletB is Attempting to send funds (20) to WalletA...");
        block3.addTransaction(walletB.sendFunds(walletA.publicKey, 20));
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        isChainValid();





        //Test public and private keys
        /**
         System.out.println("Private and public keys:");
         System.out.println(StringUtil.getStringFromKey(walletA.privateKey));
         System.out.println(StringUtil.getStringFromKey(walletA.publicKey));
         //Create a test transaction from WalletA to walletB
         Transaction transaction = new Transaction(walletA.publicKey, walletB.publicKey, 5, null);
         transaction.generateSignature(walletA.privateKey);
         //Verify the signature works and verify it from the public key
         System.out.println("Is signature verified");
         System.out.println(transaction.verifiySignature());
         **/

    } //end main

    /**
     public static Boolean isChainValid() {
     Block currentBlock;
     Block previousBlock;
     String hashTarget = new String(new char[difficulty]).replace('\0', '0');

     //loop through blockchain to check hashes:
     for (int i = 1; i < blockchain.size(); i++) {
     currentBlock = blockchain.get(i);
     previousBlock = blockchain.get(i - 1);
     //compare registered hash and calculated hash:
     if (!currentBlock.hash.equals(currentBlock.calculateHash())) {
     System.out.println("Current Hashes not equal");
     return false;
     }
     //compare previous hash and registered previous hash
     if (!previousBlock.hash.equals(currentBlock.previousHash)) {
     System.out.println("Previous Hashes not equal");
     return false;
     }
     //check if hash is solved
     if (!currentBlock.hash.substring(0, difficulty).equals(hashTarget)) {
     System.out.println("This block hasn't been mined");
     return false;
     }
     }
     return true;
     } //end method isChainValid()
     **/

    /**
     * This method will test the validity of the blockchain to make sure it hasn't been tampered with.
     * It does this by checking signature hashes of each block and the hashes of the previous transactions.
     * Each block is checked to make sure they've been mined and the details of each Transaction are
     * verified to make sure signatures, inputs, outputs, values, sender, and recipient have not been
     * modified. Any modification to any part of the blockchain, invalidates the entire blockchain.
     * 
     * @return - This method returns true if all the verification checks pass for the blockchain and
     *           returns false if a single test fails.
     */
    public static Boolean isChainValid() {
        Block currentBlock;
        Block previousBlock;
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');
        HashMap<String, TransactionOutput> tempUTXOs = new HashMap<String, TransactionOutput>(); //a temporary working list of unspent transactions at a given block state.
        tempUTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));

        //loop through blockchain to check hashes:
        for (int i = 1; i < blockchain.size(); i++) {

            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i - 1);
            //compare registered hash and calculated hash:
            if (!currentBlock.hash.equals(currentBlock.calculateHash())) {
                System.out.println("#Current Hashes not equal");
                return false;
            }
            //compare previous hash and registered previous hash
            if (!previousBlock.hash.equals(currentBlock.previousHash)) {
                System.out.println("#Previous Hashes not equal");
                return false;
            }
            //check if hash is solved
            if (!currentBlock.hash.substring(0, difficulty).equals(hashTarget)) {
                System.out.println("#This block hasn't been mined");
                return false;
            }

            //loop thru blockchains transactions:
            TransactionOutput tempOutput;
            for (int t = 0; t < currentBlock.transactions.size(); t++) {
                Transaction currentTransaction = currentBlock.transactions.get(t);

                if (!currentTransaction.verifySignature()) {
                    System.out.println("#Signature on Transaction(" + t + ") is Invalid");
                    return false;
                }
                if (currentTransaction.getInputsValue() != currentTransaction.getOutputsValue()) {
                    System.out.println("#Inputs are note equal to outputs on Transaction(" + t + ")");
                    return false;
                }

                for (TransactionInput input : currentTransaction.inputs) {
                    tempOutput = tempUTXOs.get(input.transactionOutputId);

                    if (tempOutput == null) {
                        System.out.println("#Referenced input on Transaction(" + t + ") is Missing");
                        return false;
                    }

                    if (input.UTXO.value != tempOutput.value) {
                        System.out.println("#Referenced input Transaction(" + t + ") value is Invalid");
                        return false;
                    }

                    tempUTXOs.remove(input.transactionOutputId);
                }

                for (TransactionOutput output : currentTransaction.outputs) {
                    tempUTXOs.put(output.id, output);
                }

                if (currentTransaction.outputs.get(0).recipient != currentTransaction.recipient) {
                    System.out.println("#Transaction(" + t + ") output recipient is not who it should be");
                    return false;
                }
                if (currentTransaction.outputs.get(1).recipient != currentTransaction.sender) {
                    System.out.println("#Transaction(" + t + ") output 'change' is not sender.");
                    return false;
                }

            }

        }
        System.out.println("Blockchain is valid");
        return true;
    } // end method isValid()


    /**
     * This method adds a new Block to the blockchain by first mining the block, then adding it to 
     * the blockchain, in this case represented by an ArrayList.
     * 
     * @param newBlock - This is a Block object which contains Transactions.
     */
    public static void addBlock(Block newBlock) {
        newBlock.mineBlock(difficulty);
        blockchain.add(newBlock);
    } // end method addBlock(Block newBlock)
} // end class WakandaCoin
