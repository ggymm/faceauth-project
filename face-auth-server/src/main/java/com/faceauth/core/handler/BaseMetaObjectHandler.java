package com.faceauth.core.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

/**
 * @author gongym
 * @version 创建时间: 2023-12-07 10:06
 */
@Component
public class BaseMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        Long currentTime = System.currentTimeMillis();

        this.setFieldValByName("createTime", currentTime, metaObject);
        this.setFieldValByName("updateTime", currentTime, metaObject);
        this.setFieldValByName("delFlag", 1, metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        Long currentTime = System.currentTimeMillis();

        this.setFieldValByName("updateTime", currentTime, metaObject);
        this.setFieldValByName("delFlag", 1, metaObject);
    }
}
