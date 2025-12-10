package com.faceauth.core.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author gongym
 * @version 创建时间: 2024-01-03 18:48
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResp<T> {
    private Long total;
    private List<T> records;
}
