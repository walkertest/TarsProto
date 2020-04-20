package com.tencent.prototest;

import com.alibaba.fastjson.JSON;
import com.qq.tars.common.support.Endpoint;
import com.qq.tars.common.util.HexUtil;
import com.qq.tars.protocol.tars.TarsInputStream;
import com.qq.tars.protocol.tars.TarsOutputStream;
import com.qq.tars.protocol.util.TarsDisplayer;
import com.qq.tars.support.query.prx.EndpointF;
import org.junit.Test;

/**
 * tars协议本身的序列化与反序列化
 */
public class TarsEncAndDec {

    @Test
    public void testTarsEnc() {
        EndpointF endpointF = new EndpointF();
        endpointF.setHost("127.0.0.1");

        TarsOutputStream tarsOutputStream = new TarsOutputStream();
        endpointF.writeTo(tarsOutputStream);
        String hexStr = HexUtil.bytes2HexStr(tarsOutputStream.getByteBuffer().array());
        System.out.println(hexStr);
    }

    @Test
    public void testTarsDecode() {
        String hexStr =
                "06093132372E302E302E311C2C3C4C5C6C76008C9CBCCC000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";

        TarsInputStream tarsInputStream = new TarsInputStream(HexUtil.hexStr2Bytes(hexStr));
        EndpointF endpointF = new EndpointF();
        endpointF.readFrom(tarsInputStream);

        System.out.println(JSON.toJSONString(endpointF));
    }
}
