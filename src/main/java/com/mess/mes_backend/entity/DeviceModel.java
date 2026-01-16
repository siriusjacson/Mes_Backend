package com.mess.mes_backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("mes_device_model")
public class DeviceModel {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String modelName;
    private String modelCode;
    private LocalDateTime createTime;
}
