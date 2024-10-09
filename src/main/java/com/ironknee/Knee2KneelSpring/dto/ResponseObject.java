package com.ironknee.Knee2KneelSpring.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseObject<T> {
    private String code;
    private String message;
    private T data;
}
