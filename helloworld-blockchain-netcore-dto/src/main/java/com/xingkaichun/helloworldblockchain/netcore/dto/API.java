package com.xingkaichun.helloworldblockchain.netcore.dto;

/**
 * 节点接口常量
 *
 * @author 邢开春 409060350@qq.com
 */
public class API {

    public static final String PING = "/ping";

    public static final String GET_NODES = "/get_nodes";

    public static final String GET_BLOCKCHAIN_HEIGHT = "/get_blockchain_height";
    public static final String POST_BLOCKCHAIN_HEIGHT = "/post_blockchain_height";

    public static final String GET_BLOCK = "/get_block";
    public static final String POST_BLOCK = "/post_block";

    public static final String GET_UNCONFIRMED_TRANSACTIONS = "/get_unconfirmed_transactions";
    public static final String POST_TRANSACTION = "/post_transaction";

}
