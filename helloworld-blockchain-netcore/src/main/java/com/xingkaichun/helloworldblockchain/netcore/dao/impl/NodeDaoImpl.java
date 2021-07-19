package com.xingkaichun.helloworldblockchain.netcore.dao.impl;

import com.xingkaichun.helloworldblockchain.crypto.ByteUtil;
import com.xingkaichun.helloworldblockchain.netcore.dao.NodeDao;
import com.xingkaichun.helloworldblockchain.netcore.po.NodePo;
import com.xingkaichun.helloworldblockchain.netcore.service.NetCoreConfiguration;
import com.xingkaichun.helloworldblockchain.util.FileUtil;
import com.xingkaichun.helloworldblockchain.util.JsonUtil;
import com.xingkaichun.helloworldblockchain.util.KvDbUtil;
import com.xingkaichun.helloworldblockchain.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class NodeDaoImpl implements NodeDao {

    private static final String NODE_DATABASE_NAME = "NodeDatabase";
    private NetCoreConfiguration netCoreConfiguration;

    public NodeDaoImpl(NetCoreConfiguration netCoreConfiguration) {
        this.netCoreConfiguration = netCoreConfiguration;
    }

    @Override
    public NodePo queryNode(String ip){
        List<NodePo> nodePoList = queryAllNodeList();
        if(nodePoList != null){
            for(NodePo n: nodePoList){
                if(StringUtil.isEquals(ip,n.getIp())){
                    return n;
                }
            }
        }
        return null;
    }

    @Override
    public void addNode(NodePo node){
        KvDbUtil.put(getNodeDatabasePath(),getKeyByNodePo(node), encode(node));
    }

    @Override
    public void updateNode(NodePo node){
        KvDbUtil.put(getNodeDatabasePath(),getKeyByNodePo(node),encode(node));
    }

    @Override
    public void deleteNode(String ip){
        KvDbUtil.delete(getNodeDatabasePath(),getKeyByIp(ip));
    }

    @Override
    public List<NodePo> queryAllNodeList(){
        List<NodePo> list = new ArrayList<>();
        //获取所有
        List<byte[]> bytesNodePoList = KvDbUtil.gets(getNodeDatabasePath(),1,100000000);
        if(bytesNodePoList != null){
            for(byte[] bytesNodePo:bytesNodePoList){
                NodePo nodePo = decodeToNodePo(bytesNodePo);
                list.add(nodePo);
            }
        }
        return list;
    }
    private String getNodeDatabasePath(){
        return FileUtil.newPath(netCoreConfiguration.getNetCorePath(), NODE_DATABASE_NAME);
    }
    private byte[] getKeyByNodePo(NodePo node){
        return getKeyByIp(node.getIp());
    }
    private byte[] getKeyByIp(String ip){
        return ByteUtil.stringToUtf8Bytes(ip);
    }
    private byte[] encode(NodePo node){
        return ByteUtil.stringToUtf8Bytes(JsonUtil.toString(node));
    }
    private NodePo decodeToNodePo(byte[] bytesNodePo){
        return JsonUtil.toObject(ByteUtil.utf8BytesToString(bytesNodePo), NodePo.class);
    }
}
