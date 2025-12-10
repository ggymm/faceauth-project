package com.faceauth.core.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * @author gongym
 * @version 创建时间: 2023-12-08 15:11
 */
@Data
public class GetReq {
    @NotNull(message = "id 不能为空")
    private Serializable id;
}
