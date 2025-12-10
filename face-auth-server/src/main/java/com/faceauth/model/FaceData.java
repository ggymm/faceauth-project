package com.faceauth.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("face_data")
public class FaceData {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String userId;

    private String imageUrl;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
