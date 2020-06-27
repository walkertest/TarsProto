package com.tencent.tars;

import com.google.gson.Gson;
import com.qq.tars.common.util.HexUtil;
import com.qq.tars.protocol.tars.TarsInputStream;
import com.qq.tars.protocol.tars.TarsOutputStream;
import com.tencent.tars.testtars.TestList;
import com.tencent.tars.testtars.TestMap;
import com.tencent.tars.testtars.TestStruct;
import org.junit.Test;

import java.nio.ByteBuffer;

/**
 * Created by walker on 2020/6/27.
 */
public class TarsSkipToTagTest {

    private static final Gson gson = new Gson();

    @Test
    public void testList() {
        TestList testList = new TestList();
        testList.testStr = "hello";

        TarsOutputStream tafOutputStream = new TarsOutputStream();
        tafOutputStream.setServerEncoding("UTF-8");
        testList.writeTo(tafOutputStream);
        ByteBuffer byteBuffer = tafOutputStream.getByteBuffer();
        System.out.println("hexStr:"+ HexUtil.bytes2HexStr(byteBuffer.array()));


        //解析
        TestList liveRoomView2 = new TestList();
        TarsInputStream tafInputStream = new TarsInputStream(byteBuffer.array());
        tafInputStream.setServerEncoding("UTF-8");
        liveRoomView2.readFrom(tafInputStream);
        System.out.println(gson.toJson(liveRoomView2));
    }

    @Test
    public void testMap() {
        TestMap testList = new TestMap();
        testList.testStr = "hello";

        TarsOutputStream tafOutputStream = new TarsOutputStream();
        tafOutputStream.setServerEncoding("UTF-8");
        testList.writeTo(tafOutputStream);
        ByteBuffer byteBuffer = tafOutputStream.getByteBuffer();
        System.out.println("hexStr:"+ HexUtil.bytes2HexStr(byteBuffer.array()));


        //解析
        TestMap liveRoomView2 = new TestMap();
        TarsInputStream tafInputStream = new TarsInputStream(byteBuffer.array());
        tafInputStream.setServerEncoding("UTF-8");
        liveRoomView2.readFrom(tafInputStream);
        System.out.println(gson.toJson(liveRoomView2));
    }

    @Test
    public void testStruct() {
        TestStruct testList = new TestStruct();
        testList.testStr = "hello";

        TarsOutputStream tafOutputStream = new TarsOutputStream();
        tafOutputStream.setServerEncoding("UTF-8");
        testList.writeTo(tafOutputStream);
        ByteBuffer byteBuffer = tafOutputStream.getByteBuffer();
        System.out.println("hexStr:"+ HexUtil.bytes2HexStr(byteBuffer.array()));


        //解析
        TestStruct liveRoomView2 = new TestStruct();
        TarsInputStream tafInputStream = new TarsInputStream(byteBuffer.array());
        tafInputStream.setServerEncoding("UTF-8");
        liveRoomView2.readFrom(tafInputStream);
        System.out.println(gson.toJson(liveRoomView2));
    }
}
