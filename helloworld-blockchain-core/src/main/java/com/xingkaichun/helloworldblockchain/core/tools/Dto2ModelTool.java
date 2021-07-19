package com.xingkaichun.helloworldblockchain.core.tools;

import com.xingkaichun.helloworldblockchain.core.BlockchainDatabase;
import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.model.script.InputScript;
import com.xingkaichun.helloworldblockchain.core.model.script.OutputScript;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionInput;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionType;
import com.xingkaichun.helloworldblockchain.crypto.AccountUtil;
import com.xingkaichun.helloworldblockchain.netcore.dto.*;

import java.util.ArrayList;
import java.util.List;

/**
 * dto转model工具
 *
 * @author 邢开春 409060350@qq.com
 */
public class Dto2ModelTool {

    public static Block blockDto2Block(BlockchainDatabase blockchainDatabase, BlockDto blockDto) {
        String previousBlockHash = blockDto.getPreviousHash();
        Block previousBlock = blockchainDatabase.queryBlockByBlockHash(previousBlockHash);

        Block block = new Block();
        block.setTimestamp(blockDto.getTimestamp());
        block.setPreviousHash(previousBlockHash);
        block.setNonce(blockDto.getNonce());

        long blockHeight = BlockTool.getNextBlockHeight(previousBlock);
        block.setHeight(blockHeight);

        List<Transaction> transactionList = transactionDtos2Transactions(blockchainDatabase,blockDto.getTransactions());
        block.setTransactions(transactionList);

        String merkleTreeRoot = BlockTool.calculateBlockMerkleTreeRoot(block);
        block.setMerkleTreeRoot(merkleTreeRoot);

        String blockHash = BlockTool.calculateBlockHash(block);
        block.setHash(blockHash);

        String difficult = blockchainDatabase.getConsensus().calculateDifficult(blockchainDatabase,block);
        block.setDifficulty(difficult);

        fillBlockProperty(blockchainDatabase,block);
        /*
         * 预先校验区块工作量共识。伪造工作量共识是一件十分耗费资源的事情，因此预先校验工作量共识可以抵消绝大部分的攻击。
         * 也可以选择跳过此处预检，后续业务有完整的校验检测。
         * 此处预检，只是想预先抵消绝大部分的攻击。
         */
        if(!blockchainDatabase.getConsensus().checkConsensus(blockchainDatabase,block)){
            throw new RuntimeException("区块预检失败。");
        }
        return block;
    }

    private static List<Transaction> transactionDtos2Transactions(BlockchainDatabase blockchainDatabase, List<TransactionDto> transactionDtoList) {
        List<Transaction> transactions = new ArrayList<>();
        if(transactionDtoList != null){
            for(TransactionDto transactionDto:transactionDtoList){
                Transaction transaction = transactionDto2Transaction(blockchainDatabase,transactionDto);
                transactions.add(transaction);
            }
        }
        return transactions;
    }

    public static Transaction transactionDto2Transaction(BlockchainDatabase blockchainDatabase, TransactionDto transactionDto) {
        List<TransactionInput> inputs = new ArrayList<>();
        List<TransactionInputDto> transactionInputDtos = transactionDto.getInputs();
        if(transactionInputDtos != null){
            for (TransactionInputDto transactionInputDto:transactionInputDtos){
                TransactionOutput unspentTransactionOutput = blockchainDatabase.queryUnspentTransactionOutputByTransactionOutputId(transactionInputDto.getTransactionHash(),transactionInputDto.getTransactionOutputIndex());
                if(unspentTransactionOutput == null){
                    throw new RuntimeException("非法交易。交易输入并不是一笔未花费交易输出。");
                }
                TransactionInput transactionInput = new TransactionInput();
                transactionInput.setUnspentTransactionOutput(unspentTransactionOutput);
                transactionInput.setInputScript(inputScriptDto2InputScript(transactionInputDto.getInputScript()));
                inputs.add(transactionInput);
            }
        }

        List<TransactionOutput> outputs = new ArrayList<>();
        List<TransactionOutputDto> transactionOutputDtos = transactionDto.getOutputs();
        if(transactionOutputDtos != null){
            for(TransactionOutputDto transactionOutputDto:transactionOutputDtos){
                TransactionOutput transactionOutput = transactionOutputDto2TransactionOutput(transactionOutputDto);
                outputs.add(transactionOutput);
            }
        }

        Transaction transaction = new Transaction();
        TransactionType transactionType = obtainTransactionDto(transactionDto);
        transaction.setTransactionType(transactionType);
        transaction.setTransactionHash(TransactionDtoTool.calculateTransactionHash(transactionDto));
        transaction.setInputs(inputs);
        transaction.setOutputs(outputs);
        return transaction;
    }

    private static TransactionOutput transactionOutputDto2TransactionOutput(TransactionOutputDto transactionOutputDto) {
        TransactionOutput transactionOutput = new TransactionOutput();
        String publicKeyHash = ScriptDtoTool.getPublicKeyHashFromPayToPublicKeyHashOutputScript(transactionOutputDto.getOutputScript());
        String address = AccountUtil.addressFromPublicKeyHash(publicKeyHash);
        transactionOutput.setAddress(address);
        transactionOutput.setValue(transactionOutputDto.getValue());
        transactionOutput.setOutputScript(outputScriptDto2OutputScript(transactionOutputDto.getOutputScript()));
        return transactionOutput;
    }

    private static TransactionType obtainTransactionDto(TransactionDto transactionDto) {
        if(transactionDto.getInputs() == null || transactionDto.getInputs().size()==0){
            return TransactionType.GENESIS_TRANSACTION;
        }
        return TransactionType.STANDARD_TRANSACTION;
    }

    private static OutputScript outputScriptDto2OutputScript(OutputScriptDto outputScriptDto) {
        if(outputScriptDto == null){
            return null;
        }
        OutputScript outputScript = new OutputScript();
        outputScript.addAll(outputScriptDto);
        return outputScript;
    }

    private static InputScript inputScriptDto2InputScript(InputScriptDto inputScriptDto) {
        if(inputScriptDto == null){
            return null;
        }
        InputScript inputScript = new InputScript();
        inputScript.addAll(inputScriptDto);
        return inputScript;
    }

    /**
     * 补充区块的属性
     */
    private static void fillBlockProperty(BlockchainDatabase blockchainDatabase,Block block) {
        long transactionIndex = 0;
        long transactionHeight = blockchainDatabase.queryBlockchainTransactionHeight();
        long transactionOutputHeight = blockchainDatabase.queryBlockchainTransactionOutputHeight();
        long blockHeight = block.getHeight();
        String blockHash = block.getHash();
        List<Transaction> transactions = block.getTransactions();
        long transactionCount = BlockTool.getTransactionCount(block);
        block.setTransactionCount(transactionCount);
        block.setPreviousTransactionHeight(transactionHeight);
        if(transactions != null){
            for(Transaction transaction:transactions){
                transactionIndex++;
                transactionHeight++;
                transaction.setBlockHeight(blockHeight);
                transaction.setTransactionIndex(transactionIndex);
                transaction.setTransactionHeight(transactionHeight);

                List<TransactionOutput> outputs = transaction.getOutputs();
                if(outputs != null){
                    for (int i=0; i <outputs.size(); i++){
                        transactionOutputHeight++;
                        TransactionOutput output = outputs.get(i);
                        output.setBlockHeight(blockHeight);
                        output.setBlockHash(blockHash);
                        output.setTransactionHeight(transactionHeight);
                        output.setTransactionHash(transaction.getTransactionHash());
                        output.setTransactionOutputIndex(i+1);
                        output.setTransactionIndex(transaction.getTransactionIndex());
                        output.setTransactionOutputHeight(transactionOutputHeight);
                    }
                }
            }
        }
    }
}
