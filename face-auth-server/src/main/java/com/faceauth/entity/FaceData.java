package com.faceauth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("face_data")
public class FaceData {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String userId;

    private String imageUrl;

    private String featureVector;

    private Long createTime;

    private Long updateTime;
}
