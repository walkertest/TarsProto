package com.tencent.prototest;

import com.qq.tars.common.util.HexUtil;
import com.qq.tars.net.core.IoBuffer;
import com.qq.tars.net.protocol.ProtocolException;
import com.qq.tars.protocol.util.TarsHelper;
import com.qq.tars.rpc.protocol.tars.TarsServantRequest;
import com.qq.tars.rpc.protocol.tars.TarsServantResponse;
import com.tencent.prototest.util.TarsProtoUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * Created by walker on 2020/4/19.
 */
@Slf4j
public class TarsEncAndDec {

    /**
     * tars协议序列化为字节数组.
     */
    @Test
    public void tarsEncodeRequest() throws ProtocolException {
        System.out.println("hello");
        TarsServantRequest tarsServantRequest = buildReq();
        IoBuffer ioBuffer = TarsProtoUtil.tarsProtoEncodeRequest(tarsServantRequest);
        byte[] byteArray = ioBuffer.buf().array();
        String hexByteStr = HexUtil.bytes2HexStr(byteArray);
        System.out.println(hexByteStr);
    }


    @Test
    public void tarsDecodeRequest() {
        //上面encode生成的hexStr
        String hexByteStr =
                "0000004710012C3C4001562274656E63656E742E7461727370726F746F2E74657374536572766572616E744F626A66046563686F7D00000A000000000000000000008C980CA80C000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";

        byte[] array = HexUtil.hexStr2Bytes(hexByteStr);
        IoBuffer ioBuffer = IoBuffer.wrap(array);

        TarsServantRequest tarsServantRequest  = TarsProtoUtil.tarsProtoDecodeRequest(ioBuffer);
        log.info("version:{} servant:{} functionName:{}",
                tarsServantRequest.getVersion(),
                tarsServantRequest.getServantName(),
                tarsServantRequest.getFunctionName());
    }

    @Test
    public void tarsEncodeResponse() {
        TarsServantResponse tarsServantResponse = buildResp();
        IoBuffer ioBuffer = TarsProtoUtil.tarsProtoEncodeResponse(tarsServantResponse);
        String respHexStr = HexUtil.bytes2HexStr(ioBuffer.buf().array());
        log.info("respHexStr:{}", respHexStr);
    }

    @Test
    public void tarsDecodeResponse() throws ProtocolException {
        String responseHexStr =
                "0000001910012C30FF4C5C6D00000A0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
        IoBuffer ioBuffer = IoBuffer.wrap(HexUtil.hexStr2Bytes(responseHexStr));
        TarsServantResponse tarsServantResponse = TarsProtoUtil.tarsProtoDecodeResonse(ioBuffer);
        log.info("version:{} ret:{}",
                tarsServantResponse.getVersion(),
                tarsServantResponse.getRet());
    }

    private TarsServantResponse buildResp() {
        TarsServantResponse tarsServantResponse = new TarsServantResponse(null);
        tarsServantResponse.setVersion(TarsHelper.VERSION);
        tarsServantResponse.setContext(null);
        tarsServantResponse.setRet(TarsHelper.SERVERSUCCESS);
        tarsServantResponse.setResult(0);
        return tarsServantResponse;
    }

    private TarsServantRequest buildReq() {
        TarsServantRequest tarsServantRequest = new TarsServantRequest(null);
        tarsServantRequest.setVersion(TarsHelper.VERSION);
        tarsServantRequest.setServantName("tencent.tarsproto.testServerantObj");
        tarsServantRequest.setFunctionName("echo");
        return tarsServantRequest;
    }


}
