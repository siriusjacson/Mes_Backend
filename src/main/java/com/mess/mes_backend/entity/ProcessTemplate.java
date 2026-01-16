package com.mess.mes_backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_process_template")
public class ProcessTemplate {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long modelId;
    private String processName;
    private Integer stdTime;
}
