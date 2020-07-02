package com.tencent.tars;

import com.alibaba.fastjson.JSON;
import com.qq.tars.common.util.HexUtil;
import com.qq.tars.protocol.tars.TarsInputStream;
import com.qq.tars.protocol.tars.TarsOutputStream;
import com.qq.tars.protocol.tars.TarsOutputStreamExt;
import com.tencent.model.Student;
import com.tencent.tars.testtars.LiveRoomView;
import org.junit.Test;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

public class TestTypeErasue {

    /**
     * output:
     * hello
     * {"age":0,"students":[{"age":0,"name":"hello"}]}
     * hexStr:0A0C1900010A0B0B
     * {"age":0,"students":[{"age":0}]}
     */
    @Test
    public void testWriteMethod() {
        LiveRoomView liveRoomView = getWrongTypeData();
        TarsOutputStream TarsOutputStream = new TarsOutputStream();
        TarsOutputStream.setServerEncoding("UTF-8");
        TarsOutputStream.write(liveRoomView,0);
        byte[] byteArray = TarsOutputStream.toByteArray();
        System.out.println("hexStr:"+ HexUtil.bytes2HexStr(byteArray));

        //解析
        LiveRoomView liveRoomView2 = new LiveRoomView();
        LiveRoomView liveRoomViewRet;
        TarsInputStream TarsInputStream = new TarsInputStream(byteArray);
        TarsInputStream.setServerEncoding("UTF-8");
        liveRoomViewRet = (LiveRoomView) TarsInputStream.read(liveRoomView2,0,false);
        System.out.println(JSON.toJSONString(liveRoomViewRet));
    }


    private LiveRoomView getWrongTypeData() {
        com.tencent.model.LiveRoomView liveRoomView = new com.tencent.model.LiveRoomView();
        List<com.tencent.model.Student> students = new ArrayList<>();
        com.tencent.model.Student student = new com.tencent.model.Student();
        student.setName("hello");
        students.add(student);
        liveRoomView.setStudents(students);
        List<com.tencent.model.LiveRoomView > liveRoomViewList = new ArrayList<>();
        liveRoomViewList.add(liveRoomView);

        LiveRoomView liveRoomView2 = new LiveRoomView();
        BeanUtils.copyProperties(liveRoomView, liveRoomView2);

        System.out.println("hello");
        System.out.println(JSON.toJSONString(liveRoomView2));
//        System.out.println(liveRoomView2.getStudents().get(0).getClass());
        return liveRoomView2;
    }
}
