package com.xingkaichun.helloworldblockchain.core.tools;

import com.xingkaichun.helloworldblockchain.core.model.script.OperationCodeEnum;
import com.xingkaichun.helloworldblockchain.crypto.ByteUtil;
import com.xingkaichun.helloworldblockchain.netcore.dto.InputScriptDto;
import com.xingkaichun.helloworldblockchain.netcore.dto.OutputScriptDto;
import com.xingkaichun.helloworldblockchain.netcore.dto.ScriptDto;
import com.xingkaichun.helloworldblockchain.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 脚本工具类
 *
 * @author 邢开春 409060350@qq.com
 */
public class ScriptDtoTool {

    //region 序列化与反序列化
    public static byte[] bytesInputScript(InputScriptDto inputScript) {
        return bytesScript(inputScript);
    }
    public static byte[] bytesOutputScript(OutputScriptDto outputScript) {
        return bytesScript(outputScript);
    }
    /**
     * 字节型脚本：将脚本序列化，要求序列化后的脚本可以反序列化。
     */
    public static byte[] bytesScript(ScriptDto script) {
        byte[] bytesScript = new byte[0];
        for(int i=0;i<script.size();i++){
            String operationCode = script.get(i);
            byte[] bytesOperationCode = ByteUtil.hexStringToBytes(operationCode);
            if(ByteUtil.isEquals(OperationCodeEnum.OP_DUP.getCode(),bytesOperationCode) ||
                    ByteUtil.isEquals(OperationCodeEnum.OP_HASH160.getCode(),bytesOperationCode) ||
                    ByteUtil.isEquals(OperationCodeEnum.OP_EQUALVERIFY.getCode(),bytesOperationCode) ||
                    ByteUtil.isEquals(OperationCodeEnum.OP_CHECKSIG.getCode(),bytesOperationCode)){
                bytesScript = ByteUtil.concatenate(bytesScript, ByteUtil.concatenateLength(bytesOperationCode));
            }else if(ByteUtil.isEquals(OperationCodeEnum.OP_PUSHDATA.getCode(),bytesOperationCode)){
                String operationData = script.get(++i);
                byte[] bytesOperationData = ByteUtil.hexStringToBytes(operationData);
                bytesScript = ByteUtil.concatenate3(bytesScript, ByteUtil.concatenateLength(bytesOperationCode), ByteUtil.concatenateLength(bytesOperationData));
            }else {
                throw new RuntimeException("不能识别的指令");
            }
        }
        return bytesScript;
    }
    /**
     * 脚本：将字节型脚本反序列化为脚本.
     */
    public static InputScriptDto inputScriptDto(byte[] bytesScript) {
        if(bytesScript == null || bytesScript.length == 0){
            return null;
        }
        InputScriptDto inputScriptDto = new InputScriptDto();
        List<String> script = script(bytesScript);
        inputScriptDto.addAll(script);
        return inputScriptDto;
    }
    /**
     * 脚本：将字节型脚本反序列化为脚本.
     */
    public static OutputScriptDto outputScriptDto(byte[] bytesScript) {
        if(bytesScript == null || bytesScript.length == 0){
            return null;
        }
        OutputScriptDto outputScriptDto = new OutputScriptDto();
        List<String> script = script(bytesScript);
        outputScriptDto.addAll(script);
        return outputScriptDto;
    }
    /**
     * 脚本：将字节型脚本反序列化为脚本.
     */
    private static List<String> script(byte[] bytesScript) {
        if(bytesScript == null || bytesScript.length == 0){
            return null;
        }
        int start = 0;
        List<String> script = new ArrayList<>();
        while (start<bytesScript.length){
            long bytesOperationCodeLength = ByteUtil.bytesToUint64(Arrays.copyOfRange(bytesScript,start,start + ByteUtil.BYTE8_BYTE_COUNT));
            start += ByteUtil.BYTE8_BYTE_COUNT;
            byte[] bytesOperationCode = Arrays.copyOfRange(bytesScript,start, start+(int) bytesOperationCodeLength);
            start += bytesOperationCodeLength;
            if(ByteUtil.isEquals(OperationCodeEnum.OP_DUP.getCode(),bytesOperationCode) ||
                    ByteUtil.isEquals(OperationCodeEnum.OP_HASH160.getCode(),bytesOperationCode) ||
                    ByteUtil.isEquals(OperationCodeEnum.OP_EQUALVERIFY.getCode(),bytesOperationCode) ||
                    ByteUtil.isEquals(OperationCodeEnum.OP_CHECKSIG.getCode(),bytesOperationCode)){
                String stringOperationCode = ByteUtil.bytesToHexString(bytesOperationCode);
                script.add(stringOperationCode);
            }else if(ByteUtil.isEquals(OperationCodeEnum.OP_PUSHDATA.getCode(),bytesOperationCode)){
                String stringOperationCode = ByteUtil.bytesToHexString(bytesOperationCode);
                script.add(stringOperationCode);

                long bytesOperationDataLength = ByteUtil.bytesToUint64(Arrays.copyOfRange(bytesScript,start,start + ByteUtil.BYTE8_BYTE_COUNT));
                start += ByteUtil.BYTE8_BYTE_COUNT;
                byte[] bytesOperationData = Arrays.copyOfRange(bytesScript,start, start+(int) bytesOperationDataLength);
                start += bytesOperationDataLength;
                String stringOperationData = ByteUtil.bytesToHexString(bytesOperationData);
                script.add(stringOperationData);
            }else {
                throw new RuntimeException("不能识别的指令");
            }
        }
        return script;
    }
    //endregion

    /**
     * 是否是P2PKH输入脚本
     */
    public static boolean isPayToPublicKeyHashInputScript(InputScriptDto inputScriptDto) {
        return  (inputScriptDto.size() == 4) &&
                (StringUtil.isEquals(ByteUtil.bytesToHexString(OperationCodeEnum.OP_PUSHDATA.getCode()),inputScriptDto.get(0))) &&
                (136 <= inputScriptDto.get(1).length() && 144 >= inputScriptDto.get(1).length()) &&
                (StringUtil.isEquals(ByteUtil.bytesToHexString(OperationCodeEnum.OP_PUSHDATA.getCode()),inputScriptDto.get(2))) &&
                (66 == inputScriptDto.get(3).length());
    }

    /**
     * 是否是P2PKH输出脚本
     */
    public static boolean isPayToPublicKeyHashOutputScript(OutputScriptDto outputScriptDto) {
        return  (outputScriptDto.size() == 6) &&
                (StringUtil.isEquals(ByteUtil.bytesToHexString(OperationCodeEnum.OP_DUP.getCode()),outputScriptDto.get(0))) &&
                (StringUtil.isEquals(ByteUtil.bytesToHexString(OperationCodeEnum.OP_HASH160.getCode()),outputScriptDto.get(1))) &&
                (StringUtil.isEquals(ByteUtil.bytesToHexString(OperationCodeEnum.OP_PUSHDATA.getCode()),outputScriptDto.get(2))) &&
                (40 == outputScriptDto.get(3).length()) &&
                (StringUtil.isEquals(ByteUtil.bytesToHexString(OperationCodeEnum.OP_EQUALVERIFY.getCode()),outputScriptDto.get(4))) &&
                (StringUtil.isEquals(ByteUtil.bytesToHexString(OperationCodeEnum.OP_CHECKSIG.getCode()),outputScriptDto.get(5)));
    }

    /**
     * 获取P2PKH输出脚本中的公钥哈希
     */
    public static String getPublicKeyHashFromPayToPublicKeyHashOutputScript(OutputScriptDto outputScript) {
        return outputScript.get(3);
    }
}
