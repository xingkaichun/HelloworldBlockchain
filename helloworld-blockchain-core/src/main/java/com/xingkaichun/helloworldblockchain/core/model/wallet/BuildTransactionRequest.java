package com.xingkaichun.helloworldblockchain.core.model.wallet;

import java.util.List;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class BuildTransactionRequest {

    //TODO 更恰当的命名
    private List<String> payerPrivateKeyList ;
    private List<Recipient> recipientList ;
    private long fee ;
    private String payerChangeAddress;


    //region get set

    public List<Recipient> getRecipientList() {
        return recipientList;
    }

    public void setRecipientList(List<Recipient> recipientList) {
        this.recipientList = recipientList;
    }

    public List<String> getPayerPrivateKeyList() {
        return payerPrivateKeyList;
    }

    public void setPayerPrivateKeyList(List<String> payerPrivateKeyList) {
        this.payerPrivateKeyList = payerPrivateKeyList;
    }

    public long getFee() {
        return fee;
    }

    public void setFee(long fee) {
        this.fee = fee;
    }

    public String getPayerChangeAddress() {
        return payerChangeAddress;
    }

    public void setPayerChangeAddress(String payerChangeAddress) {
        this.payerChangeAddress = payerChangeAddress;
    }
    //endregion
}
