package com.tencent.prototest.util;

import com.qq.tars.common.util.StringUtils;
import com.qq.tars.net.core.IoBuffer;
import com.qq.tars.net.protocol.ProtocolException;
import com.qq.tars.protocol.tars.TarsInputStream;
import com.qq.tars.protocol.tars.TarsOutputStream;
import com.qq.tars.protocol.util.TarsHelper;
import com.qq.tars.rpc.protocol.tars.TarsServantRequest;
import com.qq.tars.rpc.protocol.tars.TarsServantResponse;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by walker on 2020/4/19.
 */
public class TarsProtoUtil {

    public static IoBuffer tarsProtoEncodeRequest(TarsServantRequest request) throws ProtocolException {
        TarsOutputStream os = new TarsOutputStream();
        os.setServerEncoding(StandardCharsets.UTF_8.name());
        os.getByteBuffer().putInt(0);
        os.write(request.getVersion(), 1);
        os.write(request.getPacketType(), 2);
        os.write(request.getMessageType(), 3);
        os.write(request.getTicketNumber(), 4);
        os.write(request.getServantName(), 5);
        os.write(request.getFunctionName(), 6);
        //请求body，构造成字节数组存在tag=7.
        byte[] body = new byte[10];
        os.write(body, 7);
        os.write(request.getTimeout(), 8);
        os.write(request.getContext(), 9);
        os.write(request.getStatus(), 10);
        os.getByteBuffer().flip();
        int length = os.getByteBuffer().remaining();
        os.getByteBuffer().duplicate().putInt(0, length);
        if (length <= 10485760 && length > 0) {
            return IoBuffer.wrap(os.getByteBuffer());
        } else {
            throw new ProtocolException("the length header of the package must be between 0~10M bytes. data length:" + Integer.toHexString(length));
        }
    }

    public static TarsServantRequest tarsProtoDecodeRequest(IoBuffer buffer) {
        int length = buffer.getInt() - TarsHelper.HEAD_SIZE;
        System.out.println("length:" + length);
        byte[] reads = new byte[length];
        buffer.get(reads);

        TarsInputStream jis = new TarsInputStream(reads);
        TarsServantRequest request = new TarsServantRequest(null);
        try {
            short version = jis.read(TarsHelper.STAMP_SHORT.shortValue(), 1, true);
            byte packetType = jis.read(TarsHelper.STAMP_BYTE.byteValue(), 2, true);
            int messageType = jis.read(TarsHelper.STAMP_INT.intValue(), 3, true);
            int requestId = jis.read(TarsHelper.STAMP_INT.intValue(), 4, true);
            String servantName = jis.readString(5, true);
            String methodName = jis.readString(6, true);
            byte[] data = jis.read(TarsHelper.STAMP_BYTE_ARRAY, 7, true);//数据
            int timeout = jis.read(TarsHelper.STAMP_INT.intValue(), 8, true);//超时时间
            Map<String, String> context = (Map<String, String>) jis.read(TarsHelper.STAMP_MAP, 9, true);//Map<String, String> context
            Map<String, String> status = (Map<String, String>) jis.read(TarsHelper.STAMP_MAP, 10, true);

            request.setVersion(version);
            request.setPacketType(packetType);
            request.setMessageType(messageType);
            request.setRequestId(requestId);
            request.setServantName(servantName);
            request.setFunctionName(methodName);
            request.setInputStream(jis);
            request.setCharsetName(StandardCharsets.UTF_8.name());

            request.setTimeout(timeout);
            request.setContext(context);
            request.setStatus(status);

            //todo--解析data.

        } catch (Exception e ) {
            System.out.println("exception.");
        }
        return request;

    }

    public static IoBuffer tarsProtoEncodeResponse(TarsServantResponse response) {
        TarsOutputStream jos = new TarsOutputStream();
        jos.setServerEncoding(StandardCharsets.UTF_8.name());
        try {
            jos.getByteBuffer().putInt(0);
            jos.write(response.getVersion(), 1);
            jos.write(response.getPacketType(), 2);

            if (response.getVersion() == TarsHelper.VERSION) {
                jos.write(response.getRequestId(), 3);
                jos.write(response.getMessageType(), 4);
                jos.write(response.getRet(), 5);
                jos.write(encodeResult(response, StandardCharsets.UTF_8.name()), 6);
                if (response.getStatus() != null) {
                    jos.write(response.getStatus(), 7);
                }
                if (response.getRet() != TarsHelper.SERVERSUCCESS) {
                    jos.write(StringUtils.isEmpty(response.getRemark()) ? "" : response.getRemark(), 8);
                }
            } else if (TarsHelper.VERSION2 == response.getVersion() || TarsHelper.VERSION3 == response.getVersion()) {
                jos.write(response.getMessageType(), 3);
                jos.write(response.getTicketNumber(), 4);
                String servantName = response.getRequest().getServantName();
                jos.write(servantName, 5);
                jos.write(response.getRequest().getFunctionName(), 6);
                jos.write(encodeWupResult(response, StandardCharsets.UTF_8.name()), 7);
                jos.write(response.getTimeout(), 8);
                if (response.getContext() != null) {
                    jos.write(response.getContext(), 9);
                }
                if (response.getStatus() != null) {
                    jos.write(response.getStatus(), 10);
                }
            } else {
                response.setRet(TarsHelper.SERVERENCODEERR);
                System.err.println("un supported protocol, ver=" + response.getVersion());
            }
        } catch (Exception ex) {
            if (response.getRet() == TarsHelper.SERVERSUCCESS) {
                response.setRet(TarsHelper.SERVERENCODEERR);
            }
        }
        ByteBuffer buffer = jos.getByteBuffer();
        int datalen = buffer.position();
        buffer.position(0);
        buffer.putInt(datalen);
        buffer.position(datalen);
        byte[] responseByteArray = buffer.array();
        IoBuffer ioBuffer = IoBuffer.wrap(responseByteArray);
        return ioBuffer;
    }


    public static  TarsServantResponse tarsProtoDecodeResonse(IoBuffer buffer) throws ProtocolException {
        int length = buffer.getInt() - TarsHelper.HEAD_SIZE;

        byte[] bytes = new byte[length];
        buffer.get(bytes);

        TarsServantResponse response = new TarsServantResponse(null);
        response.setCharsetName(StandardCharsets.UTF_8.name());

        TarsInputStream is = new TarsInputStream(bytes);
        is.setServerEncoding(StandardCharsets.UTF_8.name());

        response.setVersion(is.read((short) 0, 1, true));
        response.setPacketType(is.read((byte) 0, 2, true));
        response.setRequestId(is.read((int) 0, 3, true));
        response.setMessageType(is.read((int) 0, 4, true));
        response.setRet(is.read((int) 0, 5, true));
        if (response.getRet() == TarsHelper.SERVERSUCCESS) {
            response.setInputStream(is);
        }

        byte[] data = is.read(new byte[]{}, 6, true);
        //todo 解码成接口描述返回体相关数据.
        response.setStatus((HashMap<String, String>) is.read(TarsHelper.STAMP_MAP, 7, false));
        return response;

    }

    private static byte[] encodeWupResult(TarsServantResponse response, String name) {
        byte[] data = new byte[10];
        return data;
    }

    private static byte[] encodeResult(TarsServantResponse response, String name) {
        byte[] data = new byte[10];
        return data;
    }

}
