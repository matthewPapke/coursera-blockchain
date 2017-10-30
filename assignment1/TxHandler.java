import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;

public class TxHandler {
    public UTXOPool utxoPool;
    /**
     * Creates a public ledger whose current UTXOPool (collection of unspent transaction outputs) is
     * {@code utxoPool}. This should make a copy of utxoPool by using the UTXOPool(UTXOPool uPool)
     * constructor.
     */
    public TxHandler(UTXOPool utxoPool) {
        this.utxoPool = new UTXOPool(utxoPool);
    }

    /**
     * @return true if:
     * (1) all outputs claimed by {@code tx} are in the current UTXO pool, 
     * (2) the signatures on each input of {@code tx} are valid, 
     * (3) no UTXO is claimed multiple times by {@code tx},
     * (4) all of {@code tx}s output values are non-negative, and
     * (5) the sum of {@code tx}s input values is greater than or equal to the sum of its output
     *     values; and false otherwise.
     */
    public boolean isValidTx(Transaction tx) {
        return allOutputsUnspent(tx)
            && signaturesValid(tx)
            && noMultipleClaims(tx)
            && nonNegativeOutputs(tx.getOutputs())
            && positiveBalance(tx.getInputs(), tx.getOutputs());
    }

    /**
     * Handles each epoch by receiving an unordered array of proposed transactions, checking each
     * transaction for correctness, returning a mutually valid array of accepted transactions, and
     * updating the current UTXO pool as appropriate.
     */
    public Transaction[] handleTxs(Transaction[] possibleTxs) {
        Transaction[] transactions = {};
        return transactions;
    }

    private boolean allOutputsUnspent(Transaction tx){
        List<UTXO> allUtxo = utxoPool.getAllUTXO();
        List<Transaction.Output> utxoOutputs = new ArrayList<Transaction.Output>();
        for(UTXO ut : allUtxo){
            Transaction.Output output = this.utxoPool.getTxOutput(ut);
            utxoOutputs.add(output);
        }

        List<Transaction.Output> outputs = tx.getOutputs();
        for(Transaction.Output output : outputs){
            //utxoPool contains output
            if(!utxoOutputs.contains(output)){
                return false;
            }
        }
        return true;    
    }

    private boolean signaturesValid(Transaction tx){
        List<Transaction.Input> inputs = tx.getInputs();
        for(Transaction.Input input : inputs){
            UTXO ut = new UTXO(input.prevTxHash, input.outputIndex);
            Transaction.Output output = this.utxoPool.getTxOutput(ut);
//            if(!(Crypto.verifySignature(output.address, tx.getRawDataToSign(input.outputIndex), input.signature))){
//                return false;
//            }
        }
        return true;
    }

    private boolean noMultipleClaims(Transaction tx){
        List<Transaction.Input> inputs = tx.getInputs();
        //hashsets are unique
        HashSet<UTXO> claimedUtxos = new HashSet<UTXO>();
        for(Transaction.Input input : inputs){
            UTXO ut = new UTXO(input.prevTxHash, input.outputIndex);
            claimedUtxos.add(ut);
        }

        return inputs.size() == claimedUtxos.size();
    }

    private boolean nonNegativeOutputs(ArrayList<Transaction.Output> outputs){
        for(Transaction.Output output : outputs){
            if(output.value < 0){
                return false;
            }
        }
        return true;
    }

    //sum inputs greater than sum outputs
    private boolean positiveBalance(ArrayList<Transaction.Input> inputs, ArrayList<Transaction.Output> outputs){
        double inputSum = 0;
        for(Transaction.Input input : inputs){
            UTXO ut = new UTXO(input.prevTxHash, input.outputIndex);
            Transaction.Output output = utxoPool.getTxOutput(ut);
            inputSum += output.value;
        }

        double outputSum = 0;
        for(Transaction.Output output : outputs){
            outputSum += output.value;
        }

        return inputSum >= outputSum;
    }
}
