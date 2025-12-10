package com.faceauth.core.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PageReq {

    /**
     * 页码
     */
    @NotNull(message = "页码不能为空")
    private Integer page;

    /**
     * 每页数量
     */
    @NotNull(message = "每页数量不能为空")
    private Integer size;
}
