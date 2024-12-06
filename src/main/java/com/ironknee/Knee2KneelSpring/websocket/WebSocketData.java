package com.ironknee.Knee2KneelSpring.websocket;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
public class WebSocketData<T> implements Serializable {
//    private String functionName;
//    private T data;
    private String action;
    private T data;
}