package com.xingkaichun.helloworldblockchain.core.tools;

import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.helloworldblockchain.crypto.ByteUtil;
import com.xingkaichun.helloworldblockchain.crypto.model.Account;
import com.xingkaichun.helloworldblockchain.netcore.dto.TransactionDto;
import com.xingkaichun.helloworldblockchain.util.JsonUtil;
import com.xingkaichun.helloworldblockchain.util.LogUtil;

/**
 * EncodeDecode工具类
 *
 * @author 邢开春 409060350@qq.com
 */
public class EncodeDecodeTool {

    public static byte[] encodeTransaction(Transaction transaction) {
        try {
            return ByteUtil.stringToUtf8Bytes(JsonUtil.toString(transaction));
        } catch (Exception e) {
            LogUtil.error("serialize Transaction failed.",e);
            throw new RuntimeException(e);
        }
    }
    public static Transaction decodeToTransaction(byte[] bytesTransaction) {
        try {
            return JsonUtil.toObject(ByteUtil.utf8BytesToString(bytesTransaction),Transaction.class);
        } catch (Exception e) {
            LogUtil.error("deserialize Transaction failed.",e);
            throw new RuntimeException(e);
        }
    }


    public static byte[] encodeTransactionOutput(TransactionOutput transactionOutput) {
        try {
            return ByteUtil.stringToUtf8Bytes(JsonUtil.toString(transactionOutput));
        } catch (Exception e) {
            LogUtil.error("serialize TransactionOutput failed.",e);
            throw new RuntimeException(e);
        }
    }
    public static TransactionOutput decodeToTransactionOutput(byte[] bytesTransactionOutput) {
        try {
            return JsonUtil.toObject(ByteUtil.utf8BytesToString(bytesTransactionOutput),TransactionOutput.class);
        } catch (Exception e) {
            LogUtil.error("deserialize TransactionOutput failed.",e);
            throw new RuntimeException(e);
        }
    }


    public static byte[] encodeBlock(Block block) {
        try {
            return ByteUtil.stringToUtf8Bytes(JsonUtil.toString(block));
        } catch (Exception e) {
            LogUtil.error("serialize Block failed.",e);
            throw new RuntimeException(e);
        }
    }
    public static Block decodeToBlock(byte[] bytesBlock) {
        try {
            return JsonUtil.toObject(ByteUtil.utf8BytesToString(bytesBlock),Block.class);
        } catch (Exception e) {
            LogUtil.error("deserialize Block failed.",e);
            throw new RuntimeException(e);
        }
    }


    public static byte[] encodeTransactionDto(TransactionDto transactionDto) {
        try {
            return ByteUtil.stringToUtf8Bytes(JsonUtil.toString(transactionDto));
        } catch (Exception e) {
            LogUtil.error("serialize TransactionDto failed.",e);
            throw new RuntimeException(e);
        }
    }
    public static TransactionDto decodeToTransactionDto(byte[] bytesTransactionDto) {
        try {
            return JsonUtil.toObject(ByteUtil.utf8BytesToString(bytesTransactionDto), TransactionDto.class);
        } catch (Exception e) {
            LogUtil.error("deserialize TransactionDto failed.",e);
            throw new RuntimeException(e);
        }
    }


    public static byte[] encodeAccount(Account account) {
        try {
            return ByteUtil.stringToUtf8Bytes(JsonUtil.toString(account));
        } catch (Exception e) {
            LogUtil.error("serialize Transaction failed.",e);
            throw new RuntimeException(e);
        }
    }
    public static Account decodeToAccount(byte[] bytesAccount) {
        try {
            return JsonUtil.toObject(ByteUtil.utf8BytesToString(bytesAccount),Account.class);
        } catch (Exception e) {
            LogUtil.error("deserialize Account failed.",e);
            throw new RuntimeException(e);
        }
    }
}
