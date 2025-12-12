package com.faceauth.core.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class DataPageReq extends PageReq{
    private String userId;
}
