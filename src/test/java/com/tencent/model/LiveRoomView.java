package com.tencent.model;


import lombok.Data;

import java.util.List;

@Data
public class LiveRoomView {
    private long age;
    private List<Student> students;
}
