package com.faceauth.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.faceauth.core.convert.DateTimeSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

@Data
@TableName("face_data")
public class FaceData {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField(value = "user_id")
    private String userId;

    @TableField(value = "image_url")
    private String imageUrl;

    @TableField(value = "feature_vector")
    private String featureVector;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonSerialize(using = DateTimeSerializer.class)
    private Long createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @JsonSerialize(using = DateTimeSerializer.class)
    private Long updateTime;

    @TableLogic
    @TableField(value = "del_flag", fill = FieldFill.INSERT)
    private Integer delFlag;
}
